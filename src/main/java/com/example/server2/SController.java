package com.example.server2;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SController {
    @FXML
    VBox leftPanel, rightPanel;
    @FXML
    LocalPanel leftPC;
    @FXML
    ServerPanel rightPC;
    @FXML
    public void initialize() {
        leftPC = (LocalPanel) leftPanel.getUserData();
        rightPC = (ServerPanel) rightPanel.getUserData();
    }

    public void exitBtnAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void copyBtnAction(ActionEvent actionEvent) {


        if (leftPC.getSelectedFilenameL() == null && rightPC.getSelectedFilenameR() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "File isn't selected", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Path srcPath = null, dstPath = null;
        if (leftPC.getSelectedFilenameL() != null) {
            srcPath = Paths.get(leftPC.getCurrentPathL(),leftPC.getSelectedFilenameL());
            dstPath = Paths.get(rightPC.getCurrentPathR()).resolve(srcPath.getFileName().toString());
        }
        if (rightPC.getSelectedFilenameR() != null) {
            srcPath = Paths.get(rightPC.getCurrentPathR(),rightPC.getSelectedFilenameR());
            dstPath = Paths.get(leftPC.getCurrentPathL()).resolve(srcPath.getFileName().toString());
        }

        try {
            Files.copy(srcPath,dstPath);
            leftPC.updateListL(Paths.get(leftPC.getCurrentPathL()));
            rightPC.updateListR(Paths.get(rightPC.getCurrentPathR()));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error", ButtonType.OK);
            alert.showAndWait();
        }
    }

}



