module com.example.dinogame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.dinogame to javafx.fxml;
    exports com.example.dinogame;
}