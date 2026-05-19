package com.rosterforge.algorithms.engine;

import com.rosterforge.algorithms.constraints.*;
import com.rosterforge.algorithms.models.*;
import com.rosterforge.algorithms.scoring.*;
import com.rosterforge.algorithms.validation.*;

import java.util.Set;

public class GreedySchedulingDemo {

    public static void main(String[] args) {
        Employee shahar = new Employee(
                1,
                "Shahar",
                Set.of(Role.SOC, Role.SHIFT_MANAGER),
                3
        );

        Employee dana = new Employee(
                2,
                "Dana",
                Set.of(Role.SOC),
                2
        );

        Employee amit = new Employee(
                3,
                "Amit",
                Set.of(Role.SECURITY),
                3
        );

        Employee noam = new Employee(
                4,
                "Noam",
                Set.of(Role.SECURITY, Role.SHIFT_MANAGER),
                2
        );

        Employee maya = new Employee(
                5,
                "Maya",
                Set.of(Role.SOC, Role.SECURITY),
                3
        );

        ShiftRequirement socRequirement = new ShiftRequirement(
                Role.SOC,
                1
        );

        ShiftRequirement securityRequirement = new ShiftRequirement(
                Role.SECURITY,
                1
        );

        ShiftRequirement managerRequirement = new ShiftRequirement(
                Role.SHIFT_MANAGER,
                1
        );

        Shift sundayMorning = new Shift(
                Set.of(socRequirement, securityRequirement, managerRequirement),
                ShiftType.MORNING,
                0
        );

        Shift sundayEvening = new Shift(
                Set.of(socRequirement, securityRequirement, managerRequirement),
                ShiftType.EVENING,
                0
        );

        Shift sundayNight = new Shift(
                Set.of(socRequirement, securityRequirement),
                ShiftType.NIGHT,
                0
        );

        Shift mondayMorning = new Shift(
                Set.of(socRequirement, securityRequirement, managerRequirement),
                ShiftType.MORNING,
                1
        );

        Set<AvailabilityPreference> preferences = Set.of(
                new AvailabilityPreference(shahar, sundayMorning, AvailabilityLevel.DARK_GREEN),
                new AvailabilityPreference(shahar, sundayEvening, AvailabilityLevel.RED),
                new AvailabilityPreference(shahar, sundayNight, AvailabilityLevel.LIGHT_GREEN),
                new AvailabilityPreference(shahar, mondayMorning, AvailabilityLevel.DARK_GREEN),

                new AvailabilityPreference(dana, sundayMorning, AvailabilityLevel.YELLOW),
                new AvailabilityPreference(dana, sundayEvening, AvailabilityLevel.DARK_GREEN),
                new AvailabilityPreference(dana, sundayNight, AvailabilityLevel.RED),
                new AvailabilityPreference(dana, mondayMorning, AvailabilityLevel.LIGHT_GREEN),

                new AvailabilityPreference(amit, sundayMorning, AvailabilityLevel.LIGHT_GREEN),
                new AvailabilityPreference(amit, sundayEvening, AvailabilityLevel.DARK_GREEN),
                new AvailabilityPreference(amit, sundayNight, AvailabilityLevel.YELLOW),
                new AvailabilityPreference(amit, mondayMorning, AvailabilityLevel.RED),

                new AvailabilityPreference(noam, sundayMorning, AvailabilityLevel.DARK_GREEN),
                new AvailabilityPreference(noam, sundayEvening, AvailabilityLevel.LIGHT_GREEN),
                new AvailabilityPreference(noam, sundayNight, AvailabilityLevel.RED),
                new AvailabilityPreference(noam, mondayMorning, AvailabilityLevel.DARK_GREEN),

                new AvailabilityPreference(maya, sundayMorning, AvailabilityLevel.LIGHT_GREEN),
                new AvailabilityPreference(maya, sundayEvening, AvailabilityLevel.YELLOW),
                new AvailabilityPreference(maya, sundayNight, AvailabilityLevel.DARK_GREEN),
                new AvailabilityPreference(maya, mondayMorning, AvailabilityLevel.LIGHT_GREEN)
        );

        SchedulingInput input = new SchedulingInput(
                Set.of(shahar, dana, amit, noam, maya),
                Set.of(sundayMorning, sundayEvening, sundayNight, mondayMorning),
                preferences
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

        System.out.println("Generated assignments:");
        for (Assignment assignment : result.getRoster().getAssignments()) {
            System.out.println(
                    assignment.getEmployee().getName()
                            + " -> "
                            + assignment.getRole()
                            + " on day "
                            + assignment.getShift().getDayIndex()
                            + " "
                            + assignment.getShift().getShiftType()
            );
        }

        System.out.println("Score: " + result.getScore());
        System.out.println("Violations found: " + result.getViolations().size());

        for (ConstraintViolation violation : result.getViolations()) {
            System.out.println("Violation: " + violation.getMessage());
        }
    }
}
