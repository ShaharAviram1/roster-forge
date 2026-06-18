package com.rosterforge.client;

import com.google.gson.JsonObject;

import java.util.Map;

public class Request {

    private final Map<String, String> headers;
    private final JsonObject body;

    public Request(String action, JsonObject body) {
        this.headers = Map.of("action", action);
        this.body = body;
    }

    public String getAction() { return headers.get("action"); }
    public JsonObject getBody() { return body; }
}
