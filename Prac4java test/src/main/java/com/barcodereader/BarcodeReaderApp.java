package com.barcodereader;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application for the Barcode and QR Code Reader
 * Provides a tabbed interface for file-based and webcam-based scanning
 */
public class BarcodeReaderApp extends JFrame {
    private FileReaderPanel fileReaderPanel;
    private WebcamReaderPanel webcamReaderPanel;
    private JTabbedPane tabbedPane;
    private JTextArea infoArea;
    
    public BarcodeReaderApp() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setFrameProperties();
    }
    
    private void initializeComponents() {
        // Create main panels
        fileReaderPanel = new FileReaderPanel();
        webcamReaderPanel = new WebcamReaderPanel();
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("📁 File Reader", fileReaderPanel);
        tabbedPane.addTab("📷 Webcam Reader", webcamReaderPanel);
        
        // Create info panel
        createInfoPanel();
    }
    
    private void createInfoPanel() {
        infoArea = new JTextArea(8, 60);
        infoArea.setEditable(false);
        infoArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        infoArea.setBackground(getBackground());
        infoArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        StringBuilder info = new StringBuilder();
        info.append("BARCODE AND QR CODE READER APPLICATION\n");
        info.append("=====================================\n\n");
        info.append("FEATURES:\n");
        info.append("• Read barcodes and QR codes from image files (JPG, PNG, BMP, GIF)\n");
        info.append("• Real-time barcode scanning using webcam\n");
        info.append("• Support for multiple barcode formats\n");
        info.append("• Batch processing for multiple codes in single image\n");
        info.append("• Continuous webcam scanning mode\n\n");
        info.append("SUPPORTED FORMATS:\n");
        info.append(QRCodeUtil.getSupportedFormats());
        info.append("\n\nHOW TO USE:\n");
        info.append("1. File Reader Tab: Select an image file and scan for barcodes\n");
        info.append("2. Webcam Reader Tab: Start webcam and capture/scan in real-time\n");
        info.append("3. Use 'Read Multiple' to detect several codes in one image\n");
        info.append("4. Use 'Continuous Scan' for hands-free webcam scanning\n");
        
        infoArea.setText(info.toString());
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Main content area
        add(tabbedPane, BorderLayout.CENTER);
        
        // Info panel at the bottom
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(new TitledBorder("Application Information"));
        infoPanel.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        infoPanel.setPreferredSize(new Dimension(0, 200));
        
        add(infoPanel, BorderLayout.SOUTH);
        
        // Add some padding
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    private void setupEventHandlers() {
        // Handle window closing to cleanup webcam resources
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
        
        // Create menu bar
        createMenuBar();
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        exitItem.addActionListener(e -> exitApplication());
        fileMenu.add(exitItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        JMenuItem instructionsItem = new JMenuItem("Instructions");
        instructionsItem.addActionListener(e -> showInstructionsDialog());
        helpMenu.add(instructionsItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void setFrameProperties() {
        setTitle("Barcode and QR Code Reader v1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Set application icon (if available)
        try {
            // You can add an icon here if you have one
            // setIconImage(ImageIO.read(getClass().getResource("/icon.png")));
        } catch (Exception e) {
            // Icon not available, continue without it
        }
        
        // Set size and center the window
        setSize(1000, 800);
        setLocationRelativeTo(null);
        
        // Set minimum size
        setMinimumSize(new Dimension(800, 600));
    }
    
    private void exitApplication() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit the application?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            // Cleanup webcam resources
            if (webcamReaderPanel != null) {
                webcamReaderPanel.cleanup();
            }
            
            System.exit(0);
        }
    }
    
    private void showAboutDialog() {
        String about = "Barcode and QR Code Reader v1.0\n\n" +
                      "A comprehensive Java application for reading barcodes and QR codes\n" +
                      "from image files and webcam feeds.\n\n" +
                      "Built with:\n" +
                      "• ZXing (Zebra Crossing) library for barcode processing\n" +
                      "• Webcam Capture library for camera access\n" +
                      "• Java Swing for the user interface\n\n" +
                      "Supports all major barcode formats including QR codes, \n" +
                      "Data Matrix, PDF417, Code 128, Code 39, and many more.\n\n" +
                      "Developed for educational and practical use.";
        
        JOptionPane.showMessageDialog(
            this,
            about,
            "About Barcode Reader",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void showInstructionsDialog() {
        String instructions = "HOW TO USE THE BARCODE READER:\n\n" +
                            "FILE READER TAB:\n" +
                            "1. Click 'Select Image File' to choose an image\n" +
                            "2. Use 'Read Single Barcode' for one code\n" +
                            "3. Use 'Read Multiple Barcodes' for several codes\n" +
                            "4. Results will appear in the text area below\n\n" +
                            "WEBCAM READER TAB:\n" +
                            "1. Select your webcam from the dropdown\n" +
                            "2. Click 'Start Webcam' to activate the camera\n" +
                            "3. Use 'Capture & Scan' for single captures\n" +
                            "4. Use 'Start Continuous Scan' for automatic scanning\n" +
                            "5. Remember to stop the webcam when done\n\n" +
                            "TIPS:\n" +
                            "• Ensure good lighting for webcam scanning\n" +
                            "• Hold barcodes steady and at readable distance\n" +
                            "• Try different angles if scanning fails\n" +
                            "• Use high-quality images for file scanning";
        
        JTextArea textArea = new JTextArea(instructions);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Instructions",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            // Use default look and feel if system L&F is not available
        }
        
        // Set some UI properties for better appearance
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Create and show the application
        SwingUtilities.invokeLater(() -> {
            try {
                BarcodeReaderApp app = new BarcodeReaderApp();
                app.setVisible(true);
                
                // Show welcome message
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                        app,
                        "Welcome to the Barcode and QR Code Reader!\n\n" +
                        "Choose the 'File Reader' tab to scan images,\n" +
                        "or the 'Webcam Reader' tab for real-time scanning.\n\n" +
                        "Check the 'Help' menu for detailed instructions.",
                        "Welcome",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                    null,
                    "Error starting application: " + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }
        });
    }
}