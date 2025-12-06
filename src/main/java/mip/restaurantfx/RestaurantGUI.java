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
            initializeazaProduse();
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
        listView.setCellFactory(param -> new ListCell<Produs>() {
            @Override
            protected void updateItem(Produs item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String details = "";
                    String type = "";

                    if (item instanceof Pizza) {
                        Pizza pizza = (Pizza) item;
                        type = "[Pizza]";
                        int toppingCount = (pizza.getToppinguri() != null) ? pizza.getToppinguri().size() : 0;
                        String blatText = (pizza.getBlat() != null) ? pizza.getBlat() : "N/A";
                        String sosText = (pizza.getSos() != null) ? pizza.getSos() : "N/A";
                        details = String.format(" - %s, %s, %d toppinguri", blatText, sosText, toppingCount);
                    } else if (item instanceof Mancare) {
                        Mancare mancare = (Mancare) item;
                        type = mancare.isVegetarian() ? "[Mancare Vegetariana]" : "[Mancare]";
                        details = String.format(" - %dg", mancare.getGramaj());
                    } else if (item instanceof Bautura) {
                        Bautura bautura = (Bautura) item;
                        type = bautura.isAlcoolica() ? "[Bautura Alcoolica]" : "[Bautura]";
                        details = String.format(" - %dml", bautura.getVolum());
                    }

                    setText(String.format("%s %s - %.2f RON%s",
                        type, item.getNume(), item.getPret(), details));
                }
            }
        });

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
        pretField.setText(String.format("%.2f", p.getPret()));

        if (p instanceof Pizza) {
            Pizza pizza = (Pizza) p;
            detaliiLabel.setText("Pizza Info:");
            String blatText = (pizza.getBlat() != null) ? pizza.getBlat() : "N/A";
            String sosText = (pizza.getSos() != null) ? pizza.getSos() : "N/A";
            String toppinguriText = (pizza.getToppinguri() != null) ? String.join(", ", pizza.getToppinguri()) : "N/A";
            detaliiField.setText(String.format("Blat: %s, Sos: %s, Toppinguri: %s", blatText, sosText, toppinguriText));
        } else if (p instanceof Mancare) {
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

    private void initializeazaProduse() {
        List<Produs> existing = repository.getAll();
        System.out.println("Current products in database: " + existing.size());

        if (existing.size() >= 20) {
            System.out.println("Database already has " + existing.size() + " products. Skipping initialization.");
            return;
        }
        if (!existing.isEmpty()) {
            System.out.println("Found partial initialization (" + existing.size() + " products). Clearing and re-initializing...");
            repository.stergeToateProdusele();
        }

        System.out.println("Initializing database with menu products...");
        int count = 0;

        System.out.println("Adding vegetarian dishes...");
        repository.salveazaProdus(new Mancare("Pizza Margherita", 45.0, 450, true)); count++;
        repository.salveazaProdus(new Mancare("Supa Crema Ciuperci", 22.0, 300, true)); count++;
        repository.salveazaProdus(new Mancare("Risotto cu Hribii de munte", 48.0, 320, true)); count++;
        repository.salveazaProdus(new Mancare("Salata de Fructe", 18.0, 200, true)); count++;
        repository.salveazaProdus(new Mancare("Panna Cotta cu fructe de padure", 28.0, 150, true)); count++;
        System.out.println("  Added " + count + " vegetarian dishes");

        System.out.println("Adding non-vegetarian dishes...");
        repository.salveazaProdus(new Mancare("Paste Carbonara", 52.5, 400, false)); count++;
        repository.salveazaProdus(new Mancare("Burger Gourmet Black Angus", 62.0, 380, false)); count++;
        repository.salveazaProdus(new Mancare("Tiramisu Special", 120.0, 250, false)); count++;
        repository.salveazaProdus(new Mancare("Cheesecake Vanilie", 32.0, 180, false)); count++;
        System.out.println("  Added " + (count - 5) + " non-vegetarian dishes");

        System.out.println("Adding non-alcoholic drinks...");
        repository.salveazaProdus(new Bautura("Limonada", 15.0, 400, false)); count++;
        repository.salveazaProdus(new Bautura("Apa Plata", 8.0, 500, false)); count++;
        repository.salveazaProdus(new Bautura("Fresh Portocale", 19.0, 450, false)); count++;
        repository.salveazaProdus(new Bautura("Ceai Verde cu miere", 14.0, 350, false)); count++;
        System.out.println("  Added " + (count - 9) + " non-alcoholic drinks");

        System.out.println("Adding alcoholic drinks...");
        repository.salveazaProdus(new Bautura("Bere", 12.0, 500, true)); count++;
        repository.salveazaProdus(new Bautura("Vin Rosu", 28.0, 150, true)); count++;
        repository.salveazaProdus(new Bautura("Aperol Spritz", 35.0, 250, true)); count++;
        repository.salveazaProdus(new Bautura("Gin Tonic", 38.0, 300, true)); count++;
        System.out.println("  Added " + (count - 13) + " alcoholic drinks");

        System.out.println("Adding custom pizzas...");
        Pizza pizzaCustom = new Pizza.PizzaBuilder("Pufos", "Rosii")
                .withExtraMozzarella()
                .withCiuperci()
                .withSalam()
                .build();
        repository.salveazaProdus(pizzaCustom); count++;
        System.out.println("  Added Pizza Custom 1");

        Pizza pizzaVeggie = new Pizza.PizzaBuilder("Subtire", "Busuioc")
                .withCiuperci()
                .withExtraMozzarella()
                .build();
        repository.salveazaProdus(pizzaVeggie); count++;
        System.out.println("  Added Pizza Custom 2");

        int finalCount = repository.getAll().size();
        System.out.println("Database initialization complete! Expected: 20, Added: " + count + ", Final count: " + finalCount);

        if (finalCount < 20) {
            System.out.println("WARNING: Not all products were saved successfully!");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}