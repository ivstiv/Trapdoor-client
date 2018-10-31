package misc;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

public class BoundMenuButton extends MenuButton {
    ObservableList<MenuItem> items;

    public BoundMenuButton(ObservableList<MenuItem> items) {
        this.items = items;

        // Listen for changes
        items.addListener((ListChangeListener.Change<? extends MenuItem> change) -> {
            updateItems();
        });
    }

    public void updateItems() {
        Platform.runLater( () -> {
            // Do updates
        });
    }
}
