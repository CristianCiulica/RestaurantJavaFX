package mip.restaurantfx;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import mip.restaurantfx.service.AdminService;
import mip.restaurantfx.service.OfferConfigService;

public class AdminView {

    private final AdminService adminService;

    public AdminView(AdminService adminService) {
        this.adminService = adminService;
    }

    public void start(Stage stage, User admin) {
        BorderPane content = new BorderPane();
        content.setPadding(new Insets(16));

        LoadingOverlay overlay = new LoadingOverlay(content);

        Button btnExit = new Button("X");
        btnExit.getStyleClass().add("exit");
        btnExit.setOnAction(e -> ExitUtil.confirmAndExit(stage));

        Button btnBack = new Button("Logout");
        btnBack.getStyleClass().add("outline");
        btnBack.setOnAction(e -> {
            try {
                WindowState.rememberFullScreen(stage.isFullScreen());
                new RestaurantGUI().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Label title = new Label("Manager • " + admin.getNume());
        title.getStyleClass().add("title");
        title.setStyle("-fx-font-size: 18px;");

        HBox top = new HBox(12, btnBack, title, new Region(), btnExit);
        HBox.setHgrow(top.getChildren().get(2), Priority.ALWAYS);
        top.getStyleClass().add("topbar");
        content.setTop(top);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabs.getTabs().addAll(
                tabPersonal(),
                tabMeniu(stage, overlay),
                tabOferte(),
                tabIstoricGlobal(overlay)
        );

        content.setCenter(tabs);

        Scene scene = new Scene(overlay.getRoot(), 1100, 720);
        var css = AdminView.class.getResource("/mip/restaurantfx/theme.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        stage.setScene(scene);

        StageUtil.keepMaximized(stage);

        stage.setTitle("La Andrei • Manager");
        stage.show();

        StageUtil.keepMaximized(stage);
    }

    private Tab tabPersonal() {
        Tab tab = new Tab("Personal");

        VBox card = new VBox(10);
        card.getStyleClass().add("card");

        Label lbl = new Label("Ospătari");

        ListView<String> list = new ListView<>();

        Runnable loadStaff = () -> list.setItems(FXCollections.observableArrayList(
                adminService.getAllStaff().stream()
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
                adminService.addStaff(txtUser.getText(), txtPass.getText(), txtNume.getText());
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

            int open = selected.lastIndexOf('(');
            int close = selected.lastIndexOf(')');
            if (open < 0 || close < 0 || close <= open + 1) {
                new Alert(Alert.AlertType.ERROR, "Nu pot determina username-ul din selecție.").show();
                return;
            }
            String username = selected.substring(open + 1, close).trim();

            Alert confirm1 = new Alert(Alert.AlertType.CONFIRMATION,
                    "Sigur vrei să concediezi ospătarul: " + selected + " ?",
                    ButtonType.YES, ButtonType.NO);
            confirm1.setHeaderText("Confirmare");
            var r1 = confirm1.showAndWait();
            if (r1.isEmpty() || r1.get() != ButtonType.YES) return;

            Alert confirm2 = new Alert(Alert.AlertType.CONFIRMATION,
                    "ATENȚIE: Această acțiune va șterge DEFINITIV ospătarul și TOT istoricul lui. \n\nConfirmi încă o dată?",
                    ButtonType.YES, ButtonType.NO);
            confirm2.setHeaderText("Ștergere critică");
            var r2 = confirm2.showAndWait();
            if (r2.isEmpty() || r2.get() != ButtonType.YES) return;

            try {
                boolean deleted = adminService.deleteStaffByUsername(username);
                if (deleted) {
                    new Alert(Alert.AlertType.INFORMATION, "Ospătar șters. Istoricul lui a fost șters în cascadă.").show();
                    loadStaff.run();
                } else {
                    new Alert(Alert.AlertType.WARNING, "Userul nu mai există.").show();
                    loadStaff.run();
                }
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Nu s-a putut șterge userul.").show();
                ex.printStackTrace();
            }
        });

        addBox.getChildren().addAll(txtUser, txtPass, txtNume, btnAdd, btnDelete);

        card.getChildren().addAll(lbl, new Separator(), addBox, list);
        tab.setContent(card);
        return tab;
    }

    private Tab tabMeniu(Stage stage, LoadingOverlay overlay) {
        Tab tab = new Tab("Meniu");

        VBox card = new VBox(10);
        card.getStyleClass().add("card");

        Label lbl = new Label("Meniu (produse din DB)");
        ListView<Produs> list = new ListView<>();

        Runnable loadProducts = () -> list.setItems(FXCollections.observableArrayList(adminService.getAllProducts()));
        loadProducts.run();

        Button btnAdd = new Button("Adaugă produs");
        btnAdd.getStyleClass().add("primary");

        Button btnEdit = new Button("Modifică produs");
        btnEdit.getStyleClass().add("outline");

        Button btnDelete = new Button("Șterge produs");
        btnDelete.getStyleClass().add("danger");

        Button btnRefresh = new Button("Refresh");
        btnRefresh.getStyleClass().add("outline");

        Button btnExport = new Button("Export JSON");
        btnExport.getStyleClass().add("outline");

        Button btnImport = new Button("Import JSON");
        btnImport.getStyleClass().add("primary");

        btnAdd.setOnAction(e -> {
            Produs created = showProductDialog(null);
            if (created == null) return;
            try {
                adminService.saveProduct(created);
                loadProducts.run();
                new Alert(Alert.AlertType.INFORMATION, "Produs adăugat.").show();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Nu s-a putut adăuga produsul: " + ex.getMessage()).show();
            }
        });

        btnEdit.setOnAction(e -> {
            Produs selected = list.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Selectează un produs ca să-l modifici.").show();
                return;
            }
            Produs updated = showProductDialog(selected);
            if (updated == null) return;
            try {
                adminService.saveProduct(updated);
                loadProducts.run();
                new Alert(Alert.AlertType.INFORMATION, "Produs modificat.").show();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Nu s-a putut modifica produsul: " + ex.getMessage()).show();
            }
        });

        btnDelete.setOnAction(e -> {
            Produs selected = list.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Selectează un produs ca să-l ștergi.").show();
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Scoți produsul din meniu: " + selected.getNume() + " ?\n\nIstoricul comenzilor rămâne intact.",
                    ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText("Confirmare");
            var res = confirm.showAndWait();
            if (res.isEmpty() || res.get() != ButtonType.YES) return;

            try {
                boolean ok = adminService.deleteProductById(selected.getId());
                loadProducts.run();
                if (ok) {
                    new Alert(Alert.AlertType.INFORMATION, "Produs scos din meniu.").show();
                } else {
                    new Alert(Alert.AlertType.WARNING, "Produsul nu mai există.").show();
                }
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Nu s-a putut scoate produsul din meniu: " + ex.getMessage()).show();
            }
        });

        btnRefresh.setOnAction(e -> loadProducts.run());
        btnExport.setOnAction(e -> new RestaurantFXExportImport().exportaProduse(stage, adminService.getAllProducts()));
        btnImport.setOnAction(e -> {
            var fileChooserRes = new RestaurantFXExportImport().importaProduse(stage);
            if (fileChooserRes == null) return;

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    for (Produs p : fileChooserRes) {
                        adminService.saveProduct(p);
                    }
                    return null;
                }
            };

            overlay.show("Importing...");
            btnImport.setDisable(true);
            btnExport.setDisable(true);
            btnRefresh.setDisable(true);

            task.setOnSucceeded(ev -> {
                btnImport.setDisable(false);
                btnExport.setDisable(false);
                btnRefresh.setDisable(false);
                overlay.hide();
                loadProducts.run();
                new Alert(Alert.AlertType.INFORMATION, "Import finalizat.").show();
            });

            task.setOnFailed(ev -> {
                btnImport.setDisable(false);
                btnExport.setDisable(false);
                btnRefresh.setDisable(false);
                overlay.hide();
                Throwable ex = task.getException();
                new Alert(Alert.AlertType.ERROR, "Import eșuat: " + (ex == null ? "" : ex.getMessage())).show();
            });

            FxExecutors.db().submit(task);
        });

        HBox crud = new HBox(10, btnAdd, btnEdit, btnDelete);
        HBox aux = new HBox(10, btnRefresh, btnExport, btnImport);
        VBox buttons = new VBox(8, crud, aux);

        card.getChildren().addAll(lbl, new Separator(), buttons, list);

        tab.setContent(card);
        return tab;
    }

    private Tab tabOferte() {
        Tab tab = new Tab("Oferte");

        VBox card = new VBox(10);
        card.getStyleClass().add("card");

        Label lbl = new Label("Oferte active");
        Label hint = new Label("Bifează/debifează ce reguli de discount sunt aplicate la comenzile ospătarilor.");
        hint.getStyleClass().add("subtitle");

        CheckBox c1 = new CheckBox("Happy Hour (a 2-a băutură -50%)");
        c1.setSelected(adminService.isOfferEnabled(OfferConfigService.OfferKey.HAPPY_HOUR));

        CheckBox c2 = new CheckBox("Meal Deal (desert -25% la Pizza)");
        c2.setSelected(adminService.isOfferEnabled(OfferConfigService.OfferKey.MEAL_DEAL));

        CheckBox c3 = new CheckBox("Party Pack (1 Pizza gratis la 4)");
        c3.setSelected(adminService.isOfferEnabled(OfferConfigService.OfferKey.PARTY_PACK));

        c1.selectedProperty().addListener((obs, oldV, newV) ->
                adminService.setOfferEnabled(OfferConfigService.OfferKey.HAPPY_HOUR, Boolean.TRUE.equals(newV)));
        c2.selectedProperty().addListener((obs, oldV, newV) ->
                adminService.setOfferEnabled(OfferConfigService.OfferKey.MEAL_DEAL, Boolean.TRUE.equals(newV)));
        c3.selectedProperty().addListener((obs, oldV, newV) ->
                adminService.setOfferEnabled(OfferConfigService.OfferKey.PARTY_PACK, Boolean.TRUE.equals(newV)));

        card.getChildren().addAll(lbl, hint, new Separator(), c1, c2, c3);
        tab.setContent(card);
        return tab;
    }

    private Tab tabIstoricGlobal(LoadingOverlay overlay) {
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

        Button btnStergeIstoric = new Button("Șterge istoric");
        btnStergeIstoric.getStyleClass().add("danger");

        HBox actions = new HBox(8, btnRefresh, btnStergeIstoric);

        HBox center = new HBox(12, new VBox(10, actions, tabel), detCard);

        card.getChildren().addAll(lbl, new Separator(), center);
        tab.setContent(card);

        Runnable loadAsync = () -> {
            Task<java.util.List<Comanda>> task = new Task<>() {
                @Override
                protected java.util.List<Comanda> call() {
                    return adminService.getGlobalHistory();
                }
            };

            overlay.show("Loading...");
            btnRefresh.setDisable(true);
            btnStergeIstoric.setDisable(true);
            tabel.setDisable(true);

            task.setOnSucceeded(ev -> {
                tabel.setItems(FXCollections.observableArrayList(task.getValue()));
                btnRefresh.setDisable(false);
                btnStergeIstoric.setDisable(false);
                tabel.setDisable(false);
                overlay.hide();
            });

            task.setOnFailed(ev -> {
                btnRefresh.setDisable(false);
                btnStergeIstoric.setDisable(false);
                tabel.setDisable(false);
                overlay.hide();
                Throwable ex = task.getException();
                new Alert(Alert.AlertType.ERROR, "Nu s-a putut încărca istoricul: " + (ex == null ? "" : ex.getMessage())).show();
            });

            FxExecutors.db().submit(task);
        };

        loadAsync.run();
        btnRefresh.setOnAction(e -> loadAsync.run());

        btnStergeIstoric.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "ATENȚIE: Aceasta va șterge DEFINITIV întreg istoricul de comenzi (toate comenzile) din baza de date.\n\nContinui?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText("Ștergere istoric global");
            var res = confirm.showAndWait();
            if (res.isEmpty() || res.get() != ButtonType.YES) return;

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    adminService.deleteAllHistory();
                    return null;
                }
            };

            overlay.show("Ștergere istoric...");
            tabel.setDisable(true);
            btnRefresh.setDisable(true);
            btnStergeIstoric.setDisable(true);

            task.setOnSucceeded(ev -> {
                tabel.setDisable(false);
                btnRefresh.setDisable(false);
                btnStergeIstoric.setDisable(false);
                overlay.hide();
                loadAsync.run();
                new Alert(Alert.AlertType.INFORMATION, "Istoricul a fost șters.").show();
            });

            task.setOnFailed(ev -> {
                tabel.setDisable(false);
                btnRefresh.setDisable(false);
                btnStergeIstoric.setDisable(false);
                overlay.hide();
                Throwable ex = task.getException();
                new Alert(Alert.AlertType.ERROR, "Nu s-a putut șterge istoricul.").show();
                ex.printStackTrace();
            });

            FxExecutors.db().submit(task);
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

    private Produs showProductDialog(Produs existing) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle(existing == null ? "Adaugă produs" : "Modifică produs");

        ComboBox<String> cmbTip = new ComboBox<>();
        cmbTip.setItems(FXCollections.observableArrayList("Mancare", "Bautura"));

        TextField txtNume = new TextField();
        TextField txtPret = new TextField();
        TextField txtCant = new TextField();
        CheckBox chkFlag = new CheckBox();

        txtNume.setPromptText("nume");
        txtPret.setPromptText("pret");
        txtCant.setPromptText("gramaj (mancare) / volum (bautura)");

        Runnable syncFlagLabel = () -> {
            String tip = cmbTip.getValue();
            chkFlag.setText("Bautura".equalsIgnoreCase(tip) ? "Alcoolică" : "Vegetarian");
        };

        if (existing instanceof Bautura) {
            cmbTip.getSelectionModel().select("Bautura");
        } else {
            cmbTip.getSelectionModel().select("Mancare");
        }

        if (existing != null) {
            txtNume.setText(existing.getNume());
            txtPret.setText(String.valueOf(existing.getPret()));
            if (existing instanceof Mancare m) {
                txtCant.setText(String.valueOf(m.getGramaj()));
                chkFlag.setSelected(m.isVegetarian());
            } else if (existing instanceof Bautura b) {
                txtCant.setText(String.valueOf(b.getVolum()));
                chkFlag.setSelected(b.isAlcoolica());
            }
        } else {
            cmbTip.getSelectionModel().selectFirst();
        }

        syncFlagLabel.run();
        cmbTip.valueProperty().addListener((obs, o, n) -> syncFlagLabel.run());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));

        int r = 0;
        grid.addRow(r++, new Label("Tip"), cmbTip);
        grid.addRow(r++, new Label("Nume"), txtNume);
        grid.addRow(r++, new Label("Preț"), txtPret);
        grid.addRow(r++, new Label("Gramaj/Volum"), txtCant);
        grid.addRow(r, new Label(""), chkFlag);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        var res = dlg.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.OK) return null;

        return buildProductFromForm(
                existing == null ? null : existing.getId(),
                cmbTip.getValue(),
                txtNume.getText(),
                txtPret.getText(),
                txtCant.getText(),
                chkFlag.isSelected()
        );
    }

    private Produs buildProductFromForm(Long idOrNull, String tip, String numeRaw, String pretRaw, String cantRaw, boolean flag) {
        String nume = numeRaw == null ? "" : numeRaw.trim();
        if (nume.isEmpty()) throw new IllegalArgumentException("Numele e gol");

        double pret;
        try {
            pret = Double.parseDouble((pretRaw == null ? "" : pretRaw.trim()).replace(',', '.'));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Preț invalid");
        }
        if (pret < 0) throw new IllegalArgumentException("Preț invalid");

        int cant;
        try {
            cant = Integer.parseInt((cantRaw == null ? "" : cantRaw.trim()));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Gramaj/volum invalid");
        }
        if (cant <= 0) throw new IllegalArgumentException("Gramaj/volum invalid");

        Produs p;
        if ("Bautura".equalsIgnoreCase(tip)) {
            p = new Bautura(nume, pret, cant, flag);
        } else {
            p = new Mancare(nume, pret, cant, flag);
        }

        if (idOrNull != null) p.setId(idOrNull);
        return p;
    }
}
