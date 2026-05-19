package com.rosterforge.algorithms.constraints;

import com.rosterforge.algorithms.models.*;

import java.util.Optional;

public class ConsecutiveShiftConstraint implements SchedulingConstraint {
    @Override
    public Optional<ConstraintViolation> validate(SchedulingInput schedulingInput, Roster currentRoster, Assignment candidateAssignment) {
        for (Assignment existingAssignment : currentRoster.getAssignments()) {
            boolean sameEmployee = existingAssignment.getEmployee().equals(candidateAssignment.getEmployee());
            int existingDay = existingAssignment.getShift().getDayIndex();
            int candidateDay = candidateAssignment.getShift().getDayIndex();
            int existingOrder = existingAssignment.getShift().getShiftType().getOrder();
            int candidateOrder = candidateAssignment.getShift().getShiftType().getOrder();

            boolean sameDay = existingDay == candidateDay;
            boolean consecutiveSameDay = Math.abs(existingOrder - candidateOrder) == 1;
            boolean nightToMorning = existingOrder == ShiftType.NIGHT.getOrder() && candidateOrder == ShiftType.MORNING.getOrder() && candidateDay == existingDay + 1;

            if (sameEmployee && ((sameDay && consecutiveSameDay) || nightToMorning)){
                return Optional.of(
                        new ConstraintViolation(
                                "Employee cannot work consecutive shifts",
                                true
                        )
                );
            }
        }
        return Optional.empty();
    }
}
