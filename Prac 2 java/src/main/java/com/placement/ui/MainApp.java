package com.placement.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainApp extends JFrame {
    private JTabbedPane tabbedPane;
    private DashboardPanel dashboardPanel;
    private TrendChartPanel trendChartPanel;
    private PredictionPanel predictionPanel;
    
    public MainApp() {
        setTitle("Placement Trend Prediction and Analytics");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Remove icon loading that's causing the NullPointerException
        // We'll use text-only tabs instead of icons
        
        initComponents();
        layoutComponents();
    }
    
    private void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        dashboardPanel = new DashboardPanel();
        trendChartPanel = new TrendChartPanel();
        predictionPanel = new PredictionPanel();
    }
    
    private void layoutComponents() {
        // Use text-only tabs instead of icons
        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("Placement Trends", trendChartPanel);
        tabbedPane.addTab("Predictions", predictionPanel);
        
        // Create a main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(245, 245, 250));
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Customize UI components
            UIManager.put("TabbedPane.selected", new Color(66, 134, 244, 30));
            UIManager.put("TabbedPane.borderHightlightColor", new Color(66, 134, 244));
            UIManager.put("TabbedPane.contentAreaColor", Color.WHITE);
            UIManager.put("TabbedPane.focus", new Color(66, 134, 244));
            UIManager.put("TabbedPane.light", Color.WHITE);
            UIManager.put("TabbedPane.tabAreaBackground", Color.WHITE);
            UIManager.put("TabbedPane.unselectedBackground", Color.WHITE);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.setVisible(true);
        });
    }
}