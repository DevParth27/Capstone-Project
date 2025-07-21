package com.placement.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DummyDataGenerator {
    private static final Random random = new Random();
    private static final String[] STUDENT_NAMES = {"Rahul", "Priya", "Amit", "Sneha", "Raj", "Neha", "Vikram", "Anjali", "Sanjay", "Meera"};
    private static final String[] BRANCHES = {"Computer Science", "Information Technology", "Electronics", "Mechanical", "Civil"};
    private static final String[] COMPANY_NAMES = {"TechSolutions", "InfoSys", "GlobalTech", "DataMinds", "CodeCrafters", "WebWizards", "CloudNine"};
    private static final String[] SECTORS = {"IT Services", "Software Development", "E-commerce", "Finance", "Consulting"};
    private static final String[] TIERS = {"Tier 1", "Tier 2", "Tier 3"};
    private static final String[] JOB_ROLES = {"Software Developer", "Data Analyst", "System Engineer", "UI/UX Designer", "Project Manager", "Business Analyst"};
    
    public static List<Student> generateStudents(int count) {
        List<Student> students = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String name = STUDENT_NAMES[random.nextInt(STUDENT_NAMES.length)];
            String branch = BRANCHES[random.nextInt(BRANCHES.length)];
            double cgpa = 6.0 + (random.nextDouble() * 4.0); // CGPA between 6.0 and 10.0
            cgpa = Math.round(cgpa * 100.0) / 100.0; // Round to 2 decimal places
            int graduationYear = 2020 + random.nextInt(4); // 2020 to 2023
            
            students.add(new Student(i, name, branch, cgpa, graduationYear));
        }
        return students;
    }
    
    public static List<Company> generateCompanies(int count) {
        List<Company> companies = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String name = COMPANY_NAMES[random.nextInt(COMPANY_NAMES.length)];
            String sector = SECTORS[random.nextInt(SECTORS.length)];
            String tier = TIERS[random.nextInt(TIERS.length)];
            
            companies.add(new Company(i, name, sector, tier));
        }
        return companies;
    }
    
    public static List<Placement> generatePlacements(List<Student> students, List<Company> companies, int count) {
        List<Placement> placements = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Student student = students.get(random.nextInt(students.size()));
            Company company = companies.get(random.nextInt(companies.size()));
            double packageOffered = 3.0 + (random.nextDouble() * 27.0); // Package between 3 and 30 LPA
            packageOffered = Math.round(packageOffered * 100.0) / 100.0; // Round to 2 decimal places
            String jobRole = JOB_ROLES[random.nextInt(JOB_ROLES.length)];
            
            // Generate a random date in the past 3 years
            int year = 2020 + random.nextInt(4); // 2020 to 2023
            int month = 1 + random.nextInt(12); // 1 to 12
            int day = 1 + random.nextInt(28); // 1 to 28 (to avoid month length issues)
            LocalDate placementDate = LocalDate.of(year, month, day);
            
            placements.add(new Placement(i, student, company, packageOffered, jobRole, placementDate));
        }
        return placements;
    }
    
    // Generate data for all years
    public static List<Placement> generateHistoricalData() {
        List<Student> students = generateStudents(50);
        List<Company> companies = generateCompanies(10);
        return generatePlacements(students, companies, 100);
    }
}