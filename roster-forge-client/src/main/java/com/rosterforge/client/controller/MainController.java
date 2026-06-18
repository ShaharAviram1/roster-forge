package com.rosterforge.client.controller;

import com.google.gson.*;
import com.rosterforge.client.Request;
import com.rosterforge.client.Response;
import com.rosterforge.client.ServerClient;
import com.rosterforge.client.dm.AvailabilityPreferenceDm;
import com.rosterforge.client.dm.EmployeeDm;
import com.rosterforge.client.dm.ShiftDm;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.*;

public class MainController {

    private final ServerClient server = new ServerClient("localhost", 8080);
    private final Gson gson = new Gson();

    // ── Employee ──
    @FXML private TextField empIdField, empNameField, empRolesField, empMaxShiftsField;
    @FXML private TableView<EmployeeDm> empTable;
    @FXML private TableColumn<EmployeeDm, Long>    empIdCol;
    @FXML private TableColumn<EmployeeDm, String>  empNameCol, empRolesCol;
    @FXML private TableColumn<EmployeeDm, Integer> empMaxShiftsCol;
    @FXML private Label empStatusLabel;

    // ── Shift ──
    @FXML private TextField shiftIdField, shiftDayField, shiftTypeField, shiftRolesField;
    @FXML private TableView<ShiftDm> shiftTable;
    @FXML private TableColumn<ShiftDm, Long>    shiftIdCol;
    @FXML private TableColumn<ShiftDm, Integer> shiftDayCol;
    @FXML private TableColumn<ShiftDm, String>  shiftTypeCol, shiftRolesCol;
    @FXML private Label shiftStatusLabel;

    // ── Preference ──
    @FXML private TextField prefIdField, prefEmpIdField, prefShiftIdField;
    @FXML private ComboBox<String> prefLevelCombo;
    @FXML private TableView<AvailabilityPreferenceDm> prefTable;
    @FXML private TableColumn<AvailabilityPreferenceDm, Long>   prefIdCol, prefEmpIdCol, prefShiftIdCol;
    @FXML private TableColumn<AvailabilityPreferenceDm, String> prefLevelCol;
    @FXML private Label prefStatusLabel;

    // ── Roster ──
    @FXML private TableView<RosterRow> rosterTable;
    @FXML private TableColumn<RosterRow, String> rosterEmpCol, rosterShiftCol, rosterRoleCol;
    @FXML private Label rosterStatusLabel;

    @FXML
    private void initialize() {
        empIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        empNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        empRolesCol.setCellValueFactory(new PropertyValueFactory<>("qualifiedRoles"));
        empMaxShiftsCol.setCellValueFactory(new PropertyValueFactory<>("maxShiftsPerWeek"));

        shiftIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        shiftDayCol.setCellValueFactory(new PropertyValueFactory<>("dayIndex"));
        shiftTypeCol.setCellValueFactory(new PropertyValueFactory<>("shiftType"));
        shiftRolesCol.setCellValueFactory(new PropertyValueFactory<>("roleRequirements"));

        prefIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        prefEmpIdCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        prefShiftIdCol.setCellValueFactory(new PropertyValueFactory<>("shiftId"));
        prefLevelCol.setCellValueFactory(new PropertyValueFactory<>("availabilityLevel"));

        rosterEmpCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        rosterShiftCol.setCellValueFactory(new PropertyValueFactory<>("shift"));
        rosterRoleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        prefLevelCombo.setItems(FXCollections.observableArrayList(
                "DARK_GREEN", "GREEN", "YELLOW", "RED"));

        loadEmployees();
        loadShifts();
        loadPreferences();
    }

    // ══ Employee handlers ══

    @FXML
    private void addEmployee() {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("id", Long.parseLong(empIdField.getText().trim()));
            body.addProperty("name", empNameField.getText().trim());
            body.add("qualifiedRoles", gson.toJsonTree(
                    Arrays.asList(empRolesField.getText().trim().split(","))));
            body.addProperty("maxShiftsPerWeek", Integer.parseInt(empMaxShiftsField.getText().trim()));

            Response r = server.send(new Request("employee/save", body));
            status(empStatusLabel, r);
            if (r.isSuccess()) loadEmployees();
        } catch (NumberFormatException e) {
            empStatusLabel.setText("Invalid number: " + e.getMessage());
            empStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void removeEmployee() {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("id", Long.parseLong(empIdField.getText().trim()));
            Response r = server.send(new Request("employee/delete", body));
            status(empStatusLabel, r);
            if (r.isSuccess()) loadEmployees();
        } catch (NumberFormatException e) {
            empStatusLabel.setText("Enter a valid ID.");
            empStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void updateEmployee() {
        addEmployee();
    }

    @FXML
    private void loadEmployees() {
        Response r = server.send(new Request("employee/getAll", new JsonObject()));
        if (!r.isSuccess()) { status(empStatusLabel, r); return; }
        EmployeeDm[] arr = gson.fromJson(r.getBody().get("employees"), EmployeeDm[].class);
        empTable.setItems(FXCollections.observableArrayList(arr));
        empStatusLabel.setText("Loaded " + arr.length + " employees.");
        empStatusLabel.setStyle("-fx-text-fill: grey;");
    }

    // ══ Shift handlers ══

    @FXML
    private void addShift() {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("id", Long.parseLong(shiftIdField.getText().trim()));
            body.addProperty("dayIndex", Integer.parseInt(shiftDayField.getText().trim()));
            body.addProperty("shiftType", shiftTypeField.getText().trim());
            body.add("roleRequirements", gson.toJsonTree(parseRoles(shiftRolesField.getText().trim())));

            Response r = server.send(new Request("shift/save", body));
            status(shiftStatusLabel, r);
            if (r.isSuccess()) loadShifts();
        } catch (Exception e) {
            shiftStatusLabel.setText("Invalid input: " + e.getMessage());
            shiftStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void removeShift() {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("id", Long.parseLong(shiftIdField.getText().trim()));
            Response r = server.send(new Request("shift/delete", body));
            status(shiftStatusLabel, r);
            if (r.isSuccess()) loadShifts();
        } catch (NumberFormatException e) {
            shiftStatusLabel.setText("Enter a valid ID.");
            shiftStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void loadShifts() {
        Response r = server.send(new Request("shift/getAll", new JsonObject()));
        if (!r.isSuccess()) { status(shiftStatusLabel, r); return; }
        ShiftDm[] arr = gson.fromJson(r.getBody().get("shifts"), ShiftDm[].class);
        shiftTable.setItems(FXCollections.observableArrayList(arr));
        shiftStatusLabel.setText("Loaded " + arr.length + " shifts.");
        shiftStatusLabel.setStyle("-fx-text-fill: grey;");
    }

    // ══ Preference handlers ══

    @FXML
    private void addPreference() {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("id", Long.parseLong(prefIdField.getText().trim()));
            body.addProperty("employeeId", Long.parseLong(prefEmpIdField.getText().trim()));
            body.addProperty("shiftId", Long.parseLong(prefShiftIdField.getText().trim()));
            body.addProperty("availabilityLevel", prefLevelCombo.getValue());

            Response r = server.send(new Request("preference/save", body));
            status(prefStatusLabel, r);
            if (r.isSuccess()) loadPreferences();
        } catch (NumberFormatException e) {
            prefStatusLabel.setText("Invalid number: " + e.getMessage());
            prefStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void removePreference() {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("id", Long.parseLong(prefIdField.getText().trim()));
            Response r = server.send(new Request("preference/delete", body));
            status(prefStatusLabel, r);
            if (r.isSuccess()) loadPreferences();
        } catch (NumberFormatException e) {
            prefStatusLabel.setText("Enter a valid ID.");
            prefStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void loadPreferences() {
        Response r = server.send(new Request("preference/getAll", new JsonObject()));
        if (!r.isSuccess()) { status(prefStatusLabel, r); return; }
        AvailabilityPreferenceDm[] arr = gson.fromJson(
                r.getBody().get("preferences"), AvailabilityPreferenceDm[].class);
        prefTable.setItems(FXCollections.observableArrayList(arr));
        prefStatusLabel.setText("Loaded " + arr.length + " preferences.");
        prefStatusLabel.setStyle("-fx-text-fill: grey;");
    }

    // ══ Roster handler ══

    @FXML
    private void generateRoster() {
        Response r = server.send(new Request("roster/generate", new JsonObject()));
        if (!r.isSuccess()) { status(rosterStatusLabel, r); return; }

        JsonArray assignments = r.getBody().getAsJsonArray("assignments");
        List<RosterRow> rows = new ArrayList<>();
        for (JsonElement el : assignments) {
            JsonObject obj = el.getAsJsonObject();
            rows.add(new RosterRow(
                    obj.get("employeeName").getAsString(),
                    "Day " + obj.get("day").getAsInt() + " – " + obj.get("shiftType").getAsString(),
                    obj.get("role").getAsString()
            ));
        }
        rosterTable.setItems(FXCollections.observableArrayList(rows));

        double score = r.getBody().get("score").getAsDouble();
        rosterStatusLabel.setText("Score: " + score + "  |  Assignments: " + rows.size());
        rosterStatusLabel.setStyle("-fx-text-fill: green;");
    }

    // ══ Helpers ══

    private void status(Label label, Response r) {
        if (r.isSuccess()) {
            JsonObject body = r.getBody();
            String msg = (body != null && body.has("message"))
                    ? body.get("message").getAsString() : "OK";
            label.setText(msg);
            label.setStyle("-fx-text-fill: green;");
        } else {
            label.setText("Error: " + r.getMessage());
            label.setStyle("-fx-text-fill: red;");
        }
    }

    private Map<String, Integer> parseRoles(String input) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (String pair : input.split(",")) {
            String[] parts = pair.trim().split(":");
            map.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
        }
        return map;
    }

    // ══ Roster row model ══

    public static class RosterRow {
        private final String employeeName;
        private final String shift;
        private final String role;

        public RosterRow(String employeeName, String shift, String role) {
            this.employeeName = employeeName;
            this.shift = shift;
            this.role = role;
        }

        public String getEmployeeName() { return employeeName; }
        public String getShift()        { return shift; }
        public String getRole()         { return role; }
    }
}
