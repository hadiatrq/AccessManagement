package uni.bager.accessmanagement.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import uni.bager.accessmanagement.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SearchController implements Initializable {
    private double x = 0;
    private double y = 0;
    @FXML
    public MenuBar menuBar;
    @FXML
    private AnchorPane topPane;
    @FXML
    private TextField input;
    @FXML
    private TableView<User> tableView;
    @FXML
    private TableColumn<User,String> name;
    @FXML
    private TableColumn<User,String> cap;
    private ObservableList<User> list;

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

    @FXML
    public void onMouseReleased() {
        menuBar.setCursor(Cursor.DEFAULT);
    }

    @FXML
    public void onAction(){
        if (list != null)
            tableView.setItems(list);
    }

    @FXML
    public void onInputEntered(){
        String inputText = input.getText();
        List<User> newList = new ArrayList<>();
        for (User user : list) {
            if (user.getName().get().contains(inputText)){
                newList.add(user);
            }
        }
        tableView.setItems(FXCollections.observableList(newList));
    }


    public static ObservableList<User> getObservableUsersList() {
        List<User> users = new ArrayList<>();
        ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", "Get-WmiObject Win32_UserAccount | Select-Object Name, Caption");
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.startsWith("Name")) {
                    String[] parts = line.trim().split("\\s+", 2);
                    if (parts.length == 2 && !parts[0].contains("-")) {
                        users.add(new User(parts[0], parts[1]));
                    }
                }
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return FXCollections.observableList(users);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        name.setCellValueFactory(cellData -> cellData.getValue().getName());
        cap.setCellValueFactory(cellData -> cellData.getValue().getCaption());
        list = getObservableUsersList();
    }
}
