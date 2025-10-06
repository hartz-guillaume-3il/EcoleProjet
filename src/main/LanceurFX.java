package main;
import User.Gestionnaire;
import javafx.application.Application;
import ui.JavaFXApp;

public class LanceurFX {
    public static void main(String[] args) {
        Gestionnaire admin = new Gestionnaire(
                "admin@mail.com",
                "admin123",
                "Martin"
        );
        Application.launch(JavaFXApp.class, args);
    }
}