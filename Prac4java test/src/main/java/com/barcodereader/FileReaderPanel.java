package com.barcodereader;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * Panel for reading barcodes and QR codes from image files
 */
public class FileReaderPanel extends JPanel {
    private JLabel imageLabel;
    private JTextArea resultArea;
    private JButton selectFileButton;
    private JButton readSingleButton;
    private JButton readMultipleButton;
    private JButton clearButton;
    private File selectedFile;
    private BufferedImage currentImage;
    
    public FileReaderPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        // Image display
        imageLabel = new JLabel("No image selected");
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setBorder(BorderFactory.createEtchedBorder());
        imageLabel.setPreferredSize(new Dimension(400, 300));
        imageLabel.setBackground(Color.WHITE);
        imageLabel.setOpaque(true);
        
        // Result display
        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultArea.setBackground(Color.WHITE);
        resultArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Buttons
        selectFileButton = new JButton("Select Image File");
        readSingleButton = new JButton("Read Single Barcode");
        readMultipleButton = new JButton("Read Multiple Barcodes");
        clearButton = new JButton("Clear Results");
        
        // Initially disable read buttons
        readSingleButton.setEnabled(false);
        readMultipleButton.setEnabled(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with image
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(new TitledBorder("Selected Image"));
        imagePanel.add(new JScrollPane(imageLabel), BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(selectFileButton);
        buttonPanel.add(readSingleButton);
        buttonPanel.add(readMultipleButton);
        buttonPanel.add(clearButton);
        
        // Result panel
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(new TitledBorder("Barcode/QR Code Results"));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Layout
        add(imagePanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(resultPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectImageFile();
            }
        });
        
        readSingleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readSingleBarcode();
            }
        });
        
        readMultipleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readMultipleBarcodes();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearResults();
            }
        });
    }
    
    private void selectImageFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Image File");
        
        // Set file filters
        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
            "Image Files (*.jpg, *.jpeg, *.png, *.bmp, *.gif)", 
            "jpg", "jpeg", "png", "bmp", "gif"
        );
        fileChooser.setFileFilter(imageFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            loadAndDisplayImage();
        }
    }
    
    private void loadAndDisplayImage() {
        try {
            currentImage = ImageIO.read(selectedFile);
            if (currentImage == null) {
                showError("Unable to load image file: " + selectedFile.getName());
                return;
            }
            
            // Scale image to fit in the label
            ImageIcon imageIcon = new ImageIcon(scaleImage(currentImage, 400, 300));
            imageLabel.setIcon(imageIcon);
            imageLabel.setText("");
            
            // Enable read buttons
            readSingleButton.setEnabled(true);
            readMultipleButton.setEnabled(true);
            
            // Update result area
            resultArea.setText("Image loaded: " + selectedFile.getName() + "\n" +
                             "Dimensions: " + currentImage.getWidth() + "x" + currentImage.getHeight() + "\n" +
                             "Click 'Read Single Barcode' or 'Read Multiple Barcodes' to scan for codes.\n\n");
            
        } catch (Exception e) {
            showError("Error loading image: " + e.getMessage());
        }
    }
    
    private BufferedImage scaleImage(BufferedImage original, int maxWidth, int maxHeight) {
        int width = original.getWidth();
        int height = original.getHeight();
        
        // Calculate scaling factor
        double scaleX = (double) maxWidth / width;
        double scaleY = (double) maxHeight / height;
        double scale = Math.min(scaleX, scaleY);
        
        // Only scale down, not up
        if (scale >= 1.0) {
            return original;
        }
        
        int scaledWidth = (int) (width * scale);
        int scaledHeight = (int) (height * scale);
        
        BufferedImage scaled = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = scaled.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(original, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();
        
        return scaled;
    }
    
    private void readSingleBarcode() {
        if (selectedFile == null || currentImage == null) {
            showError("Please select an image file first.");
            return;
        }
        
        resultArea.append("Reading single barcode from: " + selectedFile.getName() + "\n");
        resultArea.append("=" + "=".repeat(50) + "\n");
        
        try {
            QRCodeUtil.DecodedBarcodeResult result = QRCodeUtil.readBarcodeFromImage(currentImage);
            
            if (result.isSuccessful()) {
                resultArea.append("✓ SUCCESS!\n");
                resultArea.append("Format: " + result.getFormat().name() + "\n");
                resultArea.append("Content: " + result.getText() + "\n");
            } else {
                resultArea.append("✗ " + result.getError() + "\n");
            }
            
        } catch (Exception e) {
            showError("Error reading barcode: " + e.getMessage());
        }
        
        resultArea.append("\n");
        resultArea.setCaretPosition(resultArea.getDocument().getLength());
    }
    
    private void readMultipleBarcodes() {
        if (selectedFile == null || currentImage == null) {
            showError("Please select an image file first.");
            return;
        }
        
        resultArea.append("Reading multiple barcodes from: " + selectedFile.getName() + "\n");
        resultArea.append("=" + "=".repeat(50) + "\n");
        
        try {
            List<QRCodeUtil.DecodedBarcodeResult> results = QRCodeUtil.readMultipleBarcodesFromImage(currentImage);
            
            if (results.isEmpty()) {
                resultArea.append("No barcodes found.\n");
            } else {
                int successCount = 0;
                for (int i = 0; i < results.size(); i++) {
                    QRCodeUtil.DecodedBarcodeResult result = results.get(i);
                    
                    resultArea.append("Barcode #" + (i + 1) + ":\n");
                    if (result.isSuccessful()) {
                        successCount++;
                        resultArea.append("✓ Format: " + result.getFormat().name() + "\n");
                        resultArea.append("  Content: " + result.getText() + "\n");
                    } else {
                        resultArea.append("✗ " + result.getError() + "\n");
                    }
                    resultArea.append("\n");
                }
                
                resultArea.append("Summary: " + successCount + " of " + results.size() + " barcodes read successfully.\n");
            }
            
        } catch (Exception e) {
            showError("Error reading barcodes: " + e.getMessage());
        }
        
        resultArea.append("\n");
        resultArea.setCaretPosition(resultArea.getDocument().getLength());
    }
    
    private void clearResults() {
        resultArea.setText("");
        if (selectedFile != null) {
            resultArea.setText("Image loaded: " + selectedFile.getName() + "\n" +
                             "Dimensions: " + currentImage.getWidth() + "x" + currentImage.getHeight() + "\n" +
                             "Ready to scan for barcodes/QR codes.\n\n");
        }
    }
    
    private void showError(String message) {
        resultArea.append("ERROR: " + message + "\n\n");
        resultArea.setCaretPosition(resultArea.getDocument().getLength());
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}