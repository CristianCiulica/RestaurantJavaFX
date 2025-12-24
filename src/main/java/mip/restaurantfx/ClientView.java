package mip.restaurantfx;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class ClientView {

    private ProdusRepository produsRepo = new ProdusRepository();
    private ListView<Produs> listaProduse = new ListView<>();
    private Label lblNume = new Label();
    private Label lblPret = new Label();
    private Label lblDetaliiExtra = new Label();

    // Filtre
    private TextField txtSearch = new TextField();
    private CheckBox chkVegetarian = new CheckBox("Doar Vegetarian");
    private TextField txtPretMax = new TextField();

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

        // --- 3. Zona Centrala (Lista Produse) ---
        listaProduse.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> afiseazaDetalii(newVal));
        refreshLista(produsRepo.getAll());

        VBox listCard = new VBox(10, new Label("Produse"), listaProduse);
        listCard.getStyleClass().add("card");
        root.setCenter(listCard);
        BorderPane.setMargin(listCard, new Insets(12, 12, 12, 0));

        // --- 4. Zona Dreapta (Detalii) ---
        VBox detaliiPanel = new VBox(12);
        detaliiPanel.getStyleClass().add("card");
        detaliiPanel.setPrefWidth(280);

        Label lblTitluDetalii = new Label("Detalii produs");
        lblTitluDetalii.getStyleClass().add("subtitle");

        lblNume.getStyleClass().add("title");
        lblNume.setStyle("-fx-font-size: 18px;");

        detaliiPanel.getChildren().addAll(
                lblTitluDetalii,
                new Separator(),
                new Label("Produs"), lblNume,
                new Label("Preț"), lblPret,
                new Label("Info"), lblDetaliiExtra
        );
        root.setRight(detaliiPanel);

        // --- Logica Filtrare (Buton) ---
        btnFiltreaza.setOnAction(e -> aplicaFiltre());

        Scene scene = new Scene(root, 980, 620);
        scene.getStylesheets().add(ClientView.class.getResource("/mip/restaurantfx/theme.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("La Andrei • Meniu (Guest)");
    }

    private void afiseazaDetalii(Produs p) {
        if (p == null) {
            lblNume.setText(""); lblPret.setText(""); lblDetaliiExtra.setText("");
        } else {
            lblNume.setText(p.getNume());
            lblNume.setStyle("-fx-font-weight: bold;");
            lblPret.setText(String.format("%.2f RON", p.getPret()));
            lblDetaliiExtra.setText(p.getDetalii());
        }
    }

    private void aplicaFiltre() {
        List<Produs> toate = produsRepo.getAll();

        // Folosim Streams API pentru filtrare (Cerinta Iteratia 3/7)
        List<Produs> filtrate = toate.stream()
                .filter(p -> {
                    // Filtru Nume
                    String cautare = txtSearch.getText().toLowerCase();
                    if (!cautare.isEmpty() && !p.getNume().toLowerCase().contains(cautare)) return false;

                    // Filtru Vegetarian
                    if (chkVegetarian.isSelected()) {
                        if (p instanceof Mancare) {
                            return ((Mancare) p).isVegetarian();
                        }
                        return false; // Bauturile nu sunt marcate explicit ca "Vegetarian" in clasa noastra, sau le consideram false
                    }

                    // Filtru Pret Maxim
                    try {
                        if (!txtPretMax.getText().isEmpty()) {
                            double max = Double.parseDouble(txtPretMax.getText());
                            if (p.getPret() > max) return false;
                        }
                    } catch (NumberFormatException e) { /* Ignoram daca nu e numar */ }

                    return true;
                })
                .collect(Collectors.toList());

        refreshLista(filtrate);
    }

    private void refreshLista(List<Produs> produse) {
        listaProduse.setItems(FXCollections.observableArrayList(produse));
    }
}