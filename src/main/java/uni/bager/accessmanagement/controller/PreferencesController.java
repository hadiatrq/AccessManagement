package uni.bager.accessmanagement.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import uni.bager.accessmanagement.utils.PageManager;

import java.net.URL;
import java.util.ResourceBundle;

public class PreferencesController implements Initializable {
    private double x = 0;
    private double y = 0;
    @FXML
    private ToggleSwitch lazyFlag;
    @FXML
    private ToggleSwitch fullFetchFlag;
    @FXML
    private TabPane topPane;

    @FXML
    private void handleMousePressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    private void onCancelButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        PageManager.getInstance().setPageOpen("preferences", false);
    }

    @FXML
    public void onApplyButton(ActionEvent event) {
        boolean lFlag = lazyFlag.isSelected();
        boolean fFlag = fullFetchFlag.isSelected();
        setInstanceValues(lFlag, fFlag);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void setInstanceValues(boolean isLazyFlag, boolean isFullFetchFlag) {
        TogglesData.getInstance().setLazy(isLazyFlag);
        TogglesData.getInstance().setFull(isFullFetchFlag);
    }

    private void setupToggleSwitchListeners() {
        lazyFlag.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && fullFetchFlag.isSelected()) {
                fullFetchFlag.setSelected(false);
            } else if (oldValue && !fullFetchFlag.isSelected()) {
                fullFetchFlag.setSelected(true);
            }
        });

        fullFetchFlag.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && lazyFlag.isSelected()) {
                lazyFlag.setSelected(false);
            } else if (oldValue && !lazyFlag.isSelected()) {
                lazyFlag.setSelected(true);
            }
        });
    }

    public void applyToggleSwitchLogic() {
        setupToggleSwitchListeners();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindToggleSwitches();
    }

    private void bindToggleSwitches() {
        lazyFlag.selectedProperty().bindBidirectional(TogglesData.getInstance().lazyProperty());
        fullFetchFlag.selectedProperty().bindBidirectional(TogglesData.getInstance().fullProperty());
    }
/*    public void setLazyToggle(boolean lazyFlag) {
        this.lazyFlag.setSelected(lazyFlag);
    }

    public void setFullFetchToggle(boolean fullFetchFlag) {
        this.fullFetchFlag.setSelected(fullFetchFlag);
    }*/
}

class TogglesData {
    private static final TogglesData TOGGLES_DATA_INSTANCE = new TogglesData();
    private final BooleanProperty lazyProperty = new SimpleBooleanProperty(this, "lazy");
    private final BooleanProperty fullProperty = new SimpleBooleanProperty(this, "full");

    private TogglesData() {
    }

    public static TogglesData getInstance() {
        return TOGGLES_DATA_INSTANCE;
    }

    public boolean isLazy() {
        return lazyProperty.get();
    }

    public void setLazy(boolean lazy) {
        lazyProperty.set(lazy);
    }

    public boolean isFull() {
        return fullProperty.get();
    }

    public void setFull(boolean full) {
        fullProperty.set(full);
    }

    public BooleanProperty lazyProperty() {
        return lazyProperty;
    }

    public BooleanProperty fullProperty() {
        return fullProperty;
    }
}


/*
class TogglesData {
    private static final TogglesData TOGGLES_DATA_INSTANCE = new TogglesData();
    private boolean lazy;
    private boolean full;

    private TogglesData() {
    }

    public static TogglesData getInstance() {
        return TOGGLES_DATA_INSTANCE;
    }

    public boolean isLazy() {
        return lazy;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }
}
*/
