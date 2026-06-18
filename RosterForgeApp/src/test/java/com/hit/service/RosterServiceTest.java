package com.hit.service;

import com.hit.dao.DaoFileImpl;
import com.hit.dao.IDao;
import com.hit.dm.AvailabilityPreferenceDm;
import com.hit.dm.EmployeeDm;
import com.hit.dm.ShiftDm;

import com.rosterforge.algorithms.constraints.*;
import com.rosterforge.algorithms.engine.EdmondsKarpAlgoMaxFlow;
import com.rosterforge.algorithms.engine.FordFulkersonAlgoMaxFlow;
import com.rosterforge.algorithms.engine.IAlgoMaxFlow;
import com.rosterforge.algorithms.models.RosterResult;
import com.rosterforge.algorithms.scoring.AvailabilityRosterScorer;
import com.rosterforge.algorithms.validation.ConstraintValidator;

import org.junit.jupiter.api.*;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RosterServiceTest {

    @Nested
    class ShiftServiceTests {

        private ShiftService service;

        @BeforeEach
        void setUp() throws Exception {
            File tmp = File.createTempFile("shifts", ".dat");
            tmp.deleteOnExit();
            IDao<ShiftDm> dao = new DaoFileImpl<>(tmp.getAbsolutePath(), ShiftDm::getId);
            service = new ShiftService(dao);
        }

        @Test
        void addAndRetrieve() {
            service.addShift(new ShiftDm(1, 0, "MORNING", Map.of("SHIFT_MANAGER", 1)));
            ShiftDm found = service.getShift(1);
            assertNotNull(found);
            assertEquals("MORNING", found.getShiftType());
        }

        @Test
        void getAllReturnsAllSaved() {
            service.addShift(new ShiftDm(1, 0, "MORNING", Map.of("SHIFT_MANAGER", 1)));
            service.addShift(new ShiftDm(2, 1, "EVENING", Map.of("SOC", 1)));
            assertEquals(2, service.getAllShifts().size());
        }

        @Test
        void removeDeletesShift() {
            service.addShift(new ShiftDm(1, 0, "MORNING", Map.of("SHIFT_MANAGER", 1)));
            service.removeShift(1);
            assertNull(service.getShift(1));
        }

        @Test
        void findByIdReturnsNullForMissing() {
            assertNull(service.getShift(99));
        }
    }

    @Nested
    class AvailabilityPreferenceServiceTests {

        private AvailabilityPreferenceService service;

        @BeforeEach
        void setUp() throws Exception {
            File tmp = File.createTempFile("preferences", ".dat");
            tmp.deleteOnExit();
            IDao<AvailabilityPreferenceDm> dao = new DaoFileImpl<>(
                    tmp.getAbsolutePath(), AvailabilityPreferenceDm::getId);
            service = new AvailabilityPreferenceService(dao);
        }

        @Test
        void addAndRetrieve() {
            service.addPreference(new AvailabilityPreferenceDm(1, 1, 10, "DARK_GREEN"));
            AvailabilityPreferenceDm found = service.getPreference(1);
            assertNotNull(found);
            assertEquals("DARK_GREEN", found.getAvailabilityLevel());
        }

        @Test
        void getAllReturnsAllSaved() {
            service.addPreference(new AvailabilityPreferenceDm(1, 1, 10, "DARK_GREEN"));
            service.addPreference(new AvailabilityPreferenceDm(2, 2, 10, "YELLOW"));
            assertEquals(2, service.getAllPreferences().size());
        }

        @Test
        void removeDeletesPreference() {
            service.addPreference(new AvailabilityPreferenceDm(1, 1, 10, "DARK_GREEN"));
            service.removePreference(1);
            assertNull(service.getPreference(1));
        }

        @Test
        void findByIdReturnsNullForMissing() {
            assertNull(service.getPreference(99));
        }
    }

    private static ConstraintValidator buildValidator() {
        return new ConstraintValidator(Set.of(
                new RedAvailabilityConstraint(),
                new ConsecutiveShiftConstraint(),
                new MaxShiftsPerWeekConstraint(),
                new SameShiftDuplicateAssignmentConstraint()
        ));
    }

    @Nested
    class EmployeeServiceTests {

        private EmployeeService service;

        @BeforeEach
        void setUp() throws Exception {
            File tmp = File.createTempFile("employees", ".dat");
            tmp.deleteOnExit();
            IDao<EmployeeDm> dao = new DaoFileImpl<>(tmp.getAbsolutePath(), EmployeeDm::getId);
            service = new EmployeeService(dao);
        }

        @Test
        void addAndRetrieve() {
            service.addEmployee(new EmployeeDm(1, "Shahar", List.of("SHIFT_MANAGER"), 3));
            EmployeeDm found = service.getEmployee(1);
            assertNotNull(found);
            assertEquals("Shahar", found.getName());
        }

        @Test
        void getAllReturnsAllSaved() {
            service.addEmployee(new EmployeeDm(1, "Shahar", List.of("SHIFT_MANAGER"), 3));
            service.addEmployee(new EmployeeDm(2, "Noam", List.of("SOC"), 3));
            assertEquals(2, service.getAllEmployees().size());
        }

        @Test
        void removeDeletesEmployee() {
            service.addEmployee(new EmployeeDm(1, "Shahar", List.of("SHIFT_MANAGER"), 3));
            service.removeEmployee(1);
            assertNull(service.getEmployee(1));
        }

        @Test
        void updateOverwritesEmployee() {
            service.addEmployee(new EmployeeDm(1, "Shahar", List.of("SHIFT_MANAGER"), 2));
            service.updateEmployee(new EmployeeDm(1, "Shahar Updated", List.of("SHIFT_MANAGER", "SOC"), 3));
            assertEquals("Shahar Updated", service.getEmployee(1).getName());
        }

        @Test
        void findByIdReturnsNullForMissing() {
            assertNull(service.getEmployee(99));
        }
    }

    @Nested
    class RosterServiceTests {

        private IDao<EmployeeDm> empDao;
        private IDao<ShiftDm> shiftDao;
        private IDao<AvailabilityPreferenceDm> prefDao;

        @BeforeEach
        void setUp() throws Exception {
            File empFile   = File.createTempFile("emp",   ".dat"); empFile.deleteOnExit();
            File shiftFile = File.createTempFile("shift", ".dat"); shiftFile.deleteOnExit();
            File prefFile  = File.createTempFile("pref",  ".dat"); prefFile.deleteOnExit();

            empDao   = new DaoFileImpl<>(empFile.getAbsolutePath(),   EmployeeDm::getId);
            shiftDao = new DaoFileImpl<>(shiftFile.getAbsolutePath(), ShiftDm::getId);
            prefDao  = new DaoFileImpl<>(prefFile.getAbsolutePath(),  AvailabilityPreferenceDm::getId);

            empDao.save(new EmployeeDm(1, "Shahar", List.of("SHIFT_MANAGER"), 2));
            empDao.save(new EmployeeDm(2, "Dana",   List.of("SOC"),           2));
            empDao.save(new EmployeeDm(3, "Amit",   List.of("SECURITY"),      2));

            Map<String, Integer> reqs = Map.of("SHIFT_MANAGER", 1, "SOC", 1, "SECURITY", 1);
            shiftDao.save(new ShiftDm(10, 0, "MORNING", reqs));
            shiftDao.save(new ShiftDm(11, 1, "MORNING", reqs));

            prefDao.save(new AvailabilityPreferenceDm(1,  1, 10, "DARK_GREEN"));
            prefDao.save(new AvailabilityPreferenceDm(2,  1, 11, "DARK_GREEN"));
            prefDao.save(new AvailabilityPreferenceDm(3,  2, 10, "DARK_GREEN"));
            prefDao.save(new AvailabilityPreferenceDm(4,  2, 11, "DARK_GREEN"));
            prefDao.save(new AvailabilityPreferenceDm(5,  3, 10, "DARK_GREEN"));
            prefDao.save(new AvailabilityPreferenceDm(6,  3, 11, "DARK_GREEN"));
        }

        private RosterService buildService(IAlgoMaxFlow algo) {
            return new RosterService(empDao, shiftDao, prefDao, algo);
        }

        @Test
        void edmondsKarpFillsAllSlots() {
            IAlgoMaxFlow algo = new EdmondsKarpAlgoMaxFlow(buildValidator(), new AvailabilityRosterScorer());
            RosterResult result = buildService(algo).generateRoster();
            assertEquals(6, result.getRoster().getAssignments().size());
        }

        @Test
        void edmondsKarpHasZeroHardViolations() {
            IAlgoMaxFlow algo = new EdmondsKarpAlgoMaxFlow(buildValidator(), new AvailabilityRosterScorer());
            RosterResult result = buildService(algo).generateRoster();
            long hard = result.getViolations().stream().filter(v -> v.isHardViolation()).count();
            assertEquals(0, hard);
        }

        @Test
        void fordFulkersonFillsAllSlots() {
            IAlgoMaxFlow algo = new FordFulkersonAlgoMaxFlow(buildValidator(), new AvailabilityRosterScorer());
            RosterResult result = buildService(algo).generateRoster();
            assertEquals(6, result.getRoster().getAssignments().size());
        }

        @Test
        void swappingAlgorithmDoesNotChangeServiceCode() {
            IAlgoMaxFlow ek = new EdmondsKarpAlgoMaxFlow(buildValidator(), new AvailabilityRosterScorer());
            IAlgoMaxFlow ff = new FordFulkersonAlgoMaxFlow(buildValidator(), new AvailabilityRosterScorer());

            RosterResult ekResult = buildService(ek).generateRoster();
            RosterResult ffResult = buildService(ff).generateRoster();

            assertEquals(ekResult.getRoster().getAssignments().size(),
                         ffResult.getRoster().getAssignments().size());
        }
    }
}
