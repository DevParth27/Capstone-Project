import tkinter as tk
from tkinter import ttk, filedialog, messagebox
from PIL import Image, ImageTk
import os

# Import our filter module
from instagram_filters import InstagramFilterApp, FilterProcessor

class InstagramFilterGUI:
    def __init__(self, root):
        self.root = root
        self.root.title("Instagram Filters with NLP - Python App")
        self.root.geometry("1000x700")
        
        self.app = InstagramFilterApp()
        self.processor = FilterProcessor()
        
        self.setup_ui()
        
    def setup_ui(self):
        # Main container
        main_frame = ttk.Frame(self.root, padding="10")
        main_frame.grid(row=0, column=0, sticky=(tk.W, tk.E, tk.N, tk.S))
        
        # Configure grid weights
        self.root.columnconfigure(0, weight=1)
        self.root.rowconfigure(0, weight=1)
        main_frame.columnconfigure(0, weight=1)
        main_frame.rowconfigure(0, weight=1)
        
        # Left panel - Controls
        left_panel = ttk.LabelFrame(main_frame, text="Controls", padding="10")
        left_panel.grid(row=0, column=0, sticky=(tk.W, tk.E, tk.N, tk.S), padx=(0, 10))
        
        # File selection
        file_frame = ttk.Frame(left_panel)
        file_frame.pack(fill=tk.X, pady=5)
        ttk.Button(file_frame, text="Load Image", command=self.load_image).pack(side=tk.LEFT, padx=5)
        ttk.Button(file_frame, text="Save Image", command=self.save_image).pack(side=tk.LEFT, padx=5)
        
        # NLP Command section
        nlp_frame = ttk.LabelFrame(left_panel, text="NLP Filter (Natural Language)", padding="10")
        nlp_frame.pack(fill=tk.BOTH, expand=True, pady=5)
        
        ttk.Label(nlp_frame, text="Describe your desired filter:").pack(anchor=tk.W)
        self.nlp_entry = ttk.Entry(nlp_frame, width=30)
        self.nlp_entry.pack(fill=tk.X, pady=5)
        self.nlp_entry.bind('<Return>', lambda e: self.apply_nlp_filter())
        
        ttk.Button(nlp_frame, text="Apply NLP Filter", command=self.apply_nlp_filter).pack(pady=5)
        
        # Example commands
        examples_frame = ttk.LabelFrame(nlp_frame, text="Examples", padding="5")
        examples_frame.pack(fill=tk.X, pady=5)
        
        examples = [
            "make it vintage",
            "apply cool blue tones",
            "black and white",
            "warm sunset look",
            "retro sepia",
            "high contrast and bold"
        ]
        
        for example in examples:
            btn = ttk.Button(examples_frame, text=example, width=28, 
                           command=lambda e=example: self.set_example(e))
            btn.pack(pady=2)
        
        # Manual filters
        filters_frame = ttk.LabelFrame(left_panel, text="Manual Filters", padding="5")
        filters_frame.pack(fill=tk.X, pady=5)
        
        filters = [
            ("Sepia", "sepia"),
            ("Vintage", "vintage"),
            ("Grayscale", "grayscale"),
            ("Cool Tone", "cool_tone"),
            ("Warm Tone", "warm_tone"),
            ("Blue Tint", "blue_tint"),
            ("Blur", "blur"),
            ("Sharpen", "sharpen"),
            ("Emboss", "emboss"),
            ("Nostalgic", "nostalgic"),
            ("High Contrast", "high_contrast"),
            ("Pastel", "pastel")
        ]
        
        for name, filter_id in filters:
            ttk.Button(filters_frame, text=name, 
                      command=lambda f=filter_id: self.apply_manual_filter(f)).pack(pady=2)
        
        # Reset button
        ttk.Button(left_panel, text="Reset to Original", command=self.reset_image).pack(pady=10)
        
        # Image display
        right_panel = ttk.LabelFrame(main_frame, text="Image Preview", padding="10")
        right_panel.grid(row=0, column=1, sticky=(tk.W, tk.E, tk.N, tk.S))
        right_panel.columnconfigure(0, weight=1)
        right_panel.rowconfigure(0, weight=1)
        main_frame.columnconfigure(1, weight=2)
        
        # Canvas for image
        self.canvas = tk.Canvas(right_panel, bg="white", relief=tk.SUNKEN, borderwidth=2)
        self.canvas.grid(row=0, column=0, sticky=(tk.W, tk.E, tk.N, tk.S))
        self.canvas_image = None
        
        # Status bar
        self.status = ttk.Label(main_frame, text="Ready - Load an image to begin", relief=tk.SUNKEN)
        self.status.grid(row=1, column=0, columnspan=2, sticky=(tk.W, tk.E))
    
    def set_example(self, example):
        self.nlp_entry.delete(0, tk.END)
        self.nlp_entry.insert(0, example)
        self.apply_nlp_filter()
    
    def load_image(self):
        file_path = filedialog.askopenfilename(
            title="Select Image",
            filetypes=[("Image files", "*.jpg *.jpeg *.png *.bmp *.gif")]
        )
        
        if file_path:
            try:
                self.app.load_image(file_path)
                self.update_display()
                self.status.config(text=f"Loaded: {os.path.basename(file_path)}")
            except Exception as e:
                messagebox.showerror("Error", f"Failed to load image: {str(e)}")
    
    def save_image(self):
        if not self.app.current_image:
            messagebox.showwarning("Warning", "No image to save")
            return
        
        file_path = filedialog.asksaveasfilename(
            title="Save Image",
            defaultextension=".jpg",
            filetypes=[("JPEG", "*.jpg"), ("PNG", "*.png")]
        )
        
        if file_path:
            try:
                self.app.current_image.save(file_path)
                self.status.config(text=f"Saved: {os.path.basename(file_path)}")
                messagebox.showinfo("Success", "Image saved successfully!")
            except Exception as e:
                messagebox.showerror("Error", f"Failed to save image: {str(e)}")
    
    def apply_nlp_filter(self):
        if not self.app.original_image:
            messagebox.showwarning("Warning", "Please load an image first")
            return
        
        text = self.nlp_entry.get()
        if not text:
            messagebox.showwarning("Warning", "Please enter a filter description")
            return
        
        try:
            self.app.current_image = self.app.apply_filter_by_text(text)
            self.update_display()
            self.status.config(text=f"Applied NLP filter: '{text}'")
        except Exception as e:
            messagebox.showerror("Error", f"Failed to apply filter: {str(e)}")
    
    def apply_manual_filter(self, filter_name):
        if not self.app.original_image:
            messagebox.showwarning("Warning", "Please load an image first")
            return
        
        try:
            self.app.apply_filter_by_name(filter_name)
            self.update_display()
            self.status.config(text=f"Applied filter: {filter_name}")
        except Exception as e:
            messagebox.showerror("Error", f"Failed to apply filter: {str(e)}")
    
    def reset_image(self):
        if not self.app.original_image:
            messagebox.showwarning("Warning", "Please load an image first")
            return
        
        self.app.reset_image()
        self.update_display()
        self.status.config(text="Reset to original")
    
    def update_display(self):
        if not self.app.current_image:
            return
        
        # Resize image to fit canvas
        img = self.app.current_image
        self.canvas.update_idletasks()  # Ensure canvas is updated
        canvas_width = self.canvas.winfo_width()
        canvas_height = self.canvas.winfo_height()
        
        if canvas_width > 1 and canvas_height > 1:
            # Calculate scaling to fit canvas
            img_width, img_height = img.size
            scale_x = (canvas_width - 40) / img_width
            scale_y = (canvas_height - 40) / img_height
            scale = min(scale_x, scale_y)
            
            new_width = int(img_width * scale)
            new_height = int(img_height * scale)
            
            img = img.resize((new_width, new_height), Image.Resampling.LANCZOS)
        
        # Convert to PhotoImage
        self.canvas_image = ImageTk.PhotoImage(img)
        
        # Clear canvas and center image
        self.canvas.delete("all")
        x = max(0, (canvas_width - img.width) // 2)
        y = max(0, (canvas_height - img.height) // 2)
        
        self.canvas.create_image(x, y, anchor=tk.NW, image=self.canvas_image)
        self.canvas.image = self.canvas_image  # Keep a reference


def main():
    root = tk.Tk()
    app = InstagramFilterGUI(root)
    root.mainloop()


if __name__ == "__main__":
    main()
