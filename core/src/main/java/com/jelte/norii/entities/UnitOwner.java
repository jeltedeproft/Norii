package com.jelte.norii.entities;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.jelte.norii.ai.EnemyType;
import com.jelte.norii.battle.BattleManager;
import com.jelte.norii.battle.MessageToBattleScreen;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.utility.TiledMapPosition;

public interface UnitOwner {
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

	public void setBattleManager(BattleManager battleManager);

	public void removeUnit(Entity unit);

	public void addUnit(Entity unit);

	public void spawnUnits(List<TiledMapPosition> spawnPositions);

	public void sendMessageToBattleManager(MessageToBattleScreen message, Entity entity);

	public void sendMessageToBattleManager(MessageToBattleScreen message, Entity entity, TiledMapPosition oldPosition);

	public void sendMessageToBattleManager(MessageToBattleScreen message, Entity entity, int damage);

	public void dispose();

	public void resetAI(BattleState activeBattleState);

	public void processAi();

	public BattleState getNextBattleState();

	public EnemyType getType();
	
	public String getSide();
	
	public void setSide(String side);
}
