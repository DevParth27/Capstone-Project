package com.phishing.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatasetHandler {
    private List<String> urls;
    private List<Integer> labels;
    
    public DatasetHandler() {
        createSampleDataset();
    }
    
    /**
     * Creates a sample dataset with legitimate and phishing URLs
     */
    private void createSampleDataset() {
        urls = new ArrayList<>();
        labels = new ArrayList<>();
        
        // Legitimate URLs
        List<String> legitimateUrls = Arrays.asList(
            "https://www.google.com",
            "https://www.github.com",
            "https://www.stackoverflow.com",
            "https://www.wikipedia.org",
            "https://www.microsoft.com",
            "https://www.apple.com",
            "https://www.amazon.com",
            "https://www.facebook.com",
            "https://www.twitter.com",
            "https://www.linkedin.com",
            "https://www.youtube.com",
            "https://www.netflix.com",
            "https://www.reddit.com",
            "https://www.instagram.com",
            "https://www.pinterest.com"
        );
        
        // Phishing URLs (simulated examples)
        List<String> phishingUrls = Arrays.asList(
            "http://secure-paypal-update.com/login",
            "https://amazon-security-alert.net/confirm",
            "http://192.168.1.1/bank-login",
            "https://paypal-verification.suspicious-domain.com",
            "http://bank-account-update.fake-site.org",
            "https://secure-login-ebay.malicious.com",
            "http://confirm-account-amazon.phishing.net",
            "https://update-paypal-security.fake.org",
            "http://bank-alert-signin.suspicious.com",
            "https://account-verification-paypal.malware.net",
            "http://secure-bank-login.phishing-site.com",
            "https://paypal-account-limited.fake.org",
            "http://amazon-account-suspended.malicious.net",
            "https://ebay-security-notice.suspicious.com",
            "http://bank-verification-required.phishing.org"
        );
        
        // Add legitimate URLs with label 0
        for (String url : legitimateUrls) {
            urls.add(url);
            labels.add(0); // 0 = Legitimate
        }
        
        // Add phishing URLs with label 1
        for (String url : phishingUrls) {
            urls.add(url);
            labels.add(1); // 1 = Phishing
        }
    }
    
    /**
     * Gets the list of URLs in the dataset
     * @return List of URLs
     */
    public List<String> getUrls() {
        return urls;
    }
    
    /**
     * Gets the list of labels in the dataset
     * @return List of labels (0 = Legitimate, 1 = Phishing)
     */
    public List<Integer> getLabels() {
        return labels;
    }
    
    /**
     * Gets dataset information as a formatted string
     * @return Dataset information
     */
    public String getDatasetInfo() {
        int total = urls.size();
        int legitimate = 0;
        int phishing = 0;
        
        for (Integer label : labels) {
            if (label == 0) {
                legitimate++;
            } else {
                phishing++;
            }
        }
        
        StringBuilder info = new StringBuilder();
        info.append("Dataset Information:\n");
        info.append("Total URLs: ").append(total).append("\n");
        info.append("Legitimate URLs: ").append(legitimate).append("\n");
        info.append("Phishing URLs: ").append(phishing).append("\n");
        
        return info.toString();
    }
}