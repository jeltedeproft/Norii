package com.jelte.norii.headless;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Gdx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

public class GameServer {
	private final ConcurrentLinkedQueue<ServerWebSocket> clients = new ConcurrentLinkedQueue<>();
	private final HttpServer server;
	private static final int PORT = 80;
	private static final String CLIENT_TAG = "Client";

	public GameServer() {
		final Vertx vertx = Vertx.vertx();

		final HttpServerOptions options = new HttpServerOptions();
		server = vertx.createHttpServer(options);

		server.webSocketHandler(client -> {
			handleConnectedClient(client);

			client.frameHandler(this::handleEventClient);

			client.closeHandler(event -> {
				handleCloseClient(client);
			});
		});

		Gdx.app.log("Server", "Starting on port " + PORT);
		server.listen(PORT);

		Gdx.app.log("Server", "Started");
	}

	private void handleCloseClient(ServerWebSocket client) {
		Gdx.app.log(CLIENT_TAG, "Disconnected " + client.textHandlerID());
		clients.remove(client);

		clients.forEach(c -> c.writeFinalTextFrame("Client disconnected: " + client.textHandlerID()));
	}

	private void handleEventClient(WebSocketFrame event) {
		Gdx.app.log(CLIENT_TAG, "Message " + event.textData());
		clients.forEach(c -> c.writeFinalTextFrame(event.textData()));

		System.out.println(event.textData());
	}

	private void handleConnectedClient(ServerWebSocket client) {
		Gdx.app.log(CLIENT_TAG, "Connected " + client.textHandlerID());

		clients.add(client);
		clients.forEach(c -> c.writeFinalTextFrame("Client connected: " + client.textHandlerID()));
	}

}
