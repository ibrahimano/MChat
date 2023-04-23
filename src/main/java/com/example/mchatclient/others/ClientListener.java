package com.example.mchatclient.others;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import models.Message;
import models.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientListener implements Runnable {
    private Socket socket;
    private ObjectInputStream input;

    private VBox messageBox;

    private VBox usersBox;

    private volatile boolean socketOpen;

    private volatile boolean stop = false;

    private ArrayList<User> onlineUsers;

    private  int userId;

    private ArrayList<Message> recievedMessages;



    public ClientListener(Socket socket, ObjectInputStream input) {
        this.socket = socket;
        this.input = input;
        socketOpen = true;
        this.onlineUsers = new ArrayList<User>();

    }

    public ClientListener(ObjectInputStream input, VBox messageBox, VBox usersBox) {
        this.input = input;
        this.messageBox = messageBox;
        this.usersBox = usersBox;
        socketOpen = true;
    }

    public ClientListener(ObjectInputStream input, VBox messageBox, VBox usersBox, int userId) {
        this.input = input;
        this.messageBox = messageBox;
        this.usersBox = usersBox;
        this.userId = userId;
        socketOpen = true;
        recievedMessages = new ArrayList<Message>();
    }

    public void run() {
        try {
            System.out.println("1 thread arrete? " + stop);
            while (!stop) {
                //avoir la liste de tous les users existe
                String typeMsg = (String) input.readObject(); // @message si c'est un msg @listOfUsersOnline ou pour un @userOnline ou @userOffline pour users status
                System.out.println("typeMsg: " + typeMsg);
                if (typeMsg.equals("@message")) {
                    Message message = (Message) input.readObject();
                    System.out.println("message recu clientListener: " + message.getMsgContent());
                    System.out.println("message username " + message.getUsername());
                    System.out.println("user userId " + message.getUserId() + "user Id " + userId);

                    String username = message.getUsername();
                    if(message.getUserId() != userId) {
                        recievedMessages.add(message);
                        System.out.println("message recu clientListener: " + message.getUserId()  +  " il y a pour msg userId " + userId);
                        //afficher a user
                        //username
                        HBox usernameBox = new HBox();
                        usernameBox.setAlignment(Pos.CENTER_LEFT);
                        usernameBox.setPadding(new Insets(10, 5, 0, 10));
                        Text usernameText = new Text(username);
                        usernameText.setFont(Font.font("System", FontWeight.NORMAL, 12));
                        usernameText.setFill(Color.GRAY);
                        usernameBox.getChildren().add(usernameText);

                        //message
                        HBox OtherMsgsBox = new HBox();
                        OtherMsgsBox.setAlignment(Pos.CENTER_LEFT);
                        OtherMsgsBox.setPadding(new Insets(3, 5, 5, 10));

                        Text text = new Text(message.getMsgContent());
                        TextFlow textFlow = new TextFlow(text);
                        textFlow.setStyle("-fx-background-color:  #42b72a; -fx-background-radius: 5px;");
                        textFlow.setPadding(new Insets(6, 10, 8, 10));
                        text.setFill(Color.color(0.934, 0.945, 0.996));
                        text.setFont(Font.font("System", FontWeight.NORMAL, 13));
                        OtherMsgsBox.getChildren().add(textFlow);

                        //heure
                        HBox timeStampBox = new HBox();
                        timeStampBox.setAlignment(Pos.CENTER_LEFT);
                        timeStampBox.setPadding(new Insets(0, 10, 15, 15));

                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                        String formattedTimestamp = outputFormat.format(message.getTimestamp());
                        Text timeStampText = new Text(formattedTimestamp);
                        timeStampText.setFont(Font.font("System", FontWeight.NORMAL, 10));
                        timeStampText.setFill(Color.GRAY);
                        timeStampBox.getChildren().add(timeStampText);

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                messageBox.getChildren().addAll(usernameBox, OtherMsgsBox, timeStampBox);
                            }
                        });
                    }

                } else if(typeMsg.equals("@listOfUsersOnline")) {
                    onlineUsers = (ArrayList<User>) input.readObject();
                    System.out.println("listOfUsersOnline est arrive");
                    updateUserList();

                } else if(typeMsg.equals("@userOnline")) {
                    User newOnlineUser = (User) input.readObject();
                    System.out.println("nouveau userOnline est arrive");
                    onlineUsers.add(newOnlineUser);
                    updateUserList();

                } else if(typeMsg.equals("@userOffline")) {
                    User  offlineUser = (User) input.readObject();
                    System.out.println("userOffline est detecter");
                    onlineUsers.removeIf(user -> user.getUsername().equals(offlineUser.getUsername()));
                    updateUserList();
                } else if(typeMsg.equals("@editUsername")) {
                    String isValid = (String) input.readObject();
                    if(isValid.equals("ok")) {
                        //message ok
                    } else {
                        //afficher error dans userErrorLabel
                    }

                } else if(typeMsg.equals("@editEmail")) {
                    String isValid = (String) input.readObject();
                    if(isValid.equals("ok")) {
                        //message ok
                    } else {

                    }
                } else if(typeMsg.equals("@stop")) {
                    System.out.println("entrer profil thread va arreter");
                    stop = true;
                }
            }
            System.out.println("2 thread arrete? " + stop);

        }  catch (IOException e) {
            System.err.println("Erreur lors de la communication avec le serveur - (User deconneter): " + e.getMessage());
            socketOpen = false; // arreter loop

        } catch (ClassNotFoundException e) {
            System.err.println("Classe non trouvée lors de la communication avec le client: " + e.getMessage());
        }
    }


    private void  updateUserList() {
        Platform.runLater(() -> {
          usersBox.getChildren().clear();
          //ajouter le nombre de users online
            Text numberOfUsers = new Text(onlineUsers.size() + " en ligne");
            numberOfUsers.setFont(Font.font("System", FontWeight.BOLD, 13));
            numberOfUsers.setFill(Color.GRAY);
            VBox onlineUsersBox = new VBox();
            onlineUsersBox.getChildren().add(numberOfUsers);
            onlineUsersBox.setPadding(new Insets(5, 0, 5, 0));
            onlineUsersBox.setAlignment(Pos.CENTER);
            usersBox.getChildren().add(onlineUsersBox);
            //ajouter les users online
            for(User user : onlineUsers) {
                HBox userBox = new HBox();
                userBox.setAlignment(Pos.CENTER_LEFT);
                userBox.setPadding(new Insets(5, 5, 5, 5));
                //ajouter un avatar
                //ImageView avatar = new ImageView(user.getAvatar());
                ImageView avatar = new ImageView("user.png");
                avatar.setFitWidth(30);
                avatar.setFitHeight(30);
                avatar.setPreserveRatio(true);
                avatar.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 50%;");
                userBox.getChildren().add(avatar);
                //ajouter username
                Text username = new Text(user.getUsername());
                username.setFont(Font.font("System", FontWeight.NORMAL, 15));
                username.setFill(Color.BLACK);
                HBox.setMargin(username, new Insets(0, 0, 0, 10));
                userBox.getChildren().add(username);
                //ajouter box to usersBox
                usersBox.getChildren().add(userBox);

                //hover effect
                userBox.setStyle("-fx-background-color: white; -fx-border-color: #CBE4DE; -fx-border-radius: 5px;");
                userBox.setOnMouseEntered(e -> userBox.setStyle("-fx-background-color: #CBE4DE; -fx-border-color: #0E8388;"));
                userBox.setOnMouseExited(e -> userBox.setStyle("-fx-background-color: white; -fx-border-color: #CBE4DE;"));
            }

        });

    }

    public ArrayList<Message> getRecievedMessages() {
        return recievedMessages;
    }


}

