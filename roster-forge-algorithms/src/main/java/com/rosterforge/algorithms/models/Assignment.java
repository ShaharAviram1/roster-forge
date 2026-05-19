package com.rosterforge.algorithms.models;

import java.util.Objects;

public class Assignment {
    private Employee employee;
    private Shift shift;
    private Role role;

    public Assignment(Employee employee, Shift shift, Role role) {
        this.employee = employee;
        this.shift = shift;
        this.role = role;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Shift getShift() {
        return shift;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assignment that = (Assignment) o;
        return Objects.equals(getEmployee(), that.getEmployee()) && Objects.equals(getShift(), that.getShift()) && getRole() == that.getRole();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmployee(), getShift(), getRole());
    }
}
