module com.rosterforge.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.rosterforge.client to javafx.fxml, com.google.gson;
    opens com.rosterforge.client.controller to javafx.fxml;
    opens com.rosterforge.client.dm to com.google.gson;

    exports com.rosterforge.client;
}
