package com.jelte.norii.headless;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.jelte.norii.map.MapFactory;
import com.jelte.norii.map.MapFactory.MapType;
import com.jelte.norii.multiplayer.NetworkMessage;
import com.jelte.norii.multiplayer.NetworkMessage.MessageType;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

public class GameServer {
	private final ConcurrentLinkedQueue<ConnectedClient> clients = new ConcurrentLinkedQueue<>();
	private final ConcurrentLinkedQueue<ConnectedClient> searchingClients = new ConcurrentLinkedQueue<>();
	private Map<Integer, GameInstance> activeGames;
	private Vertx vertx;
	private HttpServer server;
	private Json json;

	private static final int PORT = 80;
	private static final String CLIENT_TAG = "Client";
	/**
	 * The amount of games created since the server was launched Used to create
	 * unique game ID's
	 */
	private int gamesCreated;

	/**
	 * Seperate thread that handles updating of server-side data
	 */
	UpdateThread updateThread;

	public GameServer() {
		initServer();

		Gdx.app.log("Server", "Starting on port " + PORT);
		server.listen(PORT);
		Gdx.app.log("Server", "Started");
	}

	private void initServer() {
		vertx = Vertx.vertx();
		final HttpServerOptions options = new HttpServerOptions();
		server = vertx.createHttpServer(options);
		gamesCreated = 0;
		activeGames = new HashMap<>();
		json = new Json();

		// Start update thread
		updateThread = new UpdateThread();
		updateThread.gameServer = this;
		new Thread(updateThread).start();

		server.webSocketHandler(initConnectedClient());
	}

	private Handler<ServerWebSocket> initConnectedClient() {
		return client -> {
			initClient(client);

			client.frameHandler(this::handleMessageClient);

			client.closeHandler(event -> removeClient(client));
		};
	}

	private void initClient(ServerWebSocket client) {
		Gdx.app.log(CLIENT_TAG, "Connected " + client.textHandlerID());

		ConnectedClient connectedClient = new ConnectedClient(client);
		clients.add(connectedClient);
		NetworkMessage message = new NetworkMessage(MessageType.CONNECTED);
		message.makeConnectedMessage(client.textHandlerID());
		connectedClient.getSocket().writeFinalTextFrame(message.messageToString());
	}

	private void handleMessageClient(WebSocketFrame event) {
		Gdx.app.log(CLIENT_TAG, "Message " + event.textData());

		NetworkMessage message = new NetworkMessage();
		message.importString(event.textData());

		Gdx.app.log(CLIENT_TAG, "Message imported \n" + message);

		NetworkMessage returnMessage = new NetworkMessage();

		ConnectedClient client = getClientByName(message.getSender());

		Gdx.app.log(CLIENT_TAG, "found client");

		switch (message.getType()) {
		case TRY_LOGIN:
			// Check existence and delete
			vertx.fileSystem().exists("login.txt", result -> {
				if (!(result.succeeded() && result.result())) {
					vertx.fileSystem().createFile("login.txt");
				}
			});
			vertx.fileSystem().writeFile("login.txt", Buffer.buffer("jelte"), result -> {
				if (result.succeeded()) {
					System.out.println("File written");
				} else {
					System.err.println("Oh oh ..." + result.cause());
				}
			});
			returnMessage.makeLoginValidationMessage(message.getSender(), "true", "worked");
			break;
		case SEARCH_OPPONENT:
			Gdx.app.log(CLIENT_TAG, "handling search message");
			if (client != null) {
				searchingClients.add(client);
				client.setClientState(ClientState.QUEUED);
				client.setTeamFromJson(message.getTeam());
			}
			Gdx.app.log(CLIENT_TAG, "finished search message");
			break;
		case UNIT_DEPLOYED:
			Integer id = Integer.parseInt(message.getGameID());
			GameInstance battle = activeGames.get(id);
			Gdx.app.log("gameserver, ", "received unit deployed message, with id :  " + id + " \n and battle : " + battle);
			Gdx.app.log("gameserver, ", "client:  " + client);
			Gdx.app.log("gameserver, ", "client name :  " + client.getPlayerName());
			Gdx.app.log("gameserver, ", "battle player 1 :  " + battle.getPlayer1());
			Gdx.app.log("gameserver, ", "battle player 1 client :  " + battle.getPlayer1().getConnectedClient());
			Gdx.app.log("gameserver, ", "battle player 1 clientname :  " + battle.getPlayer1().getConnectedClient().getPlayerName());
			Gdx.app.log("gameserver, ", "battle player 2 :  " + battle.getPlayer2().getConnectedClient().getPlayerName());
			Gdx.app.log("gameserver, ", "battle player 2 client :  " + battle.getPlayer2().getConnectedClient());
			Gdx.app.log("gameserver, ", "battle player 2 clientname :  " + battle.getPlayer2().getConnectedClient().getPlayerName());
			if (battle.containsClient(client)) {
				Gdx.app.log("gameserver, ", "battle contains client : " + client.getPlayerName());
				Gdx.app.log("gameserver, ", "opponent = " + battle.getOpponent(client).getPlayerName());
				battle.getOpponent(client).getSocket().writeFinalTextFrame(event.textData());
			}
			break;
		default:
			break;
		}

		if (returnMessage.getType() != null) {
			clients.forEach(c -> {
				if (c.getPlayerName().equals(message.getSender())) {
					c.getSocket().writeFinalTextFrame(returnMessage.messageToString());
				}
			});
		}
	}

	private ConnectedClient getClientByName(String sender) {
		for (ConnectedClient client : clients) {
			if ((client.getPlayerName() != null) && (client.getPlayerName().equals(sender))) {
				Gdx.app.log(CLIENT_TAG, "found client with name : " + client.getPlayerName());
				return client;
			}
		}
		return null;
	}

	private void removeClient(ServerWebSocket client) {
		Gdx.app.log(CLIENT_TAG, "Disconnected " + client.textHandlerID());
		clients.remove(new ConnectedClient(client));

		clients.forEach(c -> {
			NetworkMessage message = new NetworkMessage(MessageType.DISCONNECTED);
			message.makeConnectedMessage(client.textHandlerID());
			c.getSocket().writeFinalTextFrame(message.messageToString());
		});
	}

	public void matchPlayers() {
		if (searchingClients.size() < 2)
			return;
		Gdx.app.log(CLIENT_TAG, "matching players");
		ArrayList<ConnectedClient> players = new ArrayList<>();

		for (final ConnectedClient client : searchingClients) {
			Gdx.app.log(CLIENT_TAG, "1");
			if (client.getClientState() == ClientState.QUEUED) {
				Gdx.app.log(CLIENT_TAG, "2");
				players.add(client);
				Gdx.app.log(CLIENT_TAG, "3");
				if (players.size() == 2) {
					Gdx.app.log(CLIENT_TAG, "4");
					// Create a battle message to send to each client
					NetworkMessage battleMessage = new NetworkMessage(MessageType.BATTLE);
					Gdx.app.log(CLIENT_TAG, "5");
					// for now just select a map, later randomize this
					MapFactory.MapType mapType = MapType.BATTLE_MAP_THE_DARK_SWAMP;
					Gdx.app.log(CLIENT_TAG, "gamescreated = " + gamesCreated);
					Gdx.app.log(CLIENT_TAG, "player0 = " + players.get(0).getPlayerName());
					Gdx.app.log(CLIENT_TAG, "player1 = " + players.get(1).getPlayerName());
					Gdx.app.log(CLIENT_TAG, "map = " + mapType.name());
					Gdx.app.log(CLIENT_TAG, "team1 = " + json.toJson(players.get(0).getTeam(), Array.class));
					Gdx.app.log(CLIENT_TAG, "team2 = " + json.toJson(players.get(1).getTeam(), Array.class));
					battleMessage.makeBattleMessage(gamesCreated, players.get(0).getPlayerName(), players.get(1).getPlayerName(), mapType.name(), json.toJson(players.get(0).getTeam(), Array.class),
							json.toJson(players.get(1).getTeam(), Array.class), "true");
					Gdx.app.log(CLIENT_TAG, "6");
					// Send the packet to the first player
					players.get(0).getSocket().writeTextMessage(battleMessage.messageToString());
					Gdx.app.log(CLIENT_TAG, "sending to : " + players.get(0).getPlayerName());

					// Send the packet to the second player, just changing the side
					battleMessage.setPlayerStart("false");
					players.get(1).getSocket().writeTextMessage(battleMessage.messageToString());
					Gdx.app.log(CLIENT_TAG, "sending to : " + players.get(1).getPlayerName());
					// Change the state for each player to be ingame
					players.get(0).setClientState(ClientState.INGAME);
					players.get(1).setClientState(ClientState.INGAME);
					Gdx.app.log(CLIENT_TAG, "7");
					// Change the gameID for each player to the ID of the new game
					players.get(0).setGameID(gamesCreated);
					players.get(1).setGameID(gamesCreated);
					// Create a game instance and add it to the list of all active game instances
					final GameInstance newGame = new GameInstance(this, gamesCreated, players.get(0), players.get(1), mapType);
					activeGames.put(gamesCreated, newGame);
					// Output matchup to log
					Gdx.app.log(CLIENT_TAG, "Game " + gamesCreated + ": " + players.get(0).getPlayerName() + " vs " + players.get(1).getPlayerName());
					gamesCreated++;

					// Clear the players list and carry on running, so multiple games can be created
					// each function call
					players = new ArrayList<>();
				}
				Gdx.app.log(CLIENT_TAG, "8");
			}
			Gdx.app.log(CLIENT_TAG, "9");
		}
		Gdx.app.log(CLIENT_TAG, "10");
	}

}
