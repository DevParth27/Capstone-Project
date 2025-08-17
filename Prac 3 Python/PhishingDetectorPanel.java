package com.phishing.ui;

import com.phishing.model.PhishingDetector;
import com.phishing.util.DatasetHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class PhishingDetectorPanel extends JPanel {
    private JTextField urlField;
    private JButton checkButton;
    private JTextArea resultArea;
    private JTextArea datasetInfoArea;
    
    private PhishingDetector detector;
    private DatasetHandler datasetHandler;
    
    public PhishingDetectorPanel() {
        detector = new PhishingDetector();
        datasetHandler = new DatasetHandler();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Phishing Website Detector");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        
        // URL input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Enter URL to Check"));
        
        urlField = new JTextField();
        urlField.setFont(new Font("Arial", Font.PLAIN, 14));
        checkButton = new JButton("Check URL");
        
        inputPanel.add(urlField, BorderLayout.CENTER);
        inputPanel.add(checkButton, BorderLayout.EAST);
        
        // Result panel
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Detection Result"));
        
        resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        
        resultPanel.add(resultScroll, BorderLayout.CENTER);
        
        // Dataset info panel
        JPanel datasetPanel = new JPanel(new BorderLayout());
        datasetPanel.setBorder(BorderFactory.createTitledBorder("Dataset Information"));
        
        datasetInfoArea = new JTextArea(5, 30);
        datasetInfoArea.setEditable(false);
        datasetInfoArea.setFont(new Font("Arial", Font.PLAIN, 14));
        datasetInfoArea.setText(datasetHandler.getDatasetInfo());
        JScrollPane datasetScroll = new JScrollPane(datasetInfoArea);
        
        datasetPanel.add(datasetScroll, BorderLayout.CENTER);
        
        // Add panels to content panel
        JPanel topContent = new JPanel(new BorderLayout(10, 10));
        topContent.add(inputPanel, BorderLayout.NORTH);
        topContent.add(resultPanel, BorderLayout.CENTER);
        
        contentPanel.add(topContent, BorderLayout.CENTER);
        contentPanel.add(datasetPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Add action listener to check button
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url = urlField.getText().trim();
                if (!url.isEmpty()) {
                    checkUrl(url);
                } else {
                    resultArea.setText("Please enter a URL to check.");
                }
            }
        });
    }
    
    /**
     * Checks if a URL is phishing and displays the result
     * @param url The URL to check
     */
    private void checkUrl(String url) {
        Map<String, Object> result = detector.predict(url);
        
        String prediction = (String) result.get("prediction");
        double confidence = (double) result.get("confidence");
        
        StringBuilder sb = new StringBuilder();
        sb.append("URL: ").append(url).append("\n");
        sb.append("Prediction: ").append(prediction).append("\n");
        sb.append("Confidence: ").append(String.format("%.2f", confidence)).append("\n");
        
        // Add color coding based on prediction
        if (prediction.equals("Phishing")) {
            resultArea.setForeground(Color.RED);
        } else {
            resultArea.setForeground(new Color(0, 128, 0)); // Dark green
        }
        
        resultArea.setText(sb.toString());
    }
}