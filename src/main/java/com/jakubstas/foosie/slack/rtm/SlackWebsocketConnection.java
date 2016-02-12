package com.jakubstas.foosie.slack.rtm;

import com.jakubstas.foosie.slack.rtm.json.SlackInfo;
import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

public class SlackWebsocketConnection {

    private final Logger logger = LoggerFactory.getLogger(SlackWebsocketConnection.class);

    private SlackInfo slackInfo;
    private Session session;

    private final String token;

    protected SlackWebsocketConnection(String token) {
        this.token = token;
    }

    public boolean connect() {
        slackInfo = new SlackAuthen().tokenAuthen(token);
        ClientManager client = ClientManager.createClient();

        try {
            session = client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    session.addMessageHandler(new MessageHandler.Whole<String>() {

                        @Override
                        public void onMessage(String message) {
                            logger.debug(message);
                        }
                    });
                }
            }, URI.create(slackInfo.getUrl()));
            await();
        } catch (DeploymentException | IOException ex) {
            logger.error(ex.getMessage());
        }
        return true;
    }

    public SlackInfo getSlackInfo() {
        return slackInfo;
    }

    private void await() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        logger.error(ex.getMessage());
                    }
                }
            }
        });
        thread.start();
    }
}