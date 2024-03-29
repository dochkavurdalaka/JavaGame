module com.example.server {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.example.server to javafx.fxml, com.google.gson;
    exports com.example.server;
}