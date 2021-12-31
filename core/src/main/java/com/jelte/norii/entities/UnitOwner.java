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
		TEAM_RED, TEAM_BLUE
	}

	public void renderUnits(final Batch batch);

	public void updateUnits(final float delta);

	public int getAp();

	public void setAp(int ap);

	public void setName(String name);

	public String getName();

	public List<Entity> getTeam();

	public void setTeam(List<Entity> entities);

	public void applyModifiers();

	public boolean isPlayer();

	public boolean isAI();

	public boolean isOnlinePlayer();

	public boolean isSimulation();

	public boolean isDummy();

	public void setBattleManager(BattleManager battleManager);

	public void removeUnit(Entity unit);

	public void addUnit(Entity unit);

	public void spawnUnits(List<TiledMapPosition> spawnPositions);

	public void spawnUnit(EntityTypes entityType, int entityID, TiledMapPosition pos);

	public void playerUnitMoved(Entity entity, TiledMapPosition pos);

	public void playerUnitAttacked(Entity entity, TiledMapPosition pos);

	public void playerUnitCastedSpell(Entity entity, Ability ability, TiledMapPosition pos);

	public void playerUnitSkipped(Entity entity);

	public void playerUnitSpawned(Entity entity, TiledMapPosition pos);

	public void sendMessageToBattleManager(MessageToBattleScreen message, Entity entity);

	public void sendMessageToBattleManager(MessageToBattleScreen message, Entity entity, TiledMapPosition oldPosition);

	public void sendMessageToBattleManager(MessageToBattleScreen message, Entity entity, int damage);

	public void dispose();

	public void reset(BattleState activeBattleState);

	public void processMove();

	public BattleState getNextBattleState();

	public EnemyType getType();

	public boolean isMyTurn();

	public void setMyTurn(boolean myTurn);

	public Alliance getAlliance();

	public void setAlliance(Alliance alliance);

	public void synchronizeMultiplayerUnitsWithLocal(HashMap<String, String> teamWithIdMap);

	public int getGameID();

	public void notifyDeploymentDone();

	public UnitTurn getProcessingResult();
}
