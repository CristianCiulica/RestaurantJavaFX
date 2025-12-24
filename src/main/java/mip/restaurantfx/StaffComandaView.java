package mip.restaurantfx;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Optional;

public class StaffComandaView {

    private ProdusRepository produsRepo = new ProdusRepository();
    private ComandaRepository comandaRepo = new ComandaRepository();
    private MasaRepository masaRepo = new MasaRepository();

    private Comanda comandaCurenta;
    private User ospatar;
    private Masa masa;

    // Elemente UI
    private TableView<Object> tabelBon = new TableView<>();
    private Label lblTotal = new Label("Total: 0.00 RON");
    private Label lblDiscountInfo = new Label(""); // Pentru afisare info Happy Hour

    public void start(Stage stage, User ospatar, Masa masaSelectata) {
        this.ospatar = ospatar;
        this.masa = masaSelectata;

        // 1. Initializare Comanda (Noua sau Existenta)
        Comanda existenta = comandaRepo.getComandaActiva(masa.getId());
        if (existenta != null) {
            this.comandaCurenta = existenta;
        } else {
            this.comandaCurenta = new Comanda(masa);
            // IMPORTANT: cand deschidem o comanda noua, masa trebuie marcata ocupata imediat
            if (!masa.isEsteOcupata()) {
                masa.setEsteOcupata(true);
                masaRepo.save(masa);
            }
        }

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));

        // --- HEADER ---
        Label lblTitlu = new Label("Masa " + masa.getNumarMasa() + " • Ospătar: " + ospatar.getNume());
        lblTitlu.getStyleClass().add("title");
        lblTitlu.setStyle("-fx-font-size: 18px;");

        Button btnBack = new Button("Înapoi la mese");
        btnBack.getStyleClass().add("outline");
        btnBack.setOnAction(e -> new StaffMeseView().start(stage, ospatar));

        HBox top = new HBox(12, btnBack, lblTitlu);
        top.getStyleClass().add("topbar");
        root.setTop(top);

        // --- STANGA: Selector Produse ---
        ListView<Produs> listProduse = new ListView<>();
        listProduse.setItems(FXCollections.observableArrayList(produsRepo.getAll()));

        Button btnAdauga = new Button("Adaugă");
        btnAdauga.getStyleClass().add("primary");
        btnAdauga.setMaxWidth(Double.MAX_VALUE);

        VBox stanga = new VBox(10, new Label("Meniu"), listProduse, btnAdauga);
        stanga.getStyleClass().add("card");
        stanga.setPrefWidth(320);
        root.setLeft(stanga);
        BorderPane.setMargin(stanga, new Insets(12, 12, 12, 0));

        // --- DREAPTA: Bon ---
        tabelBon.getColumns().clear();

        TableColumn<Object, String> colNume = new TableColumn<>("Produs");
        colNume.setCellValueFactory(data -> {
            Object row = data.getValue();
            if (row instanceof ComandaItem ci) {
                return new javafx.beans.property.SimpleStringProperty(ci.getProdus().getNume());
            }
            if (row instanceof DetaliuComanda dc) {
                return new javafx.beans.property.SimpleStringProperty(dc.getDescriere());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        TableColumn<Object, String> colCant = new TableColumn<>("Cant.");
        colCant.setCellValueFactory(data -> {
            Object row = data.getValue();
            if (row instanceof ComandaItem ci) {
                return new javafx.beans.property.SimpleStringProperty(String.valueOf(ci.getCantitate()));
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        TableColumn<Object, String> colPret = new TableColumn<>("Subtotal");
        colPret.setCellValueFactory(data -> {
            Object row = data.getValue();
            if (row instanceof ComandaItem ci) {
                return new javafx.beans.property.SimpleStringProperty(String.format("%.2f", ci.getSubtotal()));
            }
            if (row instanceof DetaliuComanda dc) {
                return new javafx.beans.property.SimpleStringProperty(String.format("%.2f", dc.getValoare()));
            }
            return new javafx.beans.property.SimpleStringProperty("0.00");
        });

        tabelBon.getColumns().addAll(colNume, colCant, colPret);
        tabelBon.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        refreshTabel(); // Populeaza tabelul

        // --- ZONA DE JOS: Total si Actiuni ---
        VBox dreapta = new VBox(10);
        dreapta.getStyleClass().add("card");
        dreapta.setPrefWidth(460);

        // POS controls for selected line
        HBox posControls = new HBox(8);
        Button btnMinus = new Button("−");
        btnMinus.getStyleClass().add("outline");
        Button btnPlus = new Button("+");
        btnPlus.getStyleClass().add("outline");
        Button btnStergeLinie = new Button("Șterge linia");
        btnStergeLinie.getStyleClass().add("outline");
        posControls.getChildren().addAll(btnMinus, btnPlus, btnStergeLinie);

        lblTotal.getStyleClass().add("title");
        lblTotal.setStyle("-fx-font-size: 18px;");
        lblDiscountInfo.getStyleClass().add("subtitle");

        Button btnSalveaza = new Button("Salvează (masa rămâne ocupată)");
        btnSalveaza.getStyleClass().add("outline");

        Button btnFinalizeaza = new Button("Încasează & eliberează masa");
        btnFinalizeaza.getStyleClass().add("danger");

        dreapta.getChildren().addAll(
                new Label("Bon curent"),
                tabelBon,
                posControls,
                lblDiscountInfo,
                lblTotal,
                new Separator(),
                btnSalveaza,
                btnFinalizeaza
        );
        root.setCenter(dreapta);

        // --- LOGICA BUTOANE ---

        // 1. Adaugare Produs
        btnAdauga.setOnAction(e -> {
            Produs p = listProduse.getSelectionModel().getSelectedItem();
            if (p != null) {
                comandaCurenta.adaugaProdus(p, 1);
                recalculeazaTotal();
                refreshTabel();
            }
        });

        // 2. Salvare Intermediara
        btnSalveaza.setOnAction(e -> {
            salveazaComanda(true); // true = ramane ocupata
            new Alert(Alert.AlertType.INFORMATION, "Comanda salvata!").show();
        });

        // 3. Finalizare (Incasare)
        btnFinalizeaza.setOnAction(e -> {
            salveazaComanda(false); // false = eliberam masa
            new Alert(Alert.AlertType.INFORMATION, "Comanda finalizata! Masa este acum libera.").showAndWait();
            new StaffMeseView().start(stage, ospatar); // Ne intoarcem la sala
        });

        // POS actions
        btnPlus.setOnAction(e -> {
            Object row = tabelBon.getSelectionModel().getSelectedItem();
            if (row instanceof ComandaItem ci) {
                ci.setCantitate(ci.getCantitate() + 1);
                recalculeazaTotal();
                refreshTabel();
            }
        });

        btnMinus.setOnAction(e -> {
            Object row = tabelBon.getSelectionModel().getSelectedItem();
            if (row instanceof ComandaItem ci) {
                int newQty = ci.getCantitate() - 1;
                if (newQty <= 0) {
                    comandaCurenta.stergeProdus(ci);
                } else {
                    ci.setCantitate(newQty);
                }
                recalculeazaTotal();
                refreshTabel();
            }
        });

        btnStergeLinie.setOnAction(e -> {
            Object row = tabelBon.getSelectionModel().getSelectedItem();
            if (row instanceof ComandaItem ci) {
                comandaCurenta.stergeProdus(ci);
                recalculeazaTotal();
                refreshTabel();
            }
        });

        // Calcul initial la deschidere
        recalculeazaTotal();

        Scene scene = new Scene(root, 1040, 640);
        scene.getStylesheets().add(StaffComandaView.class.getResource("/mip/restaurantfx/theme.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("La Andrei • Comandă");
    }

    private void refreshTabel() {
        var rows = FXCollections.<Object>observableArrayList();
        if (comandaCurenta != null) {
            rows.addAll(comandaCurenta.getItems());
            rows.addAll(comandaCurenta.getDiscountLines());
        }
        tabelBon.setItems(rows);
        tabelBon.refresh();
    }

    private void recalculeazaTotal() {
        // total de baza (fara reduceri)
        comandaCurenta.clearDiscountLines();
        comandaCurenta.calculeazaTotal();

        // Oferte active (ordine fixa = rezultate predictibile)
        new HappyHourDiscount().aplicaDiscount(comandaCurenta);
        new MealDealDiscount().aplicaDiscount(comandaCurenta);
        new PartyPackDiscount().aplicaDiscount(comandaCurenta);

        // total final
        comandaCurenta.calculeazaTotal();
        lblTotal.setText(String.format("TOTAL DE PLATA: %.2f RON", comandaCurenta.getTotal()));
    }

    private void salveazaComanda(boolean ramaneOcupata) {
        // Recalculeaza inainte de salvare (ca sa persiste totalul si discounturile de azi)
        recalculeazaTotal();

        // atasam ospatarul (pentru istoric)
        comandaCurenta.setOspatar(ospatar);

        // Actualizam statusul mesei
        masa.setEsteOcupata(ramaneOcupata);
        masaRepo.save(masa);

        if (!ramaneOcupata) {
            comandaCurenta.setStatus(Comanda.StatusComanda.PLATITA);
        }

        comandaRepo.save(comandaCurenta);
    }
}

