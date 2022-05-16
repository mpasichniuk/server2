module com.example.server2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.transport;
    requires io.netty.codec;


    opens com.example.server2 to javafx.fxml;
    exports com.example.server2;
}