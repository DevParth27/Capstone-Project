package com.phishing;

import javax.swing.*;
import java.awt.*;
import com.phishing.ui.PhishingDetectorPanel;

public class MainApp {
    private JFrame frame;
    private JTabbedPane tabbedPane;
    
    public MainApp() {
        initComponents();
    }
    
    private void initComponents() {
        frame = new JFrame("Phishing Detection System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Phishing Detector", new PhishingDetectorPanel());
        
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }
    
    public void display() {
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.display();
        });
    }
}