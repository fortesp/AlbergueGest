package application;

import javafx.application.Preloader;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.MaskerPane;

public class SplashScreenLoader extends Preloader {

    private Stage splashStage;

    @FXML
    private AnchorPane root;
    private MaskerPane progressPane;

    @Override
    public void start(Stage primaryStage) throws Exception {
        splashStage = primaryStage;

        root = new AnchorPane();
        root.setStyle("-fx-background-color: transparent;");
        MaskerPane progressPane = new MaskerPane();
        progressPane.setText("Por favor, esperar...");
        progressPane.setVisible(true);
        AnchorPane.setLeftAnchor(progressPane, 0.0);
        AnchorPane.setTopAnchor(progressPane, 0.0);
        AnchorPane.setRightAnchor(progressPane, 0.0);
        AnchorPane.setBottomAnchor(progressPane, 0.0);
        root.getChildren().add(progressPane);

        Scene scene = new Scene(root, 1024, 600, Color.TRANSPARENT);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    @Override
    public void handleApplicationNotification(PreloaderNotification notification) {
        if (notification instanceof StateChangeNotification) {
            splashStage.hide();
        }
    }


}