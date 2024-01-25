module uni.bager.accessmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    // add icon pack modules
    requires org.kordamp.ikonli.fontawesome5;

    opens uni.bager.accessmanagement to javafx.fxml;
    opens uni.bager.accessmanagement.controller to javafx.fxml;
    exports uni.bager.accessmanagement;
    exports uni.bager.accessmanagement.controller;
    exports uni.bager.accessmanagement.model;
    exports uni.bager.accessmanagement.utils;
}