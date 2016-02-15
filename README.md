# Foosie - the foosball bot

Foosie is a simple Slack integration created in order to help me learn about the ways one can plug in custom integration code into Slack and also standardize the way me and my teammates organize our foosball games during our coffee breaks. This also dictated the way Foosie communicates with Slack. There are several ways I wanted to explore and this is reflected in different methods of communication used in this application.

# How to use Foosie
User needs to know only a handful of slash commands to fully master the capabilities of Foosie. In the following paragraphs, I will describe how to perform key usecases provided by this bot. Following examples rely on the configuration described in later sections of this readme file, however you can customize all of these to your liking. Let's take a look at what is available to the end user.

## How do I discover available games?
In case you want to play a game it is useful to check, if someone else is not hosting a game already. To do this simply issue following command to `#general` channel:

```
/games
```

Upon issueing the command, Foosie will respond privetely with a complete list of active games listing the host name, scheduled time and the number of players that are already in the games lobby.

## How do I create a game?
In case you want to create a new game just issue following commandto `#general` channel:

```
/new HH:MM
```

Make sure that the proposed time is in the future yet still today, otherwise you won't be able to create a new game. Upon issueing the valid command, Foosie will do following things:

* Send you a private message in the `#general` channel notifying you that your request was successful. 
* Send out a public message to the `#general` channel notifying all the users about your newly created game.
* Send you a private message in the `#general` channel presenting you with your games lobby where you can watch as other users join your game.

## How do I reschedule a game?

## How do I cancel a game?

## How do I join a game?

# How to set up Foosie
TODO

## Slash commands

Foosie is designed to leverage custom slash commands you can define in admin section of your Slack team `https://your-team.slack.com/apps/manage/custom-integrations`. In order to connect Foosie and Slack, I recommend creating a set of slash commands to drive your game management.

Each slash command comes with the token. This token will be sent in the outgoing payload and it is used to verify that the request came from your Slack team. You should specify these tokens as environment variables according to naming conventions put forward in `application.properties` file. Slack requires all integrations to make use of `https` protocol and that is why I recommend hosting this application in AWS or Heroku which provide an easy way to meet this requirement.

Integration using slash commands requires the application to use either `GET` or `POST` requests so the desing of the REST API was heavily influenced by this requirement (no use of `PUT`, `DELETE` or any other HTTP method).

I am currently using following set of slash commands but feel free to customize it in a way that best suits your and your teams needs.

* **/new [proposed time]** Creates a new game lobby and notifies all users in the channel about newly created game. Proposed time must be in future yet still today. (uses `POST` method)
* **/iamin [host name - optional]** Allows user to join an existing game. In case that there are no active games, user is prompted to create their own game. If the host name is specified, user will join an existing game hosted by specified host. If the host name is omitted, user will join any newly created game as long as there is only one active game. In case there are no active games, user is prompted to create their own game. (uses `POST` method)
* **/reschedule [proposed time]** Reschedules the game. This command is only available to the active hosts. (uses `POST` method)
* **/cancel** Cancels active game. This command is only available to the active hosts. (uses `POST` method)
* **/games** Returns the list of active games with the information about the host, scheduled time and the number of players currently in the game lobby. (uses `GET` method)
