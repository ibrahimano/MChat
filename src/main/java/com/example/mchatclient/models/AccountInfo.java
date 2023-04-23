package com.example.mchatclient.models;

import models.User;
import models.Message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class AccountInfo {
    private User user;
    private ArrayList<Message> messages;
    private String connectionResult;

    private ObjectOutputStream output;

    private ObjectInputStream input;

    public AccountInfo(User user, ArrayList<Message> messages, String connectionResult) {
        this.user = user;
        this.messages = messages;
        this.connectionResult = connectionResult;
    }

    public AccountInfo(User user, ArrayList<Message> messages, String connectionResult, ObjectOutputStream output, ObjectInputStream input) {
        this.user = user;
        this.messages = messages;
        this.connectionResult = connectionResult;
        this.output = output;
        this.input = input;
    }

    public ObjectInputStream getInput() {
        return input;
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
    }

    public ObjectOutputStream getOutput() {
        return output;
    }

    public void setOutput(ObjectOutputStream output) {
        this.output = output;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public String getConnectionResult() {
        return connectionResult;
    }

    public void setConnectionResult(String connectionResult) {
        this.connectionResult = connectionResult;
    }
}
