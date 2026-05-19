package com.rosterforge.algorithms.constraints;

import com.rosterforge.algorithms.models.Assignment;
import com.rosterforge.algorithms.models.ConstraintViolation;
import com.rosterforge.algorithms.models.Roster;
import com.rosterforge.algorithms.models.SchedulingInput;

import java.util.Optional;

public class SameShiftDuplicateAssignmentConstraint implements SchedulingConstraint {
    @Override
    public Optional<ConstraintViolation> validate(SchedulingInput schedulingInput, Roster currentRoster, Assignment candidateAssignment) {
        for (Assignment existingAssignment : currentRoster.getAssignments()) {
            boolean sameEmployee = existingAssignment.getEmployee().equals(candidateAssignment.getEmployee());
            boolean sameShift = existingAssignment.getShift().equals(candidateAssignment.getShift());

            if (sameEmployee && sameShift) {
                return Optional.of(
                        new ConstraintViolation(
                                "Employee is already assigned to this shift",
                                true
                        )
                );
            }
        }
        return Optional.empty();
    }
}
