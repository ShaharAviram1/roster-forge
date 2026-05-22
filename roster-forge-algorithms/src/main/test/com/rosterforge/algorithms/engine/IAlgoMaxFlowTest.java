package com.rosterforge.algorithms.engine;

import com.rosterforge.algorithms.constraints.*;
import com.rosterforge.algorithms.models.*;
import com.rosterforge.algorithms.scoring.AvailabilityRosterScorer;
import com.rosterforge.algorithms.scoring.RosterScorer;
import com.rosterforge.algorithms.validation.ConstraintValidator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class IAlgoMaxFlowTest {

    private ConstraintValidator validator() {
        return new ConstraintValidator(Set.of(
                new RedAvailabilityConstraint(),
                new ConsecutiveShiftConstraint(),
                new MaxShiftsPerWeekConstraint(),
                new SameShiftDuplicateAssignmentConstraint()
        ));
    }

    private RosterScorer scorer() {
        return new AvailabilityRosterScorer();
    }

    @Nested
    class FordFulkerson {

        private FordFulkersonAlgoMaxFlow algo() {
            return new FordFulkersonAlgoMaxFlow(validator(), scorer());
        }

        @Test
        void shouldFillSlotWhenOneQualifiedEmployeeIsAvailable() {
            Employee shahar = new Employee(1, "Shahar", Set.of(Role.SOC), 5);
            Shift shift = new Shift(Set.of(new ShiftRequirement(Role.SOC, 1)), ShiftType.MORNING, 0);

            SchedulingInput input = new SchedulingInput(
                    Set.of(shahar),
                    Set.of(shift),
                    Set.of(new AvailabilityPreference(shahar, shift, AvailabilityLevel.DARK_GREEN))
            );

            RosterResult result = algo().generateRoster(input);

            assertEquals(1, result.getRoster().getAssignments().size());
            assertTrue(result.getViolations().isEmpty());
            assertEquals(shahar, result.getRoster().getAssignments().iterator().next().getEmployee());
        }

        @Test
        void shouldReportViolationWhenNoEmployeeQualifiedForRole() {
            Employee shahar = new Employee(1, "Shahar", Set.of(Role.SECURITY), 5);
            Shift shift = new Shift(Set.of(new ShiftRequirement(Role.SOC, 1)), ShiftType.MORNING, 0);

            SchedulingInput input = new SchedulingInput(
                    Set.of(shahar),
                    Set.of(shift),
                    Set.of(new AvailabilityPreference(shahar, shift, AvailabilityLevel.DARK_GREEN))
            );

            RosterResult result = algo().generateRoster(input);

            assertTrue(result.getRoster().getAssignments().isEmpty());
            assertEquals(1, result.getViolations().size());
        }

        @Test
        void shouldNotAssignEmployeeWithRedAvailability() {
            Employee shahar = new Employee(1, "Shahar", Set.of(Role.SOC), 5);
            Shift shift = new Shift(Set.of(new ShiftRequirement(Role.SOC, 1)), ShiftType.MORNING, 0);

            SchedulingInput input = new SchedulingInput(
                    Set.of(shahar),
                    Set.of(shift),
                    Set.of(new AvailabilityPreference(shahar, shift, AvailabilityLevel.RED))
            );

            RosterResult result = algo().generateRoster(input);

            // RED removes the employee→slot edge from the graph entirely
            assertTrue(result.getRoster().getAssignments().isEmpty());
            assertEquals(1, result.getViolations().size());
        }

        @Test
        void shouldRespectMaxShiftsPerWeekCapacity() {
            Employee shahar = new Employee(1, "Shahar", Set.of(Role.SOC), 1);
            Shift morning = new Shift(Set.of(new ShiftRequirement(Role.SOC, 1)), ShiftType.MORNING, 0);
            Shift night   = new Shift(Set.of(new ShiftRequirement(Role.SOC, 1)), ShiftType.NIGHT,   0);

            SchedulingInput input = new SchedulingInput(
                    Set.of(shahar),
                    Set.of(morning, night),
                    Set.of(
                            new AvailabilityPreference(shahar, morning, AvailabilityLevel.DARK_GREEN),
                            new AvailabilityPreference(shahar, night,   AvailabilityLevel.DARK_GREEN)
                    )
            );

            RosterResult result = algo().generateRoster(input);

            assertEquals(1, result.getRoster().getAssignments().size());
            assertEquals(1, result.getViolations().size());
        }

        @Test
        void shouldFillAllSlotsWhenTwoEmployeesMatchTwoSlots() {
            Employee shahar = new Employee(1, "Shahar", Set.of(Role.SOC), 5);
            Employee dana   = new Employee(2, "Dana",   Set.of(Role.SOC), 5);
            Shift day0 = new Shift(Set.of(new ShiftRequirement(Role.SOC, 1)), ShiftType.MORNING, 0);
            Shift day1 = new Shift(Set.of(new ShiftRequirement(Role.SOC, 1)), ShiftType.MORNING, 1);

            SchedulingInput input = new SchedulingInput(
                    Set.of(shahar, dana),
                    Set.of(day0, day1),
                    Set.of(
                            new AvailabilityPreference(shahar, day0, AvailabilityLevel.DARK_GREEN),
                            new AvailabilityPreference(shahar, day1, AvailabilityLevel.DARK_GREEN),
                            new AvailabilityPreference(dana,   day0, AvailabilityLevel.DARK_GREEN),
                            new AvailabilityPreference(dana,   day1, AvailabilityLevel.DARK_GREEN)
                    )
            );

            RosterResult result = algo().generateRoster(input);

            assertEquals(2, result.getRoster().getAssignments().size());
            assertTrue(result.getViolations().isEmpty());
        }

        @Test
        void shouldPreferAvailableEmployeeOverUnavailable() {
            Employee shahar = new Employee(1, "Shahar", Set.of(Role.SOC), 5);
            Employee dana   = new Employee(2, "Dana",   Set.of(Role.SOC), 5);
            Shift shift = new Shift(Set.of(new ShiftRequirement(Role.SOC, 1)), ShiftType.MORNING, 0);

            SchedulingInput input = new SchedulingInput(
                    Set.of(shahar, dana),
                    Set.of(shift),
                    Set.of(
                            new AvailabilityPreference(shahar, shift, AvailabilityLevel.DARK_GREEN),
                            new AvailabilityPreference(dana,   shift, AvailabilityLevel.RED)
                    )
            );

            RosterResult result = algo().generateRoster(input);

            assertEquals(1, result.getRoster().getAssignments().size());
            assertTrue(result.getViolations().isEmpty());
            assertEquals(shahar, result.getRoster().getAssignments().iterator().next().getEmployee());
        }
    }

    @Nested
    class EdmondsKarp {

        private EdmondsKarpAlgoMaxFlow algo() {
            return new EdmondsKarpAlgoMaxFlow(validator(), scorer());
        }

        @Test
        void shouldFillSlotWhenOneQualifiedEmployeeIsAvailable() {
            Employee shahar = new Employee(1, "Shahar", Set.of(Role.SOC), 5);
            Shift shift = new Shift(Set.of(new ShiftRequirement(Role.SOC, 1)), ShiftType.MORNING, 0);

            SchedulingInput input = new SchedulingInput(
                    Set.of(shahar),
                    Set.of(shift),
                    Set.of(new AvailabilityPreference(shahar, shift, AvailabilityLevel.DARK_GREEN))
            );

            RosterResult result = algo().generateRoster(input);

            assertEquals(1, result.getRoster().getAssignments().size());
            assertTrue(result.getViolations().isEmpty());
            assertEquals(shahar, result.getRoster().getAssignments().iterator().next().getEmployee());
        }

        @Test
        void shouldReportViolationWhenNoEmployeeQualifiedForRole() {
            Employee shahar = new Employee(1, "Shahar", Set.of(Role.SECURITY), 5);
            Shift shift = new Shift(Set.of(new ShiftRequirement(Role.SOC, 1)), ShiftType.MORNING, 0);

            SchedulingInput input = new SchedulingInput(
                    Set.of(shahar),
                    Set.of(shift),
                    Set.of(new AvailabilityPreference(shahar, shift, AvailabilityLevel.DARK_GREEN))
            );

            RosterResult result = algo().generateRoster(input);

            assertTrue(result.getRoster().getAssignments().isEmpty());
            assertEquals(1, result.getViolations().size());
        }

        @Test
        void shouldNotAssignEmployeeWithRedAvailability() {
            Employee shahar = new Employee(1, "Shahar", Set.of(Role.SOC), 5);
            Shift shift = new Shift(Set.of(new ShiftRequirement(Role.SOC, 1)), ShiftType.MORNING, 0);

            SchedulingInput input = new SchedulingInput(
                    Set.of(shahar),
                    Set.of(shift),
                    Set.of(new AvailabilityPreference(shahar, shift, AvailabilityLevel.RED))
            );

            RosterResult result = algo().generateRoster(input);

            assertTrue(result.getRoster().getAssignments().isEmpty());
            assertEquals(1, result.getViolations().size());
        }

        @Test
        void shouldRespectMaxShiftsPerWeekCapacity() {
            Employee shahar = new Employee(1, "Shahar", Set.of(Role.SOC), 1);
            Shift morning = new Shift(Set.of(new ShiftRequirement(Role.SOC, 1)), ShiftType.MORNING, 0);
            Shift night   = new Shift(Set.of(new ShiftRequirement(Role.SOC, 1)), ShiftType.NIGHT,   0);

            SchedulingInput input = new SchedulingInput(
                    Set.of(shahar),
                    Set.of(morning, night),
                    Set.of(
                            new AvailabilityPreference(shahar, morning, AvailabilityLevel.DARK_GREEN),
                            new AvailabilityPreference(shahar, night,   AvailabilityLevel.DARK_GREEN)
                    )
            );

            RosterResult result = algo().generateRoster(input);

            assertEquals(1, result.getRoster().getAssignments().size());
            assertEquals(1, result.getViolations().size());
        }

        @Test
        void shouldFillAllSlotsWhenTwoEmployeesMatchTwoSlots() {
            Employee shahar = new Employee(1, "Shahar", Set.of(Role.SOC), 5);
            Employee dana   = new Employee(2, "Dana",   Set.of(Role.SOC), 5);
            Shift day0 = new Shift(Set.of(new ShiftRequirement(Role.SOC, 1)), ShiftType.MORNING, 0);
            Shift day1 = new Shift(Set.of(new ShiftRequirement(Role.SOC, 1)), ShiftType.MORNING, 1);

            SchedulingInput input = new SchedulingInput(
                    Set.of(shahar, dana),
                    Set.of(day0, day1),
                    Set.of(
                            new AvailabilityPreference(shahar, day0, AvailabilityLevel.DARK_GREEN),
                            new AvailabilityPreference(shahar, day1, AvailabilityLevel.DARK_GREEN),
                            new AvailabilityPreference(dana,   day0, AvailabilityLevel.DARK_GREEN),
                            new AvailabilityPreference(dana,   day1, AvailabilityLevel.DARK_GREEN)
                    )
            );

            RosterResult result = algo().generateRoster(input);

            assertEquals(2, result.getRoster().getAssignments().size());
            assertTrue(result.getViolations().isEmpty());
        }

        @Test
        void shouldPreferAvailableEmployeeOverUnavailable() {
            Employee shahar = new Employee(1, "Shahar", Set.of(Role.SOC), 5);
            Employee dana   = new Employee(2, "Dana",   Set.of(Role.SOC), 5);
            Shift shift = new Shift(Set.of(new ShiftRequirement(Role.SOC, 1)), ShiftType.MORNING, 0);

            SchedulingInput input = new SchedulingInput(
                    Set.of(shahar, dana),
                    Set.of(shift),
                    Set.of(
                            new AvailabilityPreference(shahar, shift, AvailabilityLevel.DARK_GREEN),
                            new AvailabilityPreference(dana,   shift, AvailabilityLevel.RED)
                    )
            );

            RosterResult result = algo().generateRoster(input);

            assertEquals(1, result.getRoster().getAssignments().size());
            assertTrue(result.getViolations().isEmpty());
            assertEquals(shahar, result.getRoster().getAssignments().iterator().next().getEmployee());
        }
    }
}
