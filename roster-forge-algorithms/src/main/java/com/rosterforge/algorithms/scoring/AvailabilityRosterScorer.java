package com.rosterforge.algorithms.scoring;

import com.rosterforge.algorithms.models.*;

public class AvailabilityRosterScorer implements RosterScorer {
    @Override
    public double scoreRoster(SchedulingInput schedulingInput, Roster roster) {
        double requiredAssignments = countRequiredAssignments(schedulingInput);
        double actualAssignments = roster.getAssignments().size();

        if (requiredAssignments == 0) {
            return 100.0;
        }

        if (actualAssignments < requiredAssignments) {
            double completionRatio = actualAssignments / requiredAssignments;
            return completionRatio * 49.0;
        }

        double rawAvailabilityScore = 0.0;

        for (Assignment assignment : roster.getAssignments()) {
            for (AvailabilityPreference availabilityPreference : schedulingInput.getAvailabilityPreferences()) {
                boolean sameEmployee = assignment.getEmployee().equals(availabilityPreference.getEmployee());
                boolean sameShift = assignment.getShift().equals(availabilityPreference.getShift());

                if (sameEmployee && sameShift) {
                    rawAvailabilityScore += scoreAvailabilityLevel(
                            availabilityPreference.getAvailabilityLevel()
                    );
                }
            }
        }

        return normalizeAvailabilityScore(rawAvailabilityScore, actualAssignments);
    }

    public double countRequiredAssignments(SchedulingInput input) {
        double requiredAssignments = 0.0;

        for (Shift shift : input.getShifts()) {
            for (ShiftRequirement requirement : shift.getRequirements()) {
                requiredAssignments += requirement.getRequiredCount();
            }
        }

        return requiredAssignments;
    }

    public double scoreAvailabilityLevel(AvailabilityLevel level) {
        return switch (level) {
            case DARK_GREEN -> 1.0;
            case LIGHT_GREEN -> 0.66;
            case YELLOW -> 0.0;
            case RED -> 0.0;
        };
    }

    public double normalizeAvailabilityScore(double rawScore, double assignmentCount) {
        if (assignmentCount == 0) {
            return 0.0;
        }

        double averageAvailabilityScore = rawScore / assignmentCount;
        return 50.0 + (averageAvailabilityScore * 50.0);
    }
}
