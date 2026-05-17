package com.rosterforge.algorithms.models;

import java.util.Set;

public class Shift {
    private int dayIndex;
    private ShiftType shiftType;
    private Set<ShiftRequirement> requirements;

    public Shift(Set<ShiftRequirement> requirements, ShiftType shiftType, int dayIndex) {
        this.requirements = requirements;
        this.shiftType = shiftType;
        this.dayIndex = dayIndex;
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public Set<ShiftRequirement> getRequirements() {
        return requirements;
    }
}
