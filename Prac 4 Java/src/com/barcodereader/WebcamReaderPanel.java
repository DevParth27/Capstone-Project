package com.barcodereader;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class WebcamReaderPanel extends JPanel implements Runnable, ThreadFactory {
    private Webcam webcam;
    private WebcamPanel webcamPanel;
    private JTextArea resultArea;
    private JButton startButton;
    private JButton stopButton;
    private JComboBox<String> webcamSelector;
    
    private Executor executor = Executors.newSingleThreadExecutor(this);
    private volatile boolean running = false;

    public WebcamReaderPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
    }

    private void initComponents() {
        // Top panel with controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Webcam selector
        webcamSelector = new JComboBox<>();
        for (Webcam cam : Webcam.getWebcams()) {
            webcamSelector.addItem(cam.getName());
        }
        topPanel.add(new JLabel("Select Webcam:"));
        topPanel.add(webcamSelector);
        
        // Control buttons
        startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startCapture();
            }
        });
        
        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopCapture();
            }
        });
        
        topPanel.add(startButton);
        topPanel.add(stopButton);
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel will contain webcam view (added when started)
        
        // Bottom panel with results
        resultArea = new JTextArea(5, 40);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        add(new JScrollPane(resultArea), BorderLayout.SOUTH);
    }

    private void startCapture() {
        if (webcam != null && webcam.isOpen()) {
            return;
        }
        
        int selectedIndex = webcamSelector.getSelectedIndex();
        if (selectedIndex == -1) {
            resultArea.setText("No webcam available");
            return;
        }
        
        webcam = Webcam.getWebcams().get(selectedIndex);
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        
        webcamPanel = new WebcamPanel(webcam, false);
        webcamPanel.setFPSDisplayed(true);
        webcamPanel.setDisplayDebugInfo(true);
        webcamPanel.setImageSizeDisplayed(true);
        webcamPanel.setMirrored(false);
        
        add(webcamPanel, BorderLayout.CENTER);
        
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        webcamSelector.setEnabled(false);
        
        webcam.open();
        webcamPanel.start();
        
        running = true;
        executor.execute(this);
        
        revalidate();
    }

    private void stopCapture() {
        running = false;
        
        if (webcamPanel != null) {
            webcamPanel.stop();
            remove(webcamPanel);
        }
        
        if (webcam != null) {
            webcam.close();
        }
        
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        webcamSelector.setEnabled(true);
        
        resultArea.setText("");
        
        revalidate();
        repaint();
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            if (!webcam.isOpen()) {
                continue;
            }
            
            BufferedImage image = webcam.getImage();
            if (image != null) {
                try {
                    LuminanceSource source = new BufferedImageLuminanceSource(image);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    
                    Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
                    hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
                    hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.values());
                    
                    MultiFormatReader reader = new MultiFormatReader();
                    Result result = reader.decode(bitmap, hints);
                    
                    if (result != null) {
                        final String text = result.getText();
                        final BarcodeFormat format = result.getBarcodeFormat();
                        
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder sb = new StringBuilder();
                                sb.append("Format: ").append(format).append("\n");
                                sb.append("Content: ").append(text).append("\n");
                                resultArea.setText(sb.toString());
                            }
                        });
                        
                        // Pause briefly after successful scan
                        Thread.sleep(2000);
                    }
                } catch (NotFoundException e) {
                    // No barcode found in this frame
                } catch (Exception e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            resultArea.setText("Error: " + e.getMessage());
                        }
                    });
                }
            }
        }
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, "webcam-reader-thread");
        thread.setDaemon(true);
        return thread;
    }
}