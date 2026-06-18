package com.rosterforge.client.dm;

import java.util.Map;

public class ShiftDm {

    private long id;
    private int dayIndex;
    private String shiftType;
    private Map<String, Integer> roleRequirements;

    public long getId() { return id; }
    public int getDayIndex() { return dayIndex; }
    public String getShiftType() { return shiftType; }
    public Map<String, Integer> getRoleRequirements() { return roleRequirements; }

    @Override
    public String toString() { return "Day " + dayIndex + " - " + shiftType; }
}
