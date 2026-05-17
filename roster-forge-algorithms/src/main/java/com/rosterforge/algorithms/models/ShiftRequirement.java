package com.rosterforge.algorithms.models;

public class ShiftRequirement {
    private Role role;
    private int requiredCount;

    public ShiftRequirement(Role role, int requiredCount) {
        this.role = role;
        this.requiredCount = requiredCount;
    }
}
