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
	private String team1 = "";
	private String team2 = "";
	private String fighter1 = "";
	private String fighter2 = "";
	private String side = "";

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
		Gdx.app.log(TAG, "making search message for : " + client);
		type = MessageType.SEARCH_OPPONENT;
		sender = client;
		this.team = team;
	}

	// add team1, team2 and whether the receiving player is first to start or not,
	// then start deployment phase based on that, either waiting for the enemy, or
	// placing a unit
	public void makeBattleMessage(String fighter1, String fighter2, String map, String team1, String team2, String side) {
		type = MessageType.BATTLE;
		this.fighter1 = fighter1;
		this.fighter2 = fighter2;
		this.map = map;
		this.team1 = team1;
		this.team2 = team2;
		this.side = side;
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

	public void makeLoginValidationMessage(String client, String loginWorked, String reason) {
		type = MessageType.LOGIN_VALIDATION;
		this.loginWorked = loginWorked;
		this.loginReason = reason;
		sender = client;
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
		stringToSend.append(SEPARATOR);
		stringToSend.append(team1);
		stringToSend.append(SEPARATOR);
		stringToSend.append(team2);
		stringToSend.append(SEPARATOR);
		stringToSend.append(fighter1);
		stringToSend.append(SEPARATOR);
		stringToSend.append(fighter2);
		stringToSend.append(SEPARATOR);
		stringToSend.append(side);
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
			team1 = extract(tags, size, 9);
			team2 = extract(tags, size, 10);
			fighter1 = extract(tags, size, 11);
			fighter2 = extract(tags, size, 12);
			side = extract(tags, size, 13);
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

	public String getTeam1() {
		return team1;
	}

	public String getTeam2() {
		return team2;
	}

	public String getFighter1() {
		return fighter1;
	}

	public String getFighter2() {
		return fighter2;
	}

	public String getSide() {
		return side;
	}

	public void setSide(String side) {
		this.side = side;
	}
}
