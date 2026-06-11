module com.consuhelp.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens com.consuhelp.client to javafx.fxml;
    opens com.consuhelp.client.controller to javafx.fxml;
    opens com.consuhelp.client.model to com.fasterxml.jackson.databind;

    exports com.consuhelp.client;
    exports com.consuhelp.client.controller;
    exports com.consuhelp.client.model;
    exports com.consuhelp.client.service;
    exports com.consuhelp.client.util;
}
