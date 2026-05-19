package com.rosterforge.algorithms.constraints;

import com.rosterforge.algorithms.models.*;

import java.util.Optional;

public class RedAvailabilityConstraint implements SchedulingConstraint {

    @Override
    public Optional<ConstraintViolation> validate(SchedulingInput schedulingInput, Roster currentRoster, Assignment candidateAssignment) {
        for (AvailabilityPreference availabilityPreference : schedulingInput.getAvailabilityPreferences()) {
            boolean sameEmployee = availabilityPreference.getEmployee().equals(candidateAssignment.getEmployee());
            boolean sameShift = availabilityPreference.getShift().equals(candidateAssignment.getShift());
            boolean isRed = availabilityPreference.getAvailabilityLevel() == AvailabilityLevel.RED;

            if (sameEmployee && sameShift && isRed) {
                return Optional.of(new ConstraintViolation("Employee is marked RED for this shift", true));
            }
        }

        return Optional.empty();
    }
}
