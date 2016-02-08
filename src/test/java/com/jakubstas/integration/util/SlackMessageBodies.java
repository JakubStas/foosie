package com.jakubstas.integration.util;

public final class SlackMessageBodies {

    public final static String noActiveGamesPrivateMessageBody = "{\"text\":\"There are no active games right now.\",\"attachments\":[{\"text\":null}]}";

    public final static String oneActiveGamePrivateMessageBody = "{\"text\":\"Active games:\\n\\n1. hosted by jakub starts at 12:00 (1 player(s))\\n\",\"attachments\":[{\"text\":null}]}";

    public final static String gameInviteAtTwelvePostedPrivateMessageBody = "{\"text\":\"Your game invite has been posted. The game is scheduled for 12:00 and following players joined in:\",\"attachments\":[{\"text\":null}]}";

    public final static String gameInviteAtTwelvePostedChannelMessageBody = "{\"text\":\"jakub wants to play a game at 12:00. Who's in?\"}";


}
