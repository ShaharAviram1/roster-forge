package com.rosterforge.service;

import com.rosterforge.dao.IDao;
import com.rosterforge.dm.EmployeeDm;

import java.util.List;

public class EmployeeService {

    private final IDao<EmployeeDm> employeeDao;

    public EmployeeService(IDao<EmployeeDm> employeeDao) {
        this.employeeDao = employeeDao;
    }

    public void addEmployee(EmployeeDm employee) {
        employeeDao.save(employee);
    }

    public EmployeeDm getEmployee(long id) {
        return employeeDao.findById(id);
    }

    public List<EmployeeDm> getAllEmployees() {
        return employeeDao.findAll();
    }

    public void removeEmployee(long id) {
        employeeDao.delete(id);
    }

    public void updateEmployee(EmployeeDm employee) {
        employeeDao.save(employee);
    }
}
