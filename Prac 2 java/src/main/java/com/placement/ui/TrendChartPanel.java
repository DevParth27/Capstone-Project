package com.placement.ui;

import com.placement.model.*;
import com.placement.util.ChartGenerator;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TrendChartPanel extends JPanel {
    private List<Placement> placements;
    private JComboBox<String> chartTypeComboBox;
    private JPanel chartPanel;
    
    public TrendChartPanel() {
        setLayout(new BorderLayout());
        initData();
        initComponents();
    }
    
    private void initData() {
        placements = DummyDataGenerator.generateHistoricalData();
    }
    
    private void initComponents() {
        // Create header
        JLabel headerLabel = new JLabel("Placement Trends Analysis");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Create control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel chartTypeLabel = new JLabel("Select Chart Type: ");
        chartTypeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        String[] chartTypes = {"Package Trends by Year", "Company Recruitment Trends", "Branch-wise Placement Trends", "Job Role Distribution"};
        chartTypeComboBox = new JComboBox<>(chartTypes);
        chartTypeComboBox.setPreferredSize(new Dimension(250, 30));
        chartTypeComboBox.addActionListener(e -> updateChart());
        
        controlPanel.add(chartTypeLabel);
        controlPanel.add(chartTypeComboBox);
        
        // Create chart panel
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        updateChart(); // Initialize with default chart
        
        // Add components to panel
        add(headerLabel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.CENTER);
        add(chartPanel, BorderLayout.SOUTH);
    }
    
    private void updateChart() {
        chartPanel.removeAll();
        
        String selectedChartType = (String) chartTypeComboBox.getSelectedItem();
        JPanel chart = null;
        
        switch (selectedChartType) {
            case "Package Trends by Year":
                chart = ChartGenerator.createPackageTrendChart(placements);
                break;
            case "Company Recruitment Trends":
                chart = ChartGenerator.createCompanyTrendChart(placements);
                break;
            case "Branch-wise Placement Trends":
                chart = ChartGenerator.createBranchTrendChart(placements);
                break;
            case "Job Role Distribution":
                chart = ChartGenerator.createJobRoleChart(placements);
                break;
            default:
                chart = ChartGenerator.createPackageTrendChart(placements);
        }
        
        chartPanel.add(chart, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }
}