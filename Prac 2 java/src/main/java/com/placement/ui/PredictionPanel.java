package com.placement.ui;

import com.placement.model.*;
import com.placement.util.PredictionUtil;
import com.placement.util.ChartGenerator;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class PredictionPanel extends JPanel {
    private List<Placement> placements;
    private JComboBox<String> branchComboBox;
    private JComboBox<Integer> yearComboBox;
    private JPanel resultPanel;
    
    public PredictionPanel() {
        setLayout(new BorderLayout());
        initData();
        initComponents();
    }
    
    private void initData() {
        placements = DummyDataGenerator.generateHistoricalData();
    }
    
    private void initComponents() {
        // Create header
        JLabel headerLabel = new JLabel("Placement Prediction");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Branch selection
        JLabel branchLabel = new JLabel("Select Branch:");
        branchLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(branchLabel, gbc);
        
        String[] branches = {"All Branches", "Computer Science", "Information Technology", "Electronics", "Mechanical", "Civil"};
        branchComboBox = new JComboBox<>(branches);
        branchComboBox.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(branchComboBox, gbc);
        
        // Year selection
        JLabel yearLabel = new JLabel("Predict for Year:");
        yearLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(yearLabel, gbc);
        
        Integer[] years = {2024, 2025, 2026};
        yearComboBox = new JComboBox<>(years);
        yearComboBox.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(yearComboBox, gbc);
        
        // Predict button
        JButton predictButton = new JButton("Predict");
        predictButton.setFont(new Font("Arial", Font.BOLD, 14));
        predictButton.setPreferredSize(new Dimension(150, 40));
        predictButton.addActionListener(e -> updatePrediction());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(predictButton, gbc);
        
        // Create result panel
        resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add components to panel
        add(headerLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(resultPanel, BorderLayout.SOUTH);
    }
    
    private void updatePrediction() {
        resultPanel.removeAll();
        
        String selectedBranch = (String) branchComboBox.getSelectedItem();
        int selectedYear = (Integer) yearComboBox.getSelectedItem();
        
        // Get predictions
        Map<String, Object> predictions = PredictionUtil.predictPlacements(placements, selectedBranch, selectedYear);
        
        // Create prediction results panel
        JPanel predictionPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        predictionPanel.setBorder(BorderFactory.createTitledBorder("Prediction Results for " + selectedYear));
        
        // Add prediction cards
        predictionPanel.add(createPredictionCard("Predicted Placement %", 
                String.format("%.1f%%", predictions.get("placementPercentage")), new Color(66, 134, 244)));
        predictionPanel.add(createPredictionCard("Avg Package Prediction", 
                String.format("Rs %.2f LPA", predictions.get("avgPackage")), new Color(76, 175, 80)));
        predictionPanel.add(createPredictionCard("Top Company Sector", 
                (String) predictions.get("topSector"), new Color(255, 152, 0)));
        predictionPanel.add(createPredictionCard("Top Job Role", 
                (String) predictions.get("topRole"), new Color(156, 39, 176)));
        
        // Create prediction chart
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("Predicted vs Historical Trends"));
        chartPanel.add(ChartGenerator.createPredictionChart(placements, predictions, selectedYear), BorderLayout.CENTER);
        
        // Add panels to result panel
        JPanel mainResultPanel = new JPanel(new BorderLayout());
        mainResultPanel.add(predictionPanel, BorderLayout.NORTH);
        mainResultPanel.add(chartPanel, BorderLayout.CENTER);
        
        resultPanel.add(mainResultPanel, BorderLayout.CENTER);
        resultPanel.revalidate();
        resultPanel.repaint();
    }
    
    private JPanel createPredictionCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(color);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
}