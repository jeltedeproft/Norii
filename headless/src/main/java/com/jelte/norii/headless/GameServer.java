package com.jelte.norii.headless;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Gdx;
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

		clients.add(new ConnectedClient(client));
		clients.forEach(c -> {
			NetworkMessage message = new NetworkMessage(MessageType.CONNECTED);
			message.makeConnectedMessage(client.textHandlerID());
			c.getSocket().writeFinalTextFrame(message.messageToString());
		});
	}

	private void handleMessageClient(WebSocketFrame event) {
		Gdx.app.log(CLIENT_TAG, "Message " + event.textData());

		NetworkMessage message = new NetworkMessage();
		message.importString(event.textData());

		NetworkMessage returnMessage = new NetworkMessage();

		switch (message.getType()) {
		case CONNECTING:
			returnMessage.makeConnectedMessage(message.getSender());
			break;
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
			returnMessage.makeLoginValidationMessage("true", "worked");
			break;
		case SEARCH_OPPONENT:
			ConnectedClient client = getClientByName(message.getSender());
			if(client != null) {
				searchingClients.add(getClientByName(message.getSender()));
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
			if ((client.getPlayerName() != null) && (client.getPlayerName() == sender)) {
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
		Gdx.app.log(CLIENT_TAG, "matching players againsteach other, searching players amount =  " + searchingClients.size());
		for(ConnectedClient client : searchingClients) {
			Gdx.app.log(CLIENT_TAG, client.getPlayerName());
		}
		
		if (searchingClients.size() < 2)
			return;

		ArrayList<ConnectedClient> players = new ArrayList<>();

		for (final ConnectedClient client : searchingClients) {
			if (client.getClientState() == ClientState.QUEUED) {
				players.add(client);
				if (players.size() == 2) {
					// Create a battle message to send to each client
					NetworkMessage battleMessage = new NetworkMessage(MessageType.BATTLE);
					battleMessage.makeBattleMessage(players.get(0).getPlayerName(), players.get(1).getPlayerName());

					// Send the packet to the first player
					players.get(0).getSocket().writeTextMessage(battleMessage.messageToString());

					// Send the packet to the second player
					players.get(1).getSocket().writeTextMessage(battleMessage.messageToString());

					// Change the state for each player to be ingame
					players.get(0).setClientState(ClientState.INGAME);
					players.get(1).setClientState(ClientState.INGAME);

					// Change the gameID for each player to the ID of the new game
					players.get(0).setGameID(gamesCreated);
					players.get(1).setGameID(gamesCreated);

					// Create a game instance and add it to the list of all active game instances
					final GameInstance newGame = new GameInstance(this, gamesCreated, players.get(0), players.get(1));
					activeGames.put(gamesCreated, newGame);

					// Output matchup to log
					Gdx.app.debug(CLIENT_TAG, "Game " + gamesCreated + ": " + players.get(0).getPlayerName() + " vs " + players.get(1).getPlayerName());
					gamesCreated++;

					// Clear the players list and carry on running, so multiple games can be created
					// each function call
					players = new ArrayList<>();
				}
			}
		}

	}

}
