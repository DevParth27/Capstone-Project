package com.phishing.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhishingDetector {
    // Threshold for phishing detection
    private static final double PHISHING_THRESHOLD = 0.6;
    
    /**
     * Predicts if a URL is phishing or legitimate
     * @param url The URL to check
     * @return A map containing prediction results
     */
    public Map<String, Object> predict(String url) {
        Map<String, Object> result = new HashMap<>();
        result.put("url", url);
        
        // Extract features from URL
        Map<String, Double> features = extractFeatures(url);
        
        // Calculate phishing score based on features
        double phishingScore = calculatePhishingScore(features);
        
        // Determine if URL is phishing based on score
        boolean isPhishing = phishingScore >= PHISHING_THRESHOLD;
        
        result.put("prediction", isPhishing ? "Phishing" : "Legitimate");
        result.put("confidence", phishingScore);
        
        return result;
    }
    
    /**
     * Extracts features from a URL for phishing detection
     * @param url The URL to extract features from
     * @return A map of feature names to values
     */
    private Map<String, Double> extractFeatures(String url) {
        Map<String, Double> features = new HashMap<>();
        
        // URL length
        features.put("url_length", (double) url.length());
        
        // Number of special characters
        features.put("num_dots", (double) countOccurrences(url, '.'));
        features.put("num_hyphens", (double) countOccurrences(url, '-'));
        features.put("num_underscores", (double) countOccurrences(url, '_'));
        features.put("num_slashes", (double) countOccurrences(url, '/'));
        features.put("num_question_marks", (double) countOccurrences(url, '?'));
        features.put("num_equal_signs", (double) countOccurrences(url, '='));
        features.put("num_at_symbols", (double) countOccurrences(url, '@'));
        features.put("num_ampersands", (double) countOccurrences(url, '&'));
        features.put("num_exclamation", (double) countOccurrences(url, '!'));
        
        // Check for suspicious keywords
        String[] suspiciousKeywords = {"secure", "account", "update", "confirm", "login", 
                                      "signin", "bank", "paypal", "ebay", "amazon"};
        int keywordCount = 0;
        String lowerUrl = url.toLowerCase();
        for (String keyword : suspiciousKeywords) {
            if (lowerUrl.contains(keyword)) {
                keywordCount++;
            }
        }
        features.put("suspicious_keywords", (double) keywordCount);
        
        // Check for IP address
        String ipPattern = "\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b";
        Pattern pattern = Pattern.compile(ipPattern);
        Matcher matcher = pattern.matcher(url);
        features.put("has_ip", matcher.find() ? 1.0 : 0.0);
        
        // Check for HTTPS
        features.put("has_https", url.startsWith("https://") ? 1.0 : 0.0);
        
        // Domain length and subdomains
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            if (domain != null) {
                features.put("domain_length", (double) domain.length());
                
                // Count subdomains
                String[] domainParts = domain.split("\\.");
                int subdomains = domainParts.length - 2;
                features.put("num_subdomains", subdomains > 0 ? (double) subdomains : 0.0);
            } else {
                features.put("domain_length", 0.0);
                features.put("num_subdomains", 0.0);
            }
        } catch (URISyntaxException e) {
            features.put("domain_length", 0.0);
            features.put("num_subdomains", 0.0);
        }
        
        return features;
    }
    
    /**
     * Calculates a phishing score based on extracted features
     * @param features The extracted features
     * @return A score between 0 and 1 (higher means more likely to be phishing)
     */
    private double calculatePhishingScore(Map<String, Double> features) {
        double score = 0.0;
        double totalWeight = 0.0;
        
        // URL length (longer URLs are more suspicious)
        double urlLength = features.get("url_length");
        if (urlLength > 75) {
            score += 0.1;
        }
        totalWeight += 0.1;
        
        // Special characters
        double specialChars = features.get("num_dots") + features.get("num_hyphens") + 
                             features.get("num_underscores") + features.get("num_at_symbols");
        if (specialChars > 5) {
            score += 0.1;
        }
        totalWeight += 0.1;
        
        // Suspicious keywords (high impact)
        double keywords = features.get("suspicious_keywords");
        if (keywords > 0) {
            score += 0.2 * Math.min(keywords / 3.0, 1.0);
        }
        totalWeight += 0.2;
        
        // IP address instead of domain name (very suspicious)
        if (features.get("has_ip") > 0) {
            score += 0.3;
        }
        totalWeight += 0.3;
        
        // HTTPS (legitimate sites tend to use HTTPS)
        if (features.get("has_https") == 0) {
            score += 0.15;
        }
        totalWeight += 0.15;
        
        // Domain length (very short or very long domains are suspicious)
        double domainLength = features.get("domain_length");
        if (domainLength < 5 || domainLength > 30) {
            score += 0.05;
        }
        totalWeight += 0.05;
        
        // Too many subdomains
        if (features.get("num_subdomains") > 3) {
            score += 0.1;
        }
        totalWeight += 0.1;
        
        // Normalize score
        return score / totalWeight;
    }
    
    /**
     * Counts occurrences of a character in a string
     * @param str The string to search in
     * @param c The character to count
     * @return The number of occurrences
     */
    private int countOccurrences(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }
}