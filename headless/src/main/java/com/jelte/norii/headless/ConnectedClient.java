package com.jelte.norii.headless;

import java.sql.Connection;

/**
 * Represents a client that is connected to the server Contains connection info about the client, as well as their name, state, and game ID
 */
public class ConnectedClient {
	/**
	 * Connection info about the client Used to send and receive packets to and from the client
	 */
	private Connection connection;

	/**
	 * This clients name This is empty until the client has registered a name
	 */
	private String name;

	/**
	 * The current state of this client
	 */
	private ClientState clientState;

	/**
	 * The current game ID of this client
	 */
	private int currentGameID;

	/**
	 * Creates a new RemoteClient with the passed in connection
	 * The clients state is automatically set to NAMELESS
	 * The clients current game ID is automatically set to -1
	 * @param c The connection of the new client
	 */
	public RemoteClient(Connection c)
    {
        connection = c;
        clientState = ClientState.NAMELESS;
        currentGameID = -1;
    }

	/**
	 * Gets the connection for this client
	 *
	 * @return The connection for this client
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Gets the name of this client Is empty until the client has registered a name
	 *
	 * @return The name of this client
	 */
	public String getPlayerName() {
		return name;
	}

	/**
	 * Gets the current state of this client
	 *
	 * @return The current state of this client
	 */
	public ClientState getClientState() {
		return clientState;
	}

	/**
	 * Gets the ID of the game that this client is currently in
	 *
	 * @return -1 if not in a game, the ID of the game otherwise
	 */
	public int getGameID() {
		return currentGameID;
	}

	/**
	 * Sets the ID of the game that this client has entered Called when a game is setup
	 *
	 * @param id The ID of the game this client has entered
	 */
	public void setGameID(int id) {
		currentGameID = id;
	}

	/**
	 * Sets the name for a client to be equal to the passed in name
	 *
	 * @param nameToSet The name to assign to this client
	 */
	public void setName(String nameToSet) {
		if (clientState == ClientState.NAMELESS) {
			name = nameToSet;
			clientState = ClientState.IDLE;
		} else {
			Log.error("Tried to set a name for a client that already has one!");
		}
	}

	/**
	 * Sets the clients state to the value of the passed in state
	 *
	 * @param state The state to set the client to
	 */
	public void setClientState(ClientState state) {
		clientState = state;
	}
}
