import sys
import threading
import time
import queue
import warnings
from dataclasses import dataclass
from typing import Tuple, Optional

import numpy as np
import pandas as pd
import matplotlib
matplotlib.use("TkAgg")  # Tkinter backend
import matplotlib.pyplot as plt
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg

from sklearn.linear_model import Ridge

try:
    import yfinance as yf
except Exception as e:
    print("Failed to import yfinance. Did you install requirements?")
    raise

import tkinter as tk
from tkinter import ttk, messagebox


def get_data(ticker: str, interval: str) -> pd.DataFrame:
    """Download latest data for the given ticker/interval and compute returns."""
    period = "5d" if interval in {"30m", "60m", "90m", "1h"} else "1d"
    df = yf.download(ticker, period=period, interval=interval, progress=False, auto_adjust=True)
    if df is None or df.empty:
        raise RuntimeError("Empty data returned from yfinance. Check ticker/interval or network.")
    df = df.dropna()
    df["ret"] = df["Close"].pct_change()
    df = df.dropna()
    return df


def make_features(df: pd.DataFrame, window: int) -> Tuple[np.ndarray, np.ndarray]:
    """Lag features + stats -> next-step return."""
    rets = df["ret"].values
    X, y = [], []
    for i in range(window, len(rets) - 1):
        past = rets[i - window:i]
        feats = np.r_[past, past.mean(), past.std()]
        X.append(feats)
        y.append(rets[i + 1])
    if not X:
        raise RuntimeError("Not enough data to build features. Reduce window or wait for more bars.")
    return np.array(X), np.array(y)


@dataclass
class Params:
    ticker: str = "AAPL"
    interval: str = "1m"
    window: int = 60
    refresh: int = 30
    alpha: float = 1.0


class LiveMLApp:
    def __init__(self, root: tk.Tk):
        self.root = root
        self.root.title("Live Price + ML Forecast")
        self.root.geometry("980x640")
        self.stop_event = threading.Event()
        self.worker: Optional[threading.Thread] = None
        self.q = queue.Queue(maxsize=5)  # (df, next_price) tuples

        self.params = Params()

        # UI layout
        self._build_controls()
        self._build_plot()

        # polling queue for updates
        self.root.after(200, self._poll_queue)

    def _build_controls(self):
        frm = ttk.Frame(self.root, padding=10)
        frm.pack(side=tk.TOP, fill=tk.X)

        # Ticker
        ttk.Label(frm, text="Ticker").grid(row=0, column=0, sticky="w")
        self.ticker_var = tk.StringVar(value=self.params.ticker)
        ttk.Entry(frm, textvariable=self.ticker_var, width=12).grid(row=1, column=0, padx=(0, 10))

        # Interval
        ttk.Label(frm, text="Interval").grid(row=0, column=1, sticky="w")
        self.interval_var = tk.StringVar(value=self.params.interval)
        cmb = ttk.Combobox(frm, textvariable=self.interval_var, width=8, state="readonly")
        cmb["values"] = ("1m", "2m", "5m", "15m", "30m", "60m", "90m", "1h", "1d")
        cmb.grid(row=1, column=1, padx=(0, 10))

        # Window
        ttk.Label(frm, text="Window").grid(row=0, column=2, sticky="w")
        self.window_var = tk.IntVar(value=self.params.window)
        ttk.Spinbox(frm, from_=10, to=1000, increment=5, textvariable=self.window_var, width=8).grid(row=1, column=2, padx=(0, 10))

        # Refresh
        ttk.Label(frm, text="Refresh (s)").grid(row=0, column=3, sticky="w")
        self.refresh_var = tk.IntVar(value=self.params.refresh)
        ttk.Spinbox(frm, from_=5, to=600, increment=5, textvariable=self.refresh_var, width=8).grid(row=1, column=3, padx=(0, 10))

        # Alpha
        ttk.Label(frm, text="Ridge α").grid(row=0, column=4, sticky="w")
        self.alpha_var = tk.DoubleVar(value=self.params.alpha)
        ttk.Spinbox(frm, from_=0.0, to=100.0, increment=0.1, textvariable=self.alpha_var, width=8).grid(row=1, column=4, padx=(0, 10))

        # Buttons
        self.start_btn = ttk.Button(frm, text="Start", command=self.start)
        self.start_btn.grid(row=1, column=5, padx=(10, 5))
        self.stop_btn = ttk.Button(frm, text="Stop", command=self.stop, state="disabled")
        self.stop_btn.grid(row=1, column=6, padx=(5, 10))

        frm.columnconfigure(7, weight=1)

        # Status
        self.status_var = tk.StringVar(value="Idle")
        ttk.Label(self.root, textvariable=self.status_var, padding=6).pack(side=tk.TOP, anchor="w")

    def _build_plot(self):
        self.fig, self.ax = plt.subplots(figsize=(9.5, 4.8), dpi=100)
        self.canvas = FigureCanvasTkAgg(self.fig, master=self.root)
        self.canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)
        self.ax.set_title("Price will appear here")
        self.ax.set_xlabel("Time")
        self.ax.set_ylabel("Price")
        self.fig.tight_layout()

    def start(self):
        if self.worker and self.worker.is_alive():
            return
        # read params
        self.params = Params(
            ticker=self.ticker_var.get().strip() or "AAPL",
            interval=self.interval_var.get(),
            window=max(10, int(self.window_var.get())),
            refresh=max(5, int(self.refresh_var.get())),
            alpha=float(self.alpha_var.get())
        )

        self.status_var.set(f"Running {self.params.ticker} ({self.params.interval})")
        self.stop_event.clear()
        self.worker = threading.Thread(target=self._worker_loop, daemon=True)
        self.worker.start()
        self.start_btn.config(state="disabled")
        self.stop_btn.config(state="normal")

    def stop(self):
        self.stop_event.set()
        self.start_btn.config(state="normal")
        self.stop_btn.config(state="disabled")
        self.status_var.set("Stopped")

    def _worker_loop(self):
        warnings.filterwarnings("ignore", category=UserWarning)
        model = Ridge(alpha=self.params.alpha)

        while not self.stop_event.is_set():
            try:
                df = get_data(self.params.ticker, self.params.interval)
            except Exception as e:
                self._post_status(f"Data fetch failed: {e}")
                time.sleep(self.params.refresh)
                continue

            try:
                X, y = make_features(df, self.params.window)
                model.set_params(alpha=self.params.alpha)
                model.fit(X, y)

                last = df["ret"].values[-self.params.window:]
                feats_next = np.r_[last, last.mean(), last.std()].reshape(1, -1)
                next_ret = float(model.predict(feats_next)[0])
                last_price = float(df["Close"].iloc[-1])
                next_price = last_price * (1.0 + next_ret)
            except Exception as e:
                self._post_status(f"Feature/fit failed: {e}")
                time.sleep(self.params.refresh)
                continue

            # put results in queue (drop oldest if full)
            try:
                if self.q.full():
                    self.q.get_nowait()
                self.q.put_nowait((df, next_price, self.params.ticker, self.params.interval, self.params.alpha))
                self._post_status(f"Last price: {last_price:.4f}  |  Pred next: {next_price:.4f}")
            except queue.Full:
                pass

            # sleep until next refresh or until stopped
            for _ in range(self.params.refresh * 10):  # check stop flag every 0.1s
                if self.stop_event.is_set():
                    break
                time.sleep(0.1)

        self._post_status("Worker stopped.")

    def _post_status(self, text: str):
        def _update():
            self.status_var.set(text)
        self.root.after(0, _update)

    def _poll_queue(self):
        """Poll queue for new plot data and redraw on the Tk main thread."""
        try:
            while True:
                df, next_price, ticker, interval, alpha = self.q.get_nowait()
                # redraw
                self.ax.clear()
                self.ax.plot(df.index, df["Close"], label="Price")
                # next timestamp guess
                if len(df.index) >= 2:
                    delta = df.index[-1] - df.index[-2]
                    next_t = df.index[-1] + delta
                else:
                    next_t = df.index[-1]
                self.ax.scatter([next_t], [next_price], marker="x", label="ML next-step")
                self.ax.set_title(f"{ticker} live price • {interval} • ridge(α={alpha})")
                self.ax.set_xlabel("Time")
                self.ax.set_ylabel("Price")
                self.ax.legend(loc="best")
                self.fig.tight_layout()
                self.canvas.draw()
        except queue.Empty:
            pass
        finally:
            self.root.after(200, self._poll_queue)  # keep polling

def main():
    root = tk.Tk()
    app = LiveMLApp(root)
    def on_close():
        if app.worker and app.worker.is_alive():
            app.stop()
            time.sleep(0.2)
        root.destroy()
    root.protocol("WM_DELETE_WINDOW", on_close)
    root.mainloop()


if __name__ == "__main__":
    main()
