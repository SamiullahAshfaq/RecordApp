module com.example.searchApp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.searchApp to javafx.fxml;
    exports com.example.searchApp;
}