package com.jakubstas.foosie.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jstas on 2/3/16.
 */
public class Hook {

    @JsonProperty
    private String token;

    @JsonProperty("team_id")
    private String teamId;

    @JsonProperty("team_domain")
    private String teamDomain;

    @JsonProperty("channel_id")
    private String channelId;

    @JsonProperty("channel_name")
    private String channelName;

    @JsonProperty("command")
    private String command;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("text")
    private String text;

    @JsonProperty("response_url")
    private String responseUrl;

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

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
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

    public String getResponseUrl() {
        return responseUrl;
    }

    public void setResponseUrl(String responseUrl) {
        this.responseUrl = responseUrl;
    }

    @Override
    public String toString() {
        return "Hook{" +
                "token='" + token + '\'' +
                ", teamId='" + teamId + '\'' +
                ", teamDomain='" + teamDomain + '\'' +
                ", channelId='" + channelId + '\'' +
                ", channelName='" + channelName + '\'' +
                ", command='" + command + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", text='" + text + '\'' +
                ", responseUrl='" + responseUrl + '\'' +
                '}';
    }
}
