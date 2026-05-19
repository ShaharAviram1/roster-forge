package com.rosterforge.algorithms.scoring;

import com.rosterforge.algorithms.models.*;

public class AvailabilityRosterScorer implements RosterScorer {
    @Override
    public double scoreRoster(SchedulingInput schedulingInput, Roster roster) {
        double score = 0;
        for (Assignment assignment : roster.getAssignments()) {
            for (AvailabilityPreference availabilityPreference : schedulingInput.getAvailabilityPreferences()) {
                boolean sameEmployee = assignment.getEmployee().equals(availabilityPreference.getEmployee());
                boolean sameShift = assignment.getShift().equals(availabilityPreference.getShift());

                if (sameEmployee && sameShift) {
                    AvailabilityLevel availabilityLevel = availabilityPreference.getAvailabilityLevel();
                    switch (availabilityLevel) {
                        case DARK_GREEN -> score += 10;
                        case LIGHT_GREEN -> score += 5;
                        case YELLOW -> score -= 5;
                        case RED -> score -= 100;
                    }
                }
            }
        }
        return score;
    }
}
