package com.example.server2;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ServerPanel implements Initializable {
    @FXML
    TableView<FileIn> filesTableR;
    @FXML
    TextField pathFieldR;

    private Path upperCatalogName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ControllerRegistry.register(this);

        TableColumn<FileIn, String> fileTypeColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getLevel().getName()));
        fileTypeColumn.setPrefWidth(20);

        TableColumn<FileIn, String> filenameColumn = new TableColumn<>("Имя");
        filenameColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getFilename()));
        filenameColumn.setPrefWidth(160);

        TableColumn<FileIn, Long> filesizeColumn = new TableColumn<>("Размер");
        filesizeColumn.setCellValueFactory(param ->
                new SimpleObjectProperty<>(param.getValue().getSize()));
        filesizeColumn.setPrefWidth(100);
        filesizeColumn.setCellFactory(param -> {
            return new TableCell<FileIn, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = String.format("%,d bytes", item);
                        if (item == -1L) {
                            text = "[DIR]";
                        }
                        setText(text);
                    }
                }
            };
        });

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        TableColumn<FileIn, String> fileDateColumn = new TableColumn<>("Change was at");
        fileDateColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
        fileDateColumn.setPrefWidth(120);

        filesTableR.getColumns().addAll(fileTypeColumn, filenameColumn, filesizeColumn, fileDateColumn);
        filesTableR.getSortOrder().add(fileTypeColumn);
        filesTableR.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    Path path = Paths.get(pathFieldR.getText()).resolve(filesTableR.getSelectionModel().getSelectedItem().getFilename());
                    if (Files.isDirectory(path)) {
                        updateListR(path);
                    }
                }
            }
        });

        upperCatalogName = Paths.get(pathFieldR.getText());
    }

    public void updateServerList(Path path, List<File> serverItemsList) {
        pathFieldR.setText(path.normalize().toAbsolutePath().toString());
        filesTableR.getItems().clear();
        List<FileIn> serverFileList = serverItemsList.stream()
                .map(File::toPath)
                .map(FileIn::new)
                .collect(Collectors.toList());
        filesTableR.getItems().addAll(serverFileList);
        filesTableR.sort();
    }

    public void updateListR(Path path) {
        try {
            pathFieldR.setText(path.normalize().toAbsolutePath().toString());
            filesTableR.getItems().clear();
            filesTableR.getItems().addAll(Files.list(path).map(FileIn::new).collect(Collectors.toList()));
            filesTableR.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Can't update the file list", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void btnPathUpActionR(ActionEvent actionEvent) {
        Path currentPath = Paths.get(pathFieldR.getText());
        Path upperPath = Paths.get(pathFieldR.getText()).getParent();
        if (currentPath.equals(upperCatalogName)) {
            return;
        }
        if (upperPath == null) {
            return;
        }
        updateListR(upperPath);
    }

    public String getSelectedFilenameR() {
        if (filesTableR == null) {
            return null;
        }

        if (!filesTableR.isFocused()) {
            return null;
        }
        return filesTableR.getSelectionModel().getSelectedItem().getFilename();
    }

    public String getCurrentPathR() {
        return pathFieldR.getText();
    }

    public void renderServerFileList(List<File> serverItemsList) {
        updateServerList(Paths.get(".", "image(3)"), serverItemsList);
    }
}



