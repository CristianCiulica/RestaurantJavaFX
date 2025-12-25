package mip.restaurantfx;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
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
        BorderPane content = new BorderPane();
        content.setPadding(new Insets(16));

        LoadingOverlay overlay = new LoadingOverlay(content);

        Button btnExit = new Button("X");
        btnExit.getStyleClass().add("exit");
        btnExit.setOnAction(e -> ExitUtil.confirmAndExit(stage));

        Button btnBack = new Button("Înapoi");
        btnBack.getStyleClass().add("outline");
        btnBack.setOnAction(e -> {
            WindowState.rememberFullScreen(stage.isFullScreen());
            new StaffMeseView(AppContext.services()).start(stage, ospatar);
        });

        Label title = new Label("Istoricul meu");
        title.getStyleClass().add("title");
        title.setStyle("-fx-font-size: 18px;");

        Button btnRefresh = new Button("Refresh");
        btnRefresh.getStyleClass().add("outline");

        HBox top = new HBox(12, btnBack, title, new Region(), btnRefresh, btnExit);
        HBox.setHgrow(top.getChildren().get(2), Priority.ALWAYS);
        top.getStyleClass().add("topbar");
        content.setTop(top);

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
        content.setCenter(center);

        Label empty = new Label("Nu există comenzi încă. Încasează o comandă și revino aici.");
        empty.getStyleClass().add("subtitle");

        Runnable loadAsync = () -> {
            Task<java.util.List<Comanda>> task = new Task<>() {
                @Override
                protected java.util.List<Comanda> call() {
                    return comandaRepo.getIstoricOspatar(ospatar.getId());
                }
            };

            overlay.show("Loading...");
            btnRefresh.setDisable(true);
            tabel.setDisable(true);
            listDetalii.setDisable(true);

            task.setOnSucceeded(ev -> {
                var comenzi = task.getValue();
                tabel.setItems(FXCollections.observableArrayList(comenzi));
                listDetalii.getItems().clear();
                if (comenzi == null || comenzi.isEmpty()) {
                    listDetalii.getItems().setAll(empty.getText());
                }
                btnRefresh.setDisable(false);
                tabel.setDisable(false);
                listDetalii.setDisable(false);
                overlay.hide();
            });

            task.setOnFailed(ev -> {
                btnRefresh.setDisable(false);
                tabel.setDisable(false);
                listDetalii.setDisable(false);
                overlay.hide();
                Throwable ex = task.getException();
                new Alert(Alert.AlertType.ERROR, "Nu s-a putut încărca istoricul: " + (ex == null ? "" : ex.getMessage())).show();
            });

            FxExecutors.db().submit(task);
        };

        loadAsync.run();

        btnRefresh.setOnAction(e -> loadAsync.run());

        Scene scene = new Scene(overlay.getRoot(), 980, 620);
        var css = StaffIstoricView.class.getResource("/mip/restaurantfx/theme.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
        stage.setScene(scene);

        StageUtil.keepMaximized(stage);

        stage.setTitle("La Andrei • Istoric");
        StageUtil.keepMaximized(stage);
    }
}
