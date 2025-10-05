package com.barcodereader;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.EnumMap;
import java.util.Map;

/**
 * Simple Barcode and QR Code Scanner
 * Focused on scanning barcodes/QR codes from image files
 */
public class SimpleBarcodeReader extends JFrame {
    private JTextArea resultArea;
    private JLabel imageLabel;
    private BufferedImage currentImage;
    private File selectedFile;
    
    private final MultiFormatReader multiFormatReader;
    private final GenericMultipleBarcodeReader multiReader;
    
    public SimpleBarcodeReader() {
        // Initialize ZXing components
        multiFormatReader = new MultiFormatReader();
        multiReader = new GenericMultipleBarcodeReader(multiFormatReader);
        
        initializeComponents();
        setupLayout();
        setFrameProperties();
    }
    
    private void initializeComponents() {
        // Set up the main frame layout first
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Image display area
        imageLabel = new JLabel("No image selected - Click 'Select Image' to load a barcode/QR code");
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setBorder(BorderFactory.createEtchedBorder());
        imageLabel.setPreferredSize(new java.awt.Dimension(500, 350));
        imageLabel.setBackground(Color.WHITE);
        imageLabel.setOpaque(true);
        
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(new TitledBorder("📷 Image Preview"));
        imagePanel.add(new JScrollPane(imageLabel), BorderLayout.CENTER);
        
        // Button panel with better visibility
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("🎛️ Controls"));
        buttonPanel.setPreferredSize(new java.awt.Dimension(0, 80));
        
        JButton selectButton = new JButton("📂 Select Image");
        JButton scanButton = new JButton("🔍 Scan Barcode/QR");
        JButton scanMultipleButton = new JButton("📊 Scan Multiple");
        JButton clearButton = new JButton("🗑️ Clear Results");
        
        // Make buttons more prominent and visible
        selectButton.setPreferredSize(new java.awt.Dimension(160, 40));
        scanButton.setPreferredSize(new java.awt.Dimension(160, 40));
        scanMultipleButton.setPreferredSize(new java.awt.Dimension(160, 40));
        clearButton.setPreferredSize(new java.awt.Dimension(160, 40));
        
        // Add some styling
        selectButton.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        scanButton.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        scanMultipleButton.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        clearButton.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        
        selectButton.addActionListener(e -> {
            System.out.println("Select button action triggered!");
            selectImageFile();
        });
        scanButton.addActionListener(e -> {
            System.out.println("Scan button action triggered!");
            scanBarcodes(false);
        });
        scanMultipleButton.addActionListener(e -> {
            System.out.println("Scan multiple button action triggered!");
            scanBarcodes(true);
        });
        clearButton.addActionListener(e -> {
            System.out.println("Clear button action triggered!");
            clearResults();
        });
        
        buttonPanel.add(selectButton);
        buttonPanel.add(scanButton);
        buttonPanel.add(scanMultipleButton);
        buttonPanel.add(clearButton);
        
        // Result area
        resultArea = new JTextArea(20, 80);
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultArea.setBackground(Color.WHITE);
        resultArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(new TitledBorder("📋 Scan Results"));
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        
        // Initialize with welcome message
        resultArea.setText("🎯 BARCODE & QR CODE SCANNER READY!\n");
        resultArea.append("=====================================\n\n");
        resultArea.append("✅ ZXing Library Successfully Loaded!\n");
        resultArea.append("✅ Real barcode decoding enabled\n");
        resultArea.append("✅ Multiple format support\n\n");
        resultArea.append("SUPPORTED FORMATS:\n");
        resultArea.append("• QR Code, Data Matrix, Aztec\n");
        resultArea.append("• Code 128, Code 39, Code 93\n");
        resultArea.append("• EAN-8, EAN-13, UPC-A, UPC-E\n");
        resultArea.append("• PDF417, Codabar, ITF\n");
        resultArea.append("• And many more!\n\n");
        resultArea.append("HOW TO USE:\n");
        resultArea.append("1. Click 'Select Image' to load a barcode/QR image\n");
        resultArea.append("2. Click 'Scan Barcode/QR' to decode the content\n");
        resultArea.append("3. Use 'Scan Multiple' for images with several codes\n\n");
        resultArea.append("Ready to scan! Load an image to begin.\n");
        
        // Layout the main panel with proper button visibility
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(imagePanel, BorderLayout.CENTER);
        topSection.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(topSection, BorderLayout.NORTH);
        mainPanel.add(resultPanel, BorderLayout.CENTER);
        
        // Add main panel to frame
        add(mainPanel, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("✅ Scanner Ready - ZXing v3.5.1 Library Active");
        statusLabel.setForeground(Color.GREEN.darker());
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.PAGE_END);
    }
    
    private void selectImageFile() {
        System.out.println("Select image button clicked!"); // Debug
        
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Barcode/QR Code Image");
            
            FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
                "Image Files (*.jpg, *.jpeg, *.png, *.bmp, *.gif)", 
                "jpg", "jpeg", "png", "bmp", "gif"
            );
            fileChooser.setFileFilter(imageFilter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            
            // Set default directory to user home or desktop
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            
            System.out.println("Opening file chooser dialog..."); // Debug
            int result = fileChooser.showOpenDialog(this);
            System.out.println("File chooser result: " + result); // Debug
            
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                System.out.println("Selected file: " + selectedFile.getAbsolutePath()); // Debug
                loadAndDisplayImage();
            } else {
                System.out.println("File selection cancelled or failed"); // Debug
                resultArea.append("File selection cancelled.\n");
            }
        } catch (Exception e) {
            System.err.println("Error in file selection: " + e.getMessage());
            e.printStackTrace();
            showError("Error opening file chooser: " + e.getMessage());
        }
    }
    
    private void loadAndDisplayImage() {
        try {
            currentImage = ImageIO.read(selectedFile);
            if (currentImage == null) {
                showError("Unable to load image file: " + selectedFile.getName());
                return;
            }
            
            // Scale and display image
            ImageIcon imageIcon = new ImageIcon(scaleImage(currentImage, 500, 350));
            imageLabel.setIcon(imageIcon);
            imageLabel.setText("");
            
            // Update result area
            resultArea.setText("📂 IMAGE LOADED SUCCESSFULLY!\n");
            resultArea.append("==============================\n");
            resultArea.append("File: " + selectedFile.getName() + "\n");
            resultArea.append("Dimensions: " + currentImage.getWidth() + " x " + currentImage.getHeight() + " pixels\n");
            resultArea.append("File Size: " + String.format("%.1f KB", selectedFile.length() / 1024.0) + "\n");
            resultArea.append("Format: " + getImageFormat(selectedFile) + "\n\n");
            resultArea.append("✅ Ready for barcode scanning!\n");
            resultArea.append("Click 'Scan Barcode/QR' to decode the content.\n\n");
            
        } catch (Exception e) {
            showError("Error loading image: " + e.getMessage());
        }
    }
    
    private String getImageFormat(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "JPEG";
        if (name.endsWith(".png")) return "PNG";
        if (name.endsWith(".bmp")) return "BMP";
        if (name.endsWith(".gif")) return "GIF";
        return "Unknown";
    }
    
    private void scanBarcodes(boolean multiple) {
        if (currentImage == null) {
            showError("Please select an image first.");
            return;
        }
        
        try {
            resultArea.append("\n🔍 SCANNING WITH ZXING LIBRARY...\n");
            resultArea.append("==================================\n");
            
            // Create LuminanceSource from BufferedImage
            LuminanceSource source = new BufferedImageLuminanceSource(currentImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            
            if (multiple) {
                scanMultipleCodes(bitmap);
            } else {
                scanSingleCode(bitmap);
            }
            
        } catch (Exception e) {
            resultArea.append("❌ Error during scan: " + e.getMessage() + "\n");
            resultArea.append("This might happen if the image doesn't contain clear barcodes.\n\n");
        }
        
        resultArea.setCaretPosition(resultArea.getDocument().getLength());
    }
    
    private void scanSingleCode(BinaryBitmap bitmap) {
        try {
            Result result = multiFormatReader.decode(bitmap);
            
            resultArea.append("🎉 BARCODE DECODED SUCCESSFULLY!\n");
            resultArea.append("Format: " + result.getBarcodeFormat() + "\n");
            resultArea.append("Content: " + result.getText() + "\n");
            
            // Show additional metadata if available
            if (result.getResultMetadata() != null && !result.getResultMetadata().isEmpty()) {
                resultArea.append("Metadata: " + result.getResultMetadata() + "\n");
            }
            
            resultArea.append("\n");
            
            // Show popup for successful decode
            JOptionPane.showMessageDialog(this, 
                "✅ Barcode Successfully Decoded!\n\n" +
                "Format: " + result.getBarcodeFormat() + "\n" +
                "Content: " + result.getText(),
                "Decode Success!", JOptionPane.INFORMATION_MESSAGE);
                
        } catch (NotFoundException e) {
            resultArea.append("❌ No barcode found in the image.\n");
            resultArea.append("Tips:\n");
            resultArea.append("• Ensure the barcode is clearly visible\n");
            resultArea.append("• Try a higher resolution image\n");
            resultArea.append("• Check that the image is not blurry or distorted\n\n");
        } catch (Exception e) {
            resultArea.append("❌ Error decoding: " + e.getMessage() + "\n\n");
        }
    }
    
    private void scanMultipleCodes(BinaryBitmap bitmap) {
        try {
            Result[] results = multiReader.decodeMultiple(bitmap);
            
            if (results != null && results.length > 0) {
                resultArea.append("🎉 MULTIPLE BARCODES FOUND: " + results.length + " codes\n");
                resultArea.append("=" + "=".repeat(50) + "\n");
                
                for (int i = 0; i < results.length; i++) {
                    Result result = results[i];
                    resultArea.append("Barcode #" + (i + 1) + ":\n");
                    resultArea.append("  Format: " + result.getBarcodeFormat() + "\n");
                    resultArea.append("  Content: " + result.getText() + "\n\n");
                }
                
                JOptionPane.showMessageDialog(this,
                    "🎉 Found " + results.length + " barcode(s) in the image!\n" +
                    "Check the results panel for all decoded content.",
                    "Multiple Codes Found!", JOptionPane.INFORMATION_MESSAGE);
                    
            } else {
                resultArea.append("❌ No barcodes found in the image.\n");
                resultArea.append("Try the single scan mode or a different image.\n\n");
            }
            
        } catch (NotFoundException e) {
            resultArea.append("❌ No multiple barcodes found. Trying single scan...\n");
            scanSingleCode(bitmap);
        } catch (Exception e) {
            resultArea.append("❌ Error in multiple scan: " + e.getMessage() + "\n");
            resultArea.append("Falling back to single scan...\n");
            scanSingleCode(bitmap);
        }
    }
    
    private void clearResults() {
        resultArea.setText("🗑️ Results cleared. Ready for new operations.\n\n");
        resultArea.append("Select an image and scan for barcodes/QR codes.\n");
    }
    
    private BufferedImage scaleImage(BufferedImage original, int maxWidth, int maxHeight) {
        int width = original.getWidth();
        int height = original.getHeight();
        
        double scaleX = (double) maxWidth / width;
        double scaleY = (double) maxHeight / height;
        double scale = Math.min(scaleX, scaleY);
        
        if (scale >= 1.0) {
            return original;
        }
        
        int scaledWidth = (int) (width * scale);
        int scaledHeight = (int) (height * scale);
        
        BufferedImage scaled = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = scaled.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(original, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();
        
        return scaled;
    }
    
    private void setupLayout() {
        // Layout is already set up in initializeComponents
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
    
    private void setFrameProperties() {
        setTitle("Barcode & QR Scanner - Simple File Reader");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new java.awt.Dimension(800, 600));
    }
    
    private void showError(String message) {
        if (resultArea != null) {
            resultArea.append("❌ ERROR: " + message + "\n\n");
            resultArea.setCaretPosition(resultArea.getDocument().getLength());
        }
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                SimpleBarcodeReader app = new SimpleBarcodeReader();
                app.setVisible(true);
                
                // Show welcome message
                JOptionPane.showMessageDialog(app,
                    "🎯 BARCODE & QR SCANNER READY!\n\n" +
                    "✅ ZXing library loaded successfully\n" +
                    "✅ Real barcode/QR decoding enabled\n" +
                    "✅ Multiple format support active\n\n" +
                    "READY TO SCAN:\n" +
                    "• Load barcode/QR images from files\n" +
                    "• Decode content from various formats\n" +
                    "• Scan multiple codes in one image\n\n" +
                    "Click 'Select Image' to begin scanning!",
                    "Scanner Ready!", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error starting scanner: " + e.getMessage(),
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}