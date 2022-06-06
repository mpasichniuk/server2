module com.example.server2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.transport;
    requires io.netty.codec;
    requires io.netty.buffer;
    requires io.netty.common;
    requires netty.handler;


    opens com.example.server2 to javafx.fxml;
    exports com.example.server2;
}