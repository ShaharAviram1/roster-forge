package com.rosterforge.controller;

import com.rosterforge.server.Request;
import com.rosterforge.server.Response;

public interface IController {
    Response handleRequest(Request request);
}
