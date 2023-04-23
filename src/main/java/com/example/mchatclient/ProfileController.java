package com.example.mchatclient;

import com.example.mchatclient.others.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import models.Message;
import models.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class ProfileController implements Initializable {
    private User user;
    private Client client;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    @FXML
    private RadioButton btn_status;

    @FXML
    private Button btn_edit_username, btn_edit_email, btn_edit_password;

    @FXML
    private Button btn_confirm_username, btn_confirm_email, btn_confirm_password;

    @FXML
    private TextField tf_username, tf_email, tf_password;

    @FXML
    private Circle avatar;

    @FXML
    private Label usernameLabel, userNameLabel, emailLabel, passwordLabel;

    @FXML
    private Label userNameErrorLabel, emailErrorLabel;

    @FXML
    private Pane pn_profil, pn_chat, pn_logout;
    private ArrayList<Message> messages;

    private boolean isUsernameEditMode = false;
    private boolean isEmailEditMode = false;
    private boolean isPasswordEditMode = false;

    private VBox usersBox;

    private boolean online;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //profil btn
        pn_profil.setOnMouseEntered(e -> {
            pn_profil.setStyle("-fx-background-color:  #0E8388;");
        });

        pn_profil.setOnMouseExited(e -> {
            pn_profil.setStyle("");
        });

        pn_profil.setOnMouseClicked(e -> {
            pn_profil.setStyle("-fx-background-color:  #CBE4DE;");
        });


        //logout btn

        pn_logout.setOnMouseEntered(e -> {
            pn_logout.setStyle("-fx-background-color:  #0E8388;");
        });

        pn_logout.setOnMouseExited(e -> {
            pn_logout.setStyle("");
        });

        pn_logout.setOnMouseClicked(e -> {
            try {
                client.deconnectFromServer(output, input, user);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) pn_logout.getScene().getWindow();
                Scene scene = new Scene(root, 718, 453);
                stage.setMinHeight(480);
                stage.setMinWidth(718);
                stage.setScene(scene);
                stage.show();
            } catch (IOException error) {
                error.printStackTrace();
            }
        });

        //chat btn
        pn_chat.setOnMouseEntered(e -> {
            pn_chat.setStyle("-fx-background-color:  #0E8388;");
        });

        pn_chat.setOnMouseExited(e -> {
            pn_chat.setStyle("");
        });

        pn_chat.setOnMouseClicked(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("loggedIn.fxml"));
                Parent root = loader.load();
                LoggedInController controller = loader.getController();
                controller.setUser(user);
                controller.setOutput(output);
                controller.setInput(input);
                controller.setClient(client);
                controller.setMessages(messages);
                output.writeObject("@chat");
                if(!online) {
                    controller.setStatusOffline();
                } else {
                    controller.setStatusOnline();
                }
                controller.setUsersBox(usersBox);
                Stage stage = (Stage) pn_chat.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException error) {
                error.printStackTrace();
            }
        });

        //pour changer le status de user online ou offline
        btn_status.setOnAction(e ->  {
            if(btn_status.isSelected()) {
                //user online
                String msg = "@online:" + user.getId();
                try {
                    online = true;
                    output.writeObject(msg);
                    output.flush();
                } catch (IOException ex) {
                    System.err.println("Erreur lors de l'envoi du message au serveur: " + ex.getMessage());
                }
            } else {
                //user offline
                String msg = "@offline:" + user.getId();
                try {
                    online = false;
                    output.writeObject(msg);
                    output.flush();
                } catch (IOException ex) {
                    System.err.println("Erreur lors de l'envoi du message au serveur: " + ex.getMessage());
                }

            }
        });

        //modifier les info de user
        tf_username.setVisible(false);
        tf_email.setVisible(false);
        tf_password.setVisible(false);

        btn_confirm_username.setVisible(false);
        btn_confirm_email.setVisible(false);
        btn_confirm_password.setVisible(false);

        btn_edit_username.setOnMouseEntered(e -> {
            btn_edit_username.setStyle("-fx-background-color:  #0E8388;");
        });

        btn_edit_email.setOnMouseEntered(e -> {
            btn_edit_email.setStyle("-fx-background-color:  #0E8388;");
        });

        btn_edit_password.setOnMouseEntered(e -> {
            btn_edit_password.setStyle("-fx-background-color:  #0E8388;");
        });

        btn_edit_username.setOnMouseExited(e -> {
            btn_edit_username.setStyle("-fx-background-color:  #CBE4DE;");
        });

        btn_edit_email.setOnMouseExited(e -> {
            btn_edit_email.setStyle("-fx-background-color:  #CBE4DE;");
        });

        btn_edit_password.setOnMouseExited(e -> {
            btn_edit_password.setStyle("-fx-background-color:  #CBE4DE;");
        });



        btn_edit_username.setOnMouseClicked(e -> {
            if(!isUsernameEditMode){
                userNameLabel.setVisible(false);
                tf_username.setVisible(true);
                btn_confirm_username.setVisible(true);
                tf_username.setText(userNameLabel.getText());
                isUsernameEditMode = true;
            } else {
                userNameLabel.setVisible(true);
                tf_username.setVisible(false);
                btn_confirm_username.setVisible(false);
                isUsernameEditMode = false;
            }
        });


        btn_edit_email.setOnMouseClicked(e -> {
            if(!isEmailEditMode) {
                emailLabel.setVisible(false);
                tf_email.setVisible(true);
                btn_confirm_email.setVisible(true);
                tf_email.setText(emailLabel.getText());
                isEmailEditMode = true;
            } else {
                emailLabel.setVisible(true);
                tf_email.setVisible(false);
                btn_confirm_email.setVisible(false);
                isEmailEditMode = false;
            }
        });

        btn_edit_password.setOnMouseClicked(e -> {
            if(!isPasswordEditMode) {
                passwordLabel.setVisible(false);
                tf_password.setVisible(true);
                btn_confirm_password.setVisible(true);
                tf_password.setText(user.getPassword());
                isPasswordEditMode = true;
            } else {
                passwordLabel.setVisible(true);
                tf_password.setVisible(false);
                btn_confirm_password.setVisible(false);
                isPasswordEditMode = false;
            }
        });


        btn_confirm_username.setOnMouseEntered(e -> {
            btn_confirm_username.setStyle("-fx-background-color:  #228B22;");
        });

        btn_confirm_email.setOnMouseEntered(e -> {
            btn_confirm_email.setStyle("-fx-background-color:  #228B22;");
        });

        btn_confirm_password.setOnMouseEntered(e -> {
            btn_confirm_password.setStyle("-fx-background-color:  #228B22;");
        });

        btn_confirm_username.setOnMouseExited(e -> {
            btn_confirm_username.setStyle("-fx-background-color: #42b72a;");
        });

        btn_confirm_email.setOnMouseExited(e -> {
            btn_confirm_email.setStyle("-fx-background-color: #42b72a;");
        });

        btn_confirm_password.setOnMouseExited(e -> {
            btn_confirm_password.setStyle("-fx-background-color:  #42b72a;");
        });

        btn_confirm_username.setOnMouseClicked(e -> {
            String oldUsername = emailLabel.getText();
            String newUsername = tf_username.getText();
            //envoyer vers le server pour modifer dans DB
            try {
                output.writeObject("@editUsername");
                output.writeObject(newUsername);
                String isValid = (String) input.readObject();
                if(isValid.equals("ok")) {
                    userNameErrorLabel.setVisible(false);
                    usernameLabel.setText(newUsername);
                    userNameLabel.setText(newUsername);
                    tf_username.setVisible(false);
                    btn_confirm_username.setVisible(false);
                    userNameLabel.setVisible(true);
                    user.setUsername(newUsername);
                    for(int i = 0; i < messages.size(); i++) {
                        if(messages.get(i).getUserId() == user.getId()) {
                            messages.get(i).setUsername(newUsername);
                        }
                    }
                } else {
                    userNameErrorLabel.setVisible(true);
                    userNameErrorLabel.setText("Cet username est déja exist");
                }
            } catch (IOException eq) {
                eq.printStackTrace();
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            //modifier user et dans liste messages where username = old username


        });
        btn_confirm_email.setOnMouseClicked(e -> {
            String newEmail = tf_email.getText();
            //envoyer vers le server pour modifer dans DB
            try {
                output.writeObject("@editEmail");
                output.writeObject(newEmail);
                String isValid = (String) input.readObject();
                if(isValid.equals("ok")) {
                    emailErrorLabel.setVisible(false);
                    emailLabel.setText(newEmail);
                    tf_email.setVisible(false);
                    btn_confirm_email.setVisible(false);
                    emailLabel.setVisible(true);
                    user.setEmail(newEmail);
                } else {
                    emailErrorLabel.setVisible(true);
                    emailErrorLabel.setText("Cet e-mail est déja exist");
                }
            } catch (IOException eq) {
                eq.printStackTrace();
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }

        });
        btn_confirm_password.setOnMouseClicked(e -> {
            String newPassword = tf_password.getText();
            //envoyer vers le server pour modifer dans DB
            try {
                output.writeObject("@editPassword");
                output.writeObject(newPassword);
                tf_password.setVisible(false);
                btn_confirm_password.setVisible(false);
                passwordLabel.setVisible(true);
                user.setPassword(newPassword);
            } catch (IOException eq) {
                eq.printStackTrace();
            }
        });

    }

    //pour avoir notre user info
    public void setUser(User user) {
        //pour profil
        usernameLabel.setText(user.getUsername());
        //pour edit
        userNameLabel.setText(user.getUsername());
        emailLabel.setText(user.getEmail());
        this.user = user;
        Image userImg = new Image("user.png");
        ImagePattern pattern = new ImagePattern(userImg);
        avatar.setFill(pattern);
    }

    //pour avoir notre user output et input
    public void setOutput(ObjectOutputStream output) {
        this.output = output;
        if(output == null) {
            System.out.println("output null");
        }
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
        if(input == null) {
            System.out.println("input null");
        }
    }

    //avoir notre client
    public void setClient(Client client) {
        this.client = client;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
        System.out.println("message size: " + messages.size());

    }


    public void setUsersBox(VBox usersBox) {
        this.usersBox = usersBox;
    }

    public void setOnline(boolean online) {
        this.online = online;
        if(online) {
            btn_status.setSelected(true);
        } else {
            btn_status.setSelected(false);
        }
    }
}
