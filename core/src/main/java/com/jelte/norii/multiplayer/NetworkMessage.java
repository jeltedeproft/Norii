package com.jelte.norii.multiplayer;

import com.badlogic.gdx.Gdx;

public class NetworkMessage {
	private static final String TAG = NetworkMessage.class.getSimpleName();
	public static final String SEPARATOR = "/";
	public static final String END_TAG = "%";

	private MessageType type;
	private String sender = "";
	private String receiver = "";
	private String username = "";
	private String password = "";
	private String loginWorked = "";
	private String loginReason = "";
	private String moveType = "";
	private String location = "";
	private String ability = "";
	private String team = "";
	private String map = "";

	public enum MessageType {
		CONNECTING, CONNECTED, SEARCH_OPPONENT, DISCONNECTED, BATTLE, TRY_LOGIN, LOGIN_VALIDATION, MOVE_MADE
	}

	public NetworkMessage() {
		// do nothing
	}

	public NetworkMessage(MessageType type) {
		this.type = type;
	}

	public void makeConnectingMessage(String client) {
		type = MessageType.CONNECTING;
		sender = client;
	}

	public void makeConnectedMessage(String client) {
		type = MessageType.CONNECTED;
		sender = client;
	}

	public void makeDisconnectedMessage(String client) {
		type = MessageType.DISCONNECTED;
		sender = client;
	}

	public void makeSearchMessage(String client, String team) {
		type = MessageType.SEARCH_OPPONENT;
		sender = client;
		this.team = team;
	}

	//add team1, team2 and wether the receiving player is first to start or not, then start deployment phae based on that, either waiting for the enemy, or placing a unit
	public void makeBattleMessage(String fighter1, String fighter2, String map) {
		type = MessageType.BATTLE;
		sender = fighter1;
		receiver = fighter2;
		this.map = map;
	}

	public void makeMoveMessage(String client, String moveType, String location, String ability) {
		type = MessageType.MOVE_MADE;
		sender = client;
		this.moveType = moveType;
		this.location = location;
		this.ability = ability;
	}

	public void makeLoginMessage(String username, String password) {
		type = MessageType.TRY_LOGIN;
		this.username = username;
		this.password = password;
		sender = ServerCommunicator.getInstance().getClientID();
	}

	public void makeLoginValidationMessage(String loginWorked, String reason) {
		type = MessageType.LOGIN_VALIDATION;
		this.loginWorked = loginWorked;
		this.loginReason = reason;
	}

	public String messageToString() {
		StringBuilder stringToSend = new StringBuilder();

		stringToSend.append(type.name());
		stringToSend.append(SEPARATOR);
		stringToSend.append(sender);
		stringToSend.append(SEPARATOR);
		stringToSend.append(receiver);
		stringToSend.append(SEPARATOR);
		stringToSend.append(username);
		stringToSend.append(SEPARATOR);
		stringToSend.append(password);
		stringToSend.append(SEPARATOR);
		stringToSend.append(loginWorked);
		stringToSend.append(SEPARATOR);
		stringToSend.append(loginReason);
		stringToSend.append(SEPARATOR);
		stringToSend.append(team);
		stringToSend.append(SEPARATOR);
		stringToSend.append(map);
		stringToSend.append(END_TAG);
		return stringToSend.toString();
	}

	public void importString(String message) {
		if (!message.isBlank()) {
			String[] tags = message.split(SEPARATOR);
			int size = tags.length;
			type = MessageType.valueOf(tags[0]);
			sender = extract(tags, size, 1);
			receiver = extract(tags, size, 2);
			username = extract(tags, size, 3);
			password = extract(tags, size, 4);
			loginWorked = extract(tags, size, 5);
			loginReason = extract(tags, size, 6);
			team = extract(tags, size, 7);
			map = extract(tags, size, 8);
		} else {
			Gdx.app.log(TAG, "message was empty");
		}
	}

	private String extract(String[] tags, int size, int i) {
		if (size > i) {
			return tags[i];
		} else {
			return "";
		}
	}

	public MessageType getType() {
		return type;
	}

	public String getSender() {
		return sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getLoginWorked() {
		return loginWorked;
	}

	public String getLoginReason() {
		return loginReason;
	}

	public String getTeam() {
		return team;
	}

	public String getMap() {
		return map;
	}
}
