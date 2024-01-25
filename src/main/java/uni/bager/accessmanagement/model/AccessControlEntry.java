package uni.bager.accessmanagement.model;

import javafx.beans.property.SimpleStringProperty;

// A record for managing acl items to pars into a table
public record AccessControlEntry(SimpleStringProperty type, SimpleStringProperty principal,
                                 SimpleStringProperty permissions) {
    public String getType() {
        return type.get();
    }

    public SimpleStringProperty typeProperty() {
        return type;
    }

    public String getPrincipal() {
        return principal.get();
    }

    public SimpleStringProperty principalProperty() {
        return principal;
    }

    public String getPermissions() {
        return permissions.get();
    }

    public SimpleStringProperty permissionsProperty() {
        return permissions;
    }
}
