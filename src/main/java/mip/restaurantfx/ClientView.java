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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ClientView {

    private ProdusRepository produsRepo = new ProdusRepository();
    private ListView<Produs> listaProduse = new ListView<>();
    private Label lblNume = new Label();
    private Label lblPret = new Label();
    private Label lblDetaliiExtra = new Label();

    private ImageView productImageView = new ImageView();

    // Filtre
    private TextField txtSearch = new TextField();
    private CheckBox chkVegetarian = new CheckBox("Doar Vegetarian");
    private TextField txtPretMax = new TextField();

    private static final String IMAGE_BASE_DIR = "/mip/restaurantfx/images/";
    private static final String DEFAULT_IMAGE_PATH = IMAGE_BASE_DIR + "no-image.png";

    /**
     * Extensii acceptate (în ordine). Include și cazuri de fișiere redenumite greșit gen "nume.jpg.png".
     */
    private static final String[] IMAGE_EXTS = new String[]{
            ".jpg", ".jpeg", ".png", ".webp",
            ".jpg.png", ".jpeg.png", ".png.png",
            ".jpg.jpg", ".png.jpg",
            ".jpg.webp", ".png.webp"
    };

    private static final Pattern NON_ALNUM = Pattern.compile("[^a-z0-9]+");

    public void start(Stage stage, User user) {
        // --- 1. Layout Principal (BorderPane) ---
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));

        // --- 2. Zona de Sus (Filtre) ---
        HBox topPanel = new HBox(10);
        topPanel.getStyleClass().add("topbar");

        Button btnLogout = new Button("Înapoi");
        btnLogout.getStyleClass().add("outline");
        btnLogout.setOnAction(e -> {
            try {
                new RestaurantGUI().start(stage); // Revenim la Login
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        txtSearch.setPromptText("Caută produs...");
        txtPretMax.setPromptText("Preț max");
        txtPretMax.setPrefWidth(90);
        Button btnFiltreaza = new Button("Aplică filtre");
        btnFiltreaza.getStyleClass().add("primary");

        topPanel.getChildren().addAll(btnLogout, txtSearch, chkVegetarian, txtPretMax, btnFiltreaza);
        root.setTop(topPanel);

        // --- 3. Zona Centrala (Stânga: Lista + Detalii sub listă) ---
        listaProduse.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            afiseazaDetalii(newVal);
            afiseazaImagine(newVal);
        });
        refreshLista(produsRepo.getAll());

        Label lblProduse = new Label("Produse");

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

        VBox listCard = new VBox(10,
                lblProduse,
                listaProduse,
                detaliiPanel
        );
        listCard.getStyleClass().add("card");

        // lasă lista să ocupe spațiul, detaliile rămân jos
        VBox.setVgrow(listaProduse, Priority.ALWAYS);

        root.setCenter(listCard);
        BorderPane.setMargin(listCard, new Insets(12, 12, 12, 0));

        // --- 4. Zona Dreapta (Poză produs curent) ---
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

        // inițial: golește detaliile + imagine default
        afiseazaDetalii(null);
        afiseazaImagine(null);

        // --- Logica Filtrare (Buton) ---
        btnFiltreaza.setOnAction(e -> aplicaFiltre());

        Scene scene = new Scene(root, 1100, 700);
        scene.getStylesheets().add(ClientView.class.getResource("/mip/restaurantfx/theme.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("La Andrei • Meniu (Guest)");
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

    /**
     * Ia automat imaginea din resources/mip/restaurantfx/images pe baza numelui produsului.
     * Convenție: nume produs -> slug (lowercase, fără diacritice, spații -> underscore).
     * Ex: "Panna Cotta cu fructe de padure" -> "panna_cotta_cu_fructe_de_padure.jpg"
     */
    private void afiseazaImagine(Produs p) {
        String path = resolveProductImagePath(p);

        Image img;
        try {
            img = new Image(Objects.requireNonNull(ClientView.class.getResource(path)).toExternalForm(), true);
        } catch (Exception ex) {
            // fallback extra, în caz că lipsește chiar și poza default
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

        // 1) cel mai comun: slug + extensie
        for (String ext : IMAGE_EXTS) {
            String candidate = IMAGE_BASE_DIR + slug + ext;
            if (ClientView.class.getResource(candidate) != null) {
                return candidate;
            }
        }

        // 2) fallback: fișier fără extensie (dacă ai ales să pui exact „slug” ca nume)
        String noExt = IMAGE_BASE_DIR + slug;
        if (ClientView.class.getResource(noExt) != null) {
            return noExt;
        }

        return DEFAULT_IMAGE_PATH;
    }

    private static String slugifyFileBaseName(String input) {
        String s = input.trim().toLowerCase();
        // scoate diacriticele
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("\\p{M}", "");
        // normalizează non-alnum
        s = NON_ALNUM.matcher(s).replaceAll("_");
        s = s.replaceAll("^_+|_+$", "");
        return s;
    }

    private void aplicaFiltre() {
        List<Produs> toate = produsRepo.getAll();

        List<Produs> filtrate = toate.stream()
                .filter(p -> {
                    String cautare = txtSearch.getText().toLowerCase();
                    if (!cautare.isEmpty() && !p.getNume().toLowerCase().contains(cautare)) return false;

                    if (chkVegetarian.isSelected()) {
                        if (p instanceof Mancare) {
                            return ((Mancare) p).isVegetarian();
                        }
                        return false;
                    }

                    try {
                        if (!txtPretMax.getText().isEmpty()) {
                            double max = Double.parseDouble(txtPretMax.getText());
                            if (p.getPret() > max) return false;
                        }
                    } catch (NumberFormatException e) { }

                    return true;
                })
                .collect(Collectors.toList());

        Produs oldSelection = listaProduse.getSelectionModel().getSelectedItem();
        refreshLista(filtrate);

        // încearcă să păstrezi selecția dacă încă există
        if (oldSelection != null && filtrate.contains(oldSelection)) {
            listaProduse.getSelectionModel().select(oldSelection);
        } else {
            listaProduse.getSelectionModel().clearSelection();
            afiseazaDetalii(null);
            afiseazaImagine(null);
        }
    }

    private void refreshLista(List<Produs> produse) {
        listaProduse.setItems(FXCollections.observableArrayList(produse));
    }
}