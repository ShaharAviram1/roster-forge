package com.rosterforge.algorithms.models;

public class ScheduleSlot {
    private Shift shift;
    private Role role;

    public ScheduleSlot(Shift shift, Role role) {
        this.shift = shift;
        this.role = role;
    }

    public Shift getShift() {
        return shift;
    }

    public Role getRole() {
        return role;
    }
}
