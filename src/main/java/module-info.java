module com.example.mchatclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires models;


    opens com.example.mchatclient to javafx.fxml;
    exports com.example.mchatclient;
}