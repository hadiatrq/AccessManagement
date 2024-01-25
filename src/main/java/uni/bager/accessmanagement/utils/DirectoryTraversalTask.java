package uni.bager.accessmanagement.utils;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

/**
 * <p>This is a simple Directory Recursive Travel optimization
 * <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/RecursiveTask.html">RecursiveTask</a>
 * </p>
 *
 * @author hadi
 * @since 1.0
 */
public class DirectoryTraversalTask extends RecursiveTask<Void> {
    private final TreeItem<String> root;
    private final File directory;
    private final CountDownLatch latch;
    private static final ConcurrentHashMap<String, List<TreeItem<String>>> cache = new ConcurrentHashMap<>();

    public DirectoryTraversalTask(TreeItem<String> root, File directory, CountDownLatch latch) {
        this.root = root;
        this.directory = directory;
        this.latch = latch;
    }

    @Override
    protected Void compute() {
        List<TreeItem<String>> cachedItems = cache.get(directory.getPath());
        if (cachedItems != null) {
            root.getChildren().addAll(cachedItems);
            latch.countDown();
            return null; // Skip processing if cached
        }

        File[] files = directory.listFiles();
        if (files != null) {
            List<DirectoryTraversalTask> subtasks = List.of(files)
                    .parallelStream()
                    .map(file -> {
                        TreeItem<String> newItem = new TreeItem<>(file.getName());
                        Platform.runLater(() -> root.getChildren().add(newItem));

                        if (file.isDirectory()) {
                            return new DirectoryTraversalTask(newItem, file, latch);
                        } else {
                            return null; // Leaf node, no subtask needed
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            invokeAll(subtasks);
        }
        cache.put(directory.getPath(), root.getChildren());
        // Signal completion when the current task finishes
        latch.countDown();

        return null;
    }

    public static void traverseDirectory(TreeItem<String> rootItem, String rootPath) {

        CountDownLatch latch = new CountDownLatch(1);
        try (ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors())) {
            forkJoinPool.invoke(new DirectoryTraversalTask(rootItem, new File(rootPath), latch));
        }
    }

    /**
     * @param item the {@link TreeItem<String>} of the parent folders to get childs
     * @deprecated since 1.0.1
     */
    private void addLazyLoadingListener(TreeItem<String> item) {
        item.expandedProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue && item.getChildren().isEmpty()) {
                // Directory is expanded, load child items
                try (ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors())) {
                    DirectoryTraversalTask task = new DirectoryTraversalTask(item, new File(item.getValue()), latch);
                    forkJoinPool.invoke(task);
                }
            }
        });
    }
}
