package com.hit.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hit.dm.ShiftDm;
import com.hit.server.Request;
import com.hit.server.Response;
import com.hit.service.ShiftService;

import java.util.List;

public class ShiftController implements IController {

    private final ShiftService shiftService;
    private final Gson gson;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
        this.gson = new Gson();
    }

    @Override
    public Response handleRequest(Request request) {
        String action = request.getAction();

        return switch (action) {
            case "shift/save"   -> save(request);
            case "shift/get"    -> get(request);
            case "shift/getAll" -> getAll();
            case "shift/delete" -> delete(request);
            default -> Response.error("Unknown shift action: " + action);
        };
    }

    private Response save(Request request) {
        ShiftDm shift = gson.fromJson(request.getBody(), ShiftDm.class);
        shiftService.addShift(shift);
        JsonObject body = new JsonObject();
        body.addProperty("message", "Shift saved successfully");
        return Response.success(body);
    }

    private Response get(Request request) {
        long id = request.getBody().get("id").getAsLong();
        ShiftDm shift = shiftService.getShift(id);
        if (shift == null) return Response.error("Shift not found: " + id);
        return Response.success(gson.toJsonTree(shift).getAsJsonObject());
    }

    private Response getAll() {
        List<ShiftDm> shifts = shiftService.getAllShifts();
        JsonObject body = new JsonObject();
        body.add("shifts", gson.toJsonTree(shifts));
        return Response.success(body);
    }

    private Response delete(Request request) {
        long id = request.getBody().get("id").getAsLong();
        shiftService.removeShift(id);
        JsonObject body = new JsonObject();
        body.addProperty("message", "Shift deleted successfully");
        return Response.success(body);
    }
}
