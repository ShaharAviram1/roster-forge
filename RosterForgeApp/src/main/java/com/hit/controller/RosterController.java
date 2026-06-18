package com.hit.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hit.server.Request;
import com.hit.server.Response;
import com.hit.service.RosterService;

import com.rosterforge.algorithms.models.Assignment;
import com.rosterforge.algorithms.models.ConstraintViolation;
import com.rosterforge.algorithms.models.RosterResult;

public class RosterController implements IController {

    private final RosterService rosterService;
    private final Gson gson;

    public RosterController(RosterService rosterService) {
        this.rosterService = rosterService;
        this.gson = new Gson();
    }

    @Override
    public Response handleRequest(Request request) {
        return switch (request.getAction()) {
            case "roster/generate" -> generate();
            default -> Response.error("Unknown roster action: " + request.getAction());
        };
    }

    private Response generate() {
        RosterResult result = rosterService.generateRoster();

        JsonObject body = new JsonObject();
        body.addProperty("score", result.getScore());
        body.add("assignments", gson.toJsonTree(
                result.getRoster().getAssignments().stream()
                        .map(this::assignmentToJson)
                        .toList()
        ));
        body.add("violations", gson.toJsonTree(
                result.getViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .toList()
        ));

        return Response.success(body);
    }

    private JsonObject assignmentToJson(Assignment a) {
        JsonObject obj = new JsonObject();
        obj.addProperty("employeeId",   a.getEmployee().getId());
        obj.addProperty("employeeName", a.getEmployee().getName());
        obj.addProperty("role",         a.getRole().name());
        obj.addProperty("day",          a.getShift().getDayIndex());
        obj.addProperty("shiftType",    a.getShift().getShiftType().name());
        return obj;
    }
}
