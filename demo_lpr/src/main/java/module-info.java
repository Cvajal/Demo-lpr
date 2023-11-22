module com.example.demo_lpr {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires java.desktop;


    opens com.example.demo_lpr to javafx.fxml;
    exports com.example.demo_lpr;
}