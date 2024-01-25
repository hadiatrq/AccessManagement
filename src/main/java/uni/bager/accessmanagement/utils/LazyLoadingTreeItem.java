package uni.bager.accessmanagement.utils;

import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.List;

public class LazyLoadingTreeItem extends TreeItem<String> {

    private final File file;
    private boolean isLeaf;
    private boolean childrenLoaded;

    public LazyLoadingTreeItem(File file) {
        super(file.getName());
        this.file = file;
        this.isLeaf = file.isFile();
        this.childrenLoaded = false;

        // Set a dummy item as the only child to show the expand arrow
        if (!isLeaf) {
            getChildren().add(new TreeItem<>(file.getName()));
        }

        // Add event listener for expanding the tree item
        expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && !childrenLoaded) {
                loadChildren();
            }
        });
    }

    private void loadChildren() {
        List<File> childFiles = listChildFiles(file);
        getChildren().clear();

        for (File childFile : childFiles) {
            LazyLoadingTreeItem childItem = new LazyLoadingTreeItem(childFile);
            getChildren().add(childItem);
        }

        childrenLoaded = true;
    }

    private List<File> listChildFiles(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            return List.of(files);
        }
        return List.of();
    }

    @Override
    public boolean isLeaf() {
        return isLeaf;
    }
}
