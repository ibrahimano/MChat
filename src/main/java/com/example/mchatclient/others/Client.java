package com.example.mchatclient.others;

import com.example.mchatclient.models.AccountInfo;
//import com.example.mchatclient.models.Message;
//import com.example.mchatclient.models.User;
import models.User;
import models.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private int port;
    private volatile boolean socketOpen = true; //volatile pour que la valeur etre vu par les threads si il est modifier

    Socket socket;

    public Client(int port) {
        this.port = port;
    }

    public AccountInfo connectToServerByLogin(String email, String password) throws Exception {
        String serverAddress = "localhost"; // "105.154.241.1";
        socket = new Socket(serverAddress, port);
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        //notifier le serveur quand va evoyer les info de login
        Scanner scan = new Scanner(System.in);
        output.writeObject("login");
        //envoyer email + password au serveur pour verifier user
        output.writeObject(email);
        output.writeObject(password);
        //le serveur va envoyer exist s'il a trouvé user sinon invalide
        String checkUser = (String) input.readObject();
        if(checkUser.equals("exist")) {
            //recevoir user info
            User user = (User) input.readObject();
            ArrayList<Message> messages = new ArrayList<Message>();
            //recevoir tous les message de user
            messages.addAll(( ArrayList<Message>) input.readObject());
            //maintenant user est pret pour la communication
            //pour envoyer les msgs au serveur
            //ClientSender sendMessages = new ClientSender(socket, output);
            //pour recevoir les msgs à partir du serveur
           // ClientListener receiveMessages = new ClientListener(socket, input);
            //Thread threadSender = new Thread(sendMessages);
            //Thread threadListener = new Thread(receiveMessages);
            //threadSender.start();
            //threadListener.start();

            return new AccountInfo(user, messages, "ok", output, input);


        } else {
            return new AccountInfo(null, null, "Not Found", output, input);

        }
    }

    public AccountInfo connectToServerByRegister(String email, String password, String username) throws Exception {
        String serverAddress = "localhost";
        socket = new Socket(serverAddress, port);
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

        //notifier le serveur quand va evoyer les info de login
        //Scanner scan = new Scanner(System.in);
        output.writeObject("signup");
        output.writeObject(email);
        output.writeObject(password);
        output.writeObject(username);

        //verifier si email n'existe pas deja dans la BD et recevoir cofirmation ou sinon error
        String messageConfirm = (String) input.readObject();
        if(messageConfirm.equals("ok")) {
            //recevoir user info
             User user = (User) input.readObject();
            //maintenant user est pret pour la communication

            //pour envoyer les msgs au serveur
            //ClientSender sendMessages = new ClientSender(socket, output);
            //pour recevoir les msgs à partir du serveur
            //ClientListener receiveMessages = new ClientListener(socket, input);
            //Thread threadSender = new Thread(sendMessages);
            //Thread threadListener = new Thread(receiveMessages);
            //threadSender.start();
            //threadListener.start();
            return new AccountInfo(user, null, "ok", output, input);
        } else {
            return new AccountInfo(null, null, messageConfirm, output, input); //message d'error messageConfirm  soit username exist soit email deja exist
        }

    }

    public void deconnectFromServer(ObjectOutputStream output, ObjectInputStream input, User user) throws IOException{
        //envoyer un message au server pour notifier que user il va deconnecter et donc mettre offline
        //@offline:userId_quit
        output.writeObject("@offline:" + user.getId() + "_quit");
        socketOpen = false;
        output.close(); // fermer output
        input.close(); // fermer input
        socket.close();
    }

}




