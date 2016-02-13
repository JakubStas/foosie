package com.jakubstas.integration.util;

public final class SlackMessageBodies {

    public final static String noActiveGamesPrivateMessageBody = "{\"text\":\"There are no active games right now.\",\"attachments\":[{\"text\":null}]}";

    public final static String oneActiveGamePrivateMessageBody = "{\"text\":\"Active games:\\n\\n1. hosted by jakub starts at 12:00 (1 player(s))\\n\",\"attachments\":[{\"text\":null}]}";

    public final static String gameInviteAtTwelvePostedPrivateMessageBody = "{\"text\":\"Your game invite has been posted. The game is scheduled for 12:00 and following players joined in:\",\"attachments\":[{\"text\":null}]}";

    public final static String gameInviteAtTwelvePostedChannelMessageBody = "{\"text\":\"jakub wants to play a game at 12:00. Who's in?\"}";

    public final static String gameLobbyHasBeenCreatedPrivateMessageBody = "{\"text\":\"Lobby for jakubs game starting at 12:00\\n:ballot_box_with_check: jakub\",\"attachments\":[{\"text\":null}]}";

    private final static String newPlayerJoinedGameNotificationPrivateMessageBody = "{\"text\":\":ballot_box_with_check: %s\",\"attachments\":[{\"text\":null}]}";

    public final static String confirmationAboutJoiningGamePrivateMessageBody = "{\"text\":\"You have successfully joined game by jakub starting at 12:00\",\"attachments\":[{\"text\":null}]}";

    public static String createNewPlayerJoinedGameNotificationPrivateMessageBody(final String userName) {
        return String.format(newPlayerJoinedGameNotificationPrivateMessageBody, userName);
    }
}
