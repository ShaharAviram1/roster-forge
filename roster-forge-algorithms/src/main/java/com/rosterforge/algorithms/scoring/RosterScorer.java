package com.rosterforge.algorithms.scoring;

import com.rosterforge.algorithms.models.Roster;
import com.rosterforge.algorithms.models.SchedulingInput;

public interface RosterScorer {
    double scoreRoster(
            SchedulingInput schedulingInput,
            Roster roster
    );
}
