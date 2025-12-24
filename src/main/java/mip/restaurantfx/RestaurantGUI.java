package mip.restaurantfx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Optional;

public class RestaurantGUI extends Application {

    private UserRepository userRepo = new UserRepository();

    @Override
    public void start(Stage stage) {
        // 1. Asiguram datele initiale
        DataSeeder.seed();

        stage.setTitle("Restaurant La Andrei - Login");

        // UI Elemente
        Label lblTitlu = new Label("La Andrei");
        lblTitlu.getStyleClass().add("title");
        Label lblSub = new Label("Login pentru Staff/Admin sau intră ca Guest");
        lblSub.getStyleClass().add("subtitle");

        TextField txtUser = new TextField();
        txtUser.setPromptText("Utilizator");

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Parola");

        Button btnLogin = new Button("Autentificare");
        btnLogin.getStyleClass().add("primary");

        Button btnGuest = new Button("Continuă ca Guest");
        btnGuest.getStyleClass().add("outline");

        Label lblMesaj = new Label();
        lblMesaj.getStyleClass().add("muted");

        VBox card = new VBox(12,
                lblTitlu,
                lblSub,
                new Separator(),
                new Label("User"), txtUser,
                new Label("Parolă"), txtPass,
                btnLogin,
                btnGuest,
                lblMesaj
        );
        card.getStyleClass().add("card");
        card.setMaxWidth(360);

        StackPane root = new StackPane(card);
        root.setPadding(new Insets(24));

        // Actiuni Butoane

        // A. LOGIN STAFF/ADMIN
        btnLogin.setOnAction(e -> {
            String u = txtUser.getText();
            String p = txtPass.getText();

            Optional<User> userGasit = userRepo.login(u, p);

            if (userGasit.isPresent()) {
                deschideInterfata(stage, userGasit.get());
            } else {
                lblMesaj.setText("Date incorecte! Incearca admin/admin sau staff/1234");
            }
        });

        // B. LOGIN GUEST
        btnGuest.setOnAction(e -> {
            // Cream un user temporar pentru guest
            User guestUser = new User("guest", "", "Vizitator", User.Role.CLIENT);
            deschideInterfata(stage, guestUser);
        });

        Scene scene = new Scene(root, 520, 480);
        scene.getStylesheets().add(RestaurantGUI.class.getResource("/mip/restaurantfx/theme.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void deschideInterfata(Stage stage, User user) {
        if (user.getRol() == User.Role.CLIENT) {
            new ClientView().start(stage, user);
        }
        else if (user.getRol() == User.Role.STAFF) {
            new StaffMeseView().start(stage, user);
        }
        else if (user.getRol() == User.Role.ADMIN) {
            new Alert(Alert.AlertType.INFORMATION, "Salut Sefu'! Interfata ta urmeaza.").show();
        }
    }
    @Override
    public void stop() {
        PersistenceManager.getInstance().close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}