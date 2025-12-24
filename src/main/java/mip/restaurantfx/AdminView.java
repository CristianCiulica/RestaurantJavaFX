package mip.restaurantfx;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Interfata Manager (Admin):
 * - Personal: lista ospatarilor (vizualizare + adaugare rapida)
 * - Meniu: refresh + import/export JSON
 * - Istoric global: tabel cu toate comenzile platite + detalii
 */
public class AdminView {

    private final UserRepository userRepo = new UserRepository();
    private final ProdusRepository produsRepo = new ProdusRepository();
    private final ComandaRepository comandaRepo = new ComandaRepository();

    public void start(Stage stage, User admin) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));

        Button btnBack = new Button("Logout");
        btnBack.getStyleClass().add("outline");
        btnBack.setOnAction(e -> {
            try {
                new RestaurantGUI().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Label title = new Label("Manager • " + admin.getNume());
        title.getStyleClass().add("title");
        title.setStyle("-fx-font-size: 18px;");

        HBox top = new HBox(12, btnBack, title);
        top.getStyleClass().add("topbar");
        root.setTop(top);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabs.getTabs().addAll(
                tabPersonal(),
                tabMeniu(stage),
                tabOferte(),
                tabIstoricGlobal()
        );

        root.setCenter(tabs);

        Scene scene = new Scene(root, 1100, 720);
        var css = AdminView.class.getResource("/mip/restaurantfx/theme.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        stage.setScene(scene);
        stage.setTitle("La Andrei • Manager");
        stage.show();
    }

    private Tab tabPersonal() {
        Tab tab = new Tab("Personal");

        VBox card = new VBox(10);
        card.getStyleClass().add("card");

        Label lbl = new Label("Ospătari");

        ListView<String> list = new ListView<>();

        Runnable loadStaff = () -> list.setItems(FXCollections.observableArrayList(
                userRepo.getAllStaff().stream()
                        .map(u -> u.getNume() + " (" + u.getUsername() + ")")
                        .toList()
        ));
        loadStaff.run();

        HBox addBox = new HBox(8);
        TextField txtUser = new TextField();
        txtUser.setPromptText("username");
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("parola");
        TextField txtNume = new TextField();
        txtNume.setPromptText("nume" );

        Button btnAdd = new Button("Adaugă");
        btnAdd.getStyleClass().add("primary");

        Button btnDelete = new Button("Șterge");
        btnDelete.getStyleClass().add("outline");

        btnAdd.setOnAction(e -> {
            if (txtUser.getText().isBlank() || txtPass.getText().isBlank() || txtNume.getText().isBlank()) {
                new Alert(Alert.AlertType.WARNING, "Completează toate câmpurile.").show();
                return;
            }
            try {
                userRepo.save(new User(txtUser.getText().trim(), txtPass.getText(), txtNume.getText().trim(), User.Role.STAFF));
                new Alert(Alert.AlertType.INFORMATION, "Ospătar adăugat.").show();
                txtUser.clear(); txtPass.clear(); txtNume.clear();
                loadStaff.run();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Nu s-a putut salva userul (posibil username deja existent).",
                        ButtonType.OK).show();
                ex.printStackTrace();
            }
        });

        btnDelete.setOnAction(e -> {
            String selected = list.getSelectionModel().getSelectedItem();
            if (selected == null || selected.isBlank()) {
                new Alert(Alert.AlertType.WARNING, "Selectează un ospătar din listă ca să-l ștergi.").show();
                return;
            }

            // format: "Nume (username)" => extragem username
            int open = selected.lastIndexOf('(');
            int close = selected.lastIndexOf(')');
            if (open < 0 || close < 0 || close <= open + 1) {
                new Alert(Alert.AlertType.ERROR, "Nu pot determina username-ul din selecție.").show();
                return;
            }
            String username = selected.substring(open + 1, close).trim();

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Sigur vrei să ștergi ospătarul: " + selected + " ?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText(null);
            var res = confirm.showAndWait();
            if (res.isEmpty() || res.get() != ButtonType.YES) return;

            try {
                boolean deleted = userRepo.deleteByUsername(username);
                if (deleted) {
                    new Alert(Alert.AlertType.INFORMATION, "Ospătar șters.").show();
                    loadStaff.run();
                } else {
                    new Alert(Alert.AlertType.WARNING, "Userul nu mai există.").show();
                    loadStaff.run();
                }
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Nu s-a putut șterge userul. Verifică dacă are comenzi asociate.").show();
                ex.printStackTrace();
            }
        });

        addBox.getChildren().addAll(txtUser, txtPass, txtNume, btnAdd, btnDelete);

        card.getChildren().addAll(lbl, new Separator(), addBox, list);
        tab.setContent(card);
        return tab;
    }

    private Tab tabMeniu(Stage stage) {
        Tab tab = new Tab("Meniu");

        VBox card = new VBox(10);
        card.getStyleClass().add("card");

        Label lbl = new Label("Meniu (produse din DB)");
        ListView<Produs> list = new ListView<>();
        list.setItems(FXCollections.observableArrayList(produsRepo.getAll()));

        HBox actions = new HBox(8);
        Button btnRefresh = new Button("Refresh");
        btnRefresh.getStyleClass().add("outline");
        btnRefresh.setOnAction(e -> list.setItems(FXCollections.observableArrayList(produsRepo.getAll())));

        Button btnExport = new Button("Export JSON");
        btnExport.getStyleClass().add("outline");
        btnExport.setOnAction(e -> new RestaurantFXExportImport().exportaProduse(stage, produsRepo.getAll()));

        Button btnImport = new Button("Import JSON");
        btnImport.getStyleClass().add("primary");
        btnImport.setOnAction(e -> {
            var imported = new RestaurantFXExportImport().importaProduse(stage);
            if (imported != null) {
                for (Produs p : imported) produsRepo.salveazaProdus(p);
                list.setItems(FXCollections.observableArrayList(produsRepo.getAll()));
            }
        });

        actions.getChildren().addAll(btnRefresh, btnExport, btnImport);
        card.getChildren().addAll(lbl, new Separator(), actions, list);

        tab.setContent(card);
        return tab;
    }

    private Tab tabOferte() {
        Tab tab = new Tab("Oferte");

        VBox card = new VBox(10);
        card.getStyleClass().add("card");

        Label lbl = new Label("Oferte active (demo)");
        Label hint = new Label("În cod, ofertele sunt active automat. Aici e panoul de control pentru manager (UI)." );
        hint.getStyleClass().add("subtitle");

        CheckBox c1 = new CheckBox("Happy Hour (a 2-a băutură -50%)");
        c1.setSelected(true);
        CheckBox c2 = new CheckBox("Meal Deal (desert -25% la Pizza)");
        c2.setSelected(true);
        CheckBox c3 = new CheckBox("Party Pack (1 Pizza gratis la 4)");
        c3.setSelected(true);

        card.getChildren().addAll(lbl, hint, new Separator(), c1, c2, c3);
        tab.setContent(card);
        return tab;
    }

    private Tab tabIstoricGlobal() {
        Tab tab = new Tab("Istoric");

        VBox card = new VBox(10);
        card.getStyleClass().add("card");

        Label lbl = new Label("Istoric global (comenzi platite)");
        lbl.getStyleClass().add("subtitle");

        TableView<Comanda> tabel = new TableView<>();
        TableColumn<Comanda, String> colMasa = new TableColumn<>("Masa");
        TableColumn<Comanda, String> colOsp = new TableColumn<>("Ospătar");
        TableColumn<Comanda, String> colTotal = new TableColumn<>("Total");

        colMasa.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getMasa() == null ? "-" : String.valueOf(c.getValue().getMasa().getNumarMasa())
        ));
        colOsp.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getOspatar() == null ? "-" : c.getValue().getOspatar().getNume()
        ));
        colTotal.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.format("%.2f", c.getValue().getTotal())));

        tabel.getColumns().addAll(colMasa, colOsp, colTotal);
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ListView<String> detalii = new ListView<>();
        VBox detCard = new VBox(10, new Label("Detalii"), detalii);
        detCard.getStyleClass().add("card");
        detCard.setPrefWidth(380);

        Button btnRefresh = new Button("Refresh");
        btnRefresh.getStyleClass().add("outline");

        HBox center = new HBox(12, new VBox(10, btnRefresh, tabel), detCard);

        card.getChildren().addAll(lbl, new Separator(), center);
        tab.setContent(card);

        Runnable load = () -> tabel.setItems(FXCollections.observableArrayList(comandaRepo.getIstoricGlobal()));
        load.run();

        btnRefresh.setOnAction(e -> {
            detalii.getItems().clear();
            load.run();
        });

        tabel.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            detalii.getItems().clear();
            if (n == null) return;
            if (n.getOspatar() != null) detalii.getItems().add("Ospătar: " + n.getOspatar().getNume());
            if (n.getMasa() != null) detalii.getItems().add("Masa: " + n.getMasa().getNumarMasa());
            detalii.getItems().add("----------------");
            for (ComandaItem it : n.getItems()) {
                detalii.getItems().add(String.format("%dx %s | %.2f", it.getCantitate(), it.getProdus().getNume(), it.getSubtotal()));
            }
            for (DetaliuComanda d : n.getDiscountLines()) {
                detalii.getItems().add(String.format("%s | %.2f", d.getDescriere(), d.getValoare()));
            }
            detalii.getItems().add("----------------");
            detalii.getItems().add(String.format("TOTAL: %.2f", n.getTotal()));
        });

        return tab;
    }
}
