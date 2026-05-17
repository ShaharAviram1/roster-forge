package com.rosterforge.algorithms.models;

import java.util.Set;

public class Employee {
    private long id;
    private String name;
    private Set<Role> qualifiedRoles;
    private int maxShiftsPerWeek;

    public Employee(long id, String name, Set<Role> qualifiedRoles, int maxShiftsPerWeek) {
        this.id = id;
        this.name = name;
        this.qualifiedRoles = qualifiedRoles;
        this.maxShiftsPerWeek = maxShiftsPerWeek;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Role> getQualifiedRoles() {
        return qualifiedRoles;
    }

    public int getMaxShiftsPerWeek() {
        return maxShiftsPerWeek;
    }
}
