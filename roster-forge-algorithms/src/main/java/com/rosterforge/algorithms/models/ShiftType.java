package com.rosterforge.algorithms.models;

public enum ShiftType {
    MORNING(0),
    EVENING(1),
    NIGHT(2);

    private final int order;

    ShiftType(int order) {
        this.order = order;
    }

    public int getOrder(){
        return order;
    }
}
