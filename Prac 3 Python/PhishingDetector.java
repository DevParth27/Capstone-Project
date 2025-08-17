package com.phishing.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhishingDetector {
    // Lower threshold for better detection
    private static final double PHISHING_THRESHOLD = 0.4;
    
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
        
        // Calculate phishing score based on features (pass URL separately)
        double phishingScore = calculatePhishingScore(features, url);
        
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
     * @param url The original URL for additional pattern checking
     * @return A score between 0 and 1 (higher means more likely to be phishing)
     */
    private double calculatePhishingScore(Map<String, Double> features, String url) {
        double score = 0.0;
        
        // URL length (longer URLs are more suspicious)
        double urlLength = features.get("url_length");
        if (urlLength > 50) {
            score += 0.15;
        }
        
        // Special characters (increased weight)
        double specialChars = features.get("num_dots") + features.get("num_hyphens") + 
                             features.get("num_underscores") + features.get("num_at_symbols");
        if (specialChars > 4) {
            score += 0.2;
        }
        
        // Suspicious keywords (SIGNIFICANTLY INCREASED WEIGHT)
        double keywords = features.get("suspicious_keywords");
        if (keywords >= 1) {
            score += 0.3; // Base score for any suspicious keyword
            if (keywords >= 2) {
                score += 0.2; // Additional score for multiple keywords
            }
            if (keywords >= 3) {
                score += 0.2; // Even more for 3+ keywords
            }
        }
        
        // IP address instead of domain name (very suspicious)
        if (features.get("has_ip") > 0) {
            score += 0.4;
        }
        
        // No HTTPS (increased weight)
        if (features.get("has_https") == 0) {
            score += 0.25;
        }
        
        // Domain length (suspicious patterns)
        double domainLength = features.get("domain_length");
        if (domainLength > 25) { // Long suspicious domains
            score += 0.15;
        }
        
        // Too many subdomains
        if (features.get("num_subdomains") > 2) {
            score += 0.15;
        }
        
        // Additional check for common phishing patterns using the URL parameter
        String lowerUrl = url.toLowerCase();
        // Check for suspicious domain patterns
        if (lowerUrl.contains("paypal") && !lowerUrl.contains("paypal.com")) {
            score += 0.3;
        }
        if (lowerUrl.contains("amazon") && !lowerUrl.contains("amazon.com")) {
            score += 0.3;
        }
        if (lowerUrl.contains("secure") && lowerUrl.contains("update")) {
            score += 0.25;
        }
        
        return Math.min(score, 1.0); // Cap at 1.0
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