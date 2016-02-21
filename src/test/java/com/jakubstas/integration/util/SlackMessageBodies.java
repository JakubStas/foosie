package com.jakubstas.integration.util;

final class SlackMessageBodies {

    private final static String internalErrorPrivateMessageBody = "{\"text\":\"Internal error occurred! Please try again and if the issue persists please create an issue here: <https://github.com/JakubStas/foosie/issues/new>\",\"attachments\":[{\"text\":null}]}";

    private final static String noActiveGamesPrivateMessageBody = "{\"text\":\"There are no active games right now.\",\"attachments\":[{\"text\":null}]}";

    private final static String oneActiveGamePrivateMessageBody = "{\"text\":\"Active games:\\n\\n1. hosted by %s starts at %s (%d player(s))\\n\",\"attachments\":[{\"text\":null}]}";

    private final static String gameInvitePrivateMessageBody = "{\"text\":\"Your game invite has been posted. The game is scheduled for %s and following players joined in:\",\"attachments\":[{\"text\":null}]}";

    private final static String gameInviteChannelMessageBody = "{\"text\":\"%s wants to play a game at %s. Who's in?\"}";

    private final static String gameLobbyHasBeenCreatedPrivateMessageBody = "{\"text\":\"Lobby for %ss game starting at %s\\n:ballot_box_with_check: %s\",\"attachments\":[{\"text\":null}]}";

    private final static String newPlayerJoinedGameNotificationPrivateMessageBody = "{\"text\":\":ballot_box_with_check: %s\",\"attachments\":[{\"text\":null}]}";

    private final static String confirmationAboutJoiningGamePrivateMessageBody = "{\"text\":\"You have successfully joined game by %s starting at %s\",\"attachments\":[{\"text\":null}]}";

    private final static String gameInviteAlreadyPostedPrivateMessageBody = "{\"text\":\"Active game created by you already exists! There is nothing left to do but wait :smile:\",\"attachments\":[{\"text\":null}]}";

    private final static String twoActiveGamesAtTheSameTimePrivateMessageBody = "{\"text\":\"Active games:\\n\\n1. hosted by %s starts at %s (%d player(s))\\n2. hosted by %s starts at %s (%d player(s))\\n\",\"attachments\":[{\"text\":null}]}";

    private final static String multipleActiveGamesToJoinPrivateMessageBody = "{\"text\":\"There are several active games at the moment. Pick the one that you like - [host_1, host_2]\",\"attachments\":[{\"text\":null}]}";

    private final static String noActiveGamesToJoinPrivateMessageBody = "{\"text\":\"There is no active game at the moment. Try creating a new one yourself!\",\"attachments\":[{\"text\":null}]}";

    static final String createInternalErrorPrivateMessageBody() {
        return internalErrorPrivateMessageBody;
    }

    static final String createNoActiveGamesPrivateMessageBody() {
        return noActiveGamesPrivateMessageBody;
    }

    static final String createOneActiveGamePrivateMessageBody(final String hostName, final String scheduledTime, final int numberOfPlayers) {
        return String.format(oneActiveGamePrivateMessageBody, hostName, scheduledTime, numberOfPlayers);
    }

    static final String createGameInvitePrivateMessageBody(final String proposedTime) {
        return String.format(gameInvitePrivateMessageBody, proposedTime);
    }

    static final String createGameInviteChannelMessageBody(final String hostName, final String proposedTime) {
        return String.format(gameInviteChannelMessageBody, hostName, proposedTime);
    }

    static final String createGameLobbyHasBeenCreatedPrivateMessageBody(final String hostName, final String proposedTime) {
        return String.format(gameLobbyHasBeenCreatedPrivateMessageBody, hostName, proposedTime, hostName);
    }

    static final String createNewPlayerJoinedGameNotificationPrivateMessageBody(final String userName) {
        return String.format(newPlayerJoinedGameNotificationPrivateMessageBody, userName);
    }

    static final String createConfirmationAboutJoiningGamePrivateMessageBody(final String hostName, final String proposedTime) {
        return String.format(confirmationAboutJoiningGamePrivateMessageBody, hostName, proposedTime);
    }

    static final String createGameInviteAlreadyPostedPrivateMessageBody() {
        return gameInviteAlreadyPostedPrivateMessageBody;
    }

    static final String createTwoActiveGamesAtTheSameTimePrivateMessageBody(final String firstHostName, final String secondHostName, final String proposedTime, final int numberOfPlayersInFirstGame, final int numberOfPlayersInSecondGame) {
        return String.format(twoActiveGamesAtTheSameTimePrivateMessageBody, firstHostName, proposedTime, numberOfPlayersInFirstGame, secondHostName, proposedTime, numberOfPlayersInSecondGame);
    }

    static final String createMultipleActiveGamesToJoinPrivateMessageBody(final String activeGames) {
        return String.format(multipleActiveGamesToJoinPrivateMessageBody, activeGames);
    }

    static final String createNoActiveGamesToJoinPrivateMessageBody() {
        return noActiveGamesToJoinPrivateMessageBody;
    }
}
