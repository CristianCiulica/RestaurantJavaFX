package mip.restaurantfx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class StaffMeseView {
    private final MasaRepository masaRepo = new MasaRepository();
    private final ComandaRepository comandaRepo = new ComandaRepository();
    private User ospatarCurent;

    public void start(Stage stage, User ospatar) {
        this.ospatarCurent = ospatar;

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));

        HBox header = new HBox(12);
        header.getStyleClass().add("topbar");

        VBox titles = new VBox(2);
        Label lblSalut = new Label("Salut, " + ospatar.getNume());
        lblSalut.getStyleClass().add("title");
        lblSalut.setStyle("-fx-font-size: 18px;");
        Label lblSub = new Label("Alege masa pentru a deschide comanda");
        lblSub.getStyleClass().add("subtitle");
        titles.getChildren().addAll(lblSalut, lblSub);

        Button btnIstoric = new Button("Istoricul meu");
        btnIstoric.getStyleClass().addAll("primary", "pos");
        btnIstoric.setOnAction(e -> new StaffIstoricView().start(stage, ospatarCurent));

        Button btnLogout = new Button("Logout");
        btnLogout.getStyleClass().addAll("outline", "pos");
        btnLogout.setOnAction(e -> {
            try {
                new RestaurantGUI().start(stage);
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Eroare la logout: " + ex.getMessage()).show();
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titles, spacer, btnIstoric, btnLogout);

        Region accent = new Region();
        accent.getStyleClass().add("accent-bar");

        VBox topBox = new VBox(header, accent);
        root.setTop(topBox);

        TilePane grid = new TilePane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setPadding(new Insets(16));
        grid.setPrefColumns(4);

        VBox centerCard = new VBox(grid);
        centerCard.getStyleClass().add("card");
        root.setCenter(centerCard);

        List<Masa> mese = masaRepo.getAllMese();
        for (Masa masa : mese) {
            boolean ocupata = comandaRepo.getComandaActiva(masa.getId()) != null;

            Button btnMasa = new Button("Masa " + masa.getNumarMasa() + "\n" + (ocupata ? "Ocupat" : "Liber"));
            btnMasa.setPrefSize(160, 120);
            btnMasa.getStyleClass().addAll("table-tile", ocupata ? "table-occupied" : "table-free");
            btnMasa.setOnAction(e -> deschideComanda(stage, masa));
            grid.getChildren().add(btnMasa);
        }

        Scene scene = new Scene(root, 920, 620);
        var css = StaffMeseView.class.getResource("/mip/restaurantfx/theme.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        stage.setScene(scene);
        stage.setTitle("La Andrei • Staff");
    }

    private void deschideComanda(Stage stage, Masa masa) {
        new StaffComandaView().start(stage, ospatarCurent, masa);
    }
}