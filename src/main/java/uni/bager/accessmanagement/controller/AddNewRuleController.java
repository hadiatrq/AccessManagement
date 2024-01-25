package uni.bager.accessmanagement.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;

import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.*;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class AddNewRuleController implements Initializable {
    private double x = 0;
    private double y = 0;
    @FXML
    public MenuBar menuBar;
    @FXML
    private Group chGroup;
    @FXML
    private TextField username;
    @FXML
    private TextField path;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private ToggleSwitch isOverWrite;
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
    protected void onCancelButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onButtonCheckAll(ActionEvent event) {
        chGroup.getChildren().forEach(node -> ((CheckBox) node).setSelected(true));
    }

    @FXML
    private void onButtonUnCheckAll(ActionEvent event) {
        chGroup.getChildren().forEach(node -> ((CheckBox) node).setSelected(false));
    }

    @FXML
    private void onResetButton(ActionEvent event) {
        username.clear();
        path.clear();
        choiceBox.getSelectionModel().clearSelection();
        isOverWrite.setSelected(false);
        onButtonUnCheckAll(event);
    }

    @FXML
    private void onQuitDragged(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }


    @FXML
    private void onButtonApply(ActionEvent event) {
        String filePath = path.getText();
        String userName = username.getText();
        AclEntryType aclEntryType = null;
        String type = choiceBox.getSelectionModel().getSelectedItem();
        switch (type) {
            case "Alarm" -> aclEntryType = AclEntryType.ALARM;
            case "Audit" -> aclEntryType = AclEntryType.AUDIT;
            case "Deny" -> aclEntryType = AclEntryType.DENY;
            case "Grant" -> aclEntryType = AclEntryType.ALLOW;
        }
        Set<AclEntryPermission> permissions = new HashSet<>();
        chGroup.getChildren().forEach(node -> {
            CheckBox checkBox = (CheckBox) node;
            String permissionName = checkBox.getText();
            boolean selected = checkBox.isSelected();
            if (selected) {
                switch (permissionName) {
                    case "APPEND_DATA" -> permissions.add(AclEntryPermission.APPEND_DATA);
                    case "DELETE" -> permissions.add(AclEntryPermission.DELETE);
                    case "DELETE_CHILD" -> permissions.add(AclEntryPermission.DELETE_CHILD);
                    case "EXECUTE" -> permissions.add(AclEntryPermission.EXECUTE);
                    case "READ_ACL" -> permissions.add(AclEntryPermission.READ_ACL);
                    case "READ_ATTRIBUTES" -> permissions.add(AclEntryPermission.READ_ATTRIBUTES);
                    case "READ_DATA" -> permissions.add(AclEntryPermission.READ_DATA);
                    case "READ_NAMED_ATTRS" -> permissions.add(AclEntryPermission.READ_NAMED_ATTRS);
                    case "SYNCHRONIZE" -> permissions.add(AclEntryPermission.SYNCHRONIZE);
                    case "WRITE_ACL" -> permissions.add(AclEntryPermission.WRITE_ACL);
                    case "WRITE_ATTRIBUTES" -> permissions.add(AclEntryPermission.WRITE_ATTRIBUTES);
                    case "WRITE_DATA" -> permissions.add(AclEntryPermission.WRITE_DATA);
                    case "WRITE_NAMED_ATTRS" -> permissions.add(AclEntryPermission.WRITE_NAMED_ATTRS);
                    case "WRITE_OWNER" -> permissions.add(AclEntryPermission.WRITE_OWNER);
                }
            }
        });
        boolean clear = isOverWrite.isSelected();
        if (!userName.isEmpty() && !filePath.isEmpty()) {
            setAccessControlRule(Paths.get(filePath), userName, aclEntryType, permissions, clear);
            closePage();
        }
    }


    private static void setAccessControlRule(Path filePath, String userName, AclEntryType type, Set<AclEntryPermission> permissions, boolean clearFlag) {
        try {
            UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
            UserPrincipal userPrincipal = lookupService.lookupPrincipalByName(userName);

            AclFileAttributeView aclFileAttributeView = Files.getFileAttributeView(filePath, AclFileAttributeView.class);
            AclEntry aclEntry = AclEntry.newBuilder()
                    .setType(type)
                    .setPrincipal(userPrincipal)
                    .setPermissions(permissions)
                    .build();
            List<AclEntry> aclList = aclFileAttributeView.getAcl();
            if (clearFlag)
                aclList.clear();
            aclList.add(aclEntry);
            aclFileAttributeView.setAcl(aclList);
        } catch (UserPrincipalNotFoundException e) {
            System.err.println("User not found: " + userName);
            System.out.println("Opps something went wrong !!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Opps something went wrong !!");
        }
    }

    private void closePage() {
        Stage stage = (Stage) path.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        path.setText(MainController.selectedPath);
    }

    public void onMouseReleased() {
        menuBar.setCursor(Cursor.DEFAULT);
    }
}
