package com.rosterforge.algorithms.models;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstraintViolation that = (ConstraintViolation) o;
        return isHardViolation() == that.isHardViolation() && Objects.equals(getMessage(), that.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessage(), isHardViolation());
    }
}
