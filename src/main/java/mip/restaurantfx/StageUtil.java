package mip.restaurantfx;

import javafx.application.Platform;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

public final class StageUtil {

    private static final String KEY_FULL_SCREEN_INSTALLED = "__fullScreenInstalled";

    private StageUtil() {}

    public static void keepMaximized(Stage stage) {
        if (stage == null) return;

        if (!Boolean.TRUE.equals(stage.getProperties().get(KEY_FULL_SCREEN_INSTALLED))) {
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            stage.setFullScreenExitHint("");
            stage.getProperties().put(KEY_FULL_SCREEN_INSTALLED, true);
        }

        if (!WindowState.preferFullScreenByDefault(true)) return;

        Runnable apply = () -> {
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            stage.setFullScreenExitHint("");
            stage.setFullScreen(true);
            if (stage.isIconified()) stage.setIconified(false);
        };
        Platform.runLater(apply);
        Platform.runLater(apply);
    }
}
