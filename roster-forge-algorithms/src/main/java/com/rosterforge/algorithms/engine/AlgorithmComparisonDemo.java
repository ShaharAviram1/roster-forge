package com.rosterforge.algorithms.engine;

import com.rosterforge.algorithms.constraints.*;
import com.rosterforge.algorithms.models.*;
import com.rosterforge.algorithms.scoring.*;
import com.rosterforge.algorithms.validation.ConstraintValidator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AlgorithmComparisonDemo {

    public static void main(String[] args) {
        Employee shahar = new Employee(1, "Shahar", linkedSet(Role.SHIFT_MANAGER), 2);
        Employee noam = new Employee(2, "Noam", linkedSet(Role.SHIFT_MANAGER), 2);
        Employee lior = new Employee(3, "Lior", linkedSet(Role.SHIFT_MANAGER), 2);

        Employee dana = new Employee(4, "Dana", linkedSet(Role.SOC), 2);
        Employee maya = new Employee(5, "Maya", linkedSet(Role.SOC), 2);
        Employee ron = new Employee(6, "Ron", linkedSet(Role.SOC), 2);

        Employee amit = new Employee(7, "Amit", linkedSet(Role.SECURITY), 2);
        Employee eyal = new Employee(8, "Eyal", linkedSet(Role.SECURITY), 2);
        Employee gal = new Employee(9, "Gal", linkedSet(Role.SECURITY), 2);

        ShiftRequirement managerRequirement = new ShiftRequirement(Role.SHIFT_MANAGER, 1);
        ShiftRequirement socRequirement = new ShiftRequirement(Role.SOC, 1);
        ShiftRequirement securityRequirement = new ShiftRequirement(Role.SECURITY, 1);

        Set<ShiftRequirement> fullShiftRequirements = linkedSet(
                managerRequirement,
                socRequirement,
                securityRequirement
        );

        Shift day0Morning = new Shift(fullShiftRequirements, ShiftType.MORNING, 0);
        Shift day0Night = new Shift(fullShiftRequirements, ShiftType.NIGHT, 0);
        Shift day1Morning = new Shift(fullShiftRequirements, ShiftType.MORNING, 1);
        Shift day1Night = new Shift(fullShiftRequirements, ShiftType.NIGHT, 1);
        Shift day2Morning = new Shift(fullShiftRequirements, ShiftType.MORNING, 2);
        Shift day2Night = new Shift(fullShiftRequirements, ShiftType.NIGHT, 2);

        Set<Shift> shifts = linkedSet(
                day0Morning,
                day0Night,
                day1Morning,
                day1Night,
                day2Morning,
                day2Night
        );

        Set<Employee> employees = linkedSet(
                shahar,
                noam,
                lior,
                dana,
                maya,
                ron,
                amit,
                eyal,
                gal
        );

        Set<AvailabilityPreference> preferences = new LinkedHashSet<>();

        addPreferences(preferences, shahar, shifts, linkedSet(day0Morning, day1Night));
        addPreferences(preferences, noam, shifts, linkedSet(day0Night, day2Morning));
        addPreferences(preferences, lior, shifts, linkedSet(day1Morning, day2Night));

        addPreferences(preferences, dana, shifts, linkedSet(day0Morning, day1Night));
        addPreferences(preferences, maya, shifts, linkedSet(day0Night, day2Morning));
        addPreferences(preferences, ron, shifts, linkedSet(day1Morning, day2Night));

        addPreferences(preferences, amit, shifts, linkedSet(day0Morning, day1Night));
        addPreferences(preferences, eyal, shifts, linkedSet(day0Night, day2Morning));
        addPreferences(preferences, gal, shifts, linkedSet(day1Morning, day2Night));

        SchedulingInput input = new SchedulingInput(
                employees,
                shifts,
                preferences
        );

        Set<SchedulingConstraint> constraints = linkedSet(
                new RedAvailabilityConstraint(),
                new ConsecutiveShiftConstraint(),
                new MaxShiftsPerWeekConstraint(),
                new SameShiftDuplicateAssignmentConstraint()
        );

        ConstraintValidator validator = new ConstraintValidator(constraints);
        RosterScorer scorer = new AvailabilityRosterScorer();

        GreedySchedulingAlgorithm greedyAlgorithm = new GreedySchedulingAlgorithm(validator, scorer);
        BacktrackingSchedulingAlgorithm backtrackingAlgorithm = new BacktrackingSchedulingAlgorithm(validator, scorer);

        printExpectedRoster();
        printResult("Greedy", greedyAlgorithm.generateRoster(input));
        printResult("Backtracking", backtrackingAlgorithm.generateRoster(input));
    }

    private static void addPreferences(
            Set<AvailabilityPreference> preferences,
            Employee employee,
            Set<Shift> allShifts,
            Set<Shift> preferredShifts
    ) {
        for (Shift shift : allShifts) {
            AvailabilityLevel availabilityLevel = AvailabilityLevel.RED;

            if (preferredShifts.contains(shift)) {
                availabilityLevel = AvailabilityLevel.DARK_GREEN;
            }

            preferences.add(
                    new AvailabilityPreference(
                            employee,
                            shift,
                            availabilityLevel
                    )
            );
        }
    }

    private static void printExpectedRoster() {
        System.out.println("EXPECTED FEASIBLE ROSTER");
        System.out.println("Day 0 MORNING: Shahar -> SHIFT_MANAGER, Dana -> SOC, Amit -> SECURITY");
        System.out.println("Day 0 NIGHT: Noam -> SHIFT_MANAGER, Maya -> SOC, Eyal -> SECURITY");
        System.out.println("Day 1 MORNING: Lior -> SHIFT_MANAGER, Ron -> SOC, Gal -> SECURITY");
        System.out.println("Day 1 NIGHT: Shahar -> SHIFT_MANAGER, Dana -> SOC, Amit -> SECURITY");
        System.out.println("Day 2 MORNING: Noam -> SHIFT_MANAGER, Maya -> SOC, Eyal -> SECURITY");
        System.out.println("Day 2 NIGHT: Lior -> SHIFT_MANAGER, Ron -> SOC, Gal -> SECURITY");
        System.out.println();
    }

    private static void printResult(String algorithmName, RosterResult result) {
        List<Assignment> assignments = new ArrayList<>(result.getRoster().getAssignments());
        assignments.sort(
                Comparator.comparingInt((Assignment assignment) -> assignment.getShift().getDayIndex())
                        .thenComparingInt(assignment -> assignment.getShift().getShiftType().getOrder())
                        .thenComparing(assignment -> assignment.getRole().name())
                        .thenComparing(assignment -> assignment.getEmployee().getName())
        );

        System.out.println(algorithmName.toUpperCase() + " RESULT");
        System.out.println("Assignments: " + assignments.size());
        System.out.println("Score: " + result.getScore());
        System.out.println("Violations: " + result.getViolations().size());

        for (Assignment assignment : assignments) {
            System.out.println(
                    "Day "
                            + assignment.getShift().getDayIndex()
                            + " "
                            + assignment.getShift().getShiftType()
                            + ": "
                            + assignment.getEmployee().getName()
                            + " -> "
                            + assignment.getRole()
            );
        }

        for (ConstraintViolation violation : result.getViolations()) {
            System.out.println("Violation: " + violation.getMessage());
        }

        System.out.println();
    }

    @SafeVarargs
    private static <T> Set<T> linkedSet(T... values) {
        Set<T> set = new LinkedHashSet<>();

        for (T value : values) {
            set.add(value);
        }

        return set;
    }
}
