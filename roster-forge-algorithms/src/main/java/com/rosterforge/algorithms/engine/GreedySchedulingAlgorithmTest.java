package com.rosterforge.algorithms.engine;

import com.rosterforge.algorithms.constraints.*;
import com.rosterforge.algorithms.models.*;
import com.rosterforge.algorithms.scoring.AvailabilityRosterScorer;
import com.rosterforge.algorithms.scoring.RosterScorer;
import com.rosterforge.algorithms.validation.ConstraintValidator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GreedySchedulingAlgorithmTest {

    @Test
    void shouldPreferDarkGreenQualifiedEmployee() {
        Employee shahar = new Employee(
                1,
                "Shahar",
                Set.of(Role.SOC),
                5
        );

        Employee dana = new Employee(
                2,
                "Dana",
                Set.of(Role.SOC),
                5
        );

        Employee amit = new Employee(
                3,
                "Amit",
                Set.of(Role.SECURITY),
                5
        );
        ShiftRequirement socRequirement = new ShiftRequirement(
                Role.SOC,
                1
        );

        Shift shift = new Shift(
                Set.of(socRequirement),
                ShiftType.MORNING,
                0
        );
        AvailabilityPreference shaharPreference = new AvailabilityPreference(
                shahar,
                shift,
                AvailabilityLevel.DARK_GREEN
        );

        AvailabilityPreference danaPreference = new AvailabilityPreference(
                dana,
                shift,
                AvailabilityLevel.YELLOW
        );

        AvailabilityPreference amitPreference = new AvailabilityPreference(
                amit,
                shift,
                AvailabilityLevel.DARK_GREEN
        );
        SchedulingInput input = new SchedulingInput(
                Set.of(shahar, dana, amit),
                Set.of(shift),
                Set.of(
                        shaharPreference,
                        danaPreference,
                        amitPreference
                )
        );
        Set<SchedulingConstraint> constraints = Set.of(
                new RedAvailabilityConstraint(),
                new ConsecutiveShiftConstraint(),
                new MaxShiftsPerWeekConstraint(),
                new SameShiftDuplicateAssignmentConstraint()
        );
        ConstraintValidator validator = new ConstraintValidator(constraints);
        RosterScorer scorer = new AvailabilityRosterScorer();
        GreedySchedulingAlgorithm algorithm = new GreedySchedulingAlgorithm(validator, scorer);

        RosterResult result = algorithm.generateRoster(input);
        Assignment assignment = result.getRoster().getAssignments().iterator().next();

        assertEquals(shahar, assignment.getEmployee());
        assertEquals(Role.SOC, assignment.getRole());
        assertEquals(10.0, result.getScore());
        assertTrue(result.getViolations().isEmpty());
    }

    @Test
    void shouldRejectRedAvailabilityAssignment() {
        Employee shahar = new Employee(
                1,
                "Shahar",
                Set.of(Role.SOC),
                5
        );

        ShiftRequirement socRequirement = new ShiftRequirement(
                Role.SOC,
                1
        );

        Shift shift = new Shift(
                Set.of(socRequirement),
                ShiftType.MORNING,
                0
        );

        AvailabilityPreference shaharPreference = new AvailabilityPreference(
                shahar,
                shift,
                AvailabilityLevel.RED
        );

        SchedulingInput input = new SchedulingInput(
                Set.of(shahar),
                Set.of(shift),
                Set.of(shaharPreference)
        );

        Set<SchedulingConstraint> constraints = Set.of(
                new RedAvailabilityConstraint(),
                new ConsecutiveShiftConstraint(),
                new MaxShiftsPerWeekConstraint(),
                new SameShiftDuplicateAssignmentConstraint()
        );

        ConstraintValidator validator = new ConstraintValidator(constraints);
        RosterScorer scorer = new AvailabilityRosterScorer();
        GreedySchedulingAlgorithm algorithm = new GreedySchedulingAlgorithm(validator, scorer);

        RosterResult result = algorithm.generateRoster(input);

        assertTrue(result.getRoster().getAssignments().isEmpty());
        assertEquals(0.0, result.getScore());
        assertEquals(1, result.getViolations().size());
    }

    @Test
    void shouldPreventDuplicateAssignmentInSameShift() {
        Employee shahar = new Employee(
                1,
                "Shahar",
                Set.of(Role.SOC, Role.SECURITY),
                5
        );

        ShiftRequirement socRequirement = new ShiftRequirement(
                Role.SOC,
                1
        );

        ShiftRequirement securityRequirement = new ShiftRequirement(
                Role.SECURITY,
                1
        );

        Shift shift = new Shift(
                Set.of(socRequirement, securityRequirement),
                ShiftType.MORNING,
                0
        );

        AvailabilityPreference shaharPreference = new AvailabilityPreference(
                shahar,
                shift,
                AvailabilityLevel.DARK_GREEN
        );

        SchedulingInput input = new SchedulingInput(
                Set.of(shahar),
                Set.of(shift),
                Set.of(shaharPreference)
        );

        Set<SchedulingConstraint> constraints = Set.of(
                new RedAvailabilityConstraint(),
                new ConsecutiveShiftConstraint(),
                new MaxShiftsPerWeekConstraint(),
                new SameShiftDuplicateAssignmentConstraint()
        );

        ConstraintValidator validator = new ConstraintValidator(constraints);
        RosterScorer scorer = new AvailabilityRosterScorer();
        GreedySchedulingAlgorithm algorithm = new GreedySchedulingAlgorithm(validator, scorer);

        RosterResult result = algorithm.generateRoster(input);

        assertEquals(1, result.getRoster().getAssignments().size());
        assertEquals(1, result.getViolations().size());
    }

    @Test
    void shouldRespectMaxShiftsPerWeek() {
        Employee shahar = new Employee(
                1,
                "Shahar",
                Set.of(Role.SOC),
                1
        );

        ShiftRequirement socRequirement = new ShiftRequirement(
                Role.SOC,
                1
        );

        Shift sundayMorning = new Shift(
                Set.of(socRequirement),
                ShiftType.MORNING,
                0
        );

        Shift sundayNight = new Shift(
                Set.of(socRequirement),
                ShiftType.NIGHT,
                0
        );

        AvailabilityPreference sundayMorningPreference = new AvailabilityPreference(
                shahar,
                sundayMorning,
                AvailabilityLevel.DARK_GREEN
        );

        AvailabilityPreference sundayNightPreference = new AvailabilityPreference(
                shahar,
                sundayNight,
                AvailabilityLevel.DARK_GREEN
        );

        SchedulingInput input = new SchedulingInput(
                Set.of(shahar),
                Set.of(sundayMorning, sundayNight),
                Set.of(sundayMorningPreference, sundayNightPreference)
        );

        Set<SchedulingConstraint> constraints = Set.of(
                new RedAvailabilityConstraint(),
                new ConsecutiveShiftConstraint(),
                new MaxShiftsPerWeekConstraint(),
                new SameShiftDuplicateAssignmentConstraint()
        );

        ConstraintValidator validator = new ConstraintValidator(constraints);
        RosterScorer scorer = new AvailabilityRosterScorer();
        GreedySchedulingAlgorithm algorithm = new GreedySchedulingAlgorithm(validator, scorer);

        RosterResult result = algorithm.generateRoster(input);

        assertEquals(1, result.getRoster().getAssignments().size());
        assertEquals(1, result.getViolations().size());
        assertEquals(10.0, result.getScore());
    }

    @Test
    void shouldPreventConsecutiveShifts() {
        Employee shahar = new Employee(
                1,
                "Shahar",
                Set.of(Role.SOC),
                5
        );

        ShiftRequirement socRequirement = new ShiftRequirement(
                Role.SOC,
                1
        );

        Shift sundayMorning = new Shift(
                Set.of(socRequirement),
                ShiftType.MORNING,
                0
        );

        Shift sundayEvening = new Shift(
                Set.of(socRequirement),
                ShiftType.EVENING,
                0
        );

        AvailabilityPreference sundayMorningPreference = new AvailabilityPreference(
                shahar,
                sundayMorning,
                AvailabilityLevel.DARK_GREEN
        );

        AvailabilityPreference sundayEveningPreference = new AvailabilityPreference(
                shahar,
                sundayEvening,
                AvailabilityLevel.DARK_GREEN
        );

        SchedulingInput input = new SchedulingInput(
                Set.of(shahar),
                Set.of(sundayMorning, sundayEvening),
                Set.of(sundayMorningPreference, sundayEveningPreference)
        );

        Set<SchedulingConstraint> constraints = Set.of(
                new RedAvailabilityConstraint(),
                new ConsecutiveShiftConstraint(),
                new MaxShiftsPerWeekConstraint(),
                new SameShiftDuplicateAssignmentConstraint()
        );

        ConstraintValidator validator = new ConstraintValidator(constraints);
        RosterScorer scorer = new AvailabilityRosterScorer();
        GreedySchedulingAlgorithm algorithm = new GreedySchedulingAlgorithm(validator, scorer);

        RosterResult result = algorithm.generateRoster(input);

        assertEquals(1, result.getRoster().getAssignments().size());
        assertEquals(1, result.getViolations().size());
        assertEquals(10.0, result.getScore());
    }

    @Test
    void shouldPrioritizeScarceRolesFirst() {
        Employee shahar = new Employee(
                1,
                "Shahar",
                Set.of(Role.SECURITY, Role.SHIFT_MANAGER),
                5
        );

        Employee amit = new Employee(
                2,
                "Amit",
                Set.of(Role.SECURITY),
                5
        );

        ShiftRequirement securityRequirement = new ShiftRequirement(
                Role.SECURITY,
                1
        );

        ShiftRequirement managerRequirement = new ShiftRequirement(
                Role.SHIFT_MANAGER,
                1
        );

        Shift shift = new Shift(
                Set.of(securityRequirement, managerRequirement),
                ShiftType.MORNING,
                0
        );

        AvailabilityPreference shaharPreference = new AvailabilityPreference(
                shahar,
                shift,
                AvailabilityLevel.DARK_GREEN
        );

        AvailabilityPreference amitPreference = new AvailabilityPreference(
                amit,
                shift,
                AvailabilityLevel.LIGHT_GREEN
        );

        SchedulingInput input = new SchedulingInput(
                Set.of(shahar, amit),
                Set.of(shift),
                Set.of(shaharPreference, amitPreference)
        );

        Set<SchedulingConstraint> constraints = Set.of(
                new RedAvailabilityConstraint(),
                new ConsecutiveShiftConstraint(),
                new MaxShiftsPerWeekConstraint(),
                new SameShiftDuplicateAssignmentConstraint()
        );

        ConstraintValidator validator = new ConstraintValidator(constraints);
        RosterScorer scorer = new AvailabilityRosterScorer();
        GreedySchedulingAlgorithm algorithm = new GreedySchedulingAlgorithm(validator, scorer);

        RosterResult result = algorithm.generateRoster(input);

        assertEquals(2, result.getRoster().getAssignments().size());
        assertTrue(result.getViolations().isEmpty());
        assertEquals(15.0, result.getScore());

        assertTrue(
                result.getRoster().getAssignments().stream().anyMatch(
                        assignment -> assignment.getEmployee().equals(shahar)
                                && assignment.getRole() == Role.SHIFT_MANAGER
                )
        );

        assertTrue(
                result.getRoster().getAssignments().stream().anyMatch(
                        assignment -> assignment.getEmployee().equals(amit)
                                && assignment.getRole() == Role.SECURITY
                )
        );
    }
}
