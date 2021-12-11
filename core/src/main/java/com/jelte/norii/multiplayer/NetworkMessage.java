package com.jelte.norii.multiplayer;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.jelte.norii.battle.battleState.MoveType;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.magic.AbilitiesEnum;
import com.jelte.norii.utility.MyPoint;
import com.jelte.norii.utility.TiledMapPosition;

public class NetworkMessage {
	private static final String TAG = NetworkMessage.class.getSimpleName();
	public static final String SEPARATOR = "/";
	public static final String END_TAG = "%";
	public static final String MOVE = "move";
	public static final String ATTACK = "attack";
	public static final String SKIP = "skip";
	public static final String SPELL = "spell";
	
	private static final Json json = new Json();

	private MessageType type;
	private String sender = "";
	private String receiver = "";
	private String username = "";
	private String password = "";
	private String loginWorked = "";
	private String loginReason = "";
	private String moveType = "";
	private String abilityType = "";
	private String team = "";
	private String mapName = "";
	private String team1 = "";
	private String team2 = "";
	private String fighter1 = "";
	private String fighter2 = "";
	private String playerStart = "";
	private String unitType = "";
	private String pos = "";
	private String gameID = "";
	private String unitID = "";
	private String teamWithIdMap = "";
	private String affectedUnits = "";

	public enum MessageType {
		CONNECTED, SEARCH_OPPONENT, DISCONNECTED, BATTLE, TRY_LOGIN, LOGIN_VALIDATION, MOVE_MADE, UNIT_DEPLOYED, SYNCHRONIZE_UNIT_IDS, DEPLOYMENT_FINISHED, UNIT_MOVED, UNIT_ATTACKED, UNIT_CASTED_SPELL, UNIT_SKIPPED, TURN_FINISHED
	}

	public NetworkMessage() {
		// do nothing
	}

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

	public void makeSearchMessage(String client, String team) {
		type = MessageType.SEARCH_OPPONENT;
		sender = client;
		this.team = team;
	}

	public void makeBattleMessage(String gameID, String fighter1, String fighter2, String map, String team1, String team2, String playerStart) {
		type = MessageType.BATTLE;
		this.fighter1 = fighter1;
		this.fighter2 = fighter2;
		this.mapName = map;
		this.team1 = team1;
		this.team2 = team2;
		this.playerStart = playerStart;
		this.gameID = gameID;
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

	public void makeUnitDeployedMessage(String unit, int unitID, String pos, int gameID) {
		type = MessageType.UNIT_DEPLOYED;
		unitType = unit;
		this.pos = pos;
		this.gameID = String.valueOf(gameID);
		this.unitID = Integer.toString(unitID);
		sender = ServerCommunicator.getInstance().getClientID();
	}

	public void makeInitEnemyTeamMessage(int gameID, String teamWithIdMap) {
		type = MessageType.SYNCHRONIZE_UNIT_IDS;
		this.teamWithIdMap = teamWithIdMap;
		this.gameID = String.valueOf(gameID);
		sender = ServerCommunicator.getInstance().getClientID();
	}

	public void makeDeploymentFinishedMessage(int gameID) {
		type = MessageType.DEPLOYMENT_FINISHED;
		this.gameID = String.valueOf(gameID);
		sender = ServerCommunicator.getInstance().getClientID();
	}
	
	public void makeUnitMovedMessage(String unit, int unitID, String pos, int gameID) {
		type = MessageType.MOVE_MADE;
		unitType = unit;
		this.moveType = MoveType.MOVE.name();
		this.pos = pos;
		this.gameID = String.valueOf(gameID);
		this.unitID = Integer.toString(unitID);
		sender = ServerCommunicator.getInstance().getClientID();
	}

	public void makeUnitAttackedMessage(String unit, int unitID, String pos, int gameID) {
		type = MessageType.MOVE_MADE;
		this.moveType = MoveType.ATTACK.name();
		unitType = unit;
		this.pos = pos;
		this.gameID = String.valueOf(gameID);
		this.unitID = Integer.toString(unitID);
		sender = ServerCommunicator.getInstance().getClientID();
	}

	public void makeUnitCastedSpellMessage(String unit, int unitID, String pos, String ability, int gameID, Array<MyPoint> affectedUnits) {
		type = MessageType.MOVE_MADE;
		this.moveType = MoveType.SPELL.name();
		unitType = unit;
		this.pos = pos;
		this.gameID = String.valueOf(gameID);
		this.unitID = Integer.toString(unitID);
		this.abilityType = ability;
		this.affectedUnits = convertArrayOfMyPointsToString(affectedUnits);
		sender = ServerCommunicator.getInstance().getClientID();
	}


	public void makeUnitSkippedMessage(String unit, int unitID, int gameID) {
		type = MessageType.MOVE_MADE;
		this.moveType = MoveType.SKIP.name();
		unitType = unit;
		this.gameID = String.valueOf(gameID);
		this.unitID = Integer.toString(unitID);
		sender = ServerCommunicator.getInstance().getClientID();
	}
	
	public void makeTurnFinishedMessage(String unit, int unitID, int gameID) {
		type = MessageType.TURN_FINISHED;
		unitType = unit;
		this.gameID = String.valueOf(gameID);
		this.unitID = Integer.toString(unitID);
		sender = ServerCommunicator.getInstance().getClientID();
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
		stringToSend.append(mapName);
		stringToSend.append(SEPARATOR);
		stringToSend.append(team1);
		stringToSend.append(SEPARATOR);
		stringToSend.append(team2);
		stringToSend.append(SEPARATOR);
		stringToSend.append(fighter1);
		stringToSend.append(SEPARATOR);
		stringToSend.append(fighter2);
		stringToSend.append(SEPARATOR);
		stringToSend.append(playerStart);
		stringToSend.append(SEPARATOR);
		stringToSend.append(unitType);
		stringToSend.append(SEPARATOR);
		stringToSend.append(pos);
		stringToSend.append(SEPARATOR);
		stringToSend.append(gameID);
		stringToSend.append(SEPARATOR);
		stringToSend.append(unitID);
		stringToSend.append(SEPARATOR);
		stringToSend.append(teamWithIdMap);
		stringToSend.append(SEPARATOR);
		stringToSend.append(moveType);
		stringToSend.append(SEPARATOR);
		stringToSend.append(abilityType);
		stringToSend.append(SEPARATOR);
		stringToSend.append(affectedUnits);
		stringToSend.append(SEPARATOR);
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
			mapName = extract(tags, size, 8);
			team1 = extract(tags, size, 9);
			team2 = extract(tags, size, 10);
			fighter1 = extract(tags, size, 11);
			fighter2 = extract(tags, size, 12);
			playerStart = extract(tags, size, 13);
			unitType = extract(tags, size, 14);
			pos = extract(tags, size, 15);
			gameID = extract(tags, size, 16);
			unitID = extract(tags, size, 17);
			teamWithIdMap = extract(tags, size, 18);
			moveType = extract(tags, size, 19);
			abilityType = extract(tags, size, 20);
			affectedUnits = extract(tags, size, 21);
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

	public boolean getLoginWorked() {
		return "true".equals(loginWorked);
	}

	public String getLoginReason() {
		return loginReason;
	}

	public String getTeam() {
		return team;
	}

	public String getMap() {
		return mapName;
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

	public String getPlayerStart() {
		return playerStart;
	}

	public void setPlayerStart(String playerStart) {
		this.playerStart = playerStart;
	}

	public EntityTypes  getUnitType() {
		if(!(unitType.isBlank() || unitType.isEmpty() || unitType == null)) {
			for(EntityTypes entityType : EntityTypes.values()) {
				if(entityType.getEntityName().equals(unitType)) {
					return entityType;
				}
			}
		}
		return null;
	}

	public TiledMapPosition getPos() {
		return new TiledMapPosition().setPosFromString(pos);
	}

	public int getGameID() {
		return Integer.parseInt(gameID);
	}

	public int getUnitID() {
		return Integer.valueOf(unitID);
	}

	@SuppressWarnings("unchecked")
	public HashMap getTeamWithIdMap() {
		return json.fromJson(HashMap.class, teamWithIdMap);
	}

	public MoveType getMoveType() {
		return MoveType.valueOf(moveType);
	}

	public AbilitiesEnum  getAbility() {
		return AbilitiesEnum.valueOf(abilityType);
	}
	
	public Array<MyPoint> getAffectedUnits(){
		return stringToArrayOfMyPoints(affectedUnits);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MESSAGE \n ");
		builder.append("______ \n");
		builder.append("type : ");
		builder.append(type.toString() + "\n");
		builder.append("sender : ");
		builder.append(sender + "\n");
		builder.append("receiver : ");
		builder.append(receiver + "\n");
		builder.append("username : ");
		builder.append(username + "\n");
		builder.append("password : ");
		builder.append(password + "\n");
		builder.append("loginWorked : ");
		builder.append(loginWorked + "\n");
		builder.append("loginreason : ");
		builder.append(loginReason + "\n");
		builder.append("moveType : ");
		builder.append(moveType + "\n");
		builder.append("ability : ");
		builder.append(abilityType + "\n");
		builder.append("team : ");
		builder.append(team + "\n");
		builder.append("map : ");
		builder.append(mapName + "\n");
		builder.append("fighter1 : ");
		builder.append(fighter1 + "\n");
		builder.append("fighter2 : ");
		builder.append(fighter2 + "\n");
		builder.append("team1 : ");
		builder.append(team1 + "\n");
		builder.append("team2 : ");
		builder.append(team2 + "\n");
		builder.append("playerStart : ");
		builder.append(playerStart + "\n");
		builder.append("unit type : ");
		builder.append(unitType + "\n");
		builder.append("position : ");
		builder.append(pos + "\n");
		builder.append("game ID : ");
		builder.append(gameID + "\n");
		builder.append("unit ID : ");
		builder.append(unitID + "\n");
		builder.append("teamWithIdMap : ");
		builder.append(teamWithIdMap + "\n");
		builder.append("moveType : ");
		builder.append(moveType + "\n");

		return builder.toString();
	}
	

	private String convertArrayOfMyPointsToString(Array<MyPoint> affectedUnits) {
		if(affectedUnits == null) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for(MyPoint myPoint : affectedUnits) {
			builder.append(myPoint.x);
			builder.append(myPoint.y);
			builder.append("@");
		}
		return builder.toString();
	}
	

	private Array<MyPoint> stringToArrayOfMyPoints(String affectedUnits) {
		if(affectedUnits.isBlank() || affectedUnits.isEmpty() || affectedUnits == null) {
			return null;
		}
		Array<MyPoint> myPoints = new Array<>();
		String[] points = affectedUnits.split("@");
		for(String point : points) {
			int x = Integer.parseInt(point.substring(0, 1));
			int y = Integer.parseInt(point.substring(1));
			myPoints.add(new MyPoint(x,y));
		}
		return myPoints;
	}

}
