package mip.restaurantfx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.util.List;

public class RestaurantGUI extends Application {

    private ProdusRepository repository;
    private ListView<Produs> listView;

    private TextField numeField;
    private TextField pretField;
    private Label detaliiLabel;
    private TextField detaliiField;

    @Override
    public void start(Stage primaryStage) {
        try {
            repository = new ProdusRepository();
        } catch (Exception e) {
            e.printStackTrace();
        }

        primaryStage.setTitle("Restaurant La Andrei");

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem exportItem = new MenuItem("Export JSON");
        exportItem.setOnAction(e -> exportaInJSON(primaryStage));

        MenuItem importItem = new MenuItem("Import JSON");
        importItem.setOnAction(e -> importaDinJSON(primaryStage));

        fileMenu.getItems().addAll(exportItem, importItem);
        menuBar.getMenus().add(fileMenu);

        listView = new ListView<>();
        refreshList();

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populeazaFormular(newVal);
            }
        });

        GridPane formGrid = new GridPane();
        formGrid.setPadding(new Insets(10));
        formGrid.setHgap(10);
        formGrid.setVgap(10);

        formGrid.add(new Label("Nume Produs:"), 0, 0);
        numeField = new TextField();
        formGrid.add(numeField, 0, 1);

        formGrid.add(new Label("Preț (RON):"), 0, 2);
        pretField = new TextField();
        formGrid.add(pretField, 0, 3);

        detaliiLabel = new Label("Detalii Extra:");
        formGrid.add(detaliiLabel, 0, 4);
        detaliiField = new TextField();
        formGrid.add(detaliiField, 0, 5);

        BorderPane root = new BorderPane();
        root.setTop(menuBar);

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(new VBox(listView), formGrid);
        splitPane.setDividerPositions(0.3);

        root.setCenter(splitPane);

        Scene scene = new Scene(root, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void refreshList() {
        listView.getItems().clear();
        if (repository != null) {
            listView.getItems().addAll(repository.getAll());
        }
    }

    private void populeazaFormular(Produs p) {
        numeField.setText(p.getNume());
        pretField.setText(String.valueOf(p.getPret()));

        if (p instanceof Mancare) {
            detaliiLabel.setText("Gramaj (g):");
            detaliiField.setText(String.valueOf(((Mancare) p).getGramaj()));
        } else if (p instanceof Bautura) {
            detaliiLabel.setText("Volum (ml):");
            detaliiField.setText(String.valueOf(((Bautura) p).getVolum()));
        } else {
            detaliiLabel.setText("Detalii:");
            detaliiField.setText("");
        }
    }

    private void exportaInJSON(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvează Meniul");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                List<Produs> listaProduse = repository.getAll();
                mapper.writeValue(file, listaProduse);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succes");
                alert.setContentText("Export realizat cu succes!");
                alert.show();
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Eroare la export: " + ex.getMessage());
                alert.show();
            }
        }
    }

    private void importaDinJSON(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Deschide Meniu");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<Produs> produseNoi = mapper.readValue(file, new TypeReference<List<Produs>>(){});

                for (Produs p : produseNoi) {
                    repository.addProdus(p);
                }
                refreshList();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succes");
                alert.setContentText("Import realizat!");
                alert.show();

            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Eroare la import: " + ex.getMessage());
                alert.show();
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}