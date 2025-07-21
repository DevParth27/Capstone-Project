package com.placement.util;

import com.placement.model.Placement;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChartGenerator {

    // Color palette for charts
    private static final Color[] CHART_COLORS = {
        new Color(66, 134, 244),   // Blue
        new Color(76, 175, 80),    // Green
        new Color(255, 152, 0),    // Orange
        new Color(156, 39, 176),   // Purple
        new Color(239, 83, 80),    // Red
        new Color(0, 188, 212),    // Cyan
        new Color(255, 193, 7),    // Amber
        new Color(121, 85, 72)     // Brown
    };

    public static JPanel createBranchPieChart(List<Placement> placements) {
        // Group placements by branch
        Map<String, Long> placementsByBranch = placements.stream()
                .collect(Collectors.groupingBy(p -> p.getStudent().getBranch(), Collectors.counting()));
        
        // Create dataset
        DefaultPieDataset dataset = new DefaultPieDataset();
        placementsByBranch.forEach(dataset::setValue);
        
        // Create chart
        JFreeChart chart = ChartFactory.createPieChart(
                null, // No title, we'll use a separate JLabel
                dataset,
                true,
                true,
                false);
        
        // Customize chart
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.setNoDataMessage("No data available");
        plot.setCircular(true);
        plot.setLabelGap(0.02);
        plot.setShadowPaint(null); // Remove shadow
        
        // Set section colors
        int colorIndex = 0;
        for (Object key : dataset.getKeys()) {
            plot.setSectionPaint((Comparable) key, CHART_COLORS[colorIndex % CHART_COLORS.length]);
            colorIndex++;
        }
        
        // Create panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        chartPanel.setBackground(Color.WHITE);
        
        return chartPanel;
    }
    
    public static JPanel createPackageTrendChart(List<Placement> placements) {
        // Group placements by year and calculate average package
        Map<Integer, Double> avgPackageByYear = placements.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getPlacementDate().getYear(),
                        Collectors.averagingDouble(Placement::getPackageOffered)));
        
        // Create dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        avgPackageByYear.forEach((year, avgPackage) -> 
                dataset.addValue(avgPackage, "Average Package (LPA)", year.toString()));
        
        // Create chart
        JFreeChart chart = ChartFactory.createLineChart(
                null, // No title, we'll use a separate JLabel
                "Year",
                "Average Package (LPA)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        
        // Customize chart
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(new Color(240, 240, 240));
        plot.setDomainGridlinesVisible(false);
        
        // Customize renderer
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, CHART_COLORS[0]);
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setDefaultShapesVisible(true);
        renderer.setSeriesShapesVisible(0, true);
        
        // Customize axes
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 12));
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 12));
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        // Create panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        chartPanel.setBackground(Color.WHITE);
        
        return chartPanel;
    }
    
    public static JPanel createYearBarChart(List<Placement> placements) {
        // Group placements by year
        Map<Integer, Long> placementsByYear = placements.stream()
                .collect(Collectors.groupingBy(p -> p.getPlacementDate().getYear(), Collectors.counting()));
        
        // Create dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        placementsByYear.forEach((year, count) -> dataset.addValue(count, "Placements", year.toString()));
        
        // Create chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Placements by Year",
                "Year",
                "Number of Placements",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        
        // Create panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        
        return chartPanel;
    }
    
    public static JPanel createCompanyTrendChart(List<Placement> placements) {
        // Group placements by year and company
        Map<String, Map<Integer, Long>> companyTrends = placements.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCompany().getName(),
                        Collectors.groupingBy(
                                p -> p.getPlacementDate().getYear(),
                                Collectors.counting())));
        
        // Create dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        companyTrends.forEach((company, yearData) -> 
                yearData.forEach((year, count) -> 
                        dataset.addValue(count, company, year.toString())));
        
        // Create chart
        JFreeChart chart = ChartFactory.createLineChart(
                "Company Recruitment Trends",
                "Year",
                "Number of Placements",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        
        // Create panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 400));
        
        return chartPanel;
    }
    
    public static JPanel createBranchTrendChart(List<Placement> placements) {
        // Group placements by year and branch
        Map<String, Map<Integer, Long>> branchTrends = placements.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getStudent().getBranch(),
                        Collectors.groupingBy(
                                p -> p.getPlacementDate().getYear(),
                                Collectors.counting())));
        
        // Create dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        branchTrends.forEach((branch, yearData) -> 
                yearData.forEach((year, count) -> 
                        dataset.addValue(count, branch, year.toString())));
        
        // Create chart
        JFreeChart chart = ChartFactory.createLineChart(
                "Branch-wise Placement Trends",
                "Year",
                "Number of Placements",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        
        // Create panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 400));
        
        return chartPanel;
    }
    
    public static JPanel createJobRoleChart(List<Placement> placements) {
        // Group placements by job role
        Map<String, Long> placementsByRole = placements.stream()
                .collect(Collectors.groupingBy(Placement::getJobRole, Collectors.counting()));
        
        // Create dataset
        DefaultPieDataset dataset = new DefaultPieDataset();
        placementsByRole.forEach(dataset::setValue);
        
        // Create chart
        JFreeChart chart = ChartFactory.createPieChart(
                "Job Role Distribution",
                dataset,
                true,
                true,
                false);
        
        // Create panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 400));
        
        return chartPanel;
    }
    
    public static JPanel createPredictionChart(List<Placement> placements, Map<String, Object> predictions, int predictionYear) {
        // Group historical data by year
        Map<Integer, Double> historicalData = placements.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getPlacementDate().getYear(),
                        Collectors.averagingDouble(Placement::getPackageOffered)));
        
        // Create dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Add historical data
        historicalData.forEach((year, avgPackage) -> 
                dataset.addValue(avgPackage, "Historical", year.toString()));
        
        // Add prediction
        dataset.addValue((Double) predictions.get("avgPackage"), "Predicted", String.valueOf(predictionYear));
        
        // Create chart
        JFreeChart chart = ChartFactory.createLineChart(
                "Average Package Prediction",
                "Year",
                "Average Package (LPA)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        
        // Create panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 400));
        
        return chartPanel;
    }
}