package com.rosterforge.algorithms.models;

import java.util.Objects;

public class ShiftRequirement {
    private Role role;
    private int requiredCount;

    public ShiftRequirement(Role role, int requiredCount) {
        this.role = role;
        this.requiredCount = requiredCount;
    }

    public Role getRole() {
        return role;
    }

    public int getRequiredCount() {
        return requiredCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShiftRequirement that = (ShiftRequirement) o;
        return getRole() == that.getRole();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getRole());
    }
}
