package com.brub.ticketer.model;

public enum Priority {
    FAIBLE("#28a745"),    // Vert
    MOYEN("#ffc107"),     // Jaune
    ELEVE("#fd7e14"),     // Orange
    CRITIQUE("#dc3545");  // Rouge

    private final String colorCode;

    Priority(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getColorCode() {
        return colorCode;
    }
}