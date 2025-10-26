import sys
import threading
import time
import queue
import warnings
from dataclasses import dataclass
from typing import Tuple, Optional, Dict
from collections import deque

import numpy as np
import pandas as pd
import matplotlib
matplotlib.use("TkAgg")  # Tkinter backend
import matplotlib.pyplot as plt
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
from matplotlib.figure import Figure

from sklearn.linear_model import Ridge

try:
    import yfinance as yf
except Exception as e:
    print("Failed to import yfinance. Did you install requirements?")
    raise

import tkinter as tk
from tkinter import ttk, messagebox


# Popular stocks to choose from
POPULAR_STOCKS = {
    "AAPL": "Apple Inc.",
    "MSFT": "Microsoft Corporation",
    "GOOGL": "Alphabet Inc.",
    "AMZN": "Amazon.com Inc.",
    "TSLA": "Tesla Inc.",
    "META": "Meta Platforms Inc.",
    "NVDA": "NVIDIA Corporation",
    "JPM": "JPMorgan Chase & Co.",
    "V": "Visa Inc.",
    "JNJ": "Johnson & Johnson",
    "WMT": "Walmart Inc.",
    "PG": "Procter & Gamble Co.",
    "DIS": "The Walt Disney Company",
    "MA": "Mastercard Incorporated",
    "HD": "The Home Depot Inc."
}


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
        self.root.title("📈 Live Stock Price + ML Forecast")
        self.root.geometry("1200x700")
        self.root.configure(bg="#2b2b2b")
        
        # Set modern theme colors
        self.bg_color = "#2b2b2b"
        self.fg_color = "#ffffff"
        self.accent_color = "#4CAF50"
        self.error_color = "#f44336"
        self.warning_color = "#ff9800"
        
        self.stop_event = threading.Event()
        self.worker: Optional[threading.Thread] = None
        self.q = queue.Queue(maxsize=5)  # (df, next_price) tuples
        self.polling_active = True

        self.params = Params()

        # UI layout
        self._build_controls()
        self._build_plot()

        # Start polling
        self._poll_queue()

    def _build_controls(self):
        # Main control frame with padding
        main_frame = tk.Frame(self.root, bg=self.bg_color, padx=15, pady=10)
        main_frame.pack(side=tk.TOP, fill=tk.X)
        
        # Title
        title_label = tk.Label(
            main_frame, 
            text="Live Stock Price Monitor & ML Forecast", 
            font=("Arial", 14, "bold"),
            bg=self.bg_color,
            fg=self.fg_color
        )
        title_label.grid(row=0, column=0, columnspan=8, pady=(0, 15), sticky="w")
        
        # Control panel with improved styling
        frm = tk.LabelFrame(
            main_frame,
            text="Control Panel",
            font=("Arial", 10, "bold"),
            bg=self.bg_color,
            fg=self.accent_color,
            padx=10,
            pady=10
        )
        frm.grid(row=1, column=0, columnspan=8, sticky="ew", pady=(0, 10))

        # Ticker with dropdown
        tk.Label(frm, text="Stock Ticker", bg=self.bg_color, fg=self.fg_color, font=("Arial", 9)).grid(row=0, column=0, sticky="w", pady=2)
        self.ticker_var = tk.StringVar(value="AAPL")
        ticker_frame = tk.Frame(frm, bg=self.bg_color)
        ticker_frame.grid(row=1, column=0, padx=(0, 15))
        
        ticker_entry = tk.Entry(
            ticker_frame, 
            textvariable=self.ticker_var, 
            width=10,
            font=("Arial", 9),
            bg="#1e1e1e",
            fg=self.fg_color,
            insertbackground=self.fg_color
        )
        ticker_entry.pack(side=tk.LEFT)
        
        ticker_combo = ttk.Combobox(
            ticker_frame,
            values=list(POPULAR_STOCKS.keys()),
            width=12,
            state="readonly"
        )
        ticker_combo.pack(side=tk.LEFT, padx=(5, 0))
        ticker_combo.bind("<<ComboboxSelected>>", lambda e: self.ticker_var.set(ticker_combo.get()))
        ticker_combo.set("AAPL")
        
        self.ticker_info_label = tk.Label(
            frm,
            text="Apple Inc.",
            bg=self.bg_color,
            fg="#888888",
            font=("Arial", 8),
            anchor="w"
        )
        self.ticker_info_label.grid(row=2, column=0, sticky="w")
        
        def update_ticker_info(*args):
            ticker = self.ticker_var.get().strip().upper()
            info = POPULAR_STOCKS.get(ticker, "Custom ticker")
            self.ticker_info_label.config(text=info)
        
        self.ticker_var.trace("w", update_ticker_info)
        ticker_combo.bind("<<ComboboxSelected>>", lambda e: (self.ticker_var.set(ticker_combo.get()), update_ticker_info()))

        # Interval
        tk.Label(frm, text="Interval", bg=self.bg_color, fg=self.fg_color, font=("Arial", 9)).grid(row=0, column=1, sticky="w", pady=2)
        self.interval_var = tk.StringVar(value="1m")
        interval_combo = ttk.Combobox(frm, textvariable=self.interval_var, width=10, state="readonly")
        interval_combo["values"] = ("1m", "2m", "5m", "15m", "30m", "60m", "90m", "1h", "1d")
        interval_combo.grid(row=1, column=1, padx=(0, 15), sticky="w")

        # Window
        tk.Label(frm, text="Window Size", bg=self.bg_color, fg=self.fg_color, font=("Arial", 9)).grid(row=0, column=2, sticky="w", pady=2)
        self.window_var = tk.IntVar(value=60)
        window_spin = ttk.Spinbox(frm, from_=10, to=500, increment=5, textvariable=self.window_var, width=10)
        window_spin.grid(row=1, column=2, padx=(0, 15), sticky="w")

        # Refresh
        tk.Label(frm, text="Refresh (sec)", bg=self.bg_color, fg=self.fg_color, font=("Arial", 9)).grid(row=0, column=3, sticky="w", pady=2)
        self.refresh_var = tk.IntVar(value=30)
        refresh_spin = ttk.Spinbox(frm, from_=5, to=300, increment=5, textvariable=self.refresh_var, width=10)
        refresh_spin.grid(row=1, column=3, padx=(0, 15), sticky="w")

        # Alpha
        tk.Label(frm, text="Ridge Alpha", bg=self.bg_color, fg=self.fg_color, font=("Arial", 9)).grid(row=0, column=4, sticky="w", pady=2)
        self.alpha_var = tk.DoubleVar(value=1.0)
        alpha_spin = ttk.Spinbox(frm, from_=0.0, to=100.0, increment=0.1, textvariable=self.alpha_var, width=10)
        alpha_spin.grid(row=1, column=4, padx=(0, 15), sticky="w")

        # Buttons with better styling
        button_frame = tk.Frame(frm, bg=self.bg_color)
        button_frame.grid(row=0, column=5, rowspan=2, padx=(10, 0))
        
        self.start_btn = tk.Button(
            button_frame,
            text="▶ Start",
            command=self.start,
            bg=self.accent_color,
            fg="white",
            font=("Arial", 10, "bold"),
            padx=20,
            pady=8,
            relief=tk.FLAT,
            cursor="hand2"
        )
        self.start_btn.pack(side=tk.LEFT, padx=5)
        
        self.stop_btn = tk.Button(
            button_frame,
            text="■ Stop",
            command=self.stop,
            bg=self.error_color,
            fg="white",
            font=("Arial", 10, "bold"),
            padx=20,
            pady=8,
            relief=tk.FLAT,
            cursor="hand2",
            state="disabled"
        )
        self.stop_btn.pack(side=tk.LEFT, padx=5)
        
        # Configure grid weights
        frm.columnconfigure(5, weight=1)

        # Status bar with better styling
        status_frame = tk.Frame(self.root, bg="#1e1e1e", height=40, relief=tk.SUNKEN)
        status_frame.pack(side=tk.BOTTOM, fill=tk.X)
        
        self.status_var = tk.StringVar(value="⏸️ Idle - Ready to start")
        status_label = tk.Label(
            status_frame,
            textvariable=self.status_var,
            bg="#1e1e1e",
            fg=self.fg_color,
            font=("Arial", 10),
            anchor="w",
            padx=15,
            pady=10
        )
        status_label.pack(fill=tk.X)
        
        # Metrics display
        self.metrics_frame = tk.Frame(status_frame, bg="#1e1e1e")
        self.metrics_frame.pack(side=tk.RIGHT, padx=15)
        
        self.current_price_label = tk.Label(
            self.metrics_frame,
            text="Current: ---",
            bg="#1e1e1e",
            fg=self.accent_color,
            font=("Arial", 9, "bold")
        )
        self.current_price_label.pack(side=tk.LEFT, padx=10)
        
        self.predicted_price_label = tk.Label(
            self.metrics_frame,
            text="Predicted: ---",
            bg="#1e1e1e",
            fg=self.warning_color,
            font=("Arial", 9, "bold")
        )
        self.predicted_price_label.pack(side=tk.LEFT, padx=10)

    def _build_plot(self):
        # Use matplotlib figure with improved styling
        self.fig = Figure(figsize=(12, 6), facecolor="#2b2b2b")
        self.ax = self.fig.add_subplot(111, facecolor="#1e1e1e")
        
        self.canvas = FigureCanvasTkAgg(self.fig, master=self.root)
        self.canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True, padx=15, pady=(0, 15))
        
        # Initial plot setup
        self.ax.set_title("Price will appear here", color=self.fg_color, fontsize=12, pad=15)
        self.ax.set_xlabel("Time", color=self.fg_color, fontsize=10)
        self.ax.set_ylabel("Price ($)", color=self.fg_color, fontsize=10)
        self.ax.tick_params(colors=self.fg_color)
        self.ax.spines['bottom'].set_color(self.fg_color)
        self.ax.spines['top'].set_color(self.fg_color)
        self.ax.spines['right'].set_color(self.fg_color)
        self.ax.spines['left'].set_color(self.fg_color)
        
        self.fig.tight_layout()

    def start(self):
        if self.worker and self.worker.is_alive():
            return
        
        # Read params
        self.params = Params(
            ticker=self.ticker_var.get().strip().upper() or "AAPL",
            interval=self.interval_var.get(),
            window=max(10, int(self.window_var.get())),
            refresh=max(5, int(self.refresh_var.get())),
            alpha=float(self.alpha_var.get())
        )

        self.status_var.set(f"🟢 Running {self.params.ticker} ({self.params.interval}) - Fetching data...")
        self.stop_event.clear()
        self.worker = threading.Thread(target=self._worker_loop, daemon=True)
        self.worker.start()
        self.start_btn.config(state="disabled")
        self.stop_btn.config(state="normal")

    def stop(self):
        self.stop_event.set()
        self.start_btn.config(state="normal")
        self.stop_btn.config(state="disabled")
        self.status_var.set("⏸️ Stopped - Processing complete")

    def _worker_loop(self):
        warnings.filterwarnings("ignore", category=UserWarning)
        model = Ridge(alpha=self.params.alpha)

        while not self.stop_event.is_set():
            try:
                df = get_data(self.params.ticker, self.params.interval)
            except Exception as e:
                self._post_status(f"❌ Data fetch failed: {str(e)}", error=True)
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
                self._post_status(f"⚠️ Feature/fit failed: {e}")
                time.sleep(self.params.refresh)
                continue

            # Put results in queue
            try:
                if self.q.full():
                    self.q.get_nowait()
                self.q.put_nowait((df, next_price, last_price, self.params.ticker, self.params.interval, self.params.alpha))
                
                # Update metrics
                self._update_metrics(last_price, next_price)
                self._post_status(f"✅ {self.params.ticker} | Last: ${last_price:.2f} | Predicted: ${next_price:.2f} | Refresh: {self.params.refresh}s")
            except queue.Full:
                pass

            # Sleep until next refresh
            for _ in range(self.params.refresh * 10):
                if self.stop_event.is_set():
                    break
                time.sleep(0.1)

        self._post_status("🔴 Worker stopped")

    def _update_metrics(self, current: float, predicted: float):
        def _update():
            self.current_price_label.config(text=f"Current: ${current:.2f}")
            
            diff = predicted - current
            pct = (diff / current * 100) if current != 0 else 0
            color = self.accent_color if diff > 0 else self.error_color if diff < 0 else self.fg_color
            symbol = "↑" if diff > 0 else "↓" if diff < 0 else "→"
            
            self.predicted_price_label.config(
                text=f"Predicted: ${predicted:.2f} ({symbol}{pct:+.2f}%)",
                fg=color
            )
        self.root.after(0, _update)

    def _post_status(self, text: str, error: bool = False):
        def _update():
            self.status_var.set(text)
        self.root.after(0, _update)

    def _poll_queue(self):
        """Poll queue for new plot data and redraw on the Tk main thread."""
        if not self.polling_active:
            return
            
        try:
            df, next_price, last_price, ticker, interval, alpha = self.q.get_nowait()
            
            # Redraw plot
            self.ax.clear()
            self.ax.set_facecolor("#1e1e1e")
            
            # Plot price history
            self.ax.plot(df.index, df["Close"], label=f"{ticker} Price", linewidth=2, color="#64b5f6")
            
            # Add prediction point
            if len(df.index) >= 2:
                delta = df.index[-1] - df.index[-2]
                next_t = df.index[-1] + delta
            else:
                next_t = df.index[-1]
            
            self.ax.scatter([next_t], [next_price], marker="*", s=200, label="ML Prediction", color=self.warning_color, zorder=5)
            
            # Current price marker
            self.ax.scatter([df.index[-1]], [last_price], marker="o", s=100, label="Current", color=self.accent_color, zorder=5)
            
            # Styling
            self.ax.set_title(f"{ticker} Live Price & ML Forecast • {interval} • Ridge(α={alpha})", 
                            color=self.fg_color, fontsize=12, pad=15)
            self.ax.set_xlabel("Time", color=self.fg_color, fontsize=10)
            self.ax.set_ylabel("Price ($)", color=self.fg_color, fontsize=10)
            self.ax.tick_params(colors=self.fg_color)
            
            # Legend with better styling
            legend = self.ax.legend(loc="best", framealpha=0.8, facecolor="#1e1e1e", edgecolor=self.fg_color)
            for text in legend.get_texts():
                text.set_color(self.fg_color)
            
            # Grid
            self.ax.grid(True, alpha=0.3, color=self.fg_color)
            self.ax.spines['bottom'].set_color(self.fg_color)
            self.ax.spines['top'].set_color(self.fg_color)
            self.ax.spines['right'].set_color(self.fg_color)
            self.ax.spines['left'].set_color(self.fg_color)
            
            self.fig.tight_layout()
            self.canvas.draw()
        except queue.Empty:
            pass
        finally:
            if self.polling_active:
                self.root.after(200, self._poll_queue)
    
    def cleanup(self):
        """Clean up resources"""
        self.polling_active = False
        if self.worker and self.worker.is_alive():
            self.stop()


def main():
    root = tk.Tk()
    app = LiveMLApp(root)
    
    def on_close():
        app.cleanup()
        root.quit()
        root.destroy()
    
    root.protocol("WM_DELETE_WINDOW", on_close)
    root.mainloop()


if __name__ == "__main__":
    main()
