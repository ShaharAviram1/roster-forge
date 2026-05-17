package com.rosterforge.algorithms.models;

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
}
