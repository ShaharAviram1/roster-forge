package com.rosterforge.server;

import com.google.gson.Gson;
import com.rosterforge.controller.EmployeeController;
import com.rosterforge.controller.IController;
import com.rosterforge.controller.PreferenceController;
import com.rosterforge.controller.RosterController;
import com.rosterforge.controller.ShiftController;
import com.rosterforge.service.AvailabilityPreferenceService;
import com.rosterforge.service.EmployeeService;
import com.rosterforge.service.RosterService;
import com.rosterforge.service.ShiftService;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class HandleRequest implements Runnable {

    private final Socket socket;
    private final EmployeeService employeeService;
    private final ShiftService shiftService;
    private final AvailabilityPreferenceService preferenceService;
    private final RosterService rosterService;
    private final Gson gson;

    public HandleRequest(Socket socket,
                         EmployeeService employeeService,
                         ShiftService shiftService,
                         AvailabilityPreferenceService preferenceService,
                         RosterService rosterService) {
        this.socket           = socket;
        this.employeeService  = employeeService;
        this.shiftService     = shiftService;
        this.preferenceService = preferenceService;
        this.rosterService    = rosterService;
        this.gson             = new Gson();
    }

    @Override
    public void run() {
        try (
            Scanner reader     = new Scanner(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)
        ) {
            try {
                String requestJson = reader.nextLine();
                System.out.println("[SERVER] Received: " + requestJson);

                Request request    = gson.fromJson(requestJson, Request.class);

                IController controller = createController(request.getAction());
                Response response      = controller != null
                        ? controller.handleRequest(request)
                        : Response.error("Unknown action: " + request.getAction());

                String responseJson = gson.toJson(response);
                System.out.println("[SERVER] Sending: " + responseJson);
                writer.println(responseJson);
            } catch (Exception e) {
                System.err.println("[SERVER] Error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                e.printStackTrace();
                writer.println(gson.toJson(Response.error("Server error: " + e.getMessage())));
            }
        } catch (IOException e) {
            System.err.println("Stream error: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private IController createController(String action) {
        String domain = action.split("/")[0];
        return switch (domain) {
            case "employee"   -> new EmployeeController(employeeService);
            case "shift"      -> new ShiftController(shiftService);
            case "preference" -> new PreferenceController(preferenceService);
            case "roster"     -> new RosterController(rosterService);
            default           -> null;
        };
    }
}
