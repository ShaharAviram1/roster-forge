package com.rosterforge.algorithms.engine;

import com.rosterforge.algorithms.constraints.*;
import com.rosterforge.algorithms.models.*;
import com.rosterforge.algorithms.validation.ConstraintValidator;

import java.util.Set;

public class ConstraintValidatorDemo {
    public static void main(String[] args) {
        Employee employee = new Employee(
                1,
                "Shahar",
                Set.of(Role.SOC),
                5
        );
        ShiftRequirement shiftRequirement = new ShiftRequirement(
                Role.SOC,
                1
        );

        Shift shift = new Shift(
                Set.of(shiftRequirement),
                ShiftType.MORNING,
                0
        );
        AvailabilityPreference availabilityPreference = new AvailabilityPreference(
                employee,
                shift,
                AvailabilityLevel.RED
        );

        Assignment assignment = new Assignment(
                employee,
                shift,
                Role.SOC
        );
        SchedulingInput schedulingInput = new SchedulingInput(
                Set.of(employee),
                Set.of(shift),
                Set.of(availabilityPreference)
        );

        Roster roster = new Roster(Set.of());
        Set<SchedulingConstraint> constraints = Set.of(
                new RedAvailabilityConstraint()
        );

        ConstraintValidator validator = new ConstraintValidator(constraints);
        Set<ConstraintViolation> violations = validator.validateAssignment(schedulingInput,roster,assignment);
        for(ConstraintViolation violation : violations){
            System.out.println("Violations found: " + violation.getMessage());
        }
    }
}
