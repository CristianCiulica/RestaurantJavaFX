package mip.restaurantfx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class RestaurantGUI extends Application {

    private static final String LOGIN_BG_RESOURCE = "/login-bg.jpg";
    // Dacă vrei să folosești un fișier local (nu recomand pentru proiect/pachet), pune aici calea completă și setează USE_LOCAL_BG=true.
    private static final boolean USE_LOCAL_BG = false;
    private static final String LOGIN_BG_LOCAL_FILE = "D:/path/to/your/image.jpg";

    private final UserRepository userRepo = new UserRepository();

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
        lblSub.setWrapText(true);

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
        lblMesaj.setWrapText(true);

        VBox card = new VBox(8,
                lblTitlu,
                lblSub,
                new Separator(),
                new Label("User"), txtUser,
                new Label("Parolă"), txtPass,
                btnLogin,
                btnGuest,
                lblMesaj
        );
        card.getStyleClass().addAll("card", "login-card");
        card.setPrefWidth(320);
        card.setMaxWidth(320);
        card.setMaxHeight(420);
        card.setMinHeight(Region.USE_PREF_SIZE);
        card.setFillWidth(true);

        // Centrare + overlay pentru fundal
        StackPane root = new StackPane();
        root.getStyleClass().add("login-root");

        Region overlay = new Region();
        overlay.setStyle("-fx-background-color: rgba(11, 15, 26, 0.35);");
        overlay.prefWidthProperty().bind(root.widthProperty());
        overlay.prefHeightProperty().bind(root.heightProperty());

        root.getChildren().addAll(overlay, card);
        root.setPadding(new Insets(18));

        // Background image: resource (recommended) or local file
        if (USE_LOCAL_BG) {
            File f = new File(LOGIN_BG_LOCAL_FILE);
            if (f.exists()) {
                root.setStyle("-fx-background-image: url('" + f.toURI() + "');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;");
            }
        } else {
            var bg = RestaurantGUI.class.getResource(LOGIN_BG_RESOURCE);
            if (bg != null) {
                root.setStyle("-fx-background-image: url('" + bg.toExternalForm() + "');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;");
            }
        }

        // Actiuni Butoane

        // A. LOGIN STAFF/ADMIN
        btnLogin.setOnAction(e -> {
            String u = txtUser.getText();
            String p = txtPass.getText();

            Optional<User> userGasit = userRepo.login(u, p);

            if (userGasit.isPresent()) {
                deschideInterfata(stage, userGasit.get());
            } else {
                lblMesaj.setText("Date incorecte. Verifică user/parola și încearcă din nou.");
            }
        });

        // B. LOGIN GUEST
        btnGuest.setOnAction(e -> {
            // Cream un user temporar pentru guest
            User guestUser = new User("guest", "", "Vizitator", User.Role.CLIENT);
            deschideInterfata(stage, guestUser);
        });

        Scene scene = new Scene(root, 520, 480);
        var css = RestaurantGUI.class.getResource("/mip/restaurantfx/theme.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
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
            new AdminView().start(stage, user);
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
