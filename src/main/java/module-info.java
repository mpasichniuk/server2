module com.example.server2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.server2 to javafx.fxml;
    exports com.example.server2;
}