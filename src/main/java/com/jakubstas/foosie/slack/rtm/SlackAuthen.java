package com.jakubstas.foosie.slack.rtm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakubstas.foosie.slack.rtm.json.SlackInfo;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SlackAuthen {

    private final Logger logger = LoggerFactory.getLogger(SlackAuthen.class);

    private static final String SLACK_RTM_AUTHEN_URL = "https://slack.com/api/rtm.start?token=";

    private ObjectMapper mapper = new ObjectMapper();

    public SlackInfo tokenAuthen(String token) {
        HttpClient client = new HttpClient();

        GetMethod getMethod = new GetMethod(SLACK_RTM_AUTHEN_URL + token);
        SlackInfo slackInfo = new SlackInfo();

        try {
            int httpStatus = client.executeMethod(getMethod);
            if (httpStatus == HttpStatus.SC_OK) {


                return mapper.readValue(getMethod.getResponseBodyAsStream(), SlackInfo.class);
            } else {
                slackInfo.setError("http_status_" + httpStatus);
                return slackInfo;
            }
        } catch (IOException ex) {
            slackInfo.setError("exception " + ex.getMessage());
            logger.error(ex.getMessage());
        } finally {
            getMethod.releaseConnection();
        }
        return slackInfo;
    }
}
