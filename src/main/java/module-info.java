module com.example.ajedrez4x4 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires gs.core;
    requires java.desktop;

    opens com.example.ajedrez4x4 to javafx.fxml;
    exports com.example.ajedrez4x4;
}