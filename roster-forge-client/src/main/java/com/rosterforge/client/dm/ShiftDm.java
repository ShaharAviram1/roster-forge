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

    private static final String[] DAY_NAMES =
            {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    @Override
    public String toString() {
        String day = (dayIndex >= 0 && dayIndex < DAY_NAMES.length) ? DAY_NAMES[dayIndex] : "Day " + dayIndex;
        return day + " - " + shiftType;
    }
}
