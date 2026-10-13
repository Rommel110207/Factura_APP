module ni.edu.uam.practicahilospae {
    requires javafx.controls;
    requires javafx.fxml;

    opens ni.edu.uam.practicahilospae to javafx.fxml;
    exports ni.edu.uam.practicahilospae;
}