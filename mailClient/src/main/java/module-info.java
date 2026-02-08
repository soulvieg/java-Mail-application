module com.example.mailclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.example.mailclient to javafx.fxml;
    opens com.example.mailclient.controller to javafx.fxml;
    opens com.example.mailclient.model to com.google.gson;
    exports com.example.mailclient;
}