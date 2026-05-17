package com.rosterforge.algorithms.models;

public class AvailabilityPreference {
    private Employee employee;
    private Shift shift;
    private AvailabilityLevel availabilityLevel;

    public AvailabilityPreference(Employee employee, Shift shift, AvailabilityLevel availabilityLevel) {
        this.employee = employee;
        this.shift = shift;
        this.availabilityLevel = availabilityLevel;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Shift getShift() {
        return shift;
    }

    public AvailabilityLevel getAvailabilityLevel() {
        return availabilityLevel;
    }
}
