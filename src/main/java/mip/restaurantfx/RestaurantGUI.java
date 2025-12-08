package mip.restaurantfx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.util.List;

public class RestaurantGUI extends Application {

    private ProdusRepository repository = new ProdusRepository();
    private ListView<Produs> list = new ListView<>();
    private TextField tNume = new TextField();
    private TextField tPret = new TextField();
    private Label lDetalii = new Label("Detalii:");

    @Override
    public void start(Stage stage) {
        MenuBar menu = new MenuBar();
        Menu file = new Menu("File");

        MenuItem exp = new MenuItem("Export JSON");
        MenuItem imp = new MenuItem("Import JSON");

        MenuItem reset = new MenuItem("Clear Database");
        reset.setOnAction(e -> {
            repository.resetDatabase();
            refreshList();
            new Alert(Alert.AlertType.WARNING, "Baza de date a fost stearsa").show();
        });
        exp.setOnAction(e -> processJson(stage, true));
        imp.setOnAction(e -> processJson(stage, false));
        file.getItems().addAll(exp, imp, new SeparatorMenuItem(), reset);
        menu.getMenus().add(file);
        refreshList();
        list.getSelectionModel().selectedItemProperty().addListener((obs, old, p) -> {
            if (p != null) {
                tNume.setText(p.getNume());
                tPret.setText(String.valueOf(p.getPret()));
                lDetalii.setText("Info: " + p.getDetalii());
            }
        });

        tNume.textProperty().addListener((obs, vechi, nou) -> {
            Produs p = list.getSelectionModel().getSelectedItem();
            if (p != null && nou != null) {
                p.setNume(nou);
                list.refresh();
            }
        });

        tPret.textProperty().addListener((obs, vechi, nou) -> {
            Produs p = list.getSelectionModel().getSelectedItem();
            if (p != null && nou != null) {
                try {
                    p.setPret(Double.parseDouble(nou));
                    list.refresh();
                } catch (NumberFormatException e) { }
            }
        });

        VBox form = new VBox(10, new Label("Nume:"), tNume, new Label("Pret:"), tPret, lDetalii);
        form.setPadding(new Insets(15));

        Button btnSave = new Button("Salveaza Modificari DB");
        btnSave.setOnAction(e -> {
            Produs p = list.getSelectionModel().getSelectedItem();
            if(p != null) {
                repository.salveazaProdus(p);
                new Alert(Alert.AlertType.INFORMATION, "Produs actualizat in baza de date").show();
            }
        });
        form.getChildren().add(btnSave);
        BorderPane root = new BorderPane();
        root.setTop(menu);
        root.setCenter(new SplitPane(list, form));

        stage.setTitle("Restaurant La Andrei");
        stage.setScene(new Scene(root, 700, 450));
        stage.show();
    }

    private void refreshList() {
        list.getItems().setAll(repository.getAll());
    }

    private void processJson(Stage stage, boolean isExport) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
        File f = isExport ? fc.showSaveDialog(stage) : fc.showOpenDialog(stage);

        if (f == null) return;

        ObjectMapper mapper = new ObjectMapper().enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);
        try {
            if (isExport) {
                mapper.writeValue(f, repository.getAll());
                new Alert(Alert.AlertType.INFORMATION, "Export reusit!").show();
            } else {
                List<Produs> produse = mapper.readValue(f, new TypeReference<List<Produs>>(){});
                produse.forEach(p -> repository.salveazaProdus(p));
                refreshList();
                new Alert(Alert.AlertType.INFORMATION, "Import reusit!").show();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Eroare: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        PersistenceManager.getInstance().close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}