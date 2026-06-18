package com.rosterforge.client;

import com.google.gson.JsonObject;

public class Response {

    private boolean success;
    private String message;
    private JsonObject body;

    private Response() {}

    public static Response error(String message) {
        Response r = new Response();
        r.success = false;
        r.message = message;
        return r;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public JsonObject getBody() { return body; }
}
