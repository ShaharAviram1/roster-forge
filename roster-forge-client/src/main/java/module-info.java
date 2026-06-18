module com.rosterforge.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.rosterforge.client to javafx.fxml;
    opens com.rosterforge.client.controller to javafx.fxml;

    exports com.rosterforge.client;
}
