package com.example.mchatclient;



import com.example.mchatclient.models.AccountInfo;

import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import models.Message;
import models.User;
import com.example.mchatclient.others.Client;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import java.util.ArrayList;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField tf_email;

    @FXML
    private TextField tf_password;

    @FXML
    private Button btn_login;

    @FXML
    private Button btn_new_account;

    @FXML
    private Label errorLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btn_login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String email = tf_email.getText();
                String password = tf_password.getText();

                int port = 7502;
                Client client = new Client(port);

                try {
                    User user = new User();
                    ArrayList<Message> messages = new ArrayList<Message>();
                    AccountInfo accountInfo = client.connectToServerByLogin(email, password);
                    String connectionResult = accountInfo.getConnectionResult();
                    if (connectionResult.equals("ok")) {
                        // afficher loggedIn.fxml
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("loggedIn.fxml"));
                        Parent root = loader.load();
                        LoggedInController controller = loader.getController();
                        user = accountInfo.getUser();
                        messages.addAll(accountInfo.getMessages());
                        controller.setUser(user);
                        controller.setOutput(accountInfo.getOutput());
                        controller.setInput(accountInfo.getInput());
                        controller.setMessages(accountInfo.getMessages());
                        controller.setClient(client);


                        Stage stage = (Stage) btn_login.getScene().getWindow();
                        Scene scene = new Scene(root, 877, 535);
                        //stage.setMinHeight(550);
                        //stage.setMinWidth(890);
                        stage.setScene(scene);
                        stage.show();
                    } else { //error
                        errorLabel.setText("Email ou mot de passe invalide");

                    }

                } catch (Exception e) {
                    //e.printStackTrace();
                    errorLabel.setText("échec de connexion, veuillez réessayer plus tard");
                    AnchorPane.setLeftAnchor(errorLabel, 0.0);
                    AnchorPane.setRightAnchor(errorLabel, 0.0);
                    errorLabel.setAlignment(Pos.CENTER);
                }
            }

        });

        btn_new_account.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("sign-up.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) btn_new_account.getScene().getWindow();
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
