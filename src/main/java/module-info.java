module ni.edu.uam.factura_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens ni.edu.uam.factura_app to javafx.fxml;
    exports ni.edu.uam.factura_app;
}