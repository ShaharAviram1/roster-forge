package com.rosterforge.algorithms.engine;

import com.rosterforge.algorithms.models.*;
import com.rosterforge.algorithms.scoring.RosterScorer;
import com.rosterforge.algorithms.validation.ConstraintValidator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BacktrackingSchedulingAlgorithm implements SchedulingAlgorithm {
    private final ConstraintValidator constraintValidator;
    private final RosterScorer rosterScorer;
    public BacktrackingSchedulingAlgorithm(ConstraintValidator constraintValidator, RosterScorer rosterScorer) {
        this.constraintValidator = constraintValidator;
        this.rosterScorer = rosterScorer;
    }

    @Override
    public RosterResult generateRoster(SchedulingInput input) {
        Roster roster = new Roster(new HashSet<>());
        Set<ConstraintViolation> violations = new HashSet<>();
        List<ScheduleSlot> slots = new ArrayList<>();
        for (Shift shift : input.getShifts()) {
            for (ShiftRequirement requirement : shift.getRequirements()) {
                for (int i = 0; i < requirement.getRequiredCount(); i++) {
                    slots.add(
                            new ScheduleSlot(
                                    shift,
                                    requirement.getRole()
                            )
                    );
                }
            }
        }
        boolean success = backtrack(
          0,
          slots,
          input,
          roster
        );
        if (!success) {
            violations.add(
              new ConstraintViolation(
                      "Unable to generate valid roster",
                      true
              )
            );
        }
        double score = rosterScorer.scoreRoster(input, roster);

        return new RosterResult(
                roster,
                score,
                violations
        );
    }

    private boolean backtrack(int slotIndex, List<ScheduleSlot> slots, SchedulingInput input, Roster roster) {
        if (slotIndex >= slots.size()) {
            return true;
        }
        ScheduleSlot currentSlot = slots.get(slotIndex);

        for (Employee employee : input.getEmployees()) {
            if (!employee.getQualifiedRoles().contains(currentSlot.getRole())) {
                continue;
            }

            Assignment assignment = new Assignment(
              employee,
              currentSlot.getShift(),
              currentSlot.getRole()
            );

            Set<ConstraintViolation> violations = constraintValidator.validateAssignment(input, roster, assignment);

            if (!violations.isEmpty()) {
                continue;
            }

            roster.addAssignment(assignment);

            boolean solved = backtrack(
                    slotIndex + 1,
                    slots,
                    input,
                    roster
            );

            if (solved) {
                return true;
            }

            roster.getAssignments().remove(assignment);
        }
        return false;
    }
}
