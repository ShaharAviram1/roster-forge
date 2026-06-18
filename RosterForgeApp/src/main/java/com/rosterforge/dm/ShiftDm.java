package com.rosterforge.dm;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class ShiftDm implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private int dayIndex;
    private String shiftType;
    private Map<String, Integer> roleRequirements;

    public ShiftDm(long id, int dayIndex, String shiftType, Map<String, Integer> roleRequirements) {
        this.id = id;
        this.dayIndex = dayIndex;
        this.shiftType = shiftType;
        this.roleRequirements = roleRequirements;
    }

    public long getId() { return id; }
    public int getDayIndex() { return dayIndex; }
    public String getShiftType() { return shiftType; }
    public Map<String, Integer> getRoleRequirements() { return roleRequirements; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShiftDm shift = (ShiftDm) o;
        return dayIndex == shift.dayIndex && Objects.equals(shiftType, shift.shiftType);
    }

    @Override
    public int hashCode() { return Objects.hash(dayIndex, shiftType); }

    @Override
    public String toString() {
        return "ShiftDm{day=" + dayIndex + ", type='" + shiftType + "', reqs=" + roleRequirements + "}";
    }
}
