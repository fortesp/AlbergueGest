package controller;

import application.Start;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;

abstract public class Controller {

    @FXML
    GridPane gpMain;

    abstract void initialize();


    <T> T getController(Class cls) {

        return (T) Start.loaders.get(cls);
    }

    Stage getStage(Class controllerClass) {

        return (Stage)((Controller) getController(controllerClass)).gpMain.getScene().getWindow();
    }

    String getLabel(String bundleKey) {

        return Start.labels.getString(bundleKey);
    }

    void closeWindow(Control control) {

        Stage stage = (Stage) control.getScene().getWindow();
        stage.close();
    }

    public void onCloseRequest() {
    }


    public void openWindowsFile(File file) {

        if( Desktop.isDesktopSupported() ) {
            new Thread(() -> {
                try {
                    //Desktop.getDesktop().open( file);
                    Runtime.getRuntime().exec("explorer.exe " + file.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

    }
}
