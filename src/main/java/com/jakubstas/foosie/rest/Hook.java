package com.jakubstas.foosie.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jstas on 2/3/16.
 */
public class Hook {

    @JsonProperty
    private String token;

    @JsonProperty
    private String teamId;

    @JsonProperty
    private String teamDomain;

    @JsonProperty
    private String channelId;

    @JsonProperty
    private String channelName;

    @JsonProperty
    private String timestamp;

    @JsonProperty
    private String userId;

    @JsonProperty
    private String userName;

    @JsonProperty
    private String text;

    @JsonProperty
    private String trigger;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamDomain() {
        return teamDomain;
    }

    public void setTeamDomain(String teamDomain) {
        this.teamDomain = teamDomain;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    @Override
    public String toString() {
        return "Hook{" +
                "token='" + token + '\'' +
                ", teamId='" + teamId + '\'' +
                ", teamDomain='" + teamDomain + '\'' +
                ", channelId='" + channelId + '\'' +
                ", channelName='" + channelName + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", text='" + text + '\'' +
                ", trigger='" + trigger + '\'' +
                '}';
    }
}
