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
}
