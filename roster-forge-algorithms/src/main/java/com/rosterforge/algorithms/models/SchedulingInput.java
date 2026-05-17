package com.rosterforge.algorithms.models;

import java.util.Set;

public class SchedulingInput {
    private Set<Employee> employees;
    private Set<Shift> shifts;
    private Set<AvailabilityPreference> availabilityPreferences;

    public SchedulingInput(Set<Employee> employees, Set<Shift> shifts, Set<AvailabilityPreference> availabilityPreferences) {
        this.employees = employees;
        this.shifts = shifts;
        this.availabilityPreferences = availabilityPreferences;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public Set<Shift> getShifts() {
        return shifts;
    }

    public Set<AvailabilityPreference> getAvailabilityPreferences() {
        return availabilityPreferences;
    }
}
