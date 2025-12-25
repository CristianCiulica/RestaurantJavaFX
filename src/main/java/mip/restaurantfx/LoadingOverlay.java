package mip.restaurantfx;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public final class LoadingOverlay {

    private final StackPane root;
    private final StackPane overlay;
    private final Label message;

    public LoadingOverlay(Node content) {
        this.root = new StackPane(content);

        this.message = new Label("Loading...");
        this.message.getStyleClass().add("subtitle");

        ProgressIndicator pi = new ProgressIndicator();
        pi.setMaxSize(64, 64);

        VBox box = new VBox(10, pi, message);
        box.setAlignment(Pos.CENTER);

        this.overlay = new StackPane(box);
        this.overlay.setStyle("-fx-background-color: rgba(0,0,0,0.25);");
        this.overlay.setVisible(false);
        this.overlay.setPickOnBounds(true);

        this.root.getChildren().add(this.overlay);
    }

    public StackPane getRoot() {
        return root;
    }

    public void show(String text) {
        message.setText(text == null || text.isBlank() ? "Loading..." : text);
        overlay.setVisible(true);
    }

    public void hide() {
        overlay.setVisible(false);
    }
}

