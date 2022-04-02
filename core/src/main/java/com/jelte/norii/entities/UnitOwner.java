package com.jelte.norii.entities;

import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.jelte.norii.ai.EnemyType;
import com.jelte.norii.ai.UnitTurn;
import com.jelte.norii.battle.BattleManager;
import com.jelte.norii.battle.MessageToBattleScreen;
import com.jelte.norii.battle.battlestate.BattleState;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.utility.TiledMapPosition;

public interface UnitOwner {
	public enum Alliance {
		TEAM_RED,
		TEAM_BLUE
	}

	void renderUnits(final Batch batch);

	void updateUnits(final float delta);

	int getAp();

	void setAp(int ap);

	void setName(String name);

	String getName();

	List<Entity> getTeam();

	void setTeam(List<Entity> entities);

	void applyModifiers();

	boolean isPlayer();

	boolean isAI();

	boolean isOnlinePlayer();

	boolean isSimulation();

	boolean isDummy();

	void setBattleManager(BattleManager battleManager);

	void removeUnit(Entity unit);

	void addUnit(Entity unit);

	void spawnUnits(List<TiledMapPosition> spawnPositions);

	void spawnUnit(EntityTypes entityType, int entityID, TiledMapPosition pos);

	void playerUnitMoved(Entity entity, TiledMapPosition pos);

	void playerUnitAttacked(Entity entity, TiledMapPosition pos);

	void playerUnitCastedSpell(Entity entity, Ability ability, TiledMapPosition pos);

	void playerUnitSkipped(Entity entity);

	void playerUnitSpawned(Entity entity, TiledMapPosition pos);

	void sendMessageToBattleManager(MessageToBattleScreen message, Entity entity);

	void sendMessageToBattleManager(MessageToBattleScreen message, Entity entity, TiledMapPosition oldPosition);

	void sendMessageToBattleManager(MessageToBattleScreen message, Entity entity, int damage);

	void dispose();

	void reset(BattleState activeBattleState);

	void processMove();

	BattleState getNextBattleState();

	EnemyType getType();

	boolean isMyTurn();

	void setMyTurn(boolean myTurn);

	Alliance getAlliance();

	void setAlliance(Alliance alliance);

	void synchronizeMultiplayerUnitsWithLocal(HashMap<String, String> teamWithIdMap);

	int getGameID();

	void notifyDeploymentDone();

	UnitTurn getProcessingResult();

	void giveTeamInformation();
}
