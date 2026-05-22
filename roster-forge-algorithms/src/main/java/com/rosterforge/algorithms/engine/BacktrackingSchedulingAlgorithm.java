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
    private Roster bestRoster;
    private double bestScore;
    private double targetBestScore;
    public BacktrackingSchedulingAlgorithm(ConstraintValidator constraintValidator, RosterScorer rosterScorer) {
        this.constraintValidator = constraintValidator;
        this.rosterScorer = rosterScorer;
    }

    @Override
    public RosterResult generateRoster(SchedulingInput input) {
        Roster roster = new Roster(new HashSet<>());
        bestRoster = null;
        bestScore = Double.NEGATIVE_INFINITY;
        targetBestScore = Double.POSITIVE_INFINITY;
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
        targetBestScore = calculateMaximumPossibleScore(input, slots);
        backtrack(
                0,
                slots,
                input,
                roster
        );

        if (bestRoster == null) {
            violations.add(
                    new ConstraintViolation(
                            "Unable to generate valid roster",
                            true
                    )
            );

            return new RosterResult(
                    roster,
                    0.0,
                    violations
            );
        }

        return new RosterResult(
                bestRoster,
                bestScore,
                violations
        );
    }

    private boolean backtrack(int slotIndex, List<ScheduleSlot> slots, SchedulingInput input, Roster roster) {
        if (slotIndex >= slots.size()) {
            double score = rosterScorer.scoreRoster(input, roster);

            if (score > bestScore) {
                bestScore = score;
                bestRoster = new Roster(new HashSet<>(roster.getAssignments()));
            }

            return bestScore >= targetBestScore;
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

            boolean shouldStopSearch = backtrack(
                    slotIndex + 1,
                    slots,
                    input,
                    roster
            );

            roster.getAssignments().remove(assignment);

            if (shouldStopSearch) {
                return true;
            }
        }
        return false;
    }

    private double calculateMaximumPossibleScore(SchedulingInput input, List<ScheduleSlot> slots) {
        double maximumPossibleScore = 0.0;

        for (ScheduleSlot slot : slots) {
            double bestSlotScore = Double.NEGATIVE_INFINITY;

            for (Employee employee : input.getEmployees()) {
                if (!employee.getQualifiedRoles().contains(slot.getRole())) {
                    continue;
                }

                Assignment assignment = new Assignment(
                        employee,
                        slot.getShift(),
                        slot.getRole()
                );

                Set<Assignment> assignments = new HashSet<>();
                assignments.add(assignment);
                Roster singleAssignmentRoster = new Roster(assignments);

                double score = rosterScorer.scoreRoster(input, singleAssignmentRoster);

                if (score > bestSlotScore) {
                    bestSlotScore = score;
                }
            }

            if (bestSlotScore != Double.NEGATIVE_INFINITY) {
                maximumPossibleScore += bestSlotScore;
            }
        }

        return maximumPossibleScore;
    }
}
