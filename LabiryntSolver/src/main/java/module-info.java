module com.example.labiryntsolver {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.labiryntsolver to javafx.fxml;
    exports com.example.labiryntsolver;
}