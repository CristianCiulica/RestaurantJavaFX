package mip.restaurantfx;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ClientView {

    private final ProdusRepository produsRepo = new ProdusRepository();
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

    private static final String IMAGES_DIR = "/mip/restaurantfx/images/";
    private static final String[] IMAGE_EXTS = new String[]{".png", ".jpg", ".jpeg"};
    private static final Pattern NON_ALNUM = Pattern.compile("[^a-z0-9]+");

    public void start(Stage stage, User user) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));

        // --- Top (filtre) ---
        HBox topPanel = new HBox(10);
        topPanel.getStyleClass().add("topbar");

        Button btnLogout = new Button("Înapoi");
        btnLogout.getStyleClass().add("outline");
        btnLogout.setOnAction(e -> {
            try {
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

        topPanel.getChildren().addAll(btnLogout, txtSearch, chkVegetarian, txtPretMin, txtPretMax, btnFiltreaza);
        root.setTop(topPanel);

        // --- Center/Left: listă produse + detalii text sub listă ---
        listaProduse.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> afiseazaDetalii(newVal));
        refreshLista(produsRepo.getAll());

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
        stage.setTitle("La Andrei • Meniu (Guest)");
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

        imgProdus.setImage(loadImageForProduct(p));
    }

    private void aplicaFiltre() {
        List<Produs> toate = produsRepo.getAll();

        String cautare = Optional.ofNullable(txtSearch.getText()).orElse("").trim().toLowerCase();
        boolean doarVeg = chkVegetarian.isSelected();

        Double min = parseDoubleOpt(txtPretMin.getText()).orElse(null);
        Double max = parseDoubleOpt(txtPretMax.getText()).orElse(null);
        if (min != null && max != null && min > max) {
            double tmp = min;
            min = max;
            max = tmp;
        }
        final Double fMin = min;
        final Double fMax = max;

        List<Produs> filtrate = toate.stream()
                .filter(p -> {
                    if (cautare.isEmpty()) return true;
                    String nume = p.getNume() == null ? "" : p.getNume().toLowerCase();
                    return nume.contains(cautare);
                })
                .filter(p -> {
                    if (!doarVeg) return true;
                    return (p instanceof Mancare) && ((Mancare) p).isVegetarian();
                })
                .filter(p -> {
                    if (fMin != null && p.getPret() < fMin) return false;
                    if (fMax != null && p.getPret() > fMax) return false;
                    return true;
                })
                .collect(Collectors.toList());

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

    private Image loadImageForProduct(Produs p) {
        if (p == null || p.getNume() == null || p.getNume().isBlank()) return null;

        String slug = slugify(p.getNume());
        for (String ext : IMAGE_EXTS) {
            String path = IMAGES_DIR + slug + ext;
            var url = ClientView.class.getResource(path);
            if (url != null) {
                return new Image(url.toExternalForm(), true);
            }
        }

        // fallback: nimic (nu avem placeholder în resources momentan)
        return null;
    }

    private static String slugify(String input) {
        String s = input.trim().toLowerCase(Locale.ROOT);
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("\\p{M}", "");
        s = NON_ALNUM.matcher(s).replaceAll("_");
        s = s.replaceAll("^_+|_+$", "");
        return s;
    }
}
