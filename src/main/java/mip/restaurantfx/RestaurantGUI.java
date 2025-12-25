package mip.restaurantfx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import mip.restaurantfx.service.AppContext;
import mip.restaurantfx.service.AppInitializer;

import java.io.File;
import java.util.Optional;

public class RestaurantGUI extends Application {

    private static final String LOGIN_BG_RESOURCE = "/login-bg.jpg";
    private static final boolean USE_LOCAL_BG = false;
    private static final String LOGIN_BG_LOCAL_FILE = "D:/path/to/your/image.jpg";
    private static final String KEY_CLOSE_HANDLER_INSTALLED = "__closeHandlerInstalled";
    private final UserRepository userRepo = AppContext.services().users();

    @Override
    public void start(Stage stage) {
        AppInitializer.init();

        if (!Boolean.TRUE.equals(stage.getProperties().get(KEY_CLOSE_HANDLER_INSTALLED))) {
            stage.setOnCloseRequest(evt -> {
                evt.consume();
                ExitUtil.confirmAndExit(stage);
            });
            stage.getProperties().put(KEY_CLOSE_HANDLER_INSTALLED, true);
        }
        if (!stage.isShowing()) {
            stage.initStyle(StageStyle.UNDECORATED);
        }

        stage.setTitle("Restaurant La Andrei - Login");
        Label lblTitlu = new Label("Restaurantul La Andrei");
        lblTitlu.getStyleClass().add("title");
        lblTitlu.setAlignment(Pos.CENTER);
        lblTitlu.setMaxWidth(Double.MAX_VALUE);

        Label lblSub = new Label("Login pentru Angajati/Admin sau continuă ca și Vizitator");
        lblSub.getStyleClass().add("subtitle");
        lblSub.setWrapText(true);

        TextField txtUser = new TextField();
        txtUser.setPromptText("Utilizator");

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Parola");

        Button btnLogin = new Button("Autentificare");
        btnLogin.getStyleClass().add("primary");

        Button btnGuest = new Button("Continuă ca Vizitator");
        btnGuest.getStyleClass().add("outline");

        VBox buttonsBox = new VBox(8, btnLogin, btnGuest);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setFillWidth(true);

        Region buttonsSpacer = new Region();
        buttonsSpacer.setMinHeight(12);

        Label lblMesaj = new Label();
        lblMesaj.getStyleClass().add("muted");
        lblMesaj.setWrapText(true);

        VBox card = new VBox(8,
                lblTitlu,
                lblSub,
                new Separator(),
                new Label("User"), txtUser,
                new Label("Parolă"), txtPass,
                buttonsSpacer,
                buttonsBox,
                lblMesaj
        );
        card.getStyleClass().addAll("card", "login-card");
        card.setPrefWidth(320);
        card.setMaxWidth(320);
        card.setMaxHeight(420);
        card.setMinHeight(Region.USE_PREF_SIZE);
        card.setFillWidth(true);
        StackPane root = new StackPane();
        root.getStyleClass().add("login-root");

        Button btnExit = new Button("X");
        btnExit.getStyleClass().add("exit");
        btnExit.setOnAction(e -> ExitUtil.confirmAndExit(stage));
        StackPane.setAlignment(btnExit, Pos.TOP_RIGHT);
        StackPane.setMargin(btnExit, new Insets(12));

        Region overlay = new Region();
        overlay.setStyle("-fx-background-color: rgba(11, 15, 26, 0.35);");
        overlay.prefWidthProperty().bind(root.widthProperty());
        overlay.prefHeightProperty().bind(root.heightProperty());

        root.getChildren().addAll(overlay, card, btnExit);
        root.setPadding(new Insets(18));
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
        btnLogin.setOnAction(e -> {
            String u = txtUser.getText();
            String p = txtPass.getText();

            Optional<User> userGasit = userRepo.login(u, p);

            if (userGasit.isPresent()) {
                WindowState.rememberFullScreen(stage.isFullScreen());
                deschideInterfata(stage, userGasit.get());
            } else {
                lblMesaj.setText("Date incorecte. Verifică user/parola și încearcă din nou.");
            }
        });

        btnGuest.setOnAction(e -> {
            User guestUser = new User("guest", "", "Vizitator", User.Role.CLIENT);
            WindowState.rememberFullScreen(stage.isFullScreen());
            deschideInterfata(stage, guestUser);
        });

        Scene scene = new Scene(root, 520, 480);
        var css = RestaurantGUI.class.getResource("/mip/restaurantfx/theme.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
        stage.setScene(scene);

        StageUtil.keepMaximized(stage);

        stage.show();

        StageUtil.keepMaximized(stage);
    }

    private void deschideInterfata(Stage stage, User user) {
        WindowState.rememberFullScreen(stage.isFullScreen());

        if (user.getRol() == User.Role.CLIENT) {
            new ClientView(AppContext.services().clientMenu(), AppContext.services().productImages()).start(stage, user);
        }
        else if (user.getRol() == User.Role.STAFF) {
            new StaffMeseView(AppContext.services()).start(stage, user);
        }
        else if (user.getRol() == User.Role.ADMIN) {
            new AdminView(AppContext.services().admin()).start(stage, user);
        }

        StageUtil.keepMaximized(stage);
    }
    @Override
    public void stop() {
        PersistenceManager.getInstance().close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
