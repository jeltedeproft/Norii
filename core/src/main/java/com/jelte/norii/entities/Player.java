package com.jelte.norii.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.EnemyType;
import com.jelte.norii.ai.UnitTurn;
import com.jelte.norii.battle.ApFileReader;
import com.jelte.norii.battle.BattleManager;
import com.jelte.norii.battle.MessageToBattleScreen;
import com.jelte.norii.battle.battlestate.BattleState;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.multiplayer.NetworkMessage;
import com.jelte.norii.multiplayer.NetworkMessage.MessageType;
import com.jelte.norii.multiplayer.ServerCommunicator;
import com.jelte.norii.profile.ProfileManager;
import com.jelte.norii.utility.TiledMapPosition;

public class Player implements UnitOwner {
	private static Player instance;
	private List<Entity> team;
	private BattleManager battleManager;
	private int ap;
	private EnemyType type;
	private String name;
	private boolean isMyTurn;
	private int gameID;
	private Alliance alliance;

	@Override
	public void updateUnits(final float delta) {
		for (final Entity entity : team) {
			entity.update(delta);

			if (!entity.getOldPlayerPosition().isTileEqualTo(entity.getCurrentPosition())) {
				entity.setOldPlayerPosition(entity.getCurrentPosition());
			}
		}
	}

	@Override
	public void renderUnits(final Batch batch) {
		for (final Entity entity : team) {
			entity.draw(batch);
		}
	}

	@Override
	public List<Entity> getTeam() {
		return team;
	}

	@Override
	public void dispose() {
		for (final Entity entity : team) {
			entity.dispose();
		}
	}

	@Override
	public void setTeam(List<Entity> playerMonsters) {
		team = playerMonsters;
	}

	@Override
	public void applyModifiers() {
		Entity[] teamCopy = team.toArray(new Entity[0]);// make copy to prevent concurrentModificationException
		for (Entity entity : teamCopy) {
			entity.applyModifiers();
		}
	}

	@Override
	public String toString() {
		return "player with team : " + team;
	}

	@Override
	public boolean isPlayer() {
		return true;
	}

	public static Player getInstance() {
		if (instance == null) {
			instance = new Player();
		}
		return instance;
	}

	private Player() {
		team = new ArrayList<>();
		type = EnemyType.PLAYER;
		isMyTurn = true;
	}

	@Override
	public void sendMessageToBattleManager(MessageToBattleScreen message, Entity entity) {
		battleManager.sendMessageToBattleScreen(message, entity);
	}

	@Override
	public void setBattleManager(BattleManager battleManager) {
		this.battleManager = battleManager;
	}

	@Override
	public void removeUnit(Entity unit) {
		team.remove(unit);
	}

	@Override
	public void addUnit(Entity unit) {
		team.add(unit);
	}

	@Override
	public void sendMessageToBattleManager(MessageToBattleScreen message, Entity entity, TiledMapPosition oldPosition) {
		battleManager.sendMessageToBattleScreen(message, entity, oldPosition);
	}

	public void initializeTeam() {
		final Array<String> heroNames = ProfileManager.getInstance().getTeamHeroes();
		for (final String heroName : heroNames) {
			for (final EntityTypes entityType : EntityTypes.values()) {
				if (entityType.getEntityName().equals(heroName)) {
					addUnit(new Entity(entityType, this, true));
				}
			}
		}
		setAp(ApFileReader.getApData(0));
	}

	@Override
	public void playerUnitSpawned(Entity entity, TiledMapPosition pos) {
		NetworkMessage message = new NetworkMessage(MessageType.UNIT_DEPLOYED);
		message.makeUnitDeployedMessage(entity.getEntityType().name(), entity.getEntityID(), pos.toString(), gameID);
		ServerCommunicator.getInstance().sendMessage(message);
		setMyTurn(false);
	}

	@Override
	public void sendMessageToBattleManager(MessageToBattleScreen message, Entity entity, int damage) {
		battleManager.sendMessageToBattleScreen(message, entity, damage);
	}

	@Override
	public void setAp(int ap) {
		this.ap = ap;
	}

	@Override
	public int getAp() {
		return ap;
	}

	@Override
	public EnemyType getType() {
		return type;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isMyTurn() {
		return isMyTurn;
	}

	@Override
	public void setMyTurn(boolean myTurn) {
		this.isMyTurn = myTurn;
	}

	@Override
	public boolean isAI() {
		return false;
	}

	@Override
	public boolean isOnlinePlayer() {
		return false;
	}

	@Override
	public int getGameID() {
		return gameID;
	}

	public void setGameID(int gameID) {
		this.gameID = gameID;
	}

	@Override
	public Alliance getAlliance() {
		return alliance;
	}

	public void setAlliance(Alliance alliance) {
		this.alliance = alliance;
	}

	@Override
	public void spawnUnits(List<TiledMapPosition> spawnPositions) {
		// nothing to do

	}

	@Override
	public void reset(BattleState activeBattleState) {
		// nothing to do

	}

	@Override
	public void processMove() {
		// nothing to do

	}

	@Override
	public BattleState getNextBattleState() {
		// nothing to do
		return null;
	}

	@Override
	public void spawnUnit(EntityTypes entityType, int entityID, TiledMapPosition pos) {
		// nothing to do

	}

	@Override
	public void synchronizeMultiplayerUnitsWithLocal(HashMap<String, String> teamWithIdMap) {
		// nothing to do

	}

	@Override
	public void notifyDeploymentDone() {
		// nothing to do

	}

	@Override
	public void playerUnitMoved(Entity entity, TiledMapPosition pos) {
		// nothing to do

	}

	@Override
	public void playerUnitAttacked(Entity entity, TiledMapPosition pos) {
		// nothing to do

	}

	@Override
	public void playerUnitCastedSpell(Entity entity, Ability ability, TiledMapPosition pos) {
		// nothing to do

	}

	@Override
	public void playerUnitSkipped(Entity entity) {
		// nothing to do
	}

	@Override
	public UnitTurn getProcessingResult() {
		return null;
	}

	@Override
	public boolean isSimulation() {
		return false;
	}

	@Override
	public boolean isDummy() {
		return false;
	}
}
