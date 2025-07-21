package com.placement.model;

public class Company {
    private int id;
    private String name;
    private String sector;
    private String tier; // Tier 1, Tier 2, etc.
    
    public Company(int id, String name, String sector, String tier) {
        this.id = id;
        this.name = name;
        this.sector = sector;
        this.tier = tier;
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }
    
    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
    
    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sector='" + sector + '\'' +
                ", tier='" + tier + '\'' +
                '}';
    }
}