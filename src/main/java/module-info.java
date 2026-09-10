module ni.edu.uam.factura_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;

    opens ni.edu.uam.factura_app.controller to javafx.fxml;
    opens ni.edu.uam.factura_app.model to javafx.base;
    exports ni.edu.uam.factura_app.application;
}