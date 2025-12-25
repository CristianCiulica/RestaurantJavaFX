package mip.restaurantfx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public final class ExitUtil {

    private ExitUtil() {}

    public static void confirmAndExit(Stage stage) {
        if (stage == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Ești sigur că vrei să ieși din aplicație?",
                ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Confirmare ieșire");

        var res = alert.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            try {
                FxExecutors.shutdown();
            } catch (Exception ignored) {
            }
            stage.close();
        }
    }
}
