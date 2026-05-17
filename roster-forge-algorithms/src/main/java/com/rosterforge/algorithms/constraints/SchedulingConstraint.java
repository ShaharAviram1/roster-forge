package com.rosterforge.algorithms.constraints;

import com.rosterforge.algorithms.models.Assignment;
import com.rosterforge.algorithms.models.ConstraintViolation;
import com.rosterforge.algorithms.models.Roster;
import com.rosterforge.algorithms.models.SchedulingInput;

import java.util.Optional;

public interface SchedulingConstraint {
    Optional<ConstraintViolation> validate(SchedulingInput schedulingInput, Roster currentRoster, Assignment candidateAssignment);
}
