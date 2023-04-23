package com.example.mchatclient;


import com.example.mchatclient.others.Client;
import com.example.mchatclient.others.ClientListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import models.Message;
import models.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LoggedInController implements Initializable {
    private User user;
    private Client client;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    @FXML
    private Circle avatar;

    @FXML
    private Label usernameLabel;

    @FXML
    private Pane pn_profil;

    @FXML
    private Pane pn_chat;

    @FXML
    private Pane pn_logout;

    @FXML
    private Button btn_send;



    @FXML
    private TextField tf_message;

    @FXML
    private VBox messageBox;

    @FXML
    private VBox usersBox;

    @FXML
    private ScrollPane scrollMsgs;

    @FXML
    private ScrollPane scrollUsers;

    @FXML
    private RadioButton btn_status;

    private ArrayList<Message> oldMessages;


    @FXML AnchorPane  anchor_chat_messages, anchorPane_chat_profil, anchor_chat_userOnline;

    //thread
    ClientListener receiveMessages;
    Thread threadListener;

    private boolean online;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {



       // System.out.println(user.toString());
       // usernameLabel.setText(user.getUsername());

        //profil btn
        pn_profil.setOnMouseEntered(e -> {
            pn_profil.setStyle("-fx-background-color:  #0E8388;");
        });

        pn_profil.setOnMouseExited(e -> {
            pn_profil.setStyle("");
        });

        pn_profil.setOnMouseClicked(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("profile.fxml"));
                Parent root = loader.load();
                ProfileController controller = loader.getController();
                oldMessages.addAll(receiveMessages.getRecievedMessages());
                controller.setUser(user);
                controller.setOutput(output);
                controller.setInput(input);
                controller.setClient(client);
                controller.setMessages(oldMessages);
                controller.setUsersBox(usersBox);
                controller.setOnline(online);
                output.writeObject("@profil");


                Stage stage = (Stage) pn_profil.getScene().getWindow();
                //Stage stage = new Stage();
                //stage.initStyle(StageStyle.UNDECORATED);
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException error) {
                error.printStackTrace();
            }
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
            pn_chat.setStyle("-fx-background-color:  #CBE4DE;");
          /*  try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("loggedIn.fxml"));
                Parent root = loader.load();
                LoggedInController controller = loader.getController();
                controller.setUser(user);
                controller.setOutput(output);
                controller.setInput(input);
                controller.setClient(client);
                controller.setMessages(oldMessages);
                Stage stage = (Stage) pn_chat.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException error) {
                error.printStackTrace();
            }*/
        });

        btn_send.setOnMouseClicked(e -> {
            String username = user.getUsername();
            String msg = tf_message.getText();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Message message = new Message(msg, timestamp, user.getId(), username);
            oldMessages.add(message);

            if(!msg.isEmpty()) {
                try {
                    output.writeObject(msg);
                    output.flush();
                    tf_message.clear();
                } catch (IOException ex) {
                    System.err.println("Erreur lors de l'envoi du message au serveur: " + ex.getMessage());
                }

                //username
                HBox usernameBox = new HBox();
                usernameBox.setAlignment(Pos.CENTER_RIGHT);
                usernameBox.setPadding(new Insets(10, 5, 0, 10));
                Text usernameText = new Text(username);
                usernameText.setFont(Font.font("System", FontWeight.NORMAL, 12));
                usernameText.setFill(Color.GRAY);
                usernameBox.getChildren().add(usernameText);

                //message
                HBox userMsgBox = new HBox();
                userMsgBox.setAlignment(Pos.CENTER_RIGHT);
                userMsgBox.setPadding(new Insets(3, 5, 5, 10));
                Text text = new Text(msg);
                TextFlow textFlow = new TextFlow(text);
                textFlow.setStyle("-fx-background-color: rgb(15, 125, 242); -fx-background-radius: 5px;");
                textFlow.setPadding(new Insets(6, 10, 8, 10));
                text.setFill(Color.color(0.934, 0.945, 0.996));
                text.setFont(Font.font("System", FontWeight.NORMAL, 13));
                userMsgBox.getChildren().add(textFlow);

                //date
                HBox timeStampBox = new HBox();
                timeStampBox.setAlignment(Pos.CENTER_RIGHT);
                timeStampBox.setPadding(new Insets(0, 15, 15, 10));

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                LocalDateTime now = LocalDateTime.now();
                Text timeStampText = new Text(dtf.format(now));
                timeStampText.setFont(Font.font("System", FontWeight.NORMAL, 10));
                timeStampText.setFill(Color.GRAY);
                timeStampBox.getChildren().add(timeStampText);

                messageBox.getChildren().addAll(usernameBox, userMsgBox, timeStampBox);

            }
        });

        //pour changer le status de user online ou offline
        btn_status.setOnAction(e ->  {
            if(btn_status.isSelected()) {
                //user online
                String msg = "@online:" + user.getId();
                try {
                    online = true;
                    tf_message.setDisable(false);
                    tf_message.setText("");
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
                    tf_message.setDisable(true);
                    tf_message.setText("Vous êtes hors ligne maintenant!!");
                    output.writeObject(msg);
                    output.flush();
                } catch (IOException ex) {
                    System.err.println("Erreur lors de l'envoi du message au serveur: " + ex.getMessage());
                }

            }
        });

            //pour scroll les messages
        messageBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                scrollMsgs.setVvalue((double) newValue);
            }
        });

        //pour scroll les users connectee
        usersBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                scrollUsers.setVvalue((double) newValue);
            }
        });


    }





    //functions

    //ajouter les anciennes messages des autres utilisateurs quand on connecte **en entrant loggedIn
    public static void addLabelForOthersOldMsgs(Message msg, VBox messageBox) {
        //username
        HBox usernameBox = new HBox();
        usernameBox.setAlignment(Pos.CENTER_LEFT);
        usernameBox.setPadding(new Insets(10, 5, 0, 10));
        Text usernameText = new Text(msg.getUsername());
        usernameText.setFont(Font.font("System", FontWeight.NORMAL, 12));
        usernameText.setFill(Color.GRAY);
        usernameBox.getChildren().add(usernameText);

       //message
        HBox OtherMsgsBox = new HBox();
        OtherMsgsBox.setAlignment(Pos.CENTER_LEFT);
        OtherMsgsBox.setPadding(new Insets(3, 5, 5, 10));

        Text text = new Text(msg.getMsgContent());
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: #42b72a; -fx-background-radius: 5px;");
        textFlow.setPadding(new Insets(6, 10, 8, 10));
        text.setFill(Color.color(0.934, 0.945, 0.996));
        text.setFont(Font.font("System", FontWeight.NORMAL, 13));
        OtherMsgsBox.getChildren().add(textFlow);
        //heure
        HBox timeStampBox = new HBox();
        timeStampBox.setAlignment(Pos.CENTER_LEFT);
        timeStampBox.setPadding(new Insets(0, 10, 15, 15));

        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        String formattedTimestamp = outputFormat.format(msg.getTimestamp());
        Text timeStampText = new Text(formattedTimestamp);
        timeStampText.setFont(Font.font("System", FontWeight.NORMAL, 10));
        timeStampText.setFill(Color.GRAY);
        timeStampBox.getChildren().add(timeStampText);

        messageBox.getChildren().addAll(usernameBox, OtherMsgsBox, timeStampBox);

    }

    //ajouter les anciennes messages de l'utilisateur connecte quand il entre loggedIn
    public static void addLabelForUserOldMsgs(Message msg, VBox messageBox) {
        //username
        HBox usernameBox = new HBox();
        usernameBox.setAlignment(Pos.CENTER_RIGHT);
        usernameBox.setPadding(new Insets(10, 5, 0, 10));
        Text usernameText = new Text(msg.getUsername());
        usernameText.setFont(Font.font("System", FontWeight.NORMAL, 12));
        usernameText.setFill(Color.GRAY);
        usernameBox.getChildren().add(usernameText);

        //message
        HBox userMsgBox = new HBox();
        userMsgBox.setAlignment(Pos.CENTER_RIGHT);
        userMsgBox.setPadding(new Insets(3, 5, 5, 10));
        Text text = new Text(msg.getMsgContent());
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(15, 125, 242); -fx-background-radius: 5px;");
        textFlow.setPadding(new Insets(6, 10, 8, 10));
        text.setFill(Color.color(0.934, 0.945, 0.996));
        text.setFont(Font.font("System", FontWeight.NORMAL, 13));
        userMsgBox.getChildren().add(textFlow);
        //heure
        HBox timeStampBox = new HBox();
        timeStampBox.setAlignment(Pos.CENTER_RIGHT);
        timeStampBox.setPadding(new Insets(0, 15, 15, 10));

        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        String formattedTimestamp = outputFormat.format(msg.getTimestamp());
        Text timeStampText = new Text(formattedTimestamp);
        timeStampText.setFont(Font.font("System", FontWeight.NORMAL, 10));
        timeStampText.setFill(Color.GRAY);
        timeStampBox.getChildren().add(timeStampText);

        messageBox.getChildren().addAll(usernameBox, userMsgBox, timeStampBox);
    }

    //pour avoir notre user info
    public void setUser(User user) {
        usernameLabel.setText(user.getUsername());
        Image userImg = new Image("user.png");
        ImagePattern pattern = new ImagePattern(userImg);
        avatar.setFill(pattern);
        this.user = user;
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



    //pour ecrire les messages anciens et apres ecouter les nouveaux messages
    public void setMessages(ArrayList<Message> messages) {
        this.oldMessages = messages;
        System.out.println("message size from loggedin: " + messages.size());
        //set anciens messages pour user et autres user
        for(int i = 0; i < messages.size(); i++) {
            if(messages.get(i).getUserId() == user.getId()) {
                addLabelForUserOldMsgs(messages.get(i), messageBox);
            } else {
                addLabelForOthersOldMsgs(messages.get(i), messageBox);
            }
        }
        //recevoir nouveaux msgs
         receiveMessages = new ClientListener(input, messageBox, usersBox, user.getId());
        threadListener = new Thread(receiveMessages);
        threadListener.start();


    }


    public void setOnlyMessages(ArrayList<Message> messages) {
        this.oldMessages = messages;
        System.out.println("message size from loggedin: " + messages.size());
        //set anciens messages pour user et autres user
        for(int i = 0; i < messages.size(); i++) {
            if(messages.get(i).getUserId() == user.getId()) {
                addLabelForUserOldMsgs(messages.get(i), messageBox);
            } else {
                addLabelForOthersOldMsgs(messages.get(i), messageBox);
            }
        }
    }

    //avoir notre client
    public void setClient(Client client) {
        this.client = client;
    }

    public void setUsersBox(VBox usersBox) {
        this.usersBox = usersBox;
    }

    public void setStatusOnline() {
        btn_status.setSelected(true);
        tf_message.setDisable(false);
        tf_message.setText("");
    }

    public void setStatusOffline() {
        btn_status.setSelected(false);
        tf_message.setDisable(true);
        tf_message.setText("Vous êtes hors ligne maintenant!!");
    }




}
