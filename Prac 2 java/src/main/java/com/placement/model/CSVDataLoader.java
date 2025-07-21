package com.placement.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVDataLoader {
    
    private static final Map<String, String> streamMapping = new HashMap<>();
    private static final Map<String, String> specializationMapping = new HashMap<>();
    
    static {
        // Initialize mappings for degree streams
        streamMapping.put("Sci&Tech", "Science & Technology");
        streamMapping.put("Comm&Mgmt", "Commerce & Management");
        
        // Initialize mappings for specializations
        specializationMapping.put("Mkt&HR", "Marketing & HR");
        specializationMapping.put("Mkt&Fin", "Marketing & Finance");
    }
    
    public static List<Placement> loadPlacementData(String csvFilePath) {
        List<Placement> placements = new ArrayList<>();
        List<Student> students = new ArrayList<>();
        List<Company> companies = new ArrayList<>();
        Map<String, Company> companyMap = new HashMap<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            // Skip header line
            String line = br.readLine();
            int id = 1;
            
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                
                // Only process if there's enough data and the student was placed
                if (data.length >= 13 && "Placed".equals(data[12])) {
                    // Create Student
                    int studentId = Integer.parseInt(data[0]);
                    String gender = data[1];
                    double tenthPercentage = Double.parseDouble(data[2]);
                    String sscBoard = data[3];
                    double twelfthPercentage = Double.parseDouble(data[4]);
                    String hscBoard = data[5];
                    String stream = data[6];
                    double degreePercentage = Double.parseDouble(data[7]);
                    String degreeStream = streamMapping.getOrDefault(data[8], data[8]);
                    boolean hasWorkExp = "Yes".equals(data[9]);
                    String specialization = specializationMapping.getOrDefault(data[10], data[10]);
                    double mbaPercentage = Double.parseDouble(data[11]);
                    
                    // Create a student name based on ID and gender
                    String studentName = "Student" + studentId + " (" + gender + ")"; 
                    
                    Student student = new Student(studentId, studentName, degreeStream, 
                            degreePercentage / 10.0, // Convert percentage to CGPA scale
                            Year.now().getValue() - 1); // Assuming graduated last year
                    students.add(student);
                    
                    // Create or get Company (using a simple approach for demo)
                    String companyName = "Company" + (id % 10 + 1);
                    Company company;
                    
                    if (companyMap.containsKey(companyName)) {
                        company = companyMap.get(companyName);
                    } else {
                        String sector = (specialization.contains("HR")) ? "HR Services" : "Financial Services";
                        String tier = (Double.parseDouble(data[13]) > 300000) ? "Tier 1" : "Tier 2";
                        company = new Company(companyMap.size() + 1, companyName, sector, tier);
                        companyMap.put(companyName, company);
                        companies.add(company);
                    }
                    
                    // Create Placement
                    double salary = Double.parseDouble(data[13]);
                    double packageLPA = salary / 100000; // Convert to Lakhs Per Annum
                    String jobRole = specialization.contains("HR") ? "HR Manager" : "Financial Analyst";
                    
                    // Generate a placement date (using current year for simplicity)
                    LocalDate placementDate = LocalDate.of(Year.now().getValue() - 1, (id % 12) + 1, (id % 28) + 1);
                    
                    Placement placement = new Placement(id, student, company, packageLPA, jobRole, placementDate);
                    placements.add(placement);
                    
                    id++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading CSV data: " + e.getMessage());
            // Return dummy data as fallback
            return DummyDataGenerator.generateHistoricalData();
        }
        
        return placements;
    }
}