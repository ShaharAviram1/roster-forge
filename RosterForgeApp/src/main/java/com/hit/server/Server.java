package com.hit.server;

import com.hit.service.AvailabilityPreferenceService;
import com.hit.service.EmployeeService;
import com.hit.service.RosterService;
import com.hit.service.ShiftService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    private final int port;
    private final EmployeeService employeeService;
    private final ShiftService shiftService;
    private final AvailabilityPreferenceService preferenceService;
    private final RosterService rosterService;

    public Server(int port,
                  EmployeeService employeeService,
                  ShiftService shiftService,
                  AvailabilityPreferenceService preferenceService,
                  RosterService rosterService) {
        this.port              = port;
        this.employeeService   = employeeService;
        this.shiftService      = shiftService;
        this.preferenceService = preferenceService;
        this.rosterService     = rosterService;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("RosterForge server listening on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new HandleRequest(
                        clientSocket,
                        employeeService,
                        shiftService,
                        preferenceService,
                        rosterService
                )).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
