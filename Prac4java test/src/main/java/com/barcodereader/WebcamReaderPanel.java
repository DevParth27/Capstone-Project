package com.barcodereader;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Panel for reading barcodes and QR codes from webcam in real-time
 */
public class WebcamReaderPanel extends JPanel {
    private Webcam webcam;
    private WebcamPanel webcamPanel;
    private JTextArea resultArea;
    private JButton startButton;
    private JButton stopButton;
    private JButton captureButton;
    private JButton scanContinuousButton;
    private JButton clearButton;
    private JComboBox<String> webcamCombo;
    private JLabel statusLabel;
    
    private ExecutorService scanExecutor;
    private Future<?> continuousScanTask;
    private volatile boolean continuousScanning = false;
    private volatile boolean webcamRunning = false;
    
    public WebcamReaderPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
        initializeWebcamList();
    }
    
    private void initializeComponents() {
        // Webcam selection
        webcamCombo = new JComboBox<>();
        
        // Status label
        statusLabel = new JLabel("Webcam not started");
        statusLabel.setForeground(Color.RED);
        
        // Result display
        resultArea = new JTextArea(12, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultArea.setBackground(Color.WHITE);
        resultArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Buttons
        startButton = new JButton("Start Webcam");
        stopButton = new JButton("Stop Webcam");
        captureButton = new JButton("Capture & Scan");
        scanContinuousButton = new JButton("Start Continuous Scan");
        clearButton = new JButton("Clear Results");
        
        // Initially disable some buttons
        stopButton.setEnabled(false);
        captureButton.setEnabled(false);
        scanContinuousButton.setEnabled(false);
        
        // Thread pool for scanning operations
        scanExecutor = Executors.newSingleThreadExecutor();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with webcam selection and status
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JPanel webcamSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        webcamSelectionPanel.add(new JLabel("Select Webcam:"));
        webcamSelectionPanel.add(webcamCombo);
        webcamSelectionPanel.add(statusLabel);
        
        topPanel.add(webcamSelectionPanel, BorderLayout.NORTH);
        
        // Webcam display panel (initially empty)
        JPanel webcamDisplayPanel = new JPanel(new BorderLayout());
        webcamDisplayPanel.setBorder(new TitledBorder("Webcam Feed"));
        webcamDisplayPanel.setPreferredSize(new Dimension(640, 480));
        webcamDisplayPanel.add(new JLabel("Start webcam to see feed", JLabel.CENTER), BorderLayout.CENTER);
        
        topPanel.add(webcamDisplayPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(captureButton);
        buttonPanel.add(scanContinuousButton);
        buttonPanel.add(clearButton);
        
        // Result panel
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(new TitledBorder("Scan Results"));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Layout
        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(resultPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startWebcam();
            }
        });
        
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopWebcam();
            }
        });
        
        captureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                captureAndScan();
            }
        });
        
        scanContinuousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleContinuousScanning();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearResults();
            }
        });
    }
    
    private void initializeWebcamList() {
        webcamCombo.removeAllItems();
        webcamCombo.addItem("Detecting webcams...");
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                List<Webcam> webcams = Webcam.getWebcams();
                SwingUtilities.invokeLater(() -> {
                    webcamCombo.removeAllItems();
                    if (webcams.isEmpty()) {
                        webcamCombo.addItem("No webcams found");
                        startButton.setEnabled(false);
                    } else {
                        for (int i = 0; i < webcams.size(); i++) {
                            webcamCombo.addItem("Webcam " + (i + 1) + ": " + webcams.get(i).getName());
                        }
                        startButton.setEnabled(true);
                    }
                });
                return null;
            }
        };
        worker.execute();
    }
    
    private void startWebcam() {
        try {
            int selectedIndex = webcamCombo.getSelectedIndex();
            if (selectedIndex < 0) {
                showError("Please select a webcam");
                return;
            }
            
            List<Webcam> webcams = Webcam.getWebcams();
            if (selectedIndex >= webcams.size()) {
                showError("Selected webcam is not available");
                return;
            }
            
            webcam = webcams.get(selectedIndex);
            webcam.setViewSize(WebcamResolution.VGA.getSize());
            
            webcamPanel = new WebcamPanel(webcam);
            webcamPanel.setFPSDisplayed(true);
            webcamPanel.setDisplayDebugInfo(true);
            webcamPanel.setImageSizeDisplayed(true);
            webcamPanel.setMirrored(true);
            
            // Replace the placeholder with webcam panel
            Container parent = getComponent(0); // topPanel
            JPanel webcamDisplayPanel = (JPanel) ((BorderLayout.LayoutManager) parent.getLayout()).getLayoutComponent(parent, BorderLayout.CENTER);
            webcamDisplayPanel.removeAll();
            webcamDisplayPanel.add(webcamPanel, BorderLayout.CENTER);
            webcamDisplayPanel.revalidate();
            webcamDisplayPanel.repaint();
            
            webcamRunning = true;
            statusLabel.setText("Webcam running");
            statusLabel.setForeground(Color.GREEN);
            
            // Update button states
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            captureButton.setEnabled(true);
            scanContinuousButton.setEnabled(true);
            webcamCombo.setEnabled(false);
            
            resultArea.append("Webcam started successfully.\n");
            resultArea.append("You can now capture images or start continuous scanning.\n\n");
            
        } catch (Exception e) {
            showError("Error starting webcam: " + e.getMessage());
        }
    }
    
    private void stopWebcam() {
        try {
            // Stop continuous scanning if running
            if (continuousScanning) {
                stopContinuousScanning();
            }
            
            if (webcam != null && webcam.isOpen()) {
                webcam.close();
            }
            
            if (webcamPanel != null) {
                // Replace webcam panel with placeholder
                Container parent = getComponent(0); // topPanel
                JPanel webcamDisplayPanel = (JPanel) ((BorderLayout.LayoutManager) parent.getLayout()).getLayoutComponent(parent, BorderLayout.CENTER);
                webcamDisplayPanel.removeAll();
                webcamDisplayPanel.add(new JLabel("Webcam stopped", JLabel.CENTER), BorderLayout.CENTER);
                webcamDisplayPanel.revalidate();
                webcamDisplayPanel.repaint();
            }
            
            webcamRunning = false;
            statusLabel.setText("Webcam stopped");
            statusLabel.setForeground(Color.RED);
            
            // Update button states
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            captureButton.setEnabled(false);
            scanContinuousButton.setEnabled(false);
            webcamCombo.setEnabled(true);
            
            resultArea.append("Webcam stopped.\n\n");
            
        } catch (Exception e) {
            showError("Error stopping webcam: " + e.getMessage());
        }
    }
    
    private void captureAndScan() {
        if (!webcamRunning || webcam == null || !webcam.isOpen()) {
            showError("Webcam is not running");
            return;
        }
        
        scanExecutor.submit(() -> {
            try {
                BufferedImage image = webcam.getImage();
                if (image == null) {
                    SwingUtilities.invokeLater(() -> showError("Failed to capture image from webcam"));
                    return;
                }
                
                SwingUtilities.invokeLater(() -> {
                    resultArea.append("Capturing and scanning image...\n");
                    resultArea.append("=" + "=".repeat(40) + "\n");
                });
                
                QRCodeUtil.DecodedBarcodeResult result = QRCodeUtil.readBarcodeFromImage(image);
                
                SwingUtilities.invokeLater(() -> {
                    if (result.isSuccessful()) {
                        resultArea.append("✓ BARCODE DETECTED!\n");
                        resultArea.append("Format: " + result.getFormat().name() + "\n");
                        resultArea.append("Content: " + result.getText() + "\n");
                        
                        // Show popup for important detections
                        JOptionPane.showMessageDialog(this, 
                            "Barcode Detected!\n\nFormat: " + result.getFormat().name() + 
                            "\nContent: " + result.getText(),
                            "Barcode Found", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        resultArea.append("✗ " + result.getError() + "\n");
                    }
                    resultArea.append("\n");
                    resultArea.setCaretPosition(resultArea.getDocument().getLength());
                });
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> showError("Error during capture and scan: " + e.getMessage()));
            }
        });
    }
    
    private void toggleContinuousScanning() {
        if (continuousScanning) {
            stopContinuousScanning();
        } else {
            startContinuousScanning();
        }
    }
    
    private void startContinuousScanning() {
        if (!webcamRunning || webcam == null || !webcam.isOpen()) {
            showError("Webcam is not running");
            return;
        }
        
        continuousScanning = true;
        scanContinuousButton.setText("Stop Continuous Scan");
        captureButton.setEnabled(false);
        
        resultArea.append("Starting continuous scanning...\n");
        resultArea.append("Scanning will continue until stopped.\n\n");
        
        continuousScanTask = scanExecutor.submit(() -> {
            while (continuousScanning && webcamRunning) {
                try {
                    BufferedImage image = webcam.getImage();
                    if (image != null) {
                        QRCodeUtil.DecodedBarcodeResult result = QRCodeUtil.readBarcodeFromImage(image);
                        
                        if (result.isSuccessful()) {
                            SwingUtilities.invokeLater(() -> {
                                resultArea.append("✓ CONTINUOUS SCAN - BARCODE DETECTED!\n");
                                resultArea.append("Time: " + java.time.LocalTime.now() + "\n");
                                resultArea.append("Format: " + result.getFormat().name() + "\n");
                                resultArea.append("Content: " + result.getText() + "\n");
                                resultArea.append("-".repeat(50) + "\n");
                                resultArea.setCaretPosition(resultArea.getDocument().getLength());
                                
                                // Beep sound for detection
                                Toolkit.getDefaultToolkit().beep();
                            });
                        }
                    }
                    
                    Thread.sleep(1000); // Scan every second
                    
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        resultArea.append("Error during continuous scan: " + e.getMessage() + "\n");
                    });
                }
            }
        });
    }
    
    private void stopContinuousScanning() {
        continuousScanning = false;
        if (continuousScanTask != null) {
            continuousScanTask.cancel(true);
        }
        
        scanContinuousButton.setText("Start Continuous Scan");
        captureButton.setEnabled(true);
        
        resultArea.append("Continuous scanning stopped.\n\n");
        resultArea.setCaretPosition(resultArea.getDocument().getLength());
    }
    
    private void clearResults() {
        resultArea.setText("");
        if (webcamRunning) {
            resultArea.setText("Webcam is running. Ready to scan barcodes/QR codes.\n\n");
        }
    }
    
    private void showError(String message) {
        resultArea.append("ERROR: " + message + "\n\n");
        resultArea.setCaretPosition(resultArea.getDocument().getLength());
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Cleanup method to be called when the panel is disposed
    public void cleanup() {
        if (continuousScanning) {
            stopContinuousScanning();
        }
        
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
        
        if (scanExecutor != null && !scanExecutor.isShutdown()) {
            scanExecutor.shutdown();
        }
    }
}