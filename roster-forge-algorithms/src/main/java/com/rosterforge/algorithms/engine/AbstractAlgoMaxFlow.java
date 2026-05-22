package com.rosterforge.algorithms.engine;

import com.rosterforge.algorithms.models.*;
import com.rosterforge.algorithms.scoring.RosterScorer;
import com.rosterforge.algorithms.validation.ConstraintValidator;

import java.util.*;

public abstract class AbstractAlgoMaxFlow implements IAlgoMaxFlow {

    protected final ConstraintValidator constraintValidator;
    protected final RosterScorer rosterScorer;

    protected AbstractAlgoMaxFlow(ConstraintValidator constraintValidator, RosterScorer rosterScorer) {
        this.constraintValidator = constraintValidator;
        this.rosterScorer = rosterScorer;
    }

    @Override
    public final RosterResult generateRoster(SchedulingInput input) {
        List<Employee> employees = sortedEmployees(input);
        List<ScheduleSlot> slots = buildSlots(input);
        Map<String, AvailabilityLevel> availability = buildAvailabilityMap(input);

        int n = employees.size();
        int m = slots.size();
        int totalNodes = 1 + n + m + 1;

        // blocked[i][j]: employee i is permanently barred from slot j after a constraint violation
        boolean[][] blocked = new boolean[n][m];

        while (true) {
            int[][] capacity = buildCapacityMatrix(employees, slots, availability, blocked, n, m, totalNodes);
            int[][] flow = computeMaxFlow(capacity, totalNodes);

            List<int[]> flowEdges = collectFlowEdges(flow, n, m, employees, slots);

            Roster progressiveRoster = new Roster(new HashSet<>());
            Set<Integer> filledSlotIndices = new HashSet<>();
            boolean foundViolation = false;

            for (int[] edge : flowEdges) {
                int empIdx  = edge[0];
                int slotIdx = edge[1];

                Assignment candidate = new Assignment(
                        employees.get(empIdx),
                        slots.get(slotIdx).getShift(),
                        slots.get(slotIdx).getRole()
                );

                Set<ConstraintViolation> violations =
                        constraintValidator.validateAssignment(input, progressiveRoster, candidate);

                if (violations.isEmpty()) {
                    progressiveRoster.addAssignment(candidate);
                    filledSlotIndices.add(slotIdx);
                } else if (useIterativeConstraintEnforcement()) {
                    blocked[empIdx][slotIdx] = true;
                    foundViolation = true;
                    break;
                }
            }

            if (!foundViolation) {
                Set<ConstraintViolation> unfilledViolations = buildUnfilledViolations(slots, filledSlotIndices);
                double score = rosterScorer.scoreRoster(input, progressiveRoster);
                return new RosterResult(progressiveRoster, score, unfilledViolations);
            }
        }
    }

    protected abstract int[][] computeMaxFlow(int[][] capacity, int totalNodes);

    protected boolean useIterativeConstraintEnforcement() {
        return true;
    }

    private int[][] buildCapacityMatrix(
            List<Employee> employees,
            List<ScheduleSlot> slots,
            Map<String, AvailabilityLevel> availability,
            boolean[][] blocked,
            int n, int m, int totalNodes) {

        int source = 0;
        int sink = totalNodes - 1;
        int[][] capacity = new int[totalNodes][totalNodes];

        for (int i = 0; i < n; i++) {
            capacity[source][1 + i] = employees.get(i).getMaxShiftsPerWeek();
        }

        for (int i = 0; i < n; i++) {
            Employee emp = employees.get(i);
            for (int j = 0; j < m; j++) {
                if (blocked[i][j]) continue;
                ScheduleSlot slot = slots.get(j);
                if (!emp.getQualifiedRoles().contains(slot.getRole())) continue;
                AvailabilityLevel level = availability.getOrDefault(
                        availabilityKey(emp.getId(), slot.getShift()), AvailabilityLevel.YELLOW);
                if (level != AvailabilityLevel.RED) {
                    capacity[1 + i][1 + n + j] = 1;
                }
            }
        }

        for (int j = 0; j < m; j++) {
            capacity[1 + n + j][sink] = 1;
        }

        return capacity;
    }

    private List<int[]> collectFlowEdges(
            int[][] flow, int n, int m,
            List<Employee> employees, List<ScheduleSlot> slots) {

        List<int[]> edges = new ArrayList<>();
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                if (flow[1 + i][1 + n + j] == 1)
                    edges.add(new int[]{i, j});

        edges.sort((a, b) -> {
            ScheduleSlot sa = slots.get(a[1]);
            ScheduleSlot sb = slots.get(b[1]);
            int cmp = Integer.compare(sa.getShift().getDayIndex(), sb.getShift().getDayIndex());
            if (cmp != 0) return cmp;
            cmp = Integer.compare(sa.getShift().getShiftType().getOrder(), sb.getShift().getShiftType().getOrder());
            if (cmp != 0) return cmp;
            cmp = sa.getRole().name().compareTo(sb.getRole().name());
            if (cmp != 0) return cmp;
            return Long.compare(employees.get(a[0]).getId(), employees.get(b[0]).getId());
        });

        return edges;
    }

    private Set<ConstraintViolation> buildUnfilledViolations(
            List<ScheduleSlot> slots, Set<Integer> filledSlotIndices) {

        Set<ConstraintViolation> violations = new HashSet<>();
        for (int j = 0; j < slots.size(); j++) {
            if (!filledSlotIndices.contains(j)) {
                ScheduleSlot slot = slots.get(j);
                violations.add(new ConstraintViolation(
                        "Unable to fill slot for role " + slot.getRole()
                                + " on day " + slot.getShift().getDayIndex()
                                + " " + slot.getShift().getShiftType(),
                        true
                ));
            }
        }
        return violations;
    }

    private List<Employee> sortedEmployees(SchedulingInput input) {
        List<Employee> list = new ArrayList<>(input.getEmployees());
        list.sort(Comparator.comparingLong(Employee::getId));
        return list;
    }

    private List<ScheduleSlot> buildSlots(SchedulingInput input) {
        List<ScheduleSlot> slots = new ArrayList<>();
        List<Shift> sortedShifts = new ArrayList<>(input.getShifts());
        sortedShifts.sort(Comparator
                .comparingInt(Shift::getDayIndex)
                .thenComparingInt(s -> s.getShiftType().getOrder()));

        for (Shift shift : sortedShifts) {
            List<ShiftRequirement> sortedReqs = new ArrayList<>(shift.getRequirements());
            sortedReqs.sort(Comparator.comparing(r -> r.getRole().name()));
            for (ShiftRequirement req : sortedReqs) {
                for (int k = 0; k < req.getRequiredCount(); k++) {
                    slots.add(new ScheduleSlot(shift, req.getRole()));
                }
            }
        }
        return slots;
    }

    private Map<String, AvailabilityLevel> buildAvailabilityMap(SchedulingInput input) {
        Map<String, AvailabilityLevel> map = new HashMap<>();
        for (AvailabilityPreference pref : input.getAvailabilityPreferences()) {
            map.put(availabilityKey(pref.getEmployee().getId(), pref.getShift()),
                    pref.getAvailabilityLevel());
        }
        return map;
    }

    private String availabilityKey(long employeeId, Shift shift) {
        return employeeId + "," + shift.getDayIndex() + "," + shift.getShiftType().name();
    }
}
