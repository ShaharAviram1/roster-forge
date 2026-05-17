package com.rosterforge.algorithms.validation;

import com.rosterforge.algorithms.constraints.SchedulingConstraint;
import com.rosterforge.algorithms.models.Assignment;
import com.rosterforge.algorithms.models.ConstraintViolation;
import com.rosterforge.algorithms.models.Roster;
import com.rosterforge.algorithms.models.SchedulingInput;

import java.util.HashSet;
import java.util.Set;
import java.util.Optional;

public class ConstraintValidator {
    private Set<SchedulingConstraint> constraints;

    public ConstraintValidator(Set<SchedulingConstraint> constraints) {
        this.constraints = constraints;
    }

    public Set<ConstraintViolation> validateAssignment(SchedulingInput schedulingInput, Roster currentRoster, Assignment candidateAssignment) {
        Set<ConstraintViolation> violations = new HashSet<>();

        for (SchedulingConstraint constraint : constraints) {
            Optional<ConstraintViolation> violation = constraint.validate(schedulingInput, currentRoster, candidateAssignment);

            if (violation.isPresent()) {
                violations.add(violation.get());
            }
        }

        return violations;
    }
}
