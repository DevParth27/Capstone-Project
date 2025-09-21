package com.barcodereader;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

public class QRCodeUtil {

    public static String readQRCode(BufferedImage image) {
        try {
            // Try multiple methods to read the QR code
            String result = tryDirectQRCodeReading(image);
            if (result != null) return result;
            
            // Try with inverted image
            BufferedImage inverted = invertImage(image);
            result = tryDirectQRCodeReading(inverted);
            if (result != null) return result;
            
            // Try with grayscale image
            BufferedImage grayscale = toGrayscale(image);
            result = tryDirectQRCodeReading(grayscale);
            if (result != null) return result;
            
            // Try with grayscale inverted image
            BufferedImage grayInverted = invertImage(grayscale);
            result = tryDirectQRCodeReading(grayInverted);
            if (result != null) return result;
            
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static String tryDirectQRCodeReading(BufferedImage image) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            
            Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
            
            QRCodeReader reader = new QRCodeReader();
            Result result = reader.decode(bitmap, hints);
            
            return result.getText();
        } catch (Exception e) {
            // Try with MultiFormatReader as fallback
            try {
                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                
                Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
                hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
                hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
                
                MultiFormatReader multiReader = new MultiFormatReader();
                Result result = multiReader.decode(bitmap, hints);
                
                return result.getText();
            } catch (Exception ex) {
                return null;
            }
        }
    }
    
    private static BufferedImage invertImage(BufferedImage image) {
        BufferedImage inverted = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                inverted.setRGB(x, y, ~rgb);
            }
        }
        return inverted;
    }
    
    private static BufferedImage toGrayscale(BufferedImage image) {
        BufferedImage grayscale = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        grayscale.getGraphics().drawImage(image, 0, 0, null);
        return grayscale;
    }
}