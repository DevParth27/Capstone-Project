# Instagram Filters with NLP - Python Project

A Python application that applies Instagram-style photo filters with Natural Language Processing (NLP) capabilities. Use natural language to describe your desired filter, and the application will apply it automatically!

## Features

### 🎨 Multiple Filter Types
- **Vintage & Retro**: Sepia, vintage, nostalgic filters
- **Tone Adjustments**: Cool tones, warm tones, blue tint
- **Artistic Effects**: Blur, sharpen, emboss
- **Color Effects**: Grayscale, pastel, high contrast
- **Manual Adjustments**: Brightness, contrast, saturation control

### 🤖 NLP-Powered
Describe your desired filter in natural language:
- "make it vintage and old"
- "apply cool blue tones"
- "make it black and white"
- "give it a warm sunset look"
- "add high contrast"
- "apply pastel colors"

### 💻 Easy-to-Use GUI
- Load any image file
- Apply filters with one click
- Use natural language to describe filters
- Save your filtered images
- Reset to original anytime

## Installation

### Prerequisites
- Python 3.8 or higher

### Setup

1. Install required packages:
```bash
pip install -r requirements.txt
```

## Usage

### GUI Application

Run the GUI application:
```bash
python instagram_filters_gui.py
```

#### GUI Features:
1. **Load Image**: Click "Load Image" to select your photo
2. **NLP Filters**: Enter natural language descriptions in the text box
3. **Manual Filters**: Click filter buttons for direct application
4. **Save**: Save your filtered images
5. **Reset**: Return to original image

### Programmatic Usage

#### Basic Example:
```python
from instagram_filters import InstagramFilterApp
from PIL import Image

# Create app instance
app = InstagramFilterApp()

# Load image
app.load_image("input.jpg")

# Apply filter by name
app.apply_filter_by_name("vintage")

# Save result
app.current_image.save("output_vintage.jpg")
```

#### NLP Example:
```python
from instagram_filters import InstagramFilterApp

app = InstagramFilterApp()
app.load_image("input.jpg")

# Apply filter using natural language
result = app.apply_filter_by_text("make it vintage with warm tones")
result.save("output_nlp.jpg")
```

#### Custom Filter Combination:
```python
from instagram_filters import FilterProcessor

processor = FilterProcessor()
img = Image.open("input.jpg")

# Apply multiple filters
img = processor.apply_vintage(img)
img = processor.adjust_brightness(img, 1.2)
img = processor.adjust_saturation(img, 0.8)

img.save("output_custom.jpg")
```

### Run Examples

```bash
# Run example usage script
python example_usage.py

# This will demonstrate:
# - Basic filter application
# - NLP command processing
# - Available filters list
# - Custom filter combinations
```

## Available Filters

### Color Filters
- **sepia**: Classic sepia tone
- **vintage**: Retro vintage look
- **grayscale**: Black and white conversion
- **cool_tone**: Cool blue tones
- **warm_tone**: Warm red/yellow tones
- **blue_tint**: Blue color tint
- **pastel**: Soft pastel colors
- **nostalgic**: Nostalgic vintage look

### Effect Filters
- **blur**: Gaussian blur effect
- **sharpen**: Sharpening filter
- **emboss**: Embossed texture effect
- **high_contrast**: High contrast and bold colors

### Adjustments
- **brightness**: Brightness control (factor 0.5-2.0)
- **contrast**: Contrast control (factor 0.5-2.0)
- **saturation**: Color saturation control (factor 0.5-2.0)

## NLP Commands

### Understanding User Intent

The application uses NLP to understand filter requests. Examples:

```
"make it vintage"                    → Applies vintage filter
"apply cool blue tones"              → Applies cool_tone filter
"black and white"                    → Converts to grayscale
"warm sunset look"                   → Applies warm_tone
"give it a retro sepia effect"       → Applies sepia filter
"make it brighter"                   → Increases brightness
"more colorful"                      → Increases saturation
"high contrast"                      → Applies high_contrast filter
```

### NLP Keywords

- **Vintage**: vintage, retro, old school, old-style
- **Sepia**: sepia, old, antique, brown
- **Grayscale**: grayscale, grey, gray, black white, monochrome
- **Cool Tone**: cool, cool tone, blue, icy, frozen, winter
- **Warm Tone**: warm, warm tone, sunset, sunny, orange
- **Blur**: blur, soft, dreamy, foggy
- **Sharpen**: sharp, sharpened, clear, crisp
- **Pastel**: pastel, soft, gentle, light, delicate
- **Contrast**: contrast, bold, vivid, striking

## Project Structure

```
Prac 6 Python/
├── instagram_filters.py      # Core filter processing and NLP
├── instagram_filters_gui.py  # GUI application
├── example_usage.py          # Usage examples
├── requirements.txt          # Python dependencies
└── README.md                  # This file
```

## How It Works

### Filter Processing
1. Load image using PIL (Python Imaging Library)
2. Apply filter algorithm to image array
3. Return processed image

### NLP Processing
1. User enters natural language command
2. NLP module extracts keywords
3. Matches keywords to filter functions
4. Applies detected filters
5. Returns filtered image

### Examples of Filter Algorithms

**Vintage Filter**:
- Adjusts color channels for warm tones
- Applies Gaussian vignette effect

**Sepia Filter**:
- Uses color transformation matrix
- Applies brown tone overlay

**Cool/Warm Tone**:
- Adjusts individual RGB channels
- Increases/decreases specific color components

## Technologies Used

- **OpenCV**: Image processing operations
- **PIL/Pillow**: Image manipulation and I/O
- **NumPy**: Array operations and color transformations
- **Tkinter**: GUI framework
- **Custom NLP**: Pattern matching and intent recognition

## Future Enhancements

- Machine learning-based filter recommendation
- More filter types
- Filter intensity sliders
- Batch processing multiple images
- Custom filter presets
- Integration with social media platforms

## License

This project is for educational purposes.

## Contact

For questions or suggestions, please refer to the project documentation.

## Credits

Developed as part of Capstone Project - Practical 6

---

**Enjoy creating beautiful filtered images with the power of NLP!** 📸✨
