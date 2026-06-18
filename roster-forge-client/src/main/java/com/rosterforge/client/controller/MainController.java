package com.rosterforge.client.controller;

import com.google.gson.*;
import com.rosterforge.client.Request;
import com.rosterforge.client.Response;
import com.rosterforge.client.ServerClient;
import com.rosterforge.client.dm.AvailabilityPreferenceDm;
import com.rosterforge.client.dm.EmployeeDm;
import com.rosterforge.client.dm.ShiftDm;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.*;

public class MainController {

    private static final List<String> DAYS = List.of(
            "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
    private static final List<String> SHIFT_TYPES  = List.of("MORNING", "EVENING", "NIGHT");
    private static final List<String> LEVELS       = List.of("DARK_GREEN", "LIGHT_GREEN", "YELLOW", "RED");

    private final ServerClient server = new ServerClient("localhost", 8080);
    private final Gson gson = new Gson();
    private long nextId = System.currentTimeMillis();

    // ── Employee ──
    @FXML private TextField  empNameField, empMaxShiftsField;
    @FXML private CheckBox   empRoleShiftManager, empRoleSoc, empRoleSecurity;
    @FXML private TableView<EmployeeDm>             empTable;
    @FXML private TableColumn<EmployeeDm, String>   empNameCol, empRolesCol, empMaxShiftsCol;
    @FXML private Label empStatusLabel;

    // ── Shift ──
    @FXML private ComboBox<String> shiftDayCombo, shiftTypeCombo;
    @FXML private TextField        shiftMgrCount, shiftSocCount, shiftSecCount;
    @FXML private TableView<ShiftDm>            shiftTable;
    @FXML private TableColumn<ShiftDm, String>  shiftDayCol, shiftTypeCol, shiftRolesCol;
    @FXML private Label shiftStatusLabel;

    // ── Preference ──
    @FXML private ComboBox<EmployeeDm>              prefEmployeeCombo;
    @FXML private ComboBox<ShiftDm>                 prefShiftCombo;
    @FXML private ComboBox<String>                  prefLevelCombo;
    @FXML private TableView<AvailabilityPreferenceDm>          prefTable;
    @FXML private TableColumn<AvailabilityPreferenceDm, String> prefEmpCol, prefShiftCol, prefLevelCol;
    @FXML private Label prefStatusLabel;

    private AvailabilityPreferenceDm selectedPref = null;

    // ── Roster ──
    @FXML private TableView<RosterRow>             rosterTable;
    @FXML private TableColumn<RosterRow, String>   rosterEmpCol, rosterShiftCol, rosterRoleCol;
    @FXML private Label    rosterStatusLabel;
    @FXML private TextArea rosterViolationsArea;

    @FXML
    private void initialize() {
        // Employee columns
        empNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        empRolesCol.setCellValueFactory(c -> new SimpleStringProperty(
                String.join(", ", c.getValue().getQualifiedRoles())));
        empMaxShiftsCol.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getMaxShiftsPerWeek())));

        // Clicking an employee row fills the form
        empTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, emp) -> { if (emp != null) fillEmpForm(emp); });

        // Shift columns
        shiftDayCol.setCellValueFactory(c -> new SimpleStringProperty(
                DAYS.get(c.getValue().getDayIndex())));
        shiftTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getShiftType()));
        shiftRolesCol.setCellValueFactory(c -> new SimpleStringProperty(
                formatRoles(c.getValue().getRoleRequirements())));

        // Preference columns — look up names from loaded combos
        prefEmpCol.setCellValueFactory(c -> new SimpleStringProperty(
                findEmployeeName(c.getValue().getEmployeeId())));
        prefShiftCol.setCellValueFactory(c -> new SimpleStringProperty(
                findShiftLabel(c.getValue().getShiftId())));
        prefLevelCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getAvailabilityLevel()));

        // Roster columns
        rosterEmpCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmployeeName()));
        rosterShiftCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getShift()));
        rosterRoleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole()));

        // Clicking a preference row fills the form for editing
        prefTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, pref) -> { if (pref != null) fillPrefForm(pref); });

        // Combo setup
        shiftDayCombo.setItems(FXCollections.observableArrayList(DAYS));
        shiftTypeCombo.setItems(FXCollections.observableArrayList(SHIFT_TYPES));
        prefLevelCombo.setItems(FXCollections.observableArrayList(LEVELS));

        loadEmployees();
        loadShifts();
        loadPreferences();
    }

    // ══ Employee ══

    @FXML
    private void addEmployee() {
        List<String> roles = selectedRoles();
        if (empNameField.getText().isBlank() || roles.isEmpty() || empMaxShiftsField.getText().isBlank()) {
            setStatus(empStatusLabel, false, "Fill in name, at least one role, and max shifts.");
            return;
        }
        try {
            JsonObject body = new JsonObject();
            body.addProperty("id", nextId++);
            body.addProperty("name", empNameField.getText().trim());
            body.add("qualifiedRoles", gson.toJsonTree(roles));
            body.addProperty("maxShiftsPerWeek", Integer.parseInt(empMaxShiftsField.getText().trim()));
            Response r = server.send(new Request("employee/save", body));
            setStatus(empStatusLabel, r);
            if (r.isSuccess()) { clearEmpForm(); loadEmployees(); }
        } catch (NumberFormatException e) {
            setStatus(empStatusLabel, false, "Max shifts must be a number.");
        }
    }

    @FXML
    private void updateEmployee() {
        EmployeeDm selected = empTable.getSelectionModel().getSelectedItem();
        if (selected == null) { setStatus(empStatusLabel, false, "Select an employee to update."); return; }
        List<String> roles = selectedRoles();
        if (empNameField.getText().isBlank() || roles.isEmpty() || empMaxShiftsField.getText().isBlank()) {
            setStatus(empStatusLabel, false, "Fill in all fields before updating.");
            return;
        }
        try {
            JsonObject body = new JsonObject();
            body.addProperty("id", selected.getId());
            body.addProperty("name", empNameField.getText().trim());
            body.add("qualifiedRoles", gson.toJsonTree(roles));
            body.addProperty("maxShiftsPerWeek", Integer.parseInt(empMaxShiftsField.getText().trim()));
            Response r = server.send(new Request("employee/save", body));
            setStatus(empStatusLabel, r);
            if (r.isSuccess()) loadEmployees();
        } catch (NumberFormatException e) {
            setStatus(empStatusLabel, false, "Max shifts must be a number.");
        }
    }

    @FXML
    private void removeEmployee() {
        EmployeeDm selected = empTable.getSelectionModel().getSelectedItem();
        if (selected == null) { setStatus(empStatusLabel, false, "Select an employee to remove."); return; }
        JsonObject body = new JsonObject();
        body.addProperty("id", selected.getId());
        Response r = server.send(new Request("employee/delete", body));
        setStatus(empStatusLabel, r);
        if (r.isSuccess()) { clearEmpForm(); loadEmployees(); }
    }

    @FXML
    private void loadEmployees() {
        Response r = server.send(new Request("employee/getAll", new JsonObject()));
        if (!r.isSuccess()) { setStatus(empStatusLabel, r); return; }
        EmployeeDm[] arr = gson.fromJson(r.getBody().get("employees"), EmployeeDm[].class);
        ObservableList<EmployeeDm> list = FXCollections.observableArrayList(arr);
        empTable.setItems(list);
        prefEmployeeCombo.setItems(list);
        setStatus(empStatusLabel, true, "Loaded " + arr.length + " employees.");
    }

    // ══ Shift ══

    @FXML
    private void addShift() {
        if (shiftDayCombo.getValue() == null || shiftTypeCombo.getValue() == null) {
            setStatus(shiftStatusLabel, false, "Select a day and shift type.");
            return;
        }
        try {
            Map<String, Integer> reqs = new LinkedHashMap<>();
            int mgr = intOrZero(shiftMgrCount.getText());
            int soc = intOrZero(shiftSocCount.getText());
            int sec = intOrZero(shiftSecCount.getText());
            if (mgr > 0) reqs.put("SHIFT_MANAGER", mgr);
            if (soc > 0) reqs.put("SOC", soc);
            if (sec > 0) reqs.put("SECURITY", sec);
            if (reqs.isEmpty()) { setStatus(shiftStatusLabel, false, "Add at least one role requirement."); return; }

            JsonObject body = new JsonObject();
            body.addProperty("id", nextId++);
            body.addProperty("dayIndex", DAYS.indexOf(shiftDayCombo.getValue()));
            body.addProperty("shiftType", shiftTypeCombo.getValue());
            body.add("roleRequirements", gson.toJsonTree(reqs));
            Response r = server.send(new Request("shift/save", body));
            setStatus(shiftStatusLabel, r);
            if (r.isSuccess()) { clearShiftForm(); loadShifts(); }
        } catch (NumberFormatException e) {
            setStatus(shiftStatusLabel, false, "Role counts must be numbers.");
        }
    }

    @FXML
    private void removeShift() {
        ShiftDm selected = shiftTable.getSelectionModel().getSelectedItem();
        if (selected == null) { setStatus(shiftStatusLabel, false, "Select a shift to remove."); return; }
        JsonObject body = new JsonObject();
        body.addProperty("id", selected.getId());
        Response r = server.send(new Request("shift/delete", body));
        setStatus(shiftStatusLabel, r);
        if (r.isSuccess()) loadShifts();
    }

    @FXML
    private void loadShifts() {
        Response r = server.send(new Request("shift/getAll", new JsonObject()));
        if (!r.isSuccess()) { setStatus(shiftStatusLabel, r); return; }
        ShiftDm[] arr = gson.fromJson(r.getBody().get("shifts"), ShiftDm[].class);
        ObservableList<ShiftDm> list = FXCollections.observableArrayList(arr);
        shiftTable.setItems(list);
        prefShiftCombo.setItems(list);
        setStatus(shiftStatusLabel, true, "Loaded " + arr.length + " shifts.");
    }

    // ══ Preference ══

    @FXML
    private void addPreference() {
        EmployeeDm emp   = prefEmployeeCombo.getValue();
        ShiftDm    shift = prefShiftCombo.getValue();
        String     level = prefLevelCombo.getValue();
        if (emp == null || shift == null || level == null) {
            setStatus(prefStatusLabel, false, "Select an employee, shift, and level.");
            return;
        }
        boolean duplicate = prefTable.getItems().stream()
                .anyMatch(p -> p.getEmployeeId() == emp.getId() && p.getShiftId() == shift.getId());
        if (duplicate) {
            setStatus(prefStatusLabel, false, "Preference already exists for this employee + shift. Select it and use Update.");
            return;
        }
        JsonObject body = new JsonObject();
        body.addProperty("id", nextId++);
        body.addProperty("employeeId", emp.getId());
        body.addProperty("shiftId", shift.getId());
        body.addProperty("availabilityLevel", level);
        Response r = server.send(new Request("preference/save", body));
        setStatus(prefStatusLabel, r);
        if (r.isSuccess()) { selectedPref = null; loadPreferences(); }
    }

    @FXML
    private void updatePreference() {
        if (selectedPref == null) { setStatus(prefStatusLabel, false, "Select a preference row to update."); return; }
        EmployeeDm emp   = prefEmployeeCombo.getValue();
        ShiftDm    shift = prefShiftCombo.getValue();
        String     level = prefLevelCombo.getValue();
        if (emp == null || shift == null || level == null) {
            setStatus(prefStatusLabel, false, "Select an employee, shift, and level.");
            return;
        }
        JsonObject body = new JsonObject();
        body.addProperty("id", selectedPref.getId());
        body.addProperty("employeeId", emp.getId());
        body.addProperty("shiftId", shift.getId());
        body.addProperty("availabilityLevel", level);
        Response r = server.send(new Request("preference/save", body));
        setStatus(prefStatusLabel, r);
        if (r.isSuccess()) { selectedPref = null; prefTable.getSelectionModel().clearSelection(); loadPreferences(); }
    }

    @FXML
    private void removePreference() {
        AvailabilityPreferenceDm selected = prefTable.getSelectionModel().getSelectedItem();
        if (selected == null) { setStatus(prefStatusLabel, false, "Select a preference to remove."); return; }
        JsonObject body = new JsonObject();
        body.addProperty("id", selected.getId());
        Response r = server.send(new Request("preference/delete", body));
        setStatus(prefStatusLabel, r);
        if (r.isSuccess()) loadPreferences();
    }

    @FXML
    private void loadPreferences() {
        Response r = server.send(new Request("preference/getAll", new JsonObject()));
        if (!r.isSuccess()) { setStatus(prefStatusLabel, r); return; }
        AvailabilityPreferenceDm[] arr = gson.fromJson(
                r.getBody().get("preferences"), AvailabilityPreferenceDm[].class);
        prefTable.setItems(FXCollections.observableArrayList(arr));
        setStatus(prefStatusLabel, true, "Loaded " + arr.length + " preferences.");
    }

    // ══ Roster ══

    @FXML
    private void generateRoster() {
        Response r = server.send(new Request("roster/generate", new JsonObject()));
        if (!r.isSuccess()) { setStatus(rosterStatusLabel, r); return; }

        JsonArray assignments = r.getBody().getAsJsonArray("assignments");
        List<RosterRow> rows = new ArrayList<>();
        for (JsonElement el : assignments) {
            JsonObject obj = el.getAsJsonObject();
            rows.add(new RosterRow(
                    obj.get("employeeName").getAsString(),
                    DAYS.get(obj.get("day").getAsInt()) + " – " + obj.get("shiftType").getAsString(),
                    obj.get("role").getAsString()
            ));
        }
        rosterTable.setItems(FXCollections.observableArrayList(rows));
        double score = r.getBody().get("score").getAsDouble();
        setStatus(rosterStatusLabel, true,
                "Score: " + score + "  |  Assignments: " + rows.size());

        JsonArray violations = r.getBody().getAsJsonArray("violations");
        if (violations == null || violations.isEmpty()) {
            rosterViolationsArea.setText("");
            rosterViolationsArea.setPromptText("No violations.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (JsonElement v : violations) sb.append("• ").append(v.getAsString()).append("\n");
            rosterViolationsArea.setText(sb.toString().trim());
        }
    }

    // ══ Helpers ══

    private List<String> selectedRoles() {
        List<String> roles = new ArrayList<>();
        if (empRoleShiftManager.isSelected()) roles.add("SHIFT_MANAGER");
        if (empRoleSoc.isSelected())          roles.add("SOC");
        if (empRoleSecurity.isSelected())     roles.add("SECURITY");
        return roles;
    }

    private void fillEmpForm(EmployeeDm emp) {
        empNameField.setText(emp.getName());
        empMaxShiftsField.setText(String.valueOf(emp.getMaxShiftsPerWeek()));
        empRoleShiftManager.setSelected(emp.getQualifiedRoles().contains("SHIFT_MANAGER"));
        empRoleSoc.setSelected(emp.getQualifiedRoles().contains("SOC"));
        empRoleSecurity.setSelected(emp.getQualifiedRoles().contains("SECURITY"));
    }

    private void clearEmpForm() {
        empNameField.clear();
        empMaxShiftsField.clear();
        empRoleShiftManager.setSelected(false);
        empRoleSoc.setSelected(false);
        empRoleSecurity.setSelected(false);
    }

    private void clearShiftForm() {
        shiftDayCombo.setValue(null);
        shiftTypeCombo.setValue(null);
        shiftMgrCount.clear();
        shiftSocCount.clear();
        shiftSecCount.clear();
    }

    private void fillPrefForm(AvailabilityPreferenceDm pref) {
        selectedPref = pref;
        prefEmployeeCombo.getItems().stream()
                .filter(e -> e.getId() == pref.getEmployeeId())
                .findFirst().ifPresent(prefEmployeeCombo::setValue);
        prefShiftCombo.getItems().stream()
                .filter(s -> s.getId() == pref.getShiftId())
                .findFirst().ifPresent(prefShiftCombo::setValue);
        prefLevelCombo.setValue(pref.getAvailabilityLevel());
    }

    private String findEmployeeName(long id) {
        return prefEmployeeCombo.getItems().stream()
                .filter(e -> e.getId() == id)
                .map(EmployeeDm::getName)
                .findFirst().orElse("Employee " + id);
    }

    private String findShiftLabel(long id) {
        return prefShiftCombo.getItems().stream()
                .filter(s -> s.getId() == id)
                .map(ShiftDm::toString)
                .findFirst().orElse("Shift " + id);
    }

    private String formatRoles(Map<String, Integer> reqs) {
        if (reqs == null) return "";
        StringBuilder sb = new StringBuilder();
        reqs.forEach((role, count) -> {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(role).append(" ×").append(count);
        });
        return sb.toString();
    }

    private int intOrZero(String text) {
        try { return text.isBlank() ? 0 : Integer.parseInt(text.trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    private void setStatus(Label label, Response r) {
        if (r.isSuccess()) {
            String msg = (r.getBody() != null && r.getBody().has("message"))
                    ? r.getBody().get("message").getAsString() : "OK";
            setStatus(label, true, msg);
        } else {
            setStatus(label, false, r.getMessage());
        }
    }

    private void setStatus(Label label, boolean ok, String msg) {
        label.setText(msg);
        label.setStyle(ok ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }

    // ══ Roster row model ══

    public static class RosterRow {
        private final String employeeName, shift, role;

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
