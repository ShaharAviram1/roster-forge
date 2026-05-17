package com.rosterforge.algorithms.models;

public class ConstraintViolation {
    private String message;
    private boolean hardViolation;

    public ConstraintViolation(String message, boolean hardViolation) {
        this.message = message;
        this.hardViolation = hardViolation;
    }

    public String getMessage() {
        return message;
    }

    public boolean isHardViolation() {
        return hardViolation;
    }
}
