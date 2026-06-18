package com.rosterforge.server;

import com.rosterforge.dao.DaoFileImpl;
import com.rosterforge.dao.IDao;
import com.rosterforge.dm.AvailabilityPreferenceDm;
import com.rosterforge.dm.EmployeeDm;
import com.rosterforge.dm.ShiftDm;
import com.rosterforge.service.AvailabilityPreferenceService;
import com.rosterforge.service.EmployeeService;
import com.rosterforge.service.RosterService;
import com.rosterforge.service.ShiftService;
import com.rosterforge.algorithms.constraints.*;
import com.rosterforge.algorithms.engine.EdmondsKarpAlgoMaxFlow;
import com.rosterforge.algorithms.engine.IAlgoMaxFlow;
import com.rosterforge.algorithms.scoring.AvailabilityRosterScorer;
import com.rosterforge.algorithms.validation.ConstraintValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

public class ServerDriver {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        Properties config = loadConfig();

        IDao<EmployeeDm> employeeDao = new DaoFileImpl<>(
                config.getProperty("employees"), EmployeeDm::getId);
        IDao<ShiftDm> shiftDao = new DaoFileImpl<>(
                config.getProperty("shifts"), ShiftDm::getId);
        IDao<AvailabilityPreferenceDm> preferenceDao = new DaoFileImpl<>(
                config.getProperty("preferences"), AvailabilityPreferenceDm::getId);

        ConstraintValidator validator = new ConstraintValidator(Set.of(
                new RedAvailabilityConstraint(),
                new ConsecutiveShiftConstraint(),
                new MaxShiftsPerWeekConstraint(),
                new SameShiftDuplicateAssignmentConstraint()
        ));

        IAlgoMaxFlow algorithm = new EdmondsKarpAlgoMaxFlow(validator, new AvailabilityRosterScorer());

        EmployeeService employeeService     = new EmployeeService(employeeDao);
        ShiftService shiftService           = new ShiftService(shiftDao);
        AvailabilityPreferenceService preferenceService =
                new AvailabilityPreferenceService(preferenceDao);
        RosterService rosterService         = new RosterService(employeeDao, shiftDao, preferenceDao, algorithm);

        new Thread(new Server(PORT, employeeService, shiftService, preferenceService, rosterService)).start();
    }

    private static Properties loadConfig() throws IOException {
        Properties config = new Properties();
        try (InputStream is = ServerDriver.class.getClassLoader().getResourceAsStream("datasource.txt")) {
            config.load(is);
        }
        return config;
    }
}
