package mip.restaurantfx;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class RestaurantGUI extends Application {
    private Meniu meniuRestaurant = new Meniu();
    private ListView<Produs> listaProduseVizuala = new ListView<>();
    private TextField numeField = new TextField();
    private TextField pretField = new TextField();
    private TextField tipField = new TextField();
    @Override
    public void start(Stage primaryStage) {
        populeazaMeniuDeTest();

        List<Produs> toateProdusele = new ArrayList<>();
        for (List<Produs> lista : meniuRestaurant.categoriiProduse.values()) {
            toateProdusele.addAll(lista);
        }
        listaProduseVizuala.getItems().addAll(toateProdusele);
        listaProduseVizuala.setPrefWidth(250);
        VBox panouDetalii = new VBox(10);
        panouDetalii.setPadding(new Insets(20));
        panouDetalii.getChildren().addAll(
                new Label("Nume Produs:"), numeField,
                new Label("Preț (RON):"), pretField,
                new Label("Detalii Extra (Gramaj/Volum):"), tipField
        );
        listaProduseVizuala.getSelectionModel().selectedItemProperty().addListener((obs, produsVechi, produsNou) -> {
            if (produsVechi != null) {
                numeField.textProperty().unbindBidirectional(produsVechi.numeProperty());
            }
            if (produsNou != null) {
                numeField.textProperty().bindBidirectional(produsNou.numeProperty());
                pretField.setText(String.valueOf(produsNou.getPret()));

                if (produsNou instanceof Mancare) {
                    tipField.setText(((Mancare) produsNou).getGramaj() + "g");
                } else if (produsNou instanceof Bautura) {
                    tipField.setText(((Bautura) produsNou).getVolum() + "ml");
                }
            }
        });
        pretField.textProperty().addListener((obs, vechi, nou) -> {
            Produs p = listaProduseVizuala.getSelectionModel().getSelectedItem();
            if (p != null && !nou.isEmpty()) {
                try {
                    p.setPret(Double.parseDouble(nou));
                    listaProduseVizuala.refresh();
                } catch (NumberFormatException e) {
                }
            }
        });
        BorderPane layoutPrincipal = new BorderPane();
        layoutPrincipal.setLeft(listaProduseVizuala);
        layoutPrincipal.setCenter(panouDetalii);

        Scene scena = new Scene(layoutPrincipal, 800, 500);
        primaryStage.setTitle("Restaurant La Andrei - Sistem Gestiune");
        primaryStage.setScene(scena);
        primaryStage.show();
    }
    private void populeazaMeniuDeTest() {
        meniuRestaurant.adaugaProdus("Fel Principal", new Mancare("Pizza Margherita", 45.0, 450, true));
        meniuRestaurant.adaugaProdus("Fel Principal", new Mancare("Paste Carbonara", 52.5, 400, false));
        meniuRestaurant.adaugaProdus("Bauturi", new Bautura("Limonada", 15.0, 400, false));
        meniuRestaurant.adaugaProdus("Bauturi", new Bautura("Cola", 10.0, 330, false));
    }

    public static void main(String[] args) {
        launch(args);
    }
}