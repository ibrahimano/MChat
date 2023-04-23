package com.example.mchatclient;

import com.example.mchatclient.models.AccountInfo;
//import com.example.mchatclient.models.Message;
import com.example.mchatclient.others.Client;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.User;
import models.Message;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {

    @FXML
    private TextField tf_email;

    @FXML
    private TextField tf_username;

    @FXML
    private TextField tf_password;

    @FXML
    private Button btn_signIn;

    @FXML
    private Button btn_login;

    @FXML
    private Label errorLabel;

    @FXML
    private AnchorPane anchorPane2;

    private ArrayList<Message> messages = new ArrayList<Message>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btn_signIn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String email = tf_email.getText();
                String password = tf_password.getText();
                String username = tf_username.getText();

                int port = 7502;
                Client client = new Client(port);
                if(email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                    errorLabel.setText("Veuillez remplir tous les champs requis.");
                    //AnchorPane.setLeftAnchor(errorLabel, 0.0);
                    //AnchorPane.setRightAnchor(errorLabel, 0.0);
                    //errorLabel.setAlignment(Pos.CENTER);
                } else {
                    try {
                        User user = new User();
                        AccountInfo accountInfo = client.connectToServerByRegister(email, password, username);
                        String connectionResult = accountInfo.getConnectionResult();
                        user = accountInfo.getUser();
                        if (connectionResult == "ok") {
                            // afficher loggedIn.fxml
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("loggedIn.fxml"));
                            Parent root = loader.load();
                            LoggedInController controller = loader.getController();
                            controller.setUser(user);
                            controller.setOutput(accountInfo.getOutput());
                            controller.setInput(accountInfo.getInput());
                            controller.setClient(client);
                            controller.setMessages(messages);


                            Stage stage = (Stage) btn_signIn.getScene().getWindow();
                            Scene scene = new Scene(root);

                            stage.setScene(scene);
                            stage.show();
                        } else {
                            errorLabel.setText(connectionResult);
                            AnchorPane.setLeftAnchor(errorLabel, 0.0);
                            AnchorPane.setRightAnchor(errorLabel, 0.0);
                            errorLabel.setAlignment(Pos.CENTER);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btn_login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) btn_login.getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
