package com.jelte.norii.multiplayer;

import com.badlogic.gdx.Gdx;

public class NetworkMessage {
	private static final String TAG = NetworkMessage.class.getSimpleName();
	public static final  String SEPARATOR = "/";
	public static final String END_TAG = "%";
	
	private MessageType type;
	private String sender = "";
	private String receiver = "";
	
	public enum MessageType {CONNECTED,SEARCH_OPPONENT,DISCONNECTED, BATTLE}
	
	public NetworkMessage(MessageType type) {
		this.type = type;
	}
	
	public void makeConnectedMessage(String client) {
		type = MessageType.CONNECTED;
		sender = client;
	}
	
	public void makeDisconnectedMessage(String client) {
		type = MessageType.DISCONNECTED;
		sender = client;
	}
	
	public void makeSearchMessage(String client) {
		type = MessageType.SEARCH_OPPONENT;
		sender = client;
	}
	
	public void makeBattleMessage(String fighter1, String fighter2) {
		type = MessageType.BATTLE;
		sender = fighter1;
		receiver = fighter2;
	}
	
	public String messageToString() {
		StringBuilder stringToSend = new StringBuilder();

		stringToSend.append(type.name());
		stringToSend.append(SEPARATOR);
		stringToSend.append(sender);
		stringToSend.append(SEPARATOR);
		stringToSend.append(receiver);
		stringToSend.append(END_TAG);
		return stringToSend.toString();
	}
	
	public void importString(String message) {
		if(!message.isBlank()) {
			String[] tags = message.split(SEPARATOR);
			type = MessageType.valueOf(tags[0]);
			sender = tags[1];
			receiver = tags[2];
		}else {
			Gdx.app.log(TAG, "message was empty");
		}
	}		
}
