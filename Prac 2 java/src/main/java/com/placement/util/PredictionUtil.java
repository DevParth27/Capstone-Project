package com.placement.util;

import com.placement.model.Placement;

import java.util.*;
import java.util.stream.Collectors;

public class PredictionUtil {

    public static Map<String, Object> predictPlacements(List<Placement> historicalData, String branch, int predictionYear) {
        // Filter data by branch if needed
        List<Placement> filteredData = historicalData;
        if (!branch.equals("All Branches")) {
            filteredData = historicalData.stream()
                    .filter(p -> p.getStudent().getBranch().equals(branch))
                    .collect(Collectors.toList());
        }
        
        // For this simple example, we'll use a basic linear trend prediction
        // In a real application, you would use more sophisticated statistical methods
        
        // Calculate average package trend
        Map<Integer, Double> packageByYear = filteredData.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getPlacementDate().getYear(),
                        Collectors.averagingDouble(Placement::getPackageOffered)));
        
        // Simple linear prediction for average package
        double avgPackagePrediction = predictLinearTrend(packageByYear, predictionYear);
        
        // Predict placement percentage (random for demo)
        double placementPercentage = 75.0 + (Math.random() * 15.0);
        
        // Find top sector and role (most frequent in historical data)
        String topSector = filteredData.stream()
                .collect(Collectors.groupingBy(p -> p.getCompany().getSector(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("IT Services");
        
        String topRole = filteredData.stream()
                .collect(Collectors.groupingBy(Placement::getJobRole, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Software Developer");
        
        // Create prediction result
        Map<String, Object> predictions = new HashMap<>();
        predictions.put("avgPackage", avgPackagePrediction);
        predictions.put("placementPercentage", placementPercentage);
        predictions.put("topSector", topSector);
        predictions.put("topRole", topRole);
        
        return predictions;
    }
    
    private static double predictLinearTrend(Map<Integer, Double> historicalData, int targetYear) {
        // If no historical data, return a default value
        if (historicalData.isEmpty()) {
            return 10.0; // Default prediction
        }
        
        // If only one data point, use it with a small increase
        if (historicalData.size() == 1) {
            Map.Entry<Integer, Double> entry = historicalData.entrySet().iterator().next();
            return entry.getValue() * (1 + 0.1 * (targetYear - entry.getKey()));
        }
        
        // Simple linear regression
        List<Integer> years = new ArrayList<>(historicalData.keySet());
        Collections.sort(years);
        
        // Calculate slope using the most recent two years
        int year1 = years.get(years.size() - 2);
        int year2 = years.get(years.size() - 1);
        double value1 = historicalData.get(year1);
        double value2 = historicalData.get(year2);
        
        double slope = (value2 - value1) / (year2 - year1);
        
        // Predict using the most recent value and the calculated slope
        double prediction = value2 + slope * (targetYear - year2);
        
        // Ensure prediction is reasonable (between 3 and 50 LPA)
        prediction = Math.max(3.0, Math.min(50.0, prediction));
        
        return prediction;
    }
}