package com.rosterforge.client.dm;

import java.util.List;

public class EmployeeDm {

    private long id;
    private String name;
    private List<String> qualifiedRoles;
    private int maxShiftsPerWeek;

    public long getId() { return id; }
    public String getName() { return name; }
    public List<String> getQualifiedRoles() { return qualifiedRoles; }
    public int getMaxShiftsPerWeek() { return maxShiftsPerWeek; }

    @Override
    public String toString() { return name; }
}
