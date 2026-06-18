package com.hit.server;

import com.google.gson.JsonObject;

public class Response {

    private String status;
    private JsonObject body;

    private Response(String status, JsonObject body) {
        this.status = status;
        this.body = body;
    }

    public static Response success(JsonObject body) {
        return new Response("success", body);
    }

    public static Response error(String message) {
        JsonObject body = new JsonObject();
        body.addProperty("message", message);
        return new Response("error", body);
    }

    public String getStatus() { return status; }
    public JsonObject getBody() { return body; }
}
