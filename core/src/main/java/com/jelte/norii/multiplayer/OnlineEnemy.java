package com.jelte.norii.multiplayer;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.jelte.norii.battle.BattleManager;
import com.jelte.norii.battle.MessageToBattleScreen;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.utility.TiledMapPosition;

public class OnlineEnemy implements UnitOwner {

	@Override
	public void renderUnits(Batch batch) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateUnits(float delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getAp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAp(int ap) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Entity> getTeam() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTeam(List<Entity> entities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void applyModifiers() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPlayer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setBattleManager(BattleManager battleManager) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeUnit(Entity unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addUnit(Entity unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void spawnUnits(List<TiledMapPosition> spawnPositions) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMessageToBattleManager(MessageToBattleScreen message, Entity entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMessageToBattleManager(MessageToBattleScreen message, Entity entity, TiledMapPosition oldPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMessageToBattleManager(MessageToBattleScreen message, Entity entity, int damage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetAI(BattleState activeBattleState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processAi() {
		// TODO Auto-generated method stub

	}

	@Override
	public BattleState getNextBattleState() {
		// TODO Auto-generated method stub
		return null;
	}

}
