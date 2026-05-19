package com.rosterforge.algorithms.models;

import java.util.Set;

public class RosterResult {
    private Roster roster;
    private double score;
    private Set<ConstraintViolation> violations;

    public RosterResult(Roster roster, double score, Set<ConstraintViolation> violations) {
        this.roster = roster;
        this.score = score;
        this.violations = violations;
    }

    public Roster getRoster() {
        return roster;
    }

    public double getScore() {
        return score;
    }

    public Set<ConstraintViolation> getViolations() {
        return violations;
    }
}
