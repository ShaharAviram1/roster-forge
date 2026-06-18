package com.rosterforge.client.dm;

public class AvailabilityPreferenceDm {

    private long id;
    private long employeeId;
    private long shiftId;
    private String availabilityLevel;

    public long getId() { return id; }
    public long getEmployeeId() { return employeeId; }
    public long getShiftId() { return shiftId; }
    public String getAvailabilityLevel() { return availabilityLevel; }

    @Override
    public String toString() {
        return "Emp " + employeeId + " → Shift " + shiftId + " [" + availabilityLevel + "]";
    }
}
