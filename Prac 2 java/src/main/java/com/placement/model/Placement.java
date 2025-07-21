package com.placement.model;

import java.time.LocalDate;

public class Placement {
    private int id;
    private Student student;
    private Company company;
    private double packageOffered; // in lakhs per annum
    private String jobRole;
    private LocalDate placementDate;
    
    public Placement(int id, Student student, Company company, double packageOffered, 
                     String jobRole, LocalDate placementDate) {
        this.id = id;
        this.student = student;
        this.company = company;
        this.packageOffered = packageOffered;
        this.jobRole = jobRole;
        this.placementDate = placementDate;
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    
    public double getPackageOffered() { return packageOffered; }
    public void setPackageOffered(double packageOffered) { this.packageOffered = packageOffered; }
    
    public String getJobRole() { return jobRole; }
    public void setJobRole(String jobRole) { this.jobRole = jobRole; }
    
    public LocalDate getPlacementDate() { return placementDate; }
    public void setPlacementDate(LocalDate placementDate) { this.placementDate = placementDate; }
    
    @Override
    public String toString() {
        return "Placement{" +
                "id=" + id +
                ", student=" + student.getName() +
                ", company=" + company.getName() +
                ", packageOffered=" + packageOffered +
                ", jobRole='" + jobRole + '\'' +
                ", placementDate=" + placementDate +
                '}';
    }
}