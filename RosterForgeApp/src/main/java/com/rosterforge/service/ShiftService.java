package com.rosterforge.service;

import com.rosterforge.dao.IDao;
import com.rosterforge.dm.ShiftDm;

import java.util.List;

public class ShiftService {

    private final IDao<ShiftDm> shiftDao;

    public ShiftService(IDao<ShiftDm> shiftDao) {
        this.shiftDao = shiftDao;
    }

    public void addShift(ShiftDm shift) {
        shiftDao.save(shift);
    }

    public ShiftDm getShift(long id) {
        return shiftDao.findById(id);
    }

    public List<ShiftDm> getAllShifts() {
        return shiftDao.findAll();
    }

    public void removeShift(long id) {
        shiftDao.delete(id);
    }

    public void updateShift(ShiftDm shift) {
        shiftDao.save(shift);
    }
}
