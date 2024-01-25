package uni.bager.accessmanagement.controller;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SearchController {
    private double x = 0;
    private double y = 0;
    @FXML
    public MenuBar menuBar;
    @FXML
    private AnchorPane topPane;
    @FXML
    private void handleMousePressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
        menuBar.setCursor(Cursor.MOVE);
    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        Stage stage = (Stage) topPane.getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }
    @FXML
    private void onQuitDragged(MouseEvent event){
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void onMouseReleased() {
        menuBar.setCursor(Cursor.DEFAULT);
    }
}
