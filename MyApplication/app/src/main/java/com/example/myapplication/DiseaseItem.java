package com.example.myapplication;

public class DiseaseItem {
    private String name;
    private String description;
    private String commonSymptoms;

    public DiseaseItem(String name, String description, String commonSymptoms) {
        this.name = name;
        this.description = description;
        this.commonSymptoms = commonSymptoms;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCommonSymptoms() {
        return commonSymptoms;
    }
}
