package com.rosterforge.dm;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class EmployeeDm implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private String name;
    private List<String> qualifiedRoles;
    private int maxShiftsPerWeek;

    public EmployeeDm(long id, String name, List<String> qualifiedRoles, int maxShiftsPerWeek) {
        this.id = id;
        this.name = name;
        this.qualifiedRoles = qualifiedRoles;
        this.maxShiftsPerWeek = maxShiftsPerWeek;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public List<String> getQualifiedRoles() { return qualifiedRoles; }
    public int getMaxShiftsPerWeek() { return maxShiftsPerWeek; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((EmployeeDm) o).id;
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }

    @Override
    public String toString() {
        return "EmployeeDm{id=" + id + ", name='" + name + "', roles=" + qualifiedRoles
                + ", maxShifts=" + maxShiftsPerWeek + "}";
    }
}
