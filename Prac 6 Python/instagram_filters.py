import cv2
import numpy as np
from PIL import Image, ImageFilter, ImageEnhance
import re

class FilterProcessor:
    """Image processing for various Instagram-style filters"""
    
    @staticmethod
    def apply_sepia(image):
        """Apply sepia filter"""
        img = np.array(image)
        sepia_filter = np.array([[0.272, 0.534, 0.131],
                                [0.349, 0.686, 0.168],
                                [0.393, 0.769, 0.189]])
        img = cv2.transform(img, sepia_filter)
        img = np.clip(img, 0, 255).astype(np.uint8)
        return Image.fromarray(img)
    
    @staticmethod
    def apply_vintage(image):
        """Apply vintage filter"""
        img = np.array(image)
        # Add warm tones
        img[:, :, 0] = np.clip(img[:, :, 0] * 1.1, 0, 255)
        img[:, :, 2] = np.clip(img[:, :, 2] * 0.9, 0, 255)
        # Add slight vignette
        rows, cols = img.shape[:2]
        X_resultant_kernel = cv2.getGaussianKernel(cols, 200)
        Y_resultant_kernel = cv2.getGaussianKernel(rows, 200)
        kernel = Y_resultant_kernel * X_resultant_kernel.T
        mask = kernel / kernel.max()
        for i in range(3):
            img[:, :, i] = img[:, :, i] * mask
        return Image.fromarray(img.astype(np.uint8))
    
    @staticmethod
    def apply_cool_tone(image):
        """Apply cool tones filter"""
        img = np.array(image)
        img[:, :, 0] = np.clip(img[:, :, 0] * 1.2, 0, 255)  # Increase blue
        img[:, :, 1] = np.clip(img[:, :, 1] * 1.1, 0, 255)  # Increase green
        img[:, :, 2] = np.clip(img[:, :, 2] * 0.9, 0, 255)  # Decrease red
        return Image.fromarray(img.astype(np.uint8))
    
    @staticmethod
    def apply_warm_tone(image):
        """Apply warm tones filter"""
        img = np.array(image)
        img[:, :, 0] = np.clip(img[:, :, 0] * 0.9, 0, 255)  # Decrease blue
        img[:, :, 1] = np.clip(img[:, :, 1] * 1.0, 0, 255)
        img[:, :, 2] = np.clip(img[:, :, 2] * 1.2, 0, 255)  # Increase red
        return Image.fromarray(img.astype(np.uint8))
    
    @staticmethod
    def apply_blue_tint(image):
        """Apply blue tint"""
        img = np.array(image)
        img[:, :, 0] = np.clip(img[:, :, 0] + 30, 0, 255)
        return Image.fromarray(img.astype(np.uint8))
    
    @staticmethod
    def apply_grayscale(image):
        """Convert to grayscale"""
        return image.convert('L').convert('RGB')
    
    @staticmethod
    def apply_blur(image, intensity=2):
        """Apply blur filter"""
        return image.filter(ImageFilter.GaussianBlur(radius=intensity))
    
    @staticmethod
    def apply_sharpen(image):
        """Apply sharpening"""
        return image.filter(ImageFilter.SHARPEN)
    
    @staticmethod
    def apply_emboss(image):
        """Apply emboss effect"""
        return image.filter(ImageFilter.EMBOSS)
    
    @staticmethod
    def adjust_brightness(image, factor):
        """Adjust brightness"""
        enhancer = ImageEnhance.Brightness(image)
        return enhancer.enhance(factor)
    
    @staticmethod
    def adjust_contrast(image, factor):
        """Adjust contrast"""
        enhancer = ImageEnhance.Contrast(image)
        return enhancer.enhance(factor)
    
    @staticmethod
    def adjust_saturation(image, factor):
        """Adjust saturation"""
        enhancer = ImageEnhance.Color(image)
        return enhancer.enhance(factor)
    
    @staticmethod
    def apply_nostalgic(image):
        """Apply nostalgic vintage look"""
        img = image.copy()
        # Apply vintage
        img = FilterProcessor.apply_vintage(img)
        # Reduce saturation
        img = FilterProcessor.adjust_saturation(img, 0.7)
        return img
    
    @staticmethod
    def apply_high_contrast(image):
        """Apply high contrast"""
        img = FilterProcessor.adjust_contrast(image, 1.5)
        img = FilterProcessor.adjust_saturation(img, 1.2)
        return img
    
    @staticmethod
    def apply_pastel(image):
        """Apply pastel filter"""
        img = np.array(image)
        # Soft colors
        img = img.astype(np.float32)
        img = (img + 255) / 2
        img = np.clip(img, 0, 255).astype(np.uint8)
        img = Image.fromarray(img)
        img = FilterProcessor.adjust_saturation(img, 0.6)
        return img


class NLPFilterMatcher:
    """Natural Language Processing to match text to filters"""
    
    def __init__(self):
        # Define filter keywords
        self.filter_keywords = {
            'sepia': ['sepia', 'old', 'antique', 'brown', 'classic'],
            'vintage': ['vintage', 'retro', 'oldschool', 'old school', 'old-style'],
            'grayscale': ['grayscale', 'grey', 'gray', 'black white', 'monochrome', 'bw'],
            'cool_tone': ['cool', 'cool tone', 'blue', 'icy', 'frozen', 'winter'],
            'warm_tone': ['warm', 'warm tone', 'sunset', 'sunny', 'orange', 'golden'],
            'blur': ['blur', 'soft', 'dreamy', 'foggy'],
            'sharpen': ['sharp', 'sharpened', 'clear', 'crisp', 'focused'],
            'emboss': ['emboss', 'textured', '3d', 'relief'],
            'blue_tint': ['blue', 'cyan', 'ocean'],
            'nostalgic': ['nostalgic', 'memory', 'remember', 'yesteryear'],
            'high_contrast': ['contrast', 'bold', 'vivid', 'striking'],
            'pastel': ['pastel', 'soft', 'gentle', 'light', 'delicate'],
            'brightness': ['bright', 'brighter', 'light', 'lighter', 'shine'],
            'saturation': ['saturated', 'colorful', 'vibrant', 'rich colors']
        }
    
    def extract_filters(self, text):
        """Extract filter names from natural language text"""
        text = text.lower()
        detected_filters = []
        
        for filter_name, keywords in self.filter_keywords.items():
            for keyword in keywords:
                if keyword in text:
                    detected_filters.append(filter_name)
                    break
        
        return list(set(detected_filters))
    
    def extract_adjustments(self, text):
        """Extract brightness, contrast, saturation adjustments"""
        text = text.lower()
        adjustments = {}
        
        # Brightness adjustments
        if 'brighter' in text or 'lighter' in text or 'brighten' in text:
            adjustments['brightness'] = 1.3
        elif 'darker' in text or 'darken' in text:
            adjustments['brightness'] = 0.7
        
        # Contrast adjustments
        if 'more contrast' in text or 'higher contrast' in text:
            adjustments['contrast'] = 1.5
        elif 'less contrast' in text:
            adjustments['contrast'] = 0.8
        
        # Saturation adjustments
        if 'more colorful' in text or 'vibrant' in text:
            adjustments['saturation'] = 1.5
        elif 'less colorful' in text or 'desaturated' in text:
            adjustments['saturation'] = 0.5
        
        return adjustments
    
    def understand_intent(self, text):
        """Understand user intent from text and return filter to apply"""
        filters = self.extract_filters(text)
        adjustments = self.extract_adjustments(text)
        
        return {
            'filters': filters,
            'adjustments': adjustments
        }


class InstagramFilterApp:
    """Main application with GUI for Instagram filters"""
    
    def __init__(self):
        self.processor = FilterProcessor()
        self.nlp = NLPFilterMatcher()
        self.original_image = None
        self.current_image = None
    
    def load_image(self, image_path):
        """Load image from path"""
        self.original_image = Image.open(image_path)
        self.current_image = self.original_image.copy()
        return self.current_image
    
    def apply_filter_by_text(self, text):
        """Apply filter based on natural language text"""
        intent = self.nlp.understand_intent(text)
        
        # Apply filters
        result_image = self.original_image.copy()
        
        for filter_name in intent['filters']:
            method = getattr(self.processor, f'apply_{filter_name}', None)
            if method:
                result_image = method(result_image)
        
        # Apply adjustments
        for adjustment, value in intent['adjustments'].items():
            method = getattr(self.processor, f'adjust_{adjustment}', None)
            if method:
                result_image = method(result_image, value)
        
        return result_image
    
    def apply_filter_by_name(self, filter_name):
        """Apply filter by direct name"""
        method = getattr(self.processor, f'apply_{filter_name}', None)
        if method and self.original_image:
            self.current_image = method(self.original_image.copy())
            return self.current_image
        return None
    
    def reset_image(self):
        """Reset to original image"""
        if self.original_image:
            self.current_image = self.original_image.copy()
            return self.current_image
        return None


if __name__ == "__main__":
    print("Instagram Filters with NLP - Python Application")
    print("=" * 50)
    print("\nAvailable filters:")
    print("- sepia, vintage, grayscale, cool_tone, warm_tone")
    print("- blur, sharpen, emboss, blue_tint")
    print("- nostalgic, high_contrast, pastel")
    print("\nExample NLP commands:")
    print("- 'make it vintage'")
    print("- 'apply cool blue tones'")
    print("- 'give it a warm sunset look'")
    print("- 'make it black and white'")
    print("- 'apply retro sepia filter'")
    print("\nUse instagram_filters_gui.py for GUI interface")
