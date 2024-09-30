module edu.school21.mazejavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;
    requires com.google.gson;
    requires static lombok;
    requires java.desktop;

    opens edu.school21.mazejavafx to javafx.fxml;
    exports edu.school21.mazejavafx;

}