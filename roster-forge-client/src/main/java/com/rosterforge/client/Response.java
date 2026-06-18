package com.rosterforge.client;

import com.google.gson.JsonObject;

public class Response {

    private String status;
    private JsonObject body;

    private Response() {}

    public static Response error(String message) {
        Response r = new Response();
        r.status = "error";
        JsonObject body = new JsonObject();
        body.addProperty("message", message);
        r.body = body;
        return r;
    }

    public boolean isSuccess() { return "success".equals(status); }

    public String getMessage() {
        if (body != null && body.has("message")) return body.get("message").getAsString();
        return status;
    }

    public JsonObject getBody() { return body; }
}
