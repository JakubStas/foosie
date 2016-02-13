package com.jakubstas.foosie.service.model;

import org.hibernate.validator.constraints.NotBlank;

public class User {

    @NotBlank
    private String userName;

    @NotBlank
    private String userId;

    public User(String userName, String userId) {
        this.userName = userName;
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
