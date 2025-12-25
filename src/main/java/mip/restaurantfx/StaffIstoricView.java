package mip.restaurantfx;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import mip.restaurantfx.service.AppContext;

import java.time.format.DateTimeFormatter;

public class StaffIstoricView {

    private final ComandaRepository comandaRepo;

    public StaffIstoricView(ComandaRepository comandaRepo) {
        this.comandaRepo = comandaRepo;
    }

    public void start(Stage stage, User ospatar) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));

        Button btnBack = new Button("Înapoi");
        btnBack.getStyleClass().add("outline");
        btnBack.setOnAction(e -> new StaffMeseView(AppContext.services()).start(stage, ospatar));

        Label title = new Label("Istoricul meu");
        title.getStyleClass().add("title");
        title.setStyle("-fx-font-size: 18px;");

        Button btnRefresh = new Button("Refresh");
        btnRefresh.getStyleClass().add("outline");

        HBox top = new HBox(12, btnBack, title, new Region(), btnRefresh);
        HBox.setHgrow(top.getChildren().get(2), Priority.ALWAYS);
        top.getStyleClass().add("topbar");
        root.setTop(top);

        TableView<Comanda> tabel = new TableView<>();
        TableColumn<Comanda, String> colData = new TableColumn<>("Data");
        TableColumn<Comanda, String> colMasa = new TableColumn<>("Masa");
        TableColumn<Comanda, String> colTotal = new TableColumn<>("Total");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM HH:mm");

        colData.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getDataCreare() == null ? "" : cd.getValue().getDataCreare().format(fmt)
        ));
        colMasa.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getMasa() == null ? "-" : String.valueOf(cd.getValue().getMasa().getNumarMasa())
        ));
        colTotal.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(String.format("%.2f", cd.getValue().getTotal())));

        tabel.getColumns().addAll(colData, colMasa, colTotal);
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ListView<String> listDetalii = new ListView<>();
        VBox detaliiCard = new VBox(10, new Label("Detalii bon"), listDetalii);
        detaliiCard.getStyleClass().add("card");
        detaliiCard.setPrefWidth(360);

        VBox tabelCard = new VBox(10, new Label("Comenzi platite"), tabel);
        tabelCard.getStyleClass().add("card");

        HBox center = new HBox(12, tabelCard, detaliiCard);
        root.setCenter(center);

        Label empty = new Label("Nu există comenzi încă. Încasează o comandă și revino aici.");
        empty.getStyleClass().add("subtitle");

        var comenzi = comandaRepo.getIstoricOspatar(ospatar.getId());
        tabel.setItems(FXCollections.observableArrayList(comenzi));
        if (comenzi.isEmpty()) {
            listDetalii.getItems().setAll(empty.getText());
        }

        tabel.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            listDetalii.getItems().clear();
            if (newV == null) return;

            for (ComandaItem it : newV.getItems()) {
                listDetalii.getItems().add(String.format("%dx %s  |  %.2f", it.getCantitate(), it.getProdus().getNume(), it.getSubtotal()));
            }
            for (DetaliuComanda d : newV.getDiscountLines()) {
                listDetalii.getItems().add(String.format("%s  |  %.2f", d.getDescriere(), d.getValoare()));
            }
            listDetalii.getItems().add("--------------------");
            listDetalii.getItems().add(String.format("TOTAL: %.2f", newV.getTotal()));
        });

        btnRefresh.setOnAction(e -> {
            var refreshed = comandaRepo.getIstoricOspatar(ospatar.getId());
            tabel.setItems(FXCollections.observableArrayList(refreshed));
            listDetalii.getItems().clear();
            if (refreshed.isEmpty()) {
                listDetalii.getItems().setAll(empty.getText());
            }
        });

        Scene scene = new Scene(root, 980, 620);
        var css = StaffIstoricView.class.getResource("/mip/restaurantfx/theme.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
        stage.setScene(scene);
        stage.setTitle("La Andrei • Istoric");
    }
}
