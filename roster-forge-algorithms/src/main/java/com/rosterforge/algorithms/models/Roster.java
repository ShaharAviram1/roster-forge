package com.rosterforge.algorithms.models;

import java.util.Set;

public class Roster {
    private Set<Assignment> assignments;

    public Roster(Set<Assignment> assignments) {
        this.assignments = assignments;
    }

    public Set<Assignment> getAssignments() {
        return assignments;
    }
    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
    }
}
