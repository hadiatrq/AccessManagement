package uni.bager.accessmanagement.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import uni.bager.accessmanagement.model.RuleEntry;

public class HelpController {
    private double x = 0;
    private double y = 0;
    @FXML
    public MenuBar menuBar;
    @FXML
    private TableView<RuleEntry> aboutRulesTableView;
    @FXML
    private TableColumn<RuleEntry, String> specialPermissionsColumn;
    @FXML
    private TableColumn<RuleEntry, String> fullControlColumn;
    @FXML
    private TableColumn<RuleEntry, String> modifyColumn;
    @FXML
    private TableColumn<RuleEntry, String> readExecuteColumn;
    @FXML
    private TableColumn<RuleEntry, String> listFolderContentsColumn;
    @FXML
    private TableColumn<RuleEntry, String> readColumn;
    @FXML
    private TableColumn<RuleEntry, String> writeColumn;
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

    public void initialize() {
        specialPermissionsColumn.setCellValueFactory(cellData -> cellData.getValue().specialPermissionsProperty());
        fullControlColumn.setCellValueFactory(cellData -> cellData.getValue().fullControlProperty());
        modifyColumn.setCellValueFactory(cellData -> cellData.getValue().modifyProperty());
        readExecuteColumn.setCellValueFactory(cellData -> cellData.getValue().readExecuteProperty());
        listFolderContentsColumn.setCellValueFactory(cellData -> cellData.getValue().listFolderContentsProperty());
        readColumn.setCellValueFactory(cellData -> cellData.getValue().readProperty());
        writeColumn.setCellValueFactory(cellData -> cellData.getValue().writeProperty());

        Callback<TableColumn<RuleEntry,String>, TableCell<RuleEntry,String>> centerAlignmentCellFactory =
                ruleEntryStringTableColumn -> new TextFieldTableCell<>(){
                    public void updateItem(String item, boolean empty){
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            setAlignment(javafx.geometry.Pos.CENTER);
                        }
                    }
                };
        specialPermissionsColumn.setCellFactory(centerAlignmentCellFactory);
        fullControlColumn.setCellFactory(centerAlignmentCellFactory);
        modifyColumn.setCellFactory(centerAlignmentCellFactory);
        readExecuteColumn.setCellFactory(centerAlignmentCellFactory);
        listFolderContentsColumn.setCellFactory(centerAlignmentCellFactory);
        readColumn.setCellFactory(centerAlignmentCellFactory);
        writeColumn.setCellFactory(centerAlignmentCellFactory);
        aboutRulesTableView.setItems(getData());
    }

    public static ObservableList<RuleEntry> getData() {
        return FXCollections.observableArrayList(
                new RuleEntry("Traverse Folder/Execute File", "x", "x", "x", "x", "", ""),
                new RuleEntry("List Folder/Read Data", "x", "x", "x", "x", "x", ""),
                new RuleEntry("Read Attributes", "x", "x", "x", "x", "x", ""),
                new RuleEntry("Read Extended Attributes", "x", "x", "x", "x", "x", ""),
                new RuleEntry("Create Files/Write Data", "x", "x", "", "", "", "x"),
                new RuleEntry("Create Folders/Append Data", "x", "x", "", "", "", "x"),
                new RuleEntry("Write Attributes", "x", "x", "", "", "", "x"),
                new RuleEntry("Write Extended Attributes", "x", "x", "", "", "", "x"),
                new RuleEntry("Delete Subfolders and Files", "x", "", "", "", "", ""),
                new RuleEntry("Delete", "x", "x", "", "", "", ""),
                new RuleEntry("Read Permissions", "x", "x", "x", "x", "x", "x"),
                new RuleEntry("Change Permissions", "x", "", "", "", "", ""),
                new RuleEntry("Take Ownership", "x", "", "", "", "", ""),
                new RuleEntry("Synchronize", "x", "x", "x", "x", "x", "x")
        );
    }

    public void onMouseReleased() {
        menuBar.setCursor(Cursor.DEFAULT);
    }
}
