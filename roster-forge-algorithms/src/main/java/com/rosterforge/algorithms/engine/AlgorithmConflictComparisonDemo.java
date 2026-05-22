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

public class AlgorithmConflictComparisonDemo {

    public static void main(String[] args) {
        Employee shahar = new Employee(1, "Shahar", linkedSet(Role.SHIFT_MANAGER, Role.SOC), 3);
        Employee noam = new Employee(2, "Noam", linkedSet(Role.SHIFT_MANAGER, Role.SECURITY), 3);
        Employee lior = new Employee(3, "Lior", linkedSet(Role.SHIFT_MANAGER), 3);

        Employee dana = new Employee(4, "Dana", linkedSet(Role.SOC, Role.SECURITY), 3);
        Employee maya = new Employee(5, "Maya", linkedSet(Role.SOC), 3);
        Employee ron = new Employee(6, "Ron", linkedSet(Role.SOC, Role.SECURITY), 3);

        Employee amit = new Employee(7, "Amit", linkedSet(Role.SECURITY), 3);
        Employee eyal = new Employee(8, "Eyal", linkedSet(Role.SECURITY, Role.SOC), 3);
        Employee gal = new Employee(9, "Gal", linkedSet(Role.SECURITY), 3);

        ShiftRequirement managerRequirement = new ShiftRequirement(Role.SHIFT_MANAGER, 1);
        ShiftRequirement socRequirement = new ShiftRequirement(Role.SOC, 1);
        ShiftRequirement securityRequirement = new ShiftRequirement(Role.SECURITY, 1);

        Set<ShiftRequirement> fullShiftRequirements = linkedSet(
                managerRequirement,
                socRequirement,
                securityRequirement
        );

        Shift day0Morning = new Shift(fullShiftRequirements, ShiftType.MORNING, 0);
        Shift day0Evening = new Shift(fullShiftRequirements, ShiftType.EVENING, 0);
        Shift day0Night = new Shift(fullShiftRequirements, ShiftType.NIGHT, 0);

        Shift day1Morning = new Shift(fullShiftRequirements, ShiftType.MORNING, 1);
        Shift day1Evening = new Shift(fullShiftRequirements, ShiftType.EVENING, 1);
        Shift day1Night = new Shift(fullShiftRequirements, ShiftType.NIGHT, 1);

        Shift day2Morning = new Shift(fullShiftRequirements, ShiftType.MORNING, 2);
        Shift day2Evening = new Shift(fullShiftRequirements, ShiftType.EVENING, 2);
        Shift day2Night = new Shift(fullShiftRequirements, ShiftType.NIGHT, 2);

        Set<Shift> shifts = linkedSet(
                day0Morning,
                day0Evening,
                day0Night,
                day1Morning,
                day1Evening,
                day1Night,
                day2Morning,
                day2Evening,
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
        initializeAllPreferencesAsRed(preferences, employees, shifts);

        setExpectedRosterPreferences(
                preferences,
                shahar,
                noam,
                lior,
                dana,
                maya,
                ron,
                amit,
                eyal,
                gal,
                day0Morning,
                day0Evening,
                day0Night,
                day1Morning,
                day1Evening,
                day1Night,
                day2Morning,
                day2Evening,
                day2Night
        );

        addConflictNoisePreferences(
                preferences,
                shahar,
                noam,
                lior,
                dana,
                maya,
                ron,
                amit,
                eyal,
                gal,
                day0Morning,
                day0Evening,
                day0Night,
                day1Morning,
                day1Evening,
                day1Night,
                day2Morning,
                day2Evening,
                day2Night
        );

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

        printScenarioExplanation();
        printExpectedRoster();
        printResult("Greedy", greedyAlgorithm.generateRoster(input));
        printResult("Backtracking", backtrackingAlgorithm.generateRoster(input));
    }

    private static void initializeAllPreferencesAsRed(
            Set<AvailabilityPreference> preferences,
            Set<Employee> employees,
            Set<Shift> shifts
    ) {
        for (Employee employee : employees) {
            for (Shift shift : shifts) {
                setPreference(preferences, employee, shift, AvailabilityLevel.RED);
            }
        }
    }

    private static void setExpectedRosterPreferences(
            Set<AvailabilityPreference> preferences,
            Employee shahar,
            Employee noam,
            Employee lior,
            Employee dana,
            Employee maya,
            Employee ron,
            Employee amit,
            Employee eyal,
            Employee gal,
            Shift day0Morning,
            Shift day0Evening,
            Shift day0Night,
            Shift day1Morning,
            Shift day1Evening,
            Shift day1Night,
            Shift day2Morning,
            Shift day2Evening,
            Shift day2Night
    ) {
        setPreference(preferences, shahar, day0Morning, AvailabilityLevel.LIGHT_GREEN);
        setPreference(preferences, dana, day0Morning, AvailabilityLevel.YELLOW);
        setPreference(preferences, amit, day0Morning, AvailabilityLevel.YELLOW);

        setPreference(preferences, noam, day0Evening, AvailabilityLevel.YELLOW);
        setPreference(preferences, maya, day0Evening, AvailabilityLevel.LIGHT_GREEN);
        setPreference(preferences, eyal, day0Evening, AvailabilityLevel.YELLOW);

        setPreference(preferences, lior, day0Night, AvailabilityLevel.YELLOW);
        setPreference(preferences, ron, day0Night, AvailabilityLevel.YELLOW);
        setPreference(preferences, gal, day0Night, AvailabilityLevel.LIGHT_GREEN);

        setPreference(preferences, shahar, day1Morning, AvailabilityLevel.YELLOW);
        setPreference(preferences, dana, day1Morning, AvailabilityLevel.LIGHT_GREEN);
        setPreference(preferences, amit, day1Morning, AvailabilityLevel.YELLOW);

        setPreference(preferences, noam, day1Evening, AvailabilityLevel.LIGHT_GREEN);
        setPreference(preferences, maya, day1Evening, AvailabilityLevel.YELLOW);
        setPreference(preferences, eyal, day1Evening, AvailabilityLevel.YELLOW);

        setPreference(preferences, lior, day1Night, AvailabilityLevel.YELLOW);
        setPreference(preferences, ron, day1Night, AvailabilityLevel.LIGHT_GREEN);
        setPreference(preferences, gal, day1Night, AvailabilityLevel.YELLOW);

        setPreference(preferences, shahar, day2Morning, AvailabilityLevel.YELLOW);
        setPreference(preferences, dana, day2Morning, AvailabilityLevel.YELLOW);
        setPreference(preferences, amit, day2Morning, AvailabilityLevel.LIGHT_GREEN);

        setPreference(preferences, noam, day2Evening, AvailabilityLevel.YELLOW);
        setPreference(preferences, maya, day2Evening, AvailabilityLevel.YELLOW);
        setPreference(preferences, eyal, day2Evening, AvailabilityLevel.LIGHT_GREEN);

        setPreference(preferences, lior, day2Night, AvailabilityLevel.LIGHT_GREEN);
        setPreference(preferences, ron, day2Night, AvailabilityLevel.YELLOW);
        setPreference(preferences, gal, day2Night, AvailabilityLevel.YELLOW);
    }

    private static void addConflictNoisePreferences(
            Set<AvailabilityPreference> preferences,
            Employee shahar,
            Employee noam,
            Employee lior,
            Employee dana,
            Employee maya,
            Employee ron,
            Employee amit,
            Employee eyal,
            Employee gal,
            Shift day0Morning,
            Shift day0Evening,
            Shift day0Night,
            Shift day1Morning,
            Shift day1Evening,
            Shift day1Night,
            Shift day2Morning,
            Shift day2Evening,
            Shift day2Night
    ) {
        setPreference(preferences, shahar, day0Evening, AvailabilityLevel.YELLOW);
        setPreference(preferences, shahar, day1Evening, AvailabilityLevel.YELLOW);
        setPreference(preferences, shahar, day2Evening, AvailabilityLevel.YELLOW);

        setPreference(preferences, noam, day0Morning, AvailabilityLevel.YELLOW);
        setPreference(preferences, noam, day1Morning, AvailabilityLevel.YELLOW);
        setPreference(preferences, noam, day2Morning, AvailabilityLevel.YELLOW);

        setPreference(preferences, lior, day0Evening, AvailabilityLevel.YELLOW);
        setPreference(preferences, lior, day1Evening, AvailabilityLevel.YELLOW);
        setPreference(preferences, lior, day2Evening, AvailabilityLevel.YELLOW);

        setPreference(preferences, dana, day0Evening, AvailabilityLevel.YELLOW);
        setPreference(preferences, dana, day1Evening, AvailabilityLevel.YELLOW);
        setPreference(preferences, dana, day2Evening, AvailabilityLevel.YELLOW);

        setPreference(preferences, ron, day0Morning, AvailabilityLevel.YELLOW);
        setPreference(preferences, ron, day1Morning, AvailabilityLevel.YELLOW);
        setPreference(preferences, ron, day2Morning, AvailabilityLevel.YELLOW);

        setPreference(preferences, eyal, day0Night, AvailabilityLevel.YELLOW);
        setPreference(preferences, eyal, day1Night, AvailabilityLevel.YELLOW);
        setPreference(preferences, eyal, day2Night, AvailabilityLevel.YELLOW);
    }

    private static void setPreference(
            Set<AvailabilityPreference> preferences,
            Employee employee,
            Shift shift,
            AvailabilityLevel availabilityLevel
    ) {
        AvailabilityPreference preference = new AvailabilityPreference(
                employee,
                shift,
                availabilityLevel
        );

        preferences.remove(preference);
        preferences.add(preference);
    }

    private static void printScenarioExplanation() {
        System.out.println("CONFLICT PRESSURE SCENARIO");
        System.out.println("3 days, 3 shifts per day, 3 required roles per shift = 27 assignments.");
        System.out.println("There are exactly 9 employees, each with max 3 shifts, so every employee must be used exactly 3 times.");
        System.out.println("The expected roster is feasible, but most assignments are only YELLOW or LIGHT_GREEN.");
        System.out.println("Most unavailable combinations are RED, with a small number of YELLOW decoys to create conflict without making the theoretical target impossible.");
        System.out.println("This scenario is intentionally much harder than the baseline perfect-preference demo.");
        System.out.println();
    }

    private static void printExpectedRoster() {
        System.out.println("EXPECTED NEAR-EDGE FEASIBLE ROSTER");
        System.out.println("Day 0 MORNING: Shahar -> SHIFT_MANAGER [LIGHT_GREEN], Dana -> SOC [YELLOW], Amit -> SECURITY [YELLOW]");
        System.out.println("Day 0 EVENING: Noam -> SHIFT_MANAGER [YELLOW], Maya -> SOC [LIGHT_GREEN], Eyal -> SECURITY [YELLOW]");
        System.out.println("Day 0 NIGHT: Lior -> SHIFT_MANAGER [YELLOW], Ron -> SOC [YELLOW], Gal -> SECURITY [LIGHT_GREEN]");
        System.out.println("Day 1 MORNING: Shahar -> SHIFT_MANAGER [YELLOW], Dana -> SOC [LIGHT_GREEN], Amit -> SECURITY [YELLOW]");
        System.out.println("Day 1 EVENING: Noam -> SHIFT_MANAGER [LIGHT_GREEN], Maya -> SOC [YELLOW], Eyal -> SECURITY [YELLOW]");
        System.out.println("Day 1 NIGHT: Lior -> SHIFT_MANAGER [YELLOW], Ron -> SOC [LIGHT_GREEN], Gal -> SECURITY [YELLOW]");
        System.out.println("Day 2 MORNING: Shahar -> SHIFT_MANAGER [YELLOW], Dana -> SOC [YELLOW], Amit -> SECURITY [LIGHT_GREEN]");
        System.out.println("Day 2 EVENING: Noam -> SHIFT_MANAGER [YELLOW], Maya -> SOC [YELLOW], Eyal -> SECURITY [LIGHT_GREEN]");
        System.out.println("Day 2 NIGHT: Lior -> SHIFT_MANAGER [LIGHT_GREEN], Ron -> SOC [YELLOW], Gal -> SECURITY [YELLOW]");
        System.out.println("No DARK_GREEN assignments are used in the intended roster.");
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
