package com.example.mchatclient.others;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientSender implements Runnable {
    private Socket socket;

    private ObjectOutputStream output;

    private volatile boolean socketOpen;

    public ClientSender(Socket socket, ObjectOutputStream output) {
        this.socket = socket;
        this.output = output;
        socketOpen = true;
    }

    public void run() {
        try {
            Scanner scan = new Scanner(System.in);
            scan.nextLine();
            while (socketOpen) {
                String msg;
                msg = scan.nextLine();
                //if(msg.startswith("@file:")) {}
                output.writeObject(msg);
                output.flush();
            }


        } catch (IOException e) {
            System.err.println("Erreur lors de la communication avec le serveur: " + e.getMessage());
        }
    }

}