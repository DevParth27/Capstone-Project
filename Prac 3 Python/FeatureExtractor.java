package com.phishing.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeatureExtractor {
    
    /**
     * Extracts features from a URL for phishing detection
     * @param url The URL to extract features from
     * @return A map of feature names to values
     */
    public static Map<String, Double> extractFromUrl(String url) {
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
     * Counts occurrences of a character in a string
     * @param str The string to search in
     * @param c The character to count
     * @return The number of occurrences
     */
    private static int countOccurrences(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }
}