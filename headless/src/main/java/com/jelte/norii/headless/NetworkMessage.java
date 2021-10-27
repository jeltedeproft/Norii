package com.jelte.norii.headless;

import com.badlogic.gdx.Gdx;

public class NetworkMessage {
	private static final String TAG = NetworkMessage.class.getSimpleName();
	public static final  String SEPARATOR = "/";
	public static final String END_TAG = "%";
	
	public enum messageType {CONNECTED,SEARCH_OPPONENT,DISCONNECTED, BATTLE};
	
	public static String createConnectedMessageForSending(String client, String clientID) {
		return buildStringFromTags(messageType.CONNECTED.name(),client,clientID).toString();
	}
	
	public static String createDisconnectedMessageForSending(String client, String clientID) {
		return buildStringFromTags(messageType.DISCONNECTED.name(),client,clientID).toString();
	}
	
	public static String createSearchMessageForSending(String client, String clientID) {
		return buildStringFromTags(messageType.SEARCH_OPPONENT.name(),client,clientID).toString();
	}
	
	public static String createBattleMessageForSending(String fighter1, String fighter1ID, String fighter2, String fighter2ID) {
		return buildStringFromTags(messageType.BATTLE.name(),fighter1,fighter1ID, fighter2,fighter2ID).toString();
	}
	
	private static StringBuilder buildStringFromTags(String... tags) {
		StringBuilder stringToSend = new StringBuilder();

		for(String tag : tags) {
			stringToSend.append(tag);
			stringToSend.append(SEPARATOR);
		}
		stringToSend.append(END_TAG);
		return stringToSend;
	}
	
	public static NetworkMessageType createRecordFromString(String message) {
		if(!message.isBlank()) {
			String[] tags = message.split(SEPARATOR);
			switch(tags[0]) {
			case "CONNECTED":
				return new ConnectedMessage(messageType.CONNECTED,tags[1],tags[2]);
			case "DISCONNECTED":
				return new DisconnectedMessage(messageType.DISCONNECTED,tags[1],tags[2]);
			case "SEARCH_OPPONENT":
				return new SearchMessage(messageType.SEARCH_OPPONENT,tags[1],tags[2]);
			case "BATTLE":
				return new BattleMessage(messageType.BATTLE,tags[1],tags[2],tags[3],tags[4]);
			default:
				Gdx.app.log(TAG, "message tag not known to the system");
				return null;
			}
		}else {
			Gdx.app.log(TAG, "message was empty");
			return null;
		}
	}
	

	public record ConnectedMessage(messageType type, String client, String clientID) implements NetworkMessageType {}
	public record DisconnectedMessage(messageType type, String client, String clientID) implements NetworkMessageType {}
	public record SearchMessage(messageType type, String client, String clientID) implements NetworkMessageType {}
	public record BattleMessage(messageType type, String fighter1, String fighter1ID, String fighter2, String fighter2ID) implements NetworkMessageType {}

		
}
