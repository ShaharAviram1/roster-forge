package com.rosterforge.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rosterforge.dm.EmployeeDm;
import com.rosterforge.server.Request;
import com.rosterforge.server.Response;
import com.rosterforge.service.EmployeeService;

import java.util.List;

public class EmployeeController implements IController {

    private final EmployeeService employeeService;
    private final Gson gson;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
        this.gson = new Gson();
    }

    @Override
    public Response handleRequest(Request request) {
        String action = request.getAction();

        return switch (action) {
            case "employee/save"   -> save(request);
            case "employee/get"    -> get(request);
            case "employee/getAll" -> getAll();
            case "employee/delete" -> delete(request);
            default -> Response.error("Unknown employee action: " + action);
        };
    }

    private Response save(Request request) {
        EmployeeDm employee = gson.fromJson(request.getBody(), EmployeeDm.class);
        employeeService.addEmployee(employee);
        JsonObject body = new JsonObject();
        body.addProperty("message", "Employee saved successfully");
        return Response.success(body);
    }

    private Response get(Request request) {
        long id = request.getBody().get("id").getAsLong();
        EmployeeDm employee = employeeService.getEmployee(id);
        if (employee == null) return Response.error("Employee not found: " + id);
        return Response.success(gson.toJsonTree(employee).getAsJsonObject());
    }

    private Response getAll() {
        List<EmployeeDm> employees = employeeService.getAllEmployees();
        JsonObject body = new JsonObject();
        body.add("employees", gson.toJsonTree(employees));
        return Response.success(body);
    }

    private Response delete(Request request) {
        long id = request.getBody().get("id").getAsLong();
        employeeService.removeEmployee(id);
        JsonObject body = new JsonObject();
        body.addProperty("message", "Employee deleted successfully");
        return Response.success(body);
    }
}
