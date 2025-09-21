import tkinter as tk
from tkinter import filedialog, scrolledtext, ttk
import os
from PIL import Image, ImageTk
from qr_code_reader import QRCodeReader

class QRCodeApp:
    def __init__(self, root):
        self.root = root
        self.root.title("Barcode & QR Code Reader")
        self.root.geometry("800x600")
        self.current_image_path = None
        
        # Set Java-like look and feel
        self.set_java_look()
        
        # Create tabbed interface like Java Swing
        self.setup_ui()
    
    def set_java_look(self):
        # Try to make it look like Java Swing
        self.root.configure(bg='#f0f0f0')
        style = ttk.Style()
        style.theme_use('clam')  # Most Java-like theme
        
        # Configure styles to look more like Java Swing
        style.configure('TButton', font=('Arial', 10))
        style.configure('TFrame', background='#f0f0f0')
        style.configure('TNotebook', background='#f0f0f0')
        style.configure('TNotebook.Tab', padding=[10, 2], font=('Arial', 10))
    
    def setup_ui(self):
        # Create main frame instead of notebook
        main_frame = ttk.Frame(self.root)
        main_frame.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        
        # Setup File Reader directly in the main frame
        self.setup_file_reader(main_frame)
    
    def setup_file_reader(self, parent):
        # Top panel with button
        top_panel = ttk.Frame(parent)
        top_panel.pack(fill=tk.X, padx=10, pady=10)
        
        select_button = ttk.Button(top_panel, text="Select Image", command=self.select_image)
        select_button.pack(side=tk.LEFT)
        
        # Center panel with image
        center_panel = ttk.Frame(parent)
        center_panel.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
        
        self.image_panel = ttk.Label(center_panel, text="No image selected", background="#e0e0e0")
        self.image_panel.pack(fill=tk.BOTH, expand=True)
        
        # Bottom panel with results
        bottom_panel = ttk.Frame(parent)
        bottom_panel.pack(fill=tk.X, padx=10, pady=10)
        
        self.result_area = scrolledtext.ScrolledText(bottom_panel, height=5, wrap=tk.WORD)
        self.result_area.pack(fill=tk.X)
    
    def select_image(self):
        file_types = [("Image files", "*.jpg *.jpeg *.png *.gif *.bmp")]
        file_path = filedialog.askopenfilename(filetypes=file_types)
        
        if file_path:
            self.current_image_path = file_path
            self.display_image(file_path)
            self.decode_image(file_path)
    
    def display_image(self, image_path):
        # Open and resize image for display
        pil_image = Image.open(image_path)
        
        # Calculate new dimensions while maintaining aspect ratio
        max_width, max_height = 400, 300
        width, height = pil_image.size
        
        if width > max_width or height > max_height:
            scale = min(max_width / width, max_height / height)
            width = int(width * scale)
            height = int(height * scale)
            pil_image = pil_image.resize((width, height), Image.LANCZOS)
        
        # Convert to PhotoImage and display
        tk_image = ImageTk.PhotoImage(pil_image)
        self.image_panel.config(image=tk_image, text="")
        self.image_panel.image = tk_image  # Keep a reference to prevent garbage collection
    
    def decode_image(self, image_path):
        result = QRCodeReader.read_qr_code(image_path)
        
        if result:
            self.result_area.delete(1.0, tk.END)
            self.result_area.insert(tk.END, f"Format: {result['format']}\nContent: {result['content']}")
        else:
            self.result_area.delete(1.0, tk.END)
            self.result_area.insert(tk.END, "No QR code found in the image.")

if __name__ == "__main__":
    root = tk.Tk()
    app = QRCodeApp(root)
    root.mainloop()