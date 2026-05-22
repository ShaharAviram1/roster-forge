package com.hit.service;

import com.hit.dao.IDao;
import com.hit.dm.AvailabilityPreferenceDm;
import com.hit.dm.EmployeeDm;
import com.hit.dm.ShiftDm;

import com.rosterforge.algorithms.engine.IAlgoMaxFlow;
import com.rosterforge.algorithms.models.*;

import java.util.*;

public class RosterService {

    private final IDao<EmployeeDm> employeeDao;
    private final IDao<ShiftDm> shiftDao;
    private final IDao<AvailabilityPreferenceDm> preferenceDao;
    private final IAlgoMaxFlow algorithm;

    public RosterService(IDao<EmployeeDm> employeeDao,
                         IDao<ShiftDm> shiftDao,
                         IDao<AvailabilityPreferenceDm> preferenceDao,
                         IAlgoMaxFlow algorithm) {
        this.employeeDao = employeeDao;
        this.shiftDao = shiftDao;
        this.preferenceDao = preferenceDao;
        this.algorithm = algorithm;
    }

    public RosterResult generateRoster() {
        return algorithm.generateRoster(buildSchedulingInput());
    }

    private SchedulingInput buildSchedulingInput() {
        List<EmployeeDm> dmEmployees = employeeDao.findAll();
        List<ShiftDm> dmShifts = shiftDao.findAll();
        List<AvailabilityPreferenceDm> dmPreferences = preferenceDao.findAll();

        Map<Long, Employee> empById = new LinkedHashMap<>();
        for (EmployeeDm dm : dmEmployees) {
            Set<Role> roles = toRoles(dm.getQualifiedRoles());
            empById.put(dm.getId(), new Employee(dm.getId(), dm.getName(), roles, dm.getMaxShiftsPerWeek()));
        }

        Map<Long, Shift> shiftById = new LinkedHashMap<>();
        for (ShiftDm dm : dmShifts) {
            Set<ShiftRequirement> reqs = toRequirements(dm.getRoleRequirements());
            ShiftType type = ShiftType.valueOf(dm.getShiftType());
            shiftById.put(dm.getId(), new Shift(reqs, type, dm.getDayIndex()));
        }

        Set<AvailabilityPreference> algoPrefs = new LinkedHashSet<>();
        for (AvailabilityPreferenceDm dm : dmPreferences) {
            Employee emp = empById.get(dm.getEmployeeId());
            Shift shift = shiftById.get(dm.getShiftId());
            if (emp == null || shift == null) continue;
            AvailabilityLevel level = AvailabilityLevel.valueOf(dm.getAvailabilityLevel());
            algoPrefs.add(new AvailabilityPreference(emp, shift, level));
        }

        return new SchedulingInput(
                new LinkedHashSet<>(empById.values()),
                new LinkedHashSet<>(shiftById.values()),
                algoPrefs);
    }

    private Set<Role> toRoles(List<String> roleNames) {
        Set<Role> roles = new LinkedHashSet<>();
        for (String name : roleNames) roles.add(Role.valueOf(name));
        return roles;
    }

    private Set<ShiftRequirement> toRequirements(Map<String, Integer> map) {
        Set<ShiftRequirement> reqs = new LinkedHashSet<>();
        for (Map.Entry<String, Integer> e : map.entrySet())
            reqs.add(new ShiftRequirement(Role.valueOf(e.getKey()), e.getValue()));
        return reqs;
    }
}
