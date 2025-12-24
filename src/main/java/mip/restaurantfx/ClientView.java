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
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ClientView {

    private final ProdusRepository produsRepo = new ProdusRepository();

    private final ListView<Produs> listaProduse = new ListView<>();
    private final Label lblNume = new Label();
    private final Label lblPret = new Label();
    private final Label lblDetaliiExtra = new Label();

    private final ImageView productImageView = new ImageView();

    // Căutare + filtre
    private final TextField txtSearch = new TextField();
    private final CheckBox chkVegetarian = new CheckBox("Vegan / Vegetarian");
    private final TextField txtPretMin = new TextField();
    private final TextField txtPretMax = new TextField();

    private final ToggleGroup tipGroup = new ToggleGroup();
    private final ToggleButton btnTipToate = new ToggleButton("Toate");
    private final ToggleButton btnTipMancare = new ToggleButton("Mâncare");
    private final ToggleButton btnTipBautura = new ToggleButton("Băutură");

    private static final String IMAGE_BASE_DIR = "/mip/restaurantfx/images/";
    private static final String DEFAULT_IMAGE_PATH = IMAGE_BASE_DIR + "no-image.png";
    private static final String[] IMAGE_EXTS = new String[]{
            ".jpg", ".jpeg", ".png", ".webp",
            ".jpg.png", ".jpeg.png", ".png.png",
            ".jpg.jpg", ".png.jpg",
            ".jpg.webp", ".png.webp"
    };

    private static final Pattern NON_ALNUM = Pattern.compile("[^a-z0-9]+");

    public void start(Stage stage, User user) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));

        // --- Top bar (modern filter card) ---
        Button btnBack = new Button("Înapoi");
        btnBack.getStyleClass().add("outline");
        btnBack.setOnAction(e -> {
            try {
                new RestaurantGUI().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        txtSearch.setPromptText("Caută produs...");
        txtSearch.setPrefWidth(220);

        chkVegetarian.getStyleClass().add("toggle");

        // chips pentru tip
        btnTipToate.setToggleGroup(tipGroup);
        btnTipMancare.setToggleGroup(tipGroup);
        btnTipBautura.setToggleGroup(tipGroup);
        btnTipToate.setSelected(true);

        for (ToggleButton b : List.of(btnTipToate, btnTipMancare, btnTipBautura)) {
            b.getStyleClass().add("chip");
            b.setFocusTraversable(false);
        }
        updateChipStyles();
        tipGroup.selectedToggleProperty().addListener((obs, o, n) -> {
            updateChipStyles();
            aplicaFiltre();
        });

        txtPretMin.setPromptText("Min");
        txtPretMax.setPromptText("Max");
        txtPretMin.setPrefWidth(76);
        txtPretMax.setPrefWidth(76);
        txtPretMin.getStyleClass().add("compact");
        txtPretMax.getStyleClass().add("compact");

        Button btnApply = new Button("Aplică");
        btnApply.getStyleClass().add("primary");

        HBox typeRow = new HBox(8, btnTipToate, btnTipMancare, btnTipBautura);
        HBox priceRow = new HBox(8, new Label("Preț"), txtPretMin, new Label("-"), txtPretMax);
        priceRow.setAlignment(Pos.CENTER_LEFT);

        VBox filterCard = new VBox(10,
                new HBox(10, btnBack, txtSearch, chkVegetarian, new Region(), btnApply),
                new HBox(12, new Label("Tip:"), typeRow, new Region(), priceRow)
        );
        ((HBox) filterCard.getChildren().get(0)).setAlignment(Pos.CENTER_LEFT);
        ((HBox) filterCard.getChildren().get(1)).setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(((HBox) filterCard.getChildren().get(0)).getChildren().get(3), Priority.ALWAYS);
        HBox.setHgrow(((HBox) filterCard.getChildren().get(1)).getChildren().get(2), Priority.ALWAYS);

        filterCard.getStyleClass().add("filter-card");
        root.setTop(filterCard);

        // --- Layout principal: stânga (listă + detalii), dreapta (poză) ---
        listaProduse.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            afiseazaDetalii(newVal);
            afiseazaImagine(newVal);
        });
        refreshLista(produsRepo.getAll());

        // Detalii (sub listă)
        VBox detaliiPanel = new VBox(10);
        detaliiPanel.getStyleClass().add("card");

        Label lblTitluDetalii = new Label("Detalii produs");
        lblTitluDetalii.getStyleClass().add("subtitle");

        lblNume.getStyleClass().add("title");
        lblNume.setStyle("-fx-font-size: 18px;");
        lblDetaliiExtra.setWrapText(true);

        detaliiPanel.getChildren().addAll(
                lblTitluDetalii,
                new Separator(),
                new Label("Produs"), lblNume,
                new Label("Preț"), lblPret,
                new Label("Info"), lblDetaliiExtra
        );

        VBox leftCard = new VBox(10, new Label("Produse"), listaProduse, detaliiPanel);
        leftCard.getStyleClass().add("card");
        VBox.setVgrow(listaProduse, Priority.ALWAYS);

        root.setCenter(leftCard);
        BorderPane.setMargin(leftCard, new Insets(12, 12, 12, 0));

        // Poză în dreapta
        VBox imagePanel = new VBox(10);
        imagePanel.getStyleClass().add("card");
        imagePanel.setPrefWidth(340);

        Label lblImageTitle = new Label("Poză produs");
        lblImageTitle.getStyleClass().add("subtitle");

        productImageView.setPreserveRatio(true);
        productImageView.setSmooth(true);
        productImageView.setCache(true);
        productImageView.setFitWidth(300);
        productImageView.setFitHeight(420);

        StackPane imageHolder = new StackPane(productImageView);
        imageHolder.setAlignment(Pos.CENTER);
        imageHolder.setPadding(new Insets(8));
        imageHolder.setMinHeight(440);

        imagePanel.getChildren().addAll(lblImageTitle, new Separator(), imageHolder);
        root.setRight(imagePanel);

        // inițial
        afiseazaDetalii(null);
        afiseazaImagine(null);

        // --- handlers ---
        btnApply.setOnAction(e -> aplicaFiltre());
        txtSearch.setOnAction(e -> aplicaFiltre());
        chkVegetarian.setOnAction(e -> aplicaFiltre());
        txtPretMin.setOnAction(e -> aplicaFiltre());
        txtPretMax.setOnAction(e -> aplicaFiltre());

        Scene scene = new Scene(root, 1100, 700);
        var css = ClientView.class.getResource("/mip/restaurantfx/theme.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        stage.setScene(scene);
        stage.setTitle("La Andrei • Meniu (Guest)");
    }

    private void updateChipStyles() {
        for (ToggleButton b : List.of(btnTipToate, btnTipMancare, btnTipBautura)) {
            b.getStyleClass().remove("chip-selected");
            if (b.isSelected()) b.getStyleClass().add("chip-selected");
        }
    }

    private String getTipSelectat() {
        Toggle t = tipGroup.getSelectedToggle();
        if (t == btnTipMancare) return "Mâncare";
        if (t == btnTipBautura) return "Băutură";
        return "Toate";
    }

    private void afiseazaDetalii(Produs p) {
        if (p == null) {
            lblNume.setText("");
            lblPret.setText("");
            lblDetaliiExtra.setText("");
        } else {
            lblNume.setText(p.getNume());
            lblNume.setStyle("-fx-font-weight: bold;");
            lblPret.setText(String.format("%.2f RON", p.getPret()));
            lblDetaliiExtra.setText(p.getDetalii());
        }
    }

    private void aplicaFiltre() {
        List<Produs> toate = produsRepo.getAll();

        String cautare = txtSearch.getText() == null ? "" : txtSearch.getText().trim();
        final String cautareLower = cautare.toLowerCase();

        boolean doarVeg = chkVegetarian.isSelected();
        String tip = getTipSelectat();

        Double pretMin = parseDoubleOpt(txtPretMin.getText()).orElse(null);
        Double pretMax = parseDoubleOpt(txtPretMax.getText()).orElse(null);

        if (pretMin != null && pretMax != null && pretMin > pretMax) {
            double tmp = pretMin;
            pretMin = pretMax;
            pretMax = tmp;
        }

        final Double fMin = pretMin;
        final Double fMax = pretMax;

        List<Produs> filtrate = toate.stream()
                .filter(p -> p != null)
                // Tip
                .filter(p -> {
                    return switch (tip) {
                        case "Mâncare" -> p instanceof Mancare;
                        case "Băutură" -> p instanceof Bautura;
                        default -> true;
                    };
                })
                // Vegetarian (pe Mancare)
                .filter(p -> {
                    if (!doarVeg) return true;
                    return (p instanceof Mancare) && ((Mancare) p).isVegetarian();
                })
                // Interval preț
                .filter(p -> {
                    if (fMin != null && p.getPret() < fMin) return false;
                    if (fMax != null && p.getPret() > fMax) return false;
                    return true;
                })
                // Căutare cu Optional
                .filter(p -> {
                    if (cautareLower.isBlank()) return true;
                    Optional<String> nume = Optional.ofNullable(p.getNume()).map(String::trim).filter(s -> !s.isBlank());
                    return nume.map(n -> n.toLowerCase().contains(cautareLower)).orElse(false);
                })
                .collect(Collectors.toList());

        Produs old = listaProduse.getSelectionModel().getSelectedItem();
        refreshLista(filtrate);

        if (old != null && filtrate.contains(old)) {
            listaProduse.getSelectionModel().select(old);
        } else {
            listaProduse.getSelectionModel().clearSelection();
            afiseazaDetalii(null);
            afiseazaImagine(null);
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

    private void afiseazaImagine(Produs p) {
        String path = resolveProductImagePath(p);
        Image img;
        try {
            img = new Image(Objects.requireNonNull(ClientView.class.getResource(path)).toExternalForm(), true);
        } catch (Exception ex) {
            try {
                img = new Image(Objects.requireNonNull(ClientView.class.getResource(DEFAULT_IMAGE_PATH)).toExternalForm(), true);
            } catch (Exception ignored) {
                img = null;
            }
        }
        productImageView.setImage(img);
    }

    private String resolveProductImagePath(Produs p) {
        if (p == null || p.getNume() == null || p.getNume().isBlank()) {
            return DEFAULT_IMAGE_PATH;
        }

        String slug = slugifyFileBaseName(p.getNume());
        for (String ext : IMAGE_EXTS) {
            String candidate = IMAGE_BASE_DIR + slug + ext;
            if (ClientView.class.getResource(candidate) != null) {
                return candidate;
            }
        }

        String noExt = IMAGE_BASE_DIR + slug;
        if (ClientView.class.getResource(noExt) != null) {
            return noExt;
        }

        return DEFAULT_IMAGE_PATH;
    }

    private static String slugifyFileBaseName(String input) {
        String s = input.trim().toLowerCase();
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("\\p{M}", "");
        s = NON_ALNUM.matcher(s).replaceAll("_");
        s = s.replaceAll("^_+|_+$", "");
        return s;
    }
}

