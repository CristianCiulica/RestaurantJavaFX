package mip.restaurantfx;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import mip.restaurantfx.service.ClientMenuService;
import mip.restaurantfx.service.ProductFilterCriteria;
import mip.restaurantfx.service.ProductImageService;

import java.util.List;
import java.util.Optional;

public class ClientView {

    private final ClientMenuService menuService;
    private final ProductImageService imageService;

    private final ListView<Produs> listaProduse = new ListView<>();

    // Detalii
    private final Label lblNume = new Label();
    private final Label lblPret = new Label();
    private final Label lblDetaliiExtra = new Label();
    private final ImageView imgProdus = new ImageView();

    // Filtre
    private final TextField txtSearch = new TextField();
    private final CheckBox chkVegetarian = new CheckBox("Doar Vegetarian");
    private final TextField txtPretMin = new TextField();
    private final TextField txtPretMax = new TextField();

    public ClientView(ClientMenuService menuService, ProductImageService imageService) {
        this.menuService = menuService;
        this.imageService = imageService;
    }

    public void start(Stage stage, User user) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));

        Button btnExit = new Button("X");
        btnExit.getStyleClass().add("exit");
        btnExit.setOnAction(e -> ExitUtil.confirmAndExit(stage));

        // --- Top (filtre) ---
        HBox topPanel = new HBox(10);
        topPanel.getStyleClass().add("topbar");

        Button btnLogout = new Button("Înapoi");
        btnLogout.getStyleClass().add("outline");
        btnLogout.setOnAction(e -> {
            try {
                WindowState.rememberFullScreen(stage.isFullScreen());
                new RestaurantGUI().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        txtSearch.setPromptText("Caută produs...");
        txtPretMin.setPromptText("Preț min");
        txtPretMax.setPromptText("Preț max");
        txtPretMin.setPrefWidth(90);
        txtPretMax.setPrefWidth(90);

        Button btnFiltreaza = new Button("Aplică filtre");
        btnFiltreaza.getStyleClass().add("primary");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topPanel.getChildren().addAll(btnLogout, txtSearch, chkVegetarian, txtPretMin, txtPretMax, btnFiltreaza, spacer, btnExit);
        root.setTop(topPanel);

        // --- Center/Left: listă produse + detalii text sub listă ---
        listaProduse.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> afiseazaDetalii(newVal));
        refreshLista(menuService.getAllProducts());

        // Card detalii text (sub listă)
        VBox detaliiText = new VBox(8);
        detaliiText.getStyleClass().add("card");

        Label lblTitluDetalii = new Label("Detalii produs");
        lblTitluDetalii.getStyleClass().add("subtitle");

        lblNume.getStyleClass().add("title");
        lblNume.setStyle("-fx-font-size: 18px;");
        lblDetaliiExtra.setWrapText(true);

        detaliiText.getChildren().addAll(
                lblTitluDetalii,
                new Separator(),
                new Label("Produs"), lblNume,
                new Label("Preț"), lblPret,
                new Label("Info"), lblDetaliiExtra
        );

        VBox left = new VBox(10);
        left.getStyleClass().add("card");
        Label lblProduse = new Label("Produse");
        left.getChildren().addAll(lblProduse, listaProduse, detaliiText);
        VBox.setVgrow(listaProduse, Priority.ALWAYS);

        root.setCenter(left);
        BorderPane.setMargin(left, new Insets(12, 12, 12, 0));

        // --- Right: doar imaginea ---
        VBox imaginePanel = new VBox(10);
        imaginePanel.getStyleClass().add("card");
        imaginePanel.setPrefWidth(380);

        Label lblImagine = new Label("Poză produs");
        lblImagine.getStyleClass().add("subtitle");

        imgProdus.setPreserveRatio(true);
        imgProdus.setSmooth(true);
        imgProdus.setCache(true);
        imgProdus.setFitWidth(340);
        imgProdus.setFitHeight(520);

        StackPane imageHolder = new StackPane(imgProdus);
        imageHolder.setAlignment(Pos.CENTER);
        imageHolder.setPadding(new Insets(8));
        imageHolder.setMinHeight(540);

        imaginePanel.getChildren().addAll(lblImagine, new Separator(), imageHolder);
        VBox.setVgrow(imageHolder, Priority.ALWAYS);

        root.setRight(imaginePanel);

        // --- Logica filtrare ---
        btnFiltreaza.setOnAction(e -> aplicaFiltre());

        // inițial curățăm detaliile + imagine
        afiseazaDetalii(null);

        Scene scene = new Scene(root, 1180, 680);
        scene.getStylesheets().add(ClientView.class.getResource("/mip/restaurantfx/theme.css").toExternalForm());
        stage.setScene(scene);

        StageUtil.keepMaximized(stage);

        stage.setTitle("La Andrei • Meniu (Guest)");

        // siguranta extra
        StageUtil.keepMaximized(stage);
    }

    private void afiseazaDetalii(Produs p) {
        if (p == null) {
            lblNume.setText("");
            lblPret.setText("");
            lblDetaliiExtra.setText("");
            imgProdus.setImage(null);
            return;
        }

        lblNume.setText(p.getNume());
        lblNume.setStyle("-fx-font-weight: bold;");
        lblPret.setText(String.format("%.2f RON", p.getPret()));
        lblDetaliiExtra.setText(p.getDetalii());

        imgProdus.setImage(imageService.loadImageForProduct(p));
    }

    private void aplicaFiltre() {
        String cautare = Optional.ofNullable(txtSearch.getText()).orElse("");
        boolean doarVeg = chkVegetarian.isSelected();
        Double min = parseDoubleOpt(txtPretMin.getText()).orElse(null);
        Double max = parseDoubleOpt(txtPretMax.getText()).orElse(null);

        List<Produs> filtrate = menuService.filterProducts(new ProductFilterCriteria(
                cautare,
                doarVeg,
                min,
                max
        ));

        refreshLista(filtrate);

        // dacă produsul selectat nu mai există în listă, curățăm detaliile
        if (!filtrate.contains(listaProduse.getSelectionModel().getSelectedItem())) {
            listaProduse.getSelectionModel().clearSelection();
            afiseazaDetalii(null);
        }
    }

    private Optional<Double> parseDoubleOpt(String raw) {
        if (raw == null) return Optional.empty();
        String s = raw.trim();
        if (s.isEmpty()) return Optional.empty();
        s = s.replace(',', '.');
        try {
            return Optional.of(Double.parseDouble(s));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private void refreshLista(List<Produs> produse) {
        listaProduse.setItems(FXCollections.observableArrayList(produse));
    }
}
