package mip.restaurantfx;

import javafx.application.Platform;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

/**
 * Utilitar mic pentru a pastra o experienta consistenta a ferestrei intre ecrane.
 *
 * Cerinta: sa ramana fullscreen/maximizat indiferent de navigare (ex: Logout din Admin -> Login).
 */
public final class StageUtil {

    private static final String KEY_FULL_SCREEN_INSTALLED = "__fullScreenInstalled";

    private StageUtil() {}

    /**
     * Forteaza fereastra sa fie maximizata.
     *
     * Nota: folosim maximized (nu fullScreen) ca sa evitam comportamente diferite pe OS
     * si necesitatea handling-ului pentru ESC.
     */
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

        // Aplica dupa ce JavaFX a procesat schimbarea de scena.
        Platform.runLater(apply);
        Platform.runLater(apply);
    }
}
