"""
Example usage of Instagram Filters with NLP
This script demonstrates how to use the filters programmatically
"""

from instagram_filters import InstagramFilterApp, NLPFilterMatcher, FilterProcessor
from PIL import Image
import os

def example_1_basic_usage():
    """Basic usage example"""
    print("\n" + "="*50)
    print("Example 1: Basic Filter Application")
    print("="*50)
    
    # Create app instance
    app = InstagramFilterApp()
    
    # Load an image (you need to provide your own image path)
    # image_path = "path/to/your/image.jpg"
    # app.load_image(image_path)
    # 
    # # Apply a filter
    # filtered = app.apply_filter_by_name("vintage")
    # filtered.save("output_vintage.jpg")
    # print("Applied vintage filter")
    
    print("Note: Uncomment the code above and provide an image path to test")


def example_2_nlp_commands():
    """NLP command processing example"""
    print("\n" + "="*50)
    print("Example 2: NLP Command Processing")
    print("="*50)
    
    nlp = NLPFilterMatcher()
    
    test_commands = [
        "make it vintage and old",
        "apply cool blue tones to the image",
        "convert to black and white",
        "give it a warm sunset look",
        "make it sepia",
        "add high contrast",
        "apply pastel colors"
    ]
    
    for command in test_commands:
        intent = nlp.understand_intent(command)
        print(f"\nCommand: '{command}'")
        print(f"  Detected filters: {intent['filters']}")
        print(f"  Adjustments: {intent['adjustments']}")


def example_3_all_filters():
    """Show all available filters"""
    print("\n" + "="*50)
    print("Example 3: Available Filters")
    print("="*50)
    
    app = InstagramFilterApp()
    processor = FilterProcessor()
    
    print("Available filter methods:")
    filters = [
        "sepia - Old sepia tone",
        "vintage - Retro vintage look",
        "grayscale - Black and white",
        "cool_tone - Cool blue tones",
        "warm_tone - Warm red/yellow tones",
        "blue_tint - Blue color tint",
        "blur - Gaussian blur effect",
        "sharpen - Sharpening filter",
        "emboss - Embossed texture",
        "nostalgic - Nostalgic vintage look",
        "high_contrast - High contrast and bold",
        "pastel - Soft pastel colors"
    ]
    
    for filter_desc in filters:
        print(f"  - {filter_desc}")
    
    print("\nAdjustment methods:")
    adjustments = [
        "adjust_brightness - Control brightness",
        "adjust_contrast - Control contrast",
        "adjust_saturation - Control color saturation"
    ]
    
    for adj_desc in adjustments:
        print(f"  - {adj_desc}")


def example_4_custom_filter():
    """Create custom filter combination"""
    print("\n" + "="*50)
    print("Example 4: Custom Filter Combination")
    print("="*50)
    
    print("To create a custom filter:")
    print("1. Load your image")
    print("2. Apply multiple filters in sequence")
    print("3. Adjust parameters")
    print("\nExample code:")
    print("""
    from instagram_filters import FilterProcessor
    from PIL import Image
    
    processor = FilterProcessor()
    img = Image.open("your_image.jpg")
    
    # Apply vintage
    img = processor.apply_vintage(img)
    
    # Adjust brightness
    img = processor.adjust_brightness(img, 1.2)
    
    # Adjust saturation
    img = processor.adjust_saturation(img, 0.8)
    
    # Save result
    img.save("custom_filtered.jpg")
    """)


def main():
    print("\n" + "="*50)
    print("Instagram Filters with NLP - Example Usage")
    print("="*50)
    
    # Run examples
    example_1_basic_usage()
    example_2_nlp_commands()
    example_3_all_filters()
    example_4_custom_filter()
    
    print("\n" + "="*50)
    print("For GUI application, run: python instagram_filters_gui.py")
    print("="*50)


if __name__ == "__main__":
    main()
