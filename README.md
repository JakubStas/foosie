# Foosie - the foosball bot

Foosie is a simple Slack integration created in order to help me learn about the ways one can plug in custom integration code into Slack and also standardize the way me and my teammates organize our foosball games during our coffee breaks. This also dictated the way Foosie communicates with Slack. There are several ways I wanted to explore and this is reflected in different methods of communication used in this application.

# How to use Foosie
User needs to know only a handful of slash commands to fully master the capabilities of Foosie. In the following paragraphs, I will describe how to perform key use-cases provided by this bot. Following examples rely on the configuration described in later sections of this read-me file, however you can customize all of these to your liking. Let's take a look at what is available to the end user.

## How do I discover available games?
In case you want to play a game it is useful to check, if someone else is not hosting a game already. To do this simply issue following command to `#general` channel:

```
/games
```

Upon issuing the command, Foosie will respond privately with a complete list of active games listing the host name, scheduled time and the number of players that are already in the games lobby.

## How do I create a game?
In case you want to create a new game just issue following command to `#general` channel:

```
/new HH:MM
```

Make sure that the proposed time is in the future yet still today, otherwise you won't be able to create a new game. Upon issuing the valid command, Foosie will do following things:

* Send you a private message in the `#general` channel notifying you that your request was successful.
* Send out a public message to the `#general` channel notifying all the users about your newly created game.
* Send you a private message in the `#general` channel presenting you with your games lobby where you can watch as other users join your game.

## How do I propose a new time for a game?
There is no need to use slash commands to do this. Just simply post a message to the `#general` channel stating your reasons and the time that suits you better.

It is up to the host to reschedule their active game.

## How do I reschedule a game?
In case you want to reschedule a game just issue following command to `#general` channel but keep in mind that you must be a host of a game:

```
/reschedule HH:MM
```

Make sure that the proposed time is in the future yet still today, otherwise you won't be able to reschedule the game. Upon issuing the valid command, Foosie will reschedule your game and post a public message to the `#general` channel notifying all the users about the change to your game. Please note that your lobby will remain unchanged so there is no need for other players to rejoin.

## How do I cancel a game?
In case you want to cancel a game just issue following command to `#general` channel but keep in mind that you must be a host of a game:

```
/cancel
```

Upon issuing this command, Foosie will cancel your game and post a public message to the `#general` channel notifying all the users about the cancellation of your game.

## How do I join a game?

In case you want to join an existing game just issue following command to `#general` specifying a name of a host that has active game:

```
/iamin HOSTNAME
```

Upon issuing the valid command, Foosie will do following things:
* Send you a private message in the `#general` channel notifying you have successfully joined the game.
* Add you to an existing games lobby visible only to the host of the game.

However, if there is only one active game you can use simplified version of this command:

```
/iamin
```

Upon issuing this command, Foosie will do following things:
* Send you a private message in the `#general` channel notifying you have successfully joined the game.
* Add you to an existing games lobby visible only to the host of the game.

In case this command is issued while several active games are waiting for the player, you are presented with a list of the hosts of these active games you may choose from and make use of the first mentioned version of this command.

## Why did my game expire?
Any game that meets the proposed time but doesn't reach the required number of players expires.

Once this happens, Foosie will do following things:
* Cancel your game and post a public message to the `#general` channel notifying all the users about the expiration of your game.
* Send you and all players in the game lobby a private message in the `#general` channel notifying them about the expiration of the game.

# How to set up Foosie
In this part I will describe how to set up the application itself and the Slack as a client. Since Foosie is using several ways to communicate with Slack, you will need to provide a bit more details than in the normal business-focused application using a single approach. Let's dig in!

## Slash commands
Foosie is designed to leverage custom slash commands you can define in admin section of your Slack team `https://your-team.slack.com/apps/manage/custom-integrations`. In order to connect Foosie and Slack, I recommend creating a set of slash commands to drive your game management.

Each slash command comes with the token. This token will be sent in the outgoing payload and it is used to verify that the request came from your Slack team. You should specify these tokens as environment variables according to naming conventions put forward in [`application.properties`](https://github.com/JakubStas/foosie/blob/master/src/main/resources/application.properties) file. Slack requires all integrations to make use of `https` protocol and that is why I recommend hosting this application in AWS or Heroku which provide an easy way to meet this requirement.

Integration using slash commands requires the application to use either `GET` or `POST` requests so the desing of the REST API was heavily influenced by this restriction (no use of `PUT`, `DELETE` or any other HTTP method).

## Incoming Webhooks
While slash commands work great to drive the 'engine' of the application, when it comes to simple posting of messages to a channel Incoming Webhooks are a good way to go. For the purposes of posting messages to `#general` channel make sure you setup an Incoming Webhook and note the URL.

```
https://hooks.slack.com/services/XXX/YYY/ZZZ
```

You should specify the last three path segments from this URL as a value of an environment property named `incoming-web-hook-uri`. This will ensure that the bot is be able to post public messages into a channel. You can configure the channel you want it to post to while setting up the incoming webhook.

## Slack Web API
Another thing left to configure is your Web API authentication token. API authentication is achieved via a bearer token which identifies a single user. In general, you can either use a generated full-access token, or register your application with Slack and use OAuth 2. For the purposes of this application I decided to go with the first option since it requires less setup work and less code. You can generate this token at the bottom of this page.

```
https://api.slack.com/web
```

You should use the full token as a value of an environment property named `slack.auth-token`. This will ensure that the bot will be able to post private messages to the user (since the full-access has been granted).

## Other properties
Last thing you can configure is the `foosie.schedule-before` environment property. This property is used once the game lobby becomes full and the game is ready. One of the internal components of Foosie is responsible for automatic scheduling of games. This means that once the game is ready to be kicked off all of the players need to be notified in advance so they can finish whatever they are working on and head for the foosball table. This property tells Foosie how many minutes before the game these players should receive this notification using direct private message.

# Recommended Slack settings
I am currently using following set of slash commands but feel free to customize it in a way that best suits your and your teams needs.

* **/new [proposed time]** (uses `POST` method to `/`)
* **/iamin [host name - optional]** (uses `POST` method to `/join`)
* **/reschedule [proposed time]** (uses `POST` method to `/update`)
* **/cancel** (uses `POST` method to `/cancel`)
* **/games** (uses `GET` method to `/`)

## Further customization
All of these Slack integration options can be further customized by providing a name, description or hint. I recommend you fill these out so that the users in your channel can fully benefit from this bot. As any other user in your team, even Foosie deserves a proper avatar. I recommend using the image included in the resources so that even Foosie can get that nice feeling of identity.

```
foosie/src/main/resources/avatar.jpg
```

Image by [Matthew MennoBoy](http://matthew.mennoboy.com/2005/07/14/foosball-desktop-wallpapers/)
