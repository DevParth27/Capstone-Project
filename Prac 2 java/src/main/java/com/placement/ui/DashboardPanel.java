package com.placement.ui;

import com.placement.model.*;
import com.placement.model.CSVDataLoader;
import com.placement.util.ChartGenerator;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardPanel extends JPanel {
    private List<Placement> placements;
    private JPanel statsPanel;
    private JPanel chartsPanel;
    
    // Modern color palette
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);      // Blue-500
    private static final Color SECONDARY_COLOR = new Color(99, 102, 241);    // Indigo-500
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);       // Green-500
    private static final Color WARNING_COLOR = new Color(249, 115, 22);      // Orange-500
    private static final Color PURPLE_COLOR = new Color(147, 51, 234);       // Purple-500
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252);  // Slate-50
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);         // Slate-900
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);    // Slate-500
    private static final Color BORDER_COLOR = new Color(226, 232, 240);      // Slate-200
    
    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        initData();
        initComponents();
    }
    
    private void initData() {
        placements = CSVDataLoader.loadPlacementData("d:\\normie\\Capstone Project\\Prac 2 java\\dataset\\Placement_Data_Full_Class.csv");
    }
    
    private void initComponents() {
        // Clean header without gradient
        JPanel headerPanel = createHeaderPanel();
        
        // Main content with proper spacing
        JPanel contentPanel = createContentPanel();
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(24, 32, 24, 32)
        ));
        
        JLabel headerLabel = new JLabel("Placement Dashboard");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        headerLabel.setForeground(TEXT_PRIMARY);
        
        JLabel subtitleLabel = new JLabel("Analytics and insights for student placements");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(headerLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        
        // Stats panel with proper spacing
        statsPanel = createStatsPanel();
        JPanel statsContainer = new JPanel(new BorderLayout());
        statsContainer.setOpaque(false);
        statsContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 32, 0));
        statsContainer.add(statsPanel, BorderLayout.CENTER);
        
        // Charts panel
        chartsPanel = createChartsPanel();
        
        contentPanel.add(statsContainer, BorderLayout.NORTH);
        contentPanel.add(chartsPanel, BorderLayout.CENTER);
        
        return contentPanel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setOpaque(false);
        
        // Calculate statistics
        double avgPackage = placements.stream()
                .mapToDouble(Placement::getPackageOffered)
                .average()
                .orElse(0.0);
        
        double highestPackage = placements.stream()
                .mapToDouble(Placement::getPackageOffered)
                .max()
                .orElse(0.0);
        
        long totalPlacements = placements.size();
        
        Map<String, Long> placementsByStream = placements.stream()
                .collect(Collectors.groupingBy(p -> p.getStudent().getBranch(), Collectors.counting()));
        
        String topStream = placementsByStream.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");
        
        // Shorten long branch names for better display
        if (topStream.length() > 15) {
            // Common abbreviations for academic streams
            topStream = topStream.replace("Commerce & Management", "Commerce & Mgmt")
                                .replace("Computer Science", "CS")
                                .replace("Information Technology", "IT")
                                .replace("Electronics and Communication", "ECE")
                                .replace("Mechanical Engineering", "Mechanical")
                                .replace("Civil Engineering", "Civil")
                                .replace("Electrical Engineering", "Electrical");
        }
        
        // Create clean stat cards
        panel.add(createCleanStatCard("Average Package", String.format("%.2f LPA", avgPackage), PRIMARY_COLOR));
        panel.add(createCleanStatCard("Highest Package", String.format("%.2f LPA", highestPackage), SUCCESS_COLOR));
        panel.add(createCleanStatCard("Total Placements", String.valueOf(totalPlacements), WARNING_COLOR));
        panel.add(createCleanStatCard("Top Stream", topStream, PURPLE_COLOR));
        
        return panel;
    }
    
    private JPanel createCleanStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(24, 20, 24, 20)
        ));
        
        // Accent bar at top
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accentColor);
        accentBar.setPreferredSize(new Dimension(0, 4));
        accentBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        
        // Title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        
        // Value label with proper text wrapping and sizing
        JLabel valueLabel = new JLabel();
        
        // Handle long text by using HTML for text wrapping
        if (value.length() > 12) {
            // Use HTML to enable text wrapping and smaller font for long text
            valueLabel.setText("<html><div style='text-align: left; line-height: 1.2;'>" + value + "</div></html>");
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        } else {
            valueLabel.setText(value);
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        }
        
        valueLabel.setForeground(TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueLabel.setVerticalAlignment(JLabel.TOP);
        
        // Set preferred size to ensure proper display
        valueLabel.setPreferredSize(new Dimension(200, 60));
        valueLabel.setMaximumSize(new Dimension(200, 60));
        
        card.add(accentBar);
        card.add(Box.createRigidArea(new Dimension(0, 16)));
        card.add(titleLabel);
        card.add(valueLabel);
        card.add(Box.createVerticalGlue());
        
        return card;
    }
    
    private JPanel createChartsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 24, 0));
        panel.setOpaque(false);
        
        // Branch chart with clean styling
        JPanel branchChart = createChartContainer(
            "Placements by Stream",
            "Distribution of placements across different academic streams",
            ChartGenerator.createBranchPieChart(placements)
        );
        
        // Package chart with clean styling
        JPanel packageChart = createChartContainer(
            "Salary Distribution",
            "Analysis of package offers across placements",
            ChartGenerator.createPackageTrendChart(placements)
        );
        
        panel.add(branchChart);
        panel.add(packageChart);
        
        return panel;
    }
    
    private JPanel createChartContainer(String title, String subtitle, Component chart) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(CARD_COLOR);
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        // Chart panel with padding
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setOpaque(false);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        chartPanel.add(chart, BorderLayout.CENTER);
        
        container.add(headerPanel, BorderLayout.NORTH);
        container.add(chartPanel, BorderLayout.CENTER);
        
        return container;
    }
}