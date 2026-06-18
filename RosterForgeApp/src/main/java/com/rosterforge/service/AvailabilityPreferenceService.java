package com.rosterforge.service;

import com.rosterforge.dao.IDao;
import com.rosterforge.dm.AvailabilityPreferenceDm;

import java.util.List;

public class AvailabilityPreferenceService {

    private final IDao<AvailabilityPreferenceDm> preferenceDao;

    public AvailabilityPreferenceService(IDao<AvailabilityPreferenceDm> preferenceDao) {
        this.preferenceDao = preferenceDao;
    }

    public void addPreference(AvailabilityPreferenceDm preference) {
        preferenceDao.save(preference);
    }

    public AvailabilityPreferenceDm getPreference(long id) {
        return preferenceDao.findById(id);
    }

    public List<AvailabilityPreferenceDm> getAllPreferences() {
        return preferenceDao.findAll();
    }

    public void removePreference(long id) {
        preferenceDao.delete(id);
    }

    public void updatePreference(AvailabilityPreferenceDm preference) {
        preferenceDao.save(preference);
    }
}
