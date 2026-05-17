package com.rosterforge.algorithms.engine;

import com.rosterforge.algorithms.models.RosterResult;
import com.rosterforge.algorithms.models.SchedulingInput;

public interface SchedulingAlgorithm {
    RosterResult generateRoster(SchedulingInput input);
}
