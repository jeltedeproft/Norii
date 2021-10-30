package com.jelte.norii.headless;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Gdx;
import com.jelte.norii.multiplayer.NetworkMessage;
import com.jelte.norii.multiplayer.NetworkMessage.MessageType;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

public class GameServer {
	private final ConcurrentLinkedQueue<ConnectedClient> clients = new ConcurrentLinkedQueue<>();
	private final ConcurrentLinkedQueue<ConnectedClient> searchingClients = new ConcurrentLinkedQueue<>();
	private HttpServer server;
	private static final int PORT = 80;
	private static final String CLIENT_TAG = "Client";
	/**
	 * The amount of games created since the server was launched Used to create
	 * unique game ID's
	 */
	private int gamesCreated;

	public GameServer() {
		initServer();

		Gdx.app.log("Server", "Starting on port " + PORT);
		server.listen(PORT);
		Gdx.app.log("Server", "Started");
	}

	private void initServer() {
		final Vertx vertx = Vertx.vertx();
		final HttpServerOptions options = new HttpServerOptions();
		server = vertx.createHttpServer(options);
		gamesCreated = 0;

		server.webSocketHandler(initConnectedClient());
	}

	private Handler<ServerWebSocket> initConnectedClient() {
		return client -> {
			handleConnectedClient(client);

			client.frameHandler(this::handleFrameClient);

			client.closeHandler(event -> handleCloseClient(client));
		};
	}

	private void handleConnectedClient(ServerWebSocket client) {
		Gdx.app.log(CLIENT_TAG, "Connected " + client.textHandlerID());

		clients.add(new ConnectedClient(client));
		clients.forEach(c -> {
			NetworkMessage message = new NetworkMessage(MessageType.CONNECTED);
			message.makeConnectedMessage(client.textHandlerID());
			c.getSocket().writeFinalTextFrame(message.messageToString());
		});
	}

	private void handleFrameClient(WebSocketFrame event) {
		Gdx.app.log(CLIENT_TAG, "Message " + event.textData());

		NetworkMessage message = new NetworkMessage();
		message.importString(event.textData());

		switch (message.getType()) {
		case CONNECTING:
			NetworkMessage returnMessage = new NetworkMessage(MessageType.CONNECTED);
			returnMessage.makeConnectedMessage(message.getSender());
			break;
		default:
			break;
		}

		clients.forEach(c -> {
			if (c.getPlayerName().equals(message.getSender())) {
				c.getSocket().writeFinalTextFrame(message.messageToString());
			}
		});
	}

	private void handleCloseClient(ServerWebSocket client) {
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
					Log.info("Game " + gamesCreated + ": " + players.get(0).getPlayerName() + " vs " + players.get(1).getPlayerName());
					gamesCreated++;

					// Clear the players list and carry on running, so multiple games can be created
					// each function call
					players = new ArrayList<>();
				}
			}
		}

	}

}
