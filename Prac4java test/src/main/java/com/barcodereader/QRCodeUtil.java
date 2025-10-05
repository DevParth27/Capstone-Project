package com.barcodereader;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for reading various types of barcodes and QR codes
 * Supports reading from files and BufferedImage objects
 */
public class QRCodeUtil {
    
    private static final MultiFormatReader reader = new MultiFormatReader();
    private static final GenericMultipleBarcodeReader multiReader = new GenericMultipleBarcodeReader(reader);
    
    static {
        // Configure decode hints for better recognition
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.values());
        reader.setHints(hints);
    }
    
    /**
     * Read barcode/QR code from an image file
     * @param imageFile The image file to read from
     * @return DecodedBarcodeResult containing the decoded text and format
     */
    public static DecodedBarcodeResult readBarcodeFromFile(File imageFile) {
        try {
            BufferedImage image = ImageIO.read(imageFile);
            if (image == null) {
                return new DecodedBarcodeResult(null, null, "Unable to read image file: " + imageFile.getName());
            }
            return readBarcodeFromImage(image);
        } catch (IOException e) {
            return new DecodedBarcodeResult(null, null, "Error reading file: " + e.getMessage());
        }
    }
    
    /**
     * Read barcode/QR code from a BufferedImage
     * @param image The BufferedImage to read from
     * @return DecodedBarcodeResult containing the decoded text and format
     */
    public static DecodedBarcodeResult readBarcodeFromImage(BufferedImage image) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            
            Result result = reader.decode(bitmap);
            return new DecodedBarcodeResult(result.getText(), result.getBarcodeFormat(), null);
            
        } catch (NotFoundException e) {
            return new DecodedBarcodeResult(null, null, "No barcode/QR code found in the image");
        } catch (ChecksumException e) {
            return new DecodedBarcodeResult(null, null, "Barcode/QR code checksum error");
        } catch (FormatException e) {
            return new DecodedBarcodeResult(null, null, "Invalid barcode/QR code format");
        } catch (Exception e) {
            return new DecodedBarcodeResult(null, null, "Error decoding: " + e.getMessage());
        }
    }
    
    /**
     * Read multiple barcodes/QR codes from an image file
     * @param imageFile The image file to read from
     * @return List of DecodedBarcodeResult objects
     */
    public static List<DecodedBarcodeResult> readMultipleBarcodesFromFile(File imageFile) {
        try {
            BufferedImage image = ImageIO.read(imageFile);
            if (image == null) {
                List<DecodedBarcodeResult> results = new ArrayList<>();
                results.add(new DecodedBarcodeResult(null, null, "Unable to read image file: " + imageFile.getName()));
                return results;
            }
            return readMultipleBarcodesFromImage(image);
        } catch (IOException e) {
            List<DecodedBarcodeResult> results = new ArrayList<>();
            results.add(new DecodedBarcodeResult(null, null, "Error reading file: " + e.getMessage()));
            return results;
        }
    }
    
    /**
     * Read multiple barcodes/QR codes from a BufferedImage
     * @param image The BufferedImage to read from
     * @return List of DecodedBarcodeResult objects
     */
    public static List<DecodedBarcodeResult> readMultipleBarcodesFromImage(BufferedImage image) {
        List<DecodedBarcodeResult> results = new ArrayList<>();
        
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            
            Result[] multiResults = multiReader.decodeMultiple(bitmap);
            
            if (multiResults != null && multiResults.length > 0) {
                for (Result result : multiResults) {
                    results.add(new DecodedBarcodeResult(result.getText(), result.getBarcodeFormat(), null));
                }
            } else {
                results.add(new DecodedBarcodeResult(null, null, "No barcodes/QR codes found in the image"));
            }
            
        } catch (NotFoundException e) {
            results.add(new DecodedBarcodeResult(null, null, "No barcodes/QR codes found in the image"));
        } catch (Exception e) {
            results.add(new DecodedBarcodeResult(null, null, "Error decoding: " + e.getMessage()));
        }
        
        return results;
    }
    
    /**
     * Get supported barcode formats as a formatted string
     * @return String listing all supported formats
     */
    public static String getSupportedFormats() {
        StringBuilder sb = new StringBuilder("Supported Formats:\n");
        BarcodeFormat[] formats = BarcodeFormat.values();
        for (int i = 0; i < formats.length; i++) {
            sb.append("• ").append(formats[i].name());
            if (i < formats.length - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
    
    /**
     * Data class to hold barcode reading results
     */
    public static class DecodedBarcodeResult {
        private final String text;
        private final BarcodeFormat format;
        private final String error;
        
        public DecodedBarcodeResult(String text, BarcodeFormat format, String error) {
            this.text = text;
            this.format = format;
            this.error = error;
        }
        
        public String getText() {
            return text;
        }
        
        public BarcodeFormat getFormat() {
            return format;
        }
        
        public String getError() {
            return error;
        }
        
        public boolean isSuccessful() {
            return text != null && error == null;
        }
        
        @Override
        public String toString() {
            if (isSuccessful()) {
                return String.format("Format: %s\nContent: %s", format.name(), text);
            } else {
                return "Error: " + error;
            }
        }
    }
}