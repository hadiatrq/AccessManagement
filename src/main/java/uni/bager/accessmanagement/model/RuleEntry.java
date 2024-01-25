package uni.bager.accessmanagement.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public record RuleEntry(String specialPermissions, String fullControl, String modify, String readExecute,
                        String listFolderContents, String read, String write) {

    // Property getters for JavaFX binding
    public StringProperty specialPermissionsProperty() {
        return new SimpleStringProperty(specialPermissions);
    }

    public StringProperty fullControlProperty() {
        return new SimpleStringProperty(fullControl);
    }

    public StringProperty modifyProperty() {
        return new SimpleStringProperty(modify);
    }

    public StringProperty readExecuteProperty() {
        return new SimpleStringProperty(readExecute);
    }

    public StringProperty listFolderContentsProperty() {
        return new SimpleStringProperty(listFolderContents);
    }

    public StringProperty readProperty() {
        return new SimpleStringProperty(read);
    }

    public StringProperty writeProperty() {
        return new SimpleStringProperty(write);
    }
}

