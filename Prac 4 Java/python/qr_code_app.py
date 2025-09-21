import tkinter as tk
from tkinter import filedialog, scrolledtext
import os
from PIL import Image, ImageTk
from qr_code_reader import QRCodeReader

class QRCodeApp:
    def __init__(self, root):
        self.root = root
        self.root.title("QR Code Reader")
        self.root.geometry("800x600")
        self.current_image_path = None
        
        self.setup_ui()
    
    def setup_ui(self):
        # Top panel with button
        top_panel = tk.Frame(self.root)
        top_panel.pack(fill=tk.X, padx=10, pady=10)
        
        select_button = tk.Button(top_panel, text="Select Image", command=self.select_image)
        select_button.pack(side=tk.LEFT)
        
        # Center panel with image
        self.image_panel = tk.Label(self.root, text="No image selected", bg="lightgray")
        self.image_panel.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
        
        # Bottom panel with results
        self.result_area = scrolledtext.ScrolledText(self.root, height=5, wrap=tk.WORD)
        self.result_area.pack(fill=tk.X, padx=10, pady=10)
    
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