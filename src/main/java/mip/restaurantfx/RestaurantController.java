package mip.restaurantfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;

public class RestaurantController {

    // --- ELEMENTE DIN FXML (Interfața) ---
    @FXML private FlowPane panelMese;      // Zona unde apar butoanele cu mese
    @FXML private FlowPane panelProduse;   // Zona cu butoanele de produse (Meniu)
    @FXML private Label labelMasaSelectata;
    @FXML private Label labelTotal;

    // Tabelul (Bonul de casă)
    @FXML private TableView<ComandaItem> tabelComanda;
    @FXML private TableColumn<ComandaItem, String> colProdus;
    @FXML private TableColumn<ComandaItem, Integer> colCantitate;
    @FXML private TableColumn<ComandaItem, Double> colPret;
    @FXML private TableColumn<ComandaItem, Double> colSubtotal;

    // --- LOGICA DE STATE ---
    private ComandaRepository comandaRepo = new ComandaRepository();
    private MasaRepository masaRepo = new MasaRepository(); // Presupun ca ai clasa asta din discutiile anterioare
    // Ai nevoie și de un ProdusRepository simplu pentru a încărca meniul
    private ProdusRepository produsRepo = new ProdusRepository();

    private Comanda comandaCurenta; // Comanda pe care lucrăm acum
    private Masa masaSelectata;

    @FXML
    public void initialize() {
        configurareTabel();
        incarcaMese();
        incarcaProduse();
    }

    // 1. Configurare coloane tabel (Mapping)
    private void configurareTabel() {
        // Pentru numele produsului, trebuie să "săpăm" în obiectul ComandaItem -> Produs -> Nume
        colProdus.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProdus().getNume()));

        colCantitate.setCellValueFactory(new PropertyValueFactory<>("cantitate"));
        colPret.setCellValueFactory(new PropertyValueFactory<>("pretUnitar"));

        // Putem calcula subtotalul direct sau din getter
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    // 2. Generare butoane Mese
    private void incarcaMese() {
        panelMese.getChildren().clear();
        List<Masa> mese = masaRepo.getAllMese();

        for (Masa masa : mese) {
            Button btn = new Button("Masa " + masa.getNumarMasa());
            btn.setPrefSize(100, 100);

            // Verificăm vizual dacă masa e ocupată (Interogăm DB)
            Comanda cmdActiva = comandaRepo.getComandaActiva(masa.getId());
            if (cmdActiva != null) {
                btn.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red;"); // Roșu dacă e ocupată
            } else {
                btn.setStyle("-fx-background-color: #ccffcc; -fx-border-color: green;"); // Verde dacă e liberă
            }

            // Eveniment Click pe Masă
            btn.setOnAction(e -> selecteazaMasa(masa));

            panelMese.getChildren().add(btn);
        }
    }

    // 3. Logica de selecție masă (AICI E FIX-UL PENTRU PROBLEMA TA)
    private void selecteazaMasa(Masa masa) {
        this.masaSelectata = masa;
        labelMasaSelectata.setText("Masa: " + masa.getNumarMasa());

        // Căutăm în baza de date comanda activă
        Comanda comandaExistenta = comandaRepo.getComandaActiva(masa.getId());

        if (comandaExistenta != null) {
            // SCENARIU: MASA OCUPATĂ
            this.comandaCurenta = comandaExistenta;
            System.out.println("S-a încărcat comanda ID: " + comandaCurenta.getId());
        } else {
            // SCENARIU: MASA LIBERĂ -> Pregătim o comandă nouă (dar n-o salvăm încă)
            this.comandaCurenta = new Comanda(masa);
            System.out.println("Comandă nouă (nesalvată încă)");
        }

        refreshTabelProduse();
    }

    // 4. Generare butoane Produse
    private void incarcaProduse() {
        List<Produs> produse = produsRepo.getAll();

        for (Produs produs : produse) {
            Button btn = new Button(produs.getNume() + "\n" + produs.getPret() + " LEI");
            btn.setPrefSize(120, 80);

            btn.setOnAction(e -> adaugaProdusInComanda(produs));

            panelProduse.getChildren().add(btn);
        }
    }

    // 5. Adăugare Produs
    private void adaugaProdusInComanda(Produs produs) {
        if (masaSelectata == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Selectează o masă mai întâi!");
            alert.show();
            return;
        }

        // Adăugăm în logică (memorie)
        comandaCurenta.adaugaProdus(produs, 1);

        // Opțional: Salvăm automat la fiecare click (ca să nu pierzi datele dacă se ia curentul)
        // comandaRepo.save(comandaCurenta);

        refreshTabelProduse();
    }

    // 6. Refresh UI (Tabel + Total)
    private void refreshTabelProduse() {
        if (comandaCurenta != null) {
            // Convertim lista din Comanda în ObservableList pentru JavaFX
            ObservableList<ComandaItem> items = FXCollections.observableArrayList(comandaCurenta.getItems());
            tabelComanda.setItems(items);
            tabelComanda.refresh(); // Forțăm refresh vizual

            labelTotal.setText(String.format("TOTAL: %.2f LEI", comandaCurenta.getTotal()));
        } else {
            tabelComanda.getItems().clear();
            labelTotal.setText("TOTAL: 0.00 LEI");
        }
    }

    // 7. Butonul "TRIMITE COMANDA / SALVEAZĂ"
    @FXML
    public void onBtnSalveazaClick() {
        if (comandaCurenta == null || comandaCurenta.getItems().isEmpty()) {
            return;
        }

        // Salvăm în DB. Hibernate va salva Comanda + toate ComandaItems (datorită CascadeType.ALL)
        comandaRepo.save(comandaCurenta);

        // Reîncărcăm culorile meselor (ca să devină roșie dacă era verde)
        incarcaMese();

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Comanda a fost salvată!");
        alert.show();
    }

    // 8. Butonul "NOTA DE PLATA / INCHIDE MASA"
    @FXML
    public void onBtnAchitaClick() {
        if (masaSelectata == null || comandaCurenta == null || comandaCurenta.getItems().isEmpty()) {
            return;
        }

        // In acest controller consideram "comanda activa" ca fiind comanda existenta in DB.
        // Daca ai un camp status in Comanda(entitate) in alt flow (StaffComandaView), acolo se gestioneaza.
        comandaRepo.save(comandaCurenta);

        // Resetam state-ul local
        comandaCurenta = null;
        masaSelectata = null;
        labelMasaSelectata.setText("Masa: -");

        // Refresh UI
        refreshTabelProduse();
        incarcaMese();

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Comanda a fost achitata. Masa este acum libera.");
        alert.show();
    }
}
