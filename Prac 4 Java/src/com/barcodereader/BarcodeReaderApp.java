package com.barcodereader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class BarcodeReaderApp extends JFrame {
    private JTabbedPane tabbedPane;
    private FileReaderPanel fileReaderPanel;
    private WebcamReaderPanel webcamReaderPanel;

    public BarcodeReaderApp() {
        setTitle("Barcode & QR Code Reader");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        
        setVisible(true);
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        
        // Create panels
        fileReaderPanel = new FileReaderPanel();
        webcamReaderPanel = new WebcamReaderPanel();
        
        // Add panels to tabbed pane
        tabbedPane.addTab("File Reader", fileReaderPanel);
        tabbedPane.addTab("Webcam Reader", webcamReaderPanel);
        
        // Add tabbed pane to frame
        add(tabbedPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new BarcodeReaderApp();
        });
    }
}