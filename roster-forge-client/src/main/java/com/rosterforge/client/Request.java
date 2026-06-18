package com.rosterforge.client;

import com.google.gson.JsonObject;

public class Request {

    private final String action;
    private final JsonObject body;

    public Request(String action, JsonObject body) {
        this.action = action;
        this.body = body;
    }

    public String getAction() { return action; }
    public JsonObject getBody() { return body; }
}
