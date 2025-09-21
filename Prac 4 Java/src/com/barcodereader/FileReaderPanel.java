package com.barcodereader;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public class FileReaderPanel extends JPanel {
    private JButton selectButton;
    private JLabel imageLabel;
    private JTextArea resultArea;
    private BufferedImage currentImage;

    public FileReaderPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
    }

    private void initComponents() {
        // Top panel with button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectButton = new JButton("Select Image");
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectImage();
            }
        });
        topPanel.add(selectButton);
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with image
        imageLabel = new JLabel("No image selected", JLabel.CENTER);
        imageLabel.setPreferredSize(new java.awt.Dimension(400, 300));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(new JScrollPane(imageLabel), BorderLayout.CENTER);
        
        // Bottom panel with results
        resultArea = new JTextArea(5, 40);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        add(new JScrollPane(resultArea), BorderLayout.SOUTH);
    }

    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Image files", "jpg", "jpeg", "png", "gif", "bmp"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                currentImage = ImageIO.read(selectedFile);
                if (currentImage != null) {
                    displayImage(currentImage);
                    decodeImage(currentImage);
                } else {
                    resultArea.setText("Failed to load image.");
                }
            } catch (IOException e) {
                resultArea.setText("Error reading file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void displayImage(BufferedImage image) {
        // Resize image for display if needed
        int maxWidth = 400;
        int maxHeight = 300;
        
        int width = image.getWidth();
        int height = image.getHeight();
        
        if (width > maxWidth || height > maxHeight) {
            double scale = Math.min((double) maxWidth / width, (double) maxHeight / height);
            width = (int) (width * scale);
            height = (int) (height * scale);
        }
        
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImage);
        imageLabel.setText("");
        imageLabel.setIcon(icon);
    }

    private void decodeImage(BufferedImage image) {
        try {
            // First try QR code specific reading
            String qrResult = QRCodeUtil.readQRCode(image);
            if (qrResult != null) {
                resultArea.setText("Format: QR_CODE\nContent: " + qrResult);
                return;
            }
            
            // If QR code reading fails, try general barcode reading
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            
            Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, Arrays.asList(BarcodeFormat.values()));
            
            MultiFormatReader reader = new MultiFormatReader();
            Result result = reader.decode(bitmap, hints);
            
            StringBuilder sb = new StringBuilder();
            sb.append("Format: ").append(result.getBarcodeFormat()).append("\n");
            sb.append("Content: ").append(result.getText()).append("\n");
            
            resultArea.setText(sb.toString());
        } catch (NotFoundException e) {
            resultArea.setText("No barcode or QR code found in the image.");
        } catch (Exception e) {
            resultArea.setText("Error decoding image: " + e.getMessage());
            e.printStackTrace();
        }
    }
}