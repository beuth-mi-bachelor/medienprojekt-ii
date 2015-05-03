package de.beuth.schabuu.user;

import de.beuth.schabuu.socketiodemo.ClientTask;

public class User {

    private final String userId;
    private ClientTask clientInstance;

    public User(String id) {
        this.userId = id;
    }

    public ClientTask getClientInstance() {
        return clientInstance;
    }

    public void setClientInstance(ClientTask clientInstance) {
        this.clientInstance = clientInstance;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", clientInstance=" + clientInstance +
                '}';
    }
}
