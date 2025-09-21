import cv2
import numpy as np
from PIL import Image, ImageOps

class QRCodeReader:
    @staticmethod
    def read_qr_code(image_path):
        """Read QR code from an image file using multiple methods"""
        # Try multiple methods to read the QR code
        try:
            # Method 1: Direct reading with OpenCV QR Code detector
            image = cv2.imread(image_path)
            if image is None:
                print(f"Could not read image from {image_path}")
                return None
                
            result = QRCodeReader._try_opencv_qr_detector(image)
            if result:
                return result
            
            # Method 2: Try with inverted image
            inverted = cv2.bitwise_not(image)
            result = QRCodeReader._try_opencv_qr_detector(inverted)
            if result:
                return result
            
            # Method 3: Try with grayscale image
            gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
            result = QRCodeReader._try_opencv_qr_detector(gray)
            if result:
                return result
            
            # Method 4: Try with grayscale inverted image
            gray_inverted = cv2.bitwise_not(gray)
            result = QRCodeReader._try_opencv_qr_detector(gray_inverted)
            if result:
                return result
            
            # Method 5: Try with different thresholds
            for threshold in range(50, 200, 30):
                _, binary = cv2.threshold(gray, threshold, 255, cv2.THRESH_BINARY)
                result = QRCodeReader._try_opencv_qr_detector(binary)
                if result:
                    return result
                    
            # Method 6: Try with adaptive thresholding
            binary_adaptive = cv2.adaptiveThreshold(gray, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, 
                                                  cv2.THRESH_BINARY, 11, 2)
            result = QRCodeReader._try_opencv_qr_detector(binary_adaptive)
            if result:
                return result
                
            # Method 7: Try with morphological operations
            kernel = np.ones((5,5), np.uint8)
            dilated = cv2.dilate(gray, kernel, iterations=1)
            eroded = cv2.erode(gray, kernel, iterations=1)
            
            result = QRCodeReader._try_opencv_qr_detector(dilated)
            if result:
                return result
                
            result = QRCodeReader._try_opencv_qr_detector(eroded)
            if result:
                return result
            
            return None
        except Exception as e:
            print(f"Error: {e}")
            return None
    
    @staticmethod
    def _try_opencv_qr_detector(image):
        """Try to decode QR code using OpenCV's QR detector"""
        try:
            # Create QR Code detector
            qr_detector = cv2.QRCodeDetector()
            
            # Detect and decode
            retval, decoded_info, points, straight_qrcode = qr_detector.detectAndDecodeMulti(image)
            
            if retval and len(decoded_info) > 0 and decoded_info[0]:
                return {
                    'format': 'QR_CODE',
                    'content': decoded_info[0]
                }
                
            # Try the simpler detect and decode method as fallback
            data, bbox, _ = qr_detector.detectAndDecode(image)
            if data:
                return {
                    'format': 'QR_CODE',
                    'content': data
                }
                
            return None
        except Exception as e:
            print(f"OpenCV QR detection error: {e}")
            return None