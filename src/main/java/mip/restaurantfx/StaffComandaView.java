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
import mip.restaurantfx.service.ClientMenuService;
import mip.restaurantfx.service.OrderService;

public class StaffComandaView {

    private final OrderService orderService;
    private final ClientMenuService menuService;

    private Comanda comandaCurenta;
    private User ospatar;
    private Masa masa;

    private TableView<Object> tabelBon = new TableView<>();
    private Label lblTotal = new Label("Total: 0.00 RON");
    private Label lblDiscountInfo = new Label("");

    public StaffComandaView(OrderService orderService, ClientMenuService menuService) {
        this.orderService = orderService;
        this.menuService = menuService;
    }

    public void start(Stage stage, User ospatar, Masa masaSelectata) {
        this.ospatar = ospatar;
        this.masa = masaSelectata;

        this.comandaCurenta = orderService.loadOrCreateActiveOrder(masa);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));

        Label lblTitlu = new Label("Masa " + masa.getNumarMasa() + " • Ospătar: " + ospatar.getNume());
        lblTitlu.getStyleClass().add("title");
        lblTitlu.setStyle("-fx-font-size: 18px;");

        Button btnBack = new Button("Înapoi la mese");
        btnBack.getStyleClass().add("outline");
        btnBack.setOnAction(e -> new StaffMeseView(mip.restaurantfx.service.AppContext.services()).start(stage, ospatar));

        HBox top = new HBox(12, btnBack, lblTitlu);
        top.getStyleClass().add("topbar");
        root.setTop(top);

        ListView<Produs> listProduse = new ListView<>();
        listProduse.setItems(FXCollections.observableArrayList(menuService.getAllProducts()));

        Button btnAdauga = new Button("Adaugă");
        btnAdauga.getStyleClass().add("primary");
        btnAdauga.setMaxWidth(Double.MAX_VALUE);

        VBox stanga = new VBox(10, new Label("Meniu"), listProduse, btnAdauga);
        stanga.getStyleClass().add("card");
        stanga.setPrefWidth(320);
        root.setLeft(stanga);
        BorderPane.setMargin(stanga, new Insets(12, 12, 12, 0));

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

        refreshTabel();

        VBox dreapta = new VBox(10);
        dreapta.getStyleClass().add("card");
        dreapta.setPrefWidth(460);

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

        btnAdauga.setOnAction(e -> {
            Produs p = listProduse.getSelectionModel().getSelectedItem();
            if (p != null) {
                orderService.addProduct(comandaCurenta, p, 1);
                refreshTabel();
                updateTotal();
            }
        });

        btnSalveaza.setOnAction(e -> {
            orderService.saveOrder(comandaCurenta, ospatar, masa, true);
            new Alert(Alert.AlertType.INFORMATION, "Comanda salvata!").show();
            refreshTabel();
            updateTotal();
        });

        btnFinalizeaza.setOnAction(e -> {
            orderService.saveOrder(comandaCurenta, ospatar, masa, false);
            new Alert(Alert.AlertType.INFORMATION, "Comanda finalizata! Masa este acum libera.").showAndWait();
            new StaffMeseView(mip.restaurantfx.service.AppContext.services()).start(stage, ospatar);
        });

        btnPlus.setOnAction(e -> {
            Object row = tabelBon.getSelectionModel().getSelectedItem();
            if (row instanceof ComandaItem ci) {
                orderService.changeQuantity(comandaCurenta, ci, +1);
                refreshTabel();
                updateTotal();
            }
        });

        btnMinus.setOnAction(e -> {
            Object row = tabelBon.getSelectionModel().getSelectedItem();
            if (row instanceof ComandaItem ci) {
                orderService.changeQuantity(comandaCurenta, ci, -1);
                refreshTabel();
                updateTotal();
            }
        });

        btnStergeLinie.setOnAction(e -> {
            Object row = tabelBon.getSelectionModel().getSelectedItem();
            if (row instanceof ComandaItem ci) {
                orderService.removeItem(comandaCurenta, ci);
                refreshTabel();
                updateTotal();
            }
        });

        orderService.recalculateTotal(comandaCurenta);
        updateTotal();

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

    private void updateTotal() {
        if (comandaCurenta == null) {
            lblTotal.setText("TOTAL DE PLATA: 0.00 RON");
            return;
        }
        lblTotal.setText(String.format("TOTAL DE PLATA: %.2f RON", comandaCurenta.getTotal()));
    }
}
