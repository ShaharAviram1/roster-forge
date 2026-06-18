package com.rosterforge.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rosterforge.dm.AvailabilityPreferenceDm;
import com.rosterforge.server.Request;
import com.rosterforge.server.Response;
import com.rosterforge.service.AvailabilityPreferenceService;

import java.util.List;

public class PreferenceController implements IController {

    private final AvailabilityPreferenceService preferenceService;
    private final Gson gson;

    public PreferenceController(AvailabilityPreferenceService preferenceService) {
        this.preferenceService = preferenceService;
        this.gson = new Gson();
    }

    @Override
    public Response handleRequest(Request request) {
        String action = request.getAction();

        return switch (action) {
            case "preference/save"   -> save(request);
            case "preference/get"    -> get(request);
            case "preference/getAll" -> getAll();
            case "preference/delete" -> delete(request);
            default -> Response.error("Unknown preference action: " + action);
        };
    }

    private Response save(Request request) {
        AvailabilityPreferenceDm preference = gson.fromJson(request.getBody(), AvailabilityPreferenceDm.class);
        preferenceService.addPreference(preference);
        JsonObject body = new JsonObject();
        body.addProperty("message", "Preference saved successfully");
        return Response.success(body);
    }

    private Response get(Request request) {
        long id = request.getBody().get("id").getAsLong();
        AvailabilityPreferenceDm preference = preferenceService.getPreference(id);
        if (preference == null) return Response.error("Preference not found: " + id);
        return Response.success(gson.toJsonTree(preference).getAsJsonObject());
    }

    private Response getAll() {
        List<AvailabilityPreferenceDm> preferences = preferenceService.getAllPreferences();
        JsonObject body = new JsonObject();
        body.add("preferences", gson.toJsonTree(preferences));
        return Response.success(body);
    }

    private Response delete(Request request) {
        long id = request.getBody().get("id").getAsLong();
        preferenceService.removePreference(id);
        JsonObject body = new JsonObject();
        body.addProperty("message", "Preference deleted successfully");
        return Response.success(body);
    }
}
