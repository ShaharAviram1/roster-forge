package com.hit.dm;

import java.io.Serializable;
import java.util.Objects;

public class AvailabilityPreferenceDm implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private long employeeId;
    private long shiftId;
    private String availabilityLevel;

    public AvailabilityPreferenceDm(long id, long employeeId, long shiftId, String availabilityLevel) {
        this.id = id;
        this.employeeId = employeeId;
        this.shiftId = shiftId;
        this.availabilityLevel = availabilityLevel;
    }

    public long getId() { return id; }
    public long getEmployeeId() { return employeeId; }
    public long getShiftId() { return shiftId; }
    public String getAvailabilityLevel() { return availabilityLevel; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvailabilityPreferenceDm that = (AvailabilityPreferenceDm) o;
        return employeeId == that.employeeId && shiftId == that.shiftId;
    }

    @Override
    public int hashCode() { return Objects.hash(employeeId, shiftId); }

    @Override
    public String toString() {
        return "AvailabilityPreferenceDm{emp=" + employeeId + ", shift=" + shiftId
                + ", level='" + availabilityLevel + "'}";
    }
}
