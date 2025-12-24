package mip.restaurantfx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class StaffMeseView {
    private MasaRepository masaRepo = new MasaRepository();
    private ComandaRepository comandaRepo = new ComandaRepository();
    private User ospatarCurent;

    public void start(Stage stage, User ospatar) {
        this.ospatarCurent = ospatar;

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));

        // --- Header ---
        HBox header = new HBox(12);
        header.getStyleClass().add("topbar");

        VBox titles = new VBox(2);
        Label lblSalut = new Label("Salut, " + ospatar.getNume());
        lblSalut.getStyleClass().add("title");
        lblSalut.setStyle("-fx-font-size: 18px;");
        Label lblSub = new Label("Selectează o masă pentru a deschide comanda");
        lblSub.getStyleClass().add("subtitle");
        titles.getChildren().addAll(lblSalut, lblSub);

        Button btnIstoric = new Button("Istoricul meu");
        btnIstoric.getStyleClass().add("outline");
        btnIstoric.setOnAction(e -> new StaffIstoricView().start(stage, ospatarCurent));

        Button btnLogout = new Button("Logout");
        btnLogout.getStyleClass().add("outline");
        btnLogout.setOnAction(e -> {
            try {
                new RestaurantGUI().start(stage);
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titles, spacer, btnIstoric, btnLogout);
        root.setTop(header);

        // --- Zona Centrala: Lista de Mese ---
        FlowPane mesePanel = new FlowPane();
        mesePanel.setHgap(16);
        mesePanel.setVgap(16);
        mesePanel.setPadding(new Insets(16));

        VBox centerCard = new VBox(12, new Label("Sala"), mesePanel);
        centerCard.getStyleClass().add("card");
        root.setCenter(centerCard);

        List<Masa> mese = masaRepo.getAllMese();

        for (Masa masa : mese) {
            boolean ocupata = comandaRepo.getComandaActiva(masa.getId()) != null;

            Button btnMasa = new Button("Masa " + masa.getNumarMasa() + "\n" + (ocupata ? "Ocupat" : "Liber"));
            btnMasa.setPrefSize(140, 110);
            btnMasa.getStyleClass().addAll("table-tile", ocupata ? "table-occupied" : "table-free");

            btnMasa.setOnAction(e -> {
                deschideComanda(stage, masa);
            });
            mesePanel.getChildren().add(btnMasa);
        }

        Scene scene = new Scene(root, 920, 620);
        scene.getStylesheets().add(StaffMeseView.class.getResource("/mip/restaurantfx/theme.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("La Andrei • Staff");
    }

    private void deschideComanda(Stage stage, Masa masa) {
        new StaffComandaView().start(stage, ospatarCurent, masa);
    }
}