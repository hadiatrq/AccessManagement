package uni.bager.accessmanagement.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import uni.bager.accessmanagement.common.ResourcePath;
import uni.bager.accessmanagement.utils.DirectoryTraversalTask;
import uni.bager.accessmanagement.utils.LazyLoadingTreeItem;
import uni.bager.accessmanagement.model.AccessControlEntry;
import uni.bager.accessmanagement.utils.PageManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

public class MainController implements Initializable {
    private double x = 0;
    private double y = 0;
    @FXML
    public AnchorPane middleAnchor;
    @FXML
    private TextField middleAnchorLabel;
    @FXML
    TreeView<String> treeView;
    @FXML
    private TextField pathTextArea;
    @FXML
    public ListView<String> resultListView;
    @FXML
    private TableView<AccessControlEntry> resultTableView;
    @FXML
    private TableColumn<AccessControlEntry, String> typeColumn;
    @FXML
    private TableColumn<AccessControlEntry, String> principalColumn;
    @FXML
    private TableColumn<AccessControlEntry, String> permissionsColumn;
    @FXML
    private Label detailLabel;
    @FXML
    private AnchorPane topPane;
    @FXML
    private MenuBar menuBar;
    @FXML
    private ProgressIndicator spinner;
    private Stage stage;
    static String selectedPath;

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
    private void onMouseReleased() {
        menuBar.setCursor(Cursor.DEFAULT);
    }

    @FXML
    private void onQuitDragged(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void minimizeWindow(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    protected void onPathInputAction() {
        String path = pathTextArea.getText();
        path = pathFormat(path);
        File rootFile = new File(path);
        if (rootFile.exists()) {
            String finalPath = path;

            Platform.runLater(() -> spinner.setVisible(true));
            // the thread safe to call recursively the directory tree
            Task<Void> buildTreeTask = new Task<>() {
                @Override
                protected Void call() {
                    boolean lazyFlag = TogglesData.getInstance().isLazy();
                    boolean fullFetchFlag = TogglesData.getInstance().isFull();
                    if (fullFetchFlag) {
                        TreeItem<String> rootItem = new TreeItem<>(rootFile.getName());
                        rootItem.setExpanded(true);
                        DirectoryTraversalTask.traverseDirectory(rootItem, finalPath);
                        Platform.runLater((() -> treeView.setRoot(rootItem)));
                    } else if (lazyFlag) {
                        LazyLoadingTreeItem root = new LazyLoadingTreeItem(rootFile);
                        Platform.runLater(() -> treeView.setRoot(root));
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    Platform.runLater(() -> spinner.setVisible(false));
                }

                @Override
                protected void failed() {
                    spinner.setVisible(true);
                }
            };
            new Thread(buildTreeTask).start();
        }
    }

    @FXML
    protected void onTreeViewItemSelected() {
        TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            selectedPath = getPathForTreeItem(selectedItem);
            updateMiddleAnchorTableSection(selectedPath);
        }
    }

    @FXML
    protected void onTableViewItemSelected() {
        AccessControlEntry selectedTableItem = resultTableView.getSelectionModel().getSelectedItem();
        if (selectedTableItem != null) {
            updateRightAnchorListView(selectedTableItem);
        }
    }

    // exit from app
    @FXML
    protected void onQuitMIClick() {
        javafx.application.Platform.exit();
    }

    @FXML
    private void resetAllFields() {
        pathTextArea.clear();
        middleAnchorLabel.clear();
        treeView.setRoot(new TreeItem<>());
        resultTableView.getItems().clear();
        resultListView.getItems().clear();
    }

    @FXML
    protected void openAboutRulesPage() {
        stage = switchPage(ResourcePath.ABOUT_RULES.getPath());
        if (stage != null) {
            stage.getScene().setFill(Color.TRANSPARENT);
            stage.setResizable(false);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.show();
        }
    }

    @FXML
    protected void openAddNewRulePage() {
        stage = switchPage(ResourcePath.ADD_RULES.getPath());
        if (stage != null) {
            stage.getScene().setFill(Color.TRANSPARENT);
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.show();
        }
    }

    @FXML
    protected void openSearchPage() {
        stage = switchPage(ResourcePath.SEARCH.getPath());
        if (stage != null) {
            stage.getScene().setFill(Color.TRANSPARENT);
            stage.setResizable(false);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.show();
        }
    }

    @FXML
    protected void openPreferencesPage() {
        if (onePage(ResourcePath.PREFERENCES.getPath())) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(ResourcePath.PREFERENCES.getPath()));
                Parent root;
                root = loader.load();
                PreferencesController preferencesController = loader.getController();
                preferencesController.applyToggleSwitchLogic();
                Scene scene = new Scene(root);
                scene.setFill(Color.TRANSPARENT);
                stage = new Stage();
                stage.setScene(scene);
                stage.setResizable(false);
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.setOnHidden(windowEvent -> PageManager.getInstance().setPageOpen(ResourcePath.PREFERENCES.getPath(), false));
                PageManager.getInstance().setPageOpen(ResourcePath.PREFERENCES.getPath(), true);
                stage.setAlwaysOnTop(true);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean onePage(String fxmlPath) {
        // Check if the page is already open
        if (PageManager.getInstance().isPageOpen(fxmlPath)) {
            System.err.println("Page is already open: " + fxmlPath);
            return false;
        }
        return true;
    }

    /////////////////////////////////////////////////////////////
    @Deprecated
    private void buildDirectoryTreeSecond(TreeItem<String> parent, File parentFile) {
        try (Stream<Path> paths = Files.walk(parentFile.toPath())) {
            paths.filter(Files::isRegularFile) // Filter for regular files if needed
                    .map(path -> new TreeItem<>(path.getFileName().toString()))
                    .forEach(parent.getChildren()::add);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Deprecated
    private void buildDirectoryTree(TreeItem<String> parent, File parentFile) {
        parent.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && parent.getChildren().isEmpty()) {
                loadChildren(parent, parentFile);
            }
        });
    }

    @Deprecated
    private void loadChildren(TreeItem<String> parent, File parentFile) {
        // List files in the current directory
        File[] files = parentFile.listFiles();
        if (files != null) {
            for (File file : files) {
                TreeItem<String> newItem = new TreeItem<>(file.getName());
                // If the current file is a directory, add a dummy item and set an event handler
                if (file.isDirectory()) {
                    newItem.expandedProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue && newItem.getChildren().isEmpty()) {
                            loadChildren(newItem, file);
                        }
                    });
                }
                parent.getChildren().add(newItem);
            }
        }
    }

    private void buildDirectoryTree(List<String> pathList, File parentFile) {
        // List files in the current directory
        File[] files = parentFile.listFiles();
        if (files != null) {
            for (File file : files) {
                // If the current file is a directory, recursively build its tree
                if (file.isDirectory()) {
                    buildDirectoryTree(pathList, file);
                }
                pathList.add(file.getAbsolutePath());
            }
        }
    }

    /////////////////////////////////////////////////////////////
    // Get the path of file or directory and then find the all subItems access control and then
    // add and return it as ObservableList !!
    private static ObservableList<AccessControlEntry> getAccessControlRule(Path path) {
        ObservableList<AccessControlEntry> result = FXCollections.observableArrayList();
        try {
            AclFileAttributeView aclFileAttributeView = Files.getFileAttributeView(path, AclFileAttributeView.class);
            List<AclEntry> aclList = aclFileAttributeView.getAcl();
            for (AclEntry aclEntry : aclList) {
                if (aclEntry.permissions().toString().equals("[]"))
                    continue;
                result.add(new AccessControlEntry(
                        new SimpleStringProperty(aclEntry.type().toString()),
                        new SimpleStringProperty(aclEntry.principal().toString()),
                        new SimpleStringProperty(aclEntry.permissions().toString())
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.add(new AccessControlEntry(new SimpleStringProperty("Error"), new SimpleStringProperty(""),
                    new SimpleStringProperty("Error retrieving access control entries.")));
        }
        return result;
    }

    // convert the tree item to understandable path for operating system
    String getPathForTreeItem(TreeItem<String> treeItem) {
        StringBuilder pathBuilder = new StringBuilder(treeItem.getValue());
        TreeItem<String> parent = treeItem.getParent();
        if (parent != null) {
            pathBuilder.insert(0, getPathForTreeItem(parent) + File.separator);
        } else
            return pathTextArea.getText();
        return pathBuilder.toString();
    }

    private void updateRightAnchorListView(AccessControlEntry selectedTableItem) {
        ObservableList<String> listViewObservableList = getPermissionProperties(selectedTableItem);
        resultListView.getItems().clear();
        detailLabel.setText("Permission Details");
        resultListView.setItems(listViewObservableList);
    }

    private ObservableList<String> getPermissionProperties(AccessControlEntry selectedTableItem) {
        ObservableList<String> observableList = FXCollections.observableArrayList();
        String selectedPermission = selectedTableItem.getPermissions();
        if (!selectedPermission.isEmpty()) {
            String[] permissions = selectedPermission.replaceAll("[\\[\\]]", "").split(",");
            for (String permission : permissions) {
                observableList.add(permission.trim());
            }
        }
        return observableList;
    }

    // update the table according to given ObservableList in getAccessControlRule method
    private void updateMiddleAnchorTableSection(String selectedPath) {
        // Detail Section data
        Path path = Paths.get(selectedPath);
        ObservableList<String> resultView = getFileProperties(path);
        detailLabel.setText("File Details");
        resultListView.setItems(resultView);

        // Middle Section data
        middleAnchorLabel.setText(pathFormat(selectedPath));
        ObservableList<AccessControlEntry> result = getAccessControlRule(path);
        resultTableView.setItems(result);
    }

    // get the permission data and convert to ObservableList to show in list view
    private ObservableList<String> getFileProperties(Path path) {
        ObservableList<String> result = FXCollections.observableArrayList();
        try {
            File file = new File(path.toUri());
            if (file.exists()) {
                BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
                // Size
                long size = attributes.size();
                DecimalFormat df = new DecimalFormat("#.00");
                if (sizeDigitCount(size) <= 3) {
                    result.add("Size : " + size + " bytes");
                } else if (sizeDigitCount(size) <= 6 && sizeDigitCount(size) >= 3) {
                    double doubleSize = (double) size / 1_024;
                    result.add("Size : " + df.format(doubleSize) + " KB");
                } else if (sizeDigitCount(size) <= 9 && sizeDigitCount(size) >= 6) {
                    double doubleSize = (double) size / 1_048_576;
                    result.add("Size : " + df.format(doubleSize) + " MB");
                } else if (sizeDigitCount(size) <= 12 && sizeDigitCount(size) >= 9) {
                    double doubleSize = (double) size / 1_073_741_824;
                    result.add("Size : " + df.format(doubleSize) + " GB");
                }
                // Location (Absolute path)
                result.add("Location : " + file.getAbsolutePath());
                // Type (File or Directory)
                String type = attributes.isDirectory() ? "Directory" : "File";
                result.add("Type : " + type);
                // Created date
                FileTime creationTime = attributes.creationTime();
                result.add("Created Date : " + formatDateTime(creationTime.toMillis()));
                // Modified date
                FileTime lastModifiedTime = attributes.lastModifiedTime();
                result.add("Modified Date : " + formatDateTime(lastModifiedTime.toMillis()));
                // Accessed date
                FileTime lastAccessedTime = attributes.lastAccessTime();
                result.add("Accessed Date : " + formatDateTime(lastAccessedTime.toMillis()));

            } else
                System.out.println("File not found : " + file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private int sizeDigitCount(long size) {
        int count = 0;
        while (size >= 1) {
            size /= 10;
            count++;
        }
        return count;
    }

    private static String formatDateTime(long millis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date(millis));
    }

    private Stage switchPage(String FXML) {
        if (onePage(FXML)) {
            Parent root;
            stage = new Stage();
            try {
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FXML)));
                Scene scene = new Scene(root);
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setOnHidden(windowEvent -> PageManager.getInstance().setPageOpen(FXML, false));
            PageManager.getInstance().setPageOpen(FXML, true);
            return stage;
        }
        return null;
    }

    private String pathFormat(String path) {
        path = path.replaceAll("\\\\+", "\\\\");
        if (path.endsWith("\\"))
            path = path.substring(0, path.length() - 1);
        return path;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        principalColumn.setCellValueFactory(cellData -> cellData.getValue().principalProperty());
        permissionsColumn.setCellValueFactory(cellData -> cellData.getValue().permissionsProperty());
        middleAnchor.setVisible(true);
        spinner.setVisible(false);
        TogglesData.getInstance().setLazy(false);
        TogglesData.getInstance().setFull(true);
    }
}