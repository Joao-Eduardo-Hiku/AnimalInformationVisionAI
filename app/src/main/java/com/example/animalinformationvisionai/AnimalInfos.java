package com.example.animalinformationvisionai;

import java.util.List;

public class AnimalInfos {

    private String name;
    private List<String> locations;
    private Taxonomy taxonomy;
    private Characteristics characteristics;

    // Getters básicos
    public String getName() {
        return name;
    }

    public List<String> getLocations() {
        return locations;
    }

    // Getters para campos de Taxonomy
    public String getScientific_name() {
        if (taxonomy != null) {
            return taxonomy.scientific_name;
        } else {
            return null;
        }
    }

    public String getKingdom() {
        if (taxonomy != null) {
            return taxonomy.kingdom;
        } else {
            return null;
        }
    }

    public String getFamily() {
        if (taxonomy != null) {
            return taxonomy.family;
        } else {
            return null;
        }
    }

    public String getOrder() {
        if (taxonomy != null) {
            return taxonomy.order;
        } else {
            return null;
        }
    }

    public String getPhylum() {
        if (taxonomy != null) {
            return taxonomy.phylum;
        } else {
            return null;
        }
    }

    // Getters para campos de Characteristics
    public String getPrey() {
        if (characteristics != null) {
            return characteristics.prey;
        } else {
            return null;
        }
    }

    // Classe interna para Taxonomy
    public static class Taxonomy {
        private String kingdom;
        private String phylum;
        private String order;
        private String family;
        private String scientific_name;
    }

    // Classe interna para Characteristics
    public static class Characteristics {
        private String prey;
    }
}