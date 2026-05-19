package com.rosterforge.algorithms.engine;

import com.rosterforge.algorithms.models.*;
import com.rosterforge.algorithms.scoring.RosterScorer;
import com.rosterforge.algorithms.validation.ConstraintValidator;

import java.util.*;

public class GreedySchedulingAlgorithm implements SchedulingAlgorithm {
    private final ConstraintValidator constraintValidator;
    private final RosterScorer rosterScorer;

    public GreedySchedulingAlgorithm(ConstraintValidator constraintValidator, RosterScorer rosterScorer) {
        this.constraintValidator = constraintValidator;
        this.rosterScorer = rosterScorer;
    }

    @Override
    public RosterResult generateRoster(SchedulingInput input) {
        Roster roster = new Roster(new HashSet<>());
        Set<ConstraintViolation> allViolations = new HashSet<>();

        List<Shift> sortedShifts = new ArrayList<>(input.getShifts());
        sortedShifts.sort(
                Comparator.comparingInt(Shift::getDayIndex)
                        .thenComparingInt(shift -> shift.getShiftType().getOrder())
        );

        for (Shift shift : sortedShifts) {
            List<ShiftRequirement> sortedRequirements = new ArrayList<>(shift.getRequirements());
            sortedRequirements.sort(Comparator.comparingInt(requirement -> countQualifiedEmployees(input, requirement.getRole())));
            for (ShiftRequirement requirement : sortedRequirements) {
                for (int slot = 0; slot < requirement.getRequiredCount(); slot++) {
                    Assignment bestAssignment = null;
                    double bestScore = Double.NEGATIVE_INFINITY;

                    for (Employee employee : input.getEmployees()) {
                        if (!employee.getQualifiedRoles().contains(requirement.getRole())) {
                            continue;
                        }

                        Assignment candidateAssignment = new Assignment(
                                employee,
                                shift,
                                requirement.getRole()
                        );

                        Set<ConstraintViolation> violations =
                                constraintValidator.validateAssignment(input, roster, candidateAssignment);

                        if (!violations.isEmpty()) {
                            continue;
                        }

                        Set<Assignment> temporaryAssignments = new HashSet<>(roster.getAssignments());
                        temporaryAssignments.add(candidateAssignment);
                        Roster temporaryRoster = new Roster(temporaryAssignments);

                        double candidateScore = rosterScorer.scoreRoster(input, temporaryRoster);

                        if (candidateScore > bestScore) {
                            bestScore = candidateScore;
                            bestAssignment = candidateAssignment;
                        }
                    }

                    if (bestAssignment != null) {
                        roster.addAssignment(bestAssignment);
                    } else {
                        allViolations.add(
                                new ConstraintViolation(
                                        "Unable to fill requirement for role "
                                                + requirement.getRole()
                                                + " on day "
                                                + shift.getDayIndex()
                                                + " "
                                                + shift.getShiftType(),
                                        true
                                )
                        );
                    }
                }
            }
        }

        double finalScore = rosterScorer.scoreRoster(input, roster);

        return new RosterResult(
                roster,
                finalScore,
                allViolations
        );
    }

    private int countQualifiedEmployees(SchedulingInput input, Role role) {
        int count = 0;

        for (Employee employee : input.getEmployees()) {
            if (employee.getQualifiedRoles().contains(role)) {
                count++;
            }
        }

        return count;
    }
}