package com.rosterforge.algorithms.constraints;

import com.rosterforge.algorithms.models.*;

import java.util.Optional;

public class MaxShiftsPerWeekConstraint implements SchedulingConstraint {
    @Override
    public Optional<ConstraintViolation> validate(
            SchedulingInput schedulingInput,
            Roster currentRoster,
            Assignment candidateAssignment
    ) {
        int currentShiftCount = 0;

        for (Assignment existingAssignment : currentRoster.getAssignments()) {
            boolean sameEmployee =
                    existingAssignment.getEmployee().equals(candidateAssignment.getEmployee());

            if (sameEmployee) {
                currentShiftCount++;
            }
        }

        boolean exceedsMaxShifts =
                currentShiftCount + 1 >
                candidateAssignment
                        .getEmployee()
                        .getMaxShiftsPerWeek();

        if (exceedsMaxShifts) {
            return Optional.of(
                    new ConstraintViolation(
                            "Employee exceeds maximum shifts per week",
                            true
                    )
            );
        }

        return Optional.empty();
    }
}
