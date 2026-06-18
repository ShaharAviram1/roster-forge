package com.hit.server;

import com.google.gson.JsonObject;

import java.util.Map;

public class Request {

    private Map<String, String> headers;
    private JsonObject body;

    public Request(Map<String, String> headers, JsonObject body) {
        this.headers = headers;
        this.body = body;
    }

    public String getAction() {
        return headers.get("action");
    }

    public JsonObject getBody() {
        return body;
    }
}
