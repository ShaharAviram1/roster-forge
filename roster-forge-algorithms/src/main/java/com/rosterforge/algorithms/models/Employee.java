package com.rosterforge.algorithms.models;

import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return getId() == employee.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
