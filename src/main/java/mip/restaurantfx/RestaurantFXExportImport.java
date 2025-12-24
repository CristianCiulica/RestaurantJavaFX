package mip.restaurantfx;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * Utilitar mic, folosit de Manager pentru Import/Export JSON.
 * IMPORTANT: folosim tipuri polimorfice (Mancare/Bautura/Pizza), deci activam metadata de tip.
 */
public class RestaurantFXExportImport {

    private ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Include tipul concret (@class) pentru a putea importa o lista de Produs corect.
        var ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("mip.restaurantfx")
                .build();
        mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);

        return mapper;
    }

    public void exportaProduse(Stage stage, List<Produs> produse) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export meniu");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
        File file = fc.showSaveDialog(stage);
        if (file == null) return;

        try {
            mapper().writeValue(file, produse);
            new Alert(Alert.AlertType.INFORMATION, "Export realizat!").show();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Eroare la export: " + ex.getMessage()).show();
        }
    }

    public List<Produs> importaProduse(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import meniu");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
        File file = fc.showOpenDialog(stage);
        if (file == null) return null;

        try {
            return mapper().readValue(file, new TypeReference<>() {});
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Eroare la import: " + ex.getMessage()).show();
            return null;
        }
    }
}
