package com.rosterforge.algorithms.models;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvailabilityPreference that = (AvailabilityPreference) o;
        return Objects.equals(getEmployee(), that.getEmployee()) && Objects.equals(getShift(), that.getShift());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmployee(), getShift());
    }
}
