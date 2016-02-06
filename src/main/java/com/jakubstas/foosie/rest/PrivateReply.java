package com.jakubstas.foosie.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PrivateReply {

    @JsonProperty
    private String text;

    @JsonProperty
    private Attachment[] attachments = new Attachment[1];

    public PrivateReply(String text) {
        this.text = text;
        attachments[0] = new Attachment();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Attachment[] getAttachments() {
        return attachments;
    }

    public void setAttachments(Attachment[] attachments) {
        this.attachments = attachments;
    }

    class Attachment {

        @JsonProperty
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
