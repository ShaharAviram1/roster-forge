package com.rosterforge.algorithms.models;

import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if(this ==o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shift shift = (Shift) o;
        return getDayIndex() == shift.getDayIndex() && getShiftType() == shift.getShiftType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDayIndex(), getShiftType());
    }
}
