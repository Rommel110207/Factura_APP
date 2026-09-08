module ni.edu.uam.factura_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens ni.edu.uam.factura_app.controller to javafx.fxml;
    exports ni.edu.uam.factura_app;
}