package com.example.mchatclient;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FirstController implements Initializable {
    @FXML
    private AnchorPane anchor_first;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Change the background color after 2 seconds
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(1000), e -> {
                    anchor_first.setStyle("-fx-background-color: #CBE4DE;");
                })
        );
        timeline.play();

        Timeline navigateTimeline = new Timeline(
                new KeyFrame(Duration.seconds(2), e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage) anchor_first.getScene().getWindow();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                })
        );
        navigateTimeline.play();
    }
}



