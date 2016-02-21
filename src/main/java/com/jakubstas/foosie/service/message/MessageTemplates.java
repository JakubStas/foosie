package com.jakubstas.foosie.service.message;

public final class MessageTemplates {

    private final static String internalErrorPrivateMessageBody = "Internal error occurred! Please try again and if the issue persists please create an issue here: <https://github.com/JakubStas/foosie/issues/new>";

    private final static String noActiveGamesPrivateMessageBody = "There are no active games right now.";

    private final static String oneActiveGamePrivateMessageBody = "Active games:\n\n1. hosted by %s starts at %s (%d player(s))\n";

    private final static String gameInvitePrivateMessageForHostBody = "Your game invite has been posted. The game is scheduled for %s and following players joined in:";

    private final static String gameInviteChannelMessageBody = "%s wants to play a game at %s. Who's in?";

    private final static String gameLobbyHasBeenCreatedPrivateMessageBody = "Lobby for %ss game starting at %s\n:ballot_box_with_check: %s";

    private final static String newPlayerJoinedGameNotificationPrivateMessageBody = ":ballot_box_with_check: %s";

    private final static String confirmationAboutJoiningGamePrivateMessageBody = "You have successfully joined game by %s starting at %s";

    private final static String gameInviteAlreadyPostedPrivateMessageBody = "Active game created by you already exists! There is nothing left to do but wait :smile:";

    private final static String multipleActiveGamesToJoinPrivateMessageBody = "There are several active games at the moment. Pick the one that you like - %s";

    private final static String noActiveGamesToJoinPrivateMessageBody = "There is no active game at the moment. Try creating a new one yourself!";

    private final static String noActiveGamesToJoinByHostPrivateMessageBody = "There is no active game by %s at the moment. Try creating a new one yourself!";

    private final static String alreadyJoinedThisGamePrivateMessageBody = "You have already joined this game! Nothing left but wait for other players to join in";

    private final static String gameLobbyPlayerStatusPrivateMessageBody = ":ballot_box_with_check: %s";

    private final static String successfullyJoinedGamePrivateMessageBody = "You have successfully joined game by %s starting at %s";

    public static final String createInternalErrorPrivateMessageBody() {
        return internalErrorPrivateMessageBody;
    }

    public static final String createNoActiveGamesPrivateMessageBody() {
        return noActiveGamesPrivateMessageBody;
    }

    public static final String createOneActiveGamePrivateMessageBody(final String hostName, final String scheduledTime, final int numberOfPlayers) {
        return String.format(oneActiveGamePrivateMessageBody, hostName, scheduledTime, numberOfPlayers);
    }

    public static final String createGameInvitePrivateMessageForHostBody(final String proposedTime) {
        return String.format(gameInvitePrivateMessageForHostBody, proposedTime);
    }

    public static final String createGameInviteChannelMessageBody(final String hostName, final String proposedTime) {
        return String.format(gameInviteChannelMessageBody, hostName, proposedTime);
    }

    public static final String createGameLobbyHasBeenCreatedPrivateMessageBody(final String hostName, final String proposedTime) {
        return String.format(gameLobbyHasBeenCreatedPrivateMessageBody, hostName, proposedTime, hostName);
    }

    public static final String createNewPlayerJoinedGameNotificationPrivateMessageBody(final String userName) {
        return String.format(newPlayerJoinedGameNotificationPrivateMessageBody, userName);
    }

    public static final String createConfirmationAboutJoiningGamePrivateMessageBody(final String hostName, final String proposedTime) {
        return String.format(confirmationAboutJoiningGamePrivateMessageBody, hostName, proposedTime);
    }

    public static final String createGameInviteAlreadyPostedPrivateMessageBody() {
        return gameInviteAlreadyPostedPrivateMessageBody;
    }

    public static final String createMultipleActiveGamesToJoinPrivateMessageBody(final String availableGames) {
        return String.format(multipleActiveGamesToJoinPrivateMessageBody, availableGames);
    }

    public static final String createNoActiveGamesToJoinPrivateMessageBody() {
        return noActiveGamesToJoinPrivateMessageBody;
    }

    public static final String createNoActiveGamesToJoinByHostPrivateMessageBody(final String hostName) {
        return String.format(noActiveGamesToJoinByHostPrivateMessageBody, hostName);
    }

    public static final String createAlreadyJoinedThisGamePrivateMessageBody() {
        return alreadyJoinedThisGamePrivateMessageBody;
    }

    public static final String createGameLobbyPlayerStatusPrivateMessageBody(final String hostName) {
        return String.format(gameLobbyPlayerStatusPrivateMessageBody, hostName);
    }

    public static final String createSuccessfullyJoinedGamePrivateMessageBody(final String hostName, final String proposedTime) {
        return String.format(successfullyJoinedGamePrivateMessageBody, hostName, proposedTime);
    }
}
