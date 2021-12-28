package testBalancing.helpClasses;

import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.jelte.norii.ai.EnemyType;
import com.jelte.norii.ai.UnitTurn;
import com.jelte.norii.battle.BattleManager;
import com.jelte.norii.battle.MessageToBattleScreen;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.utility.TiledMapPosition;

public class DummyOwner implements UnitOwner {

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
	public void setName(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
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
	public void spawnUnit(EntityTypes entityType, int entityID, TiledMapPosition pos) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerUnitMoved(Entity entity, TiledMapPosition pos) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerUnitAttacked(Entity entity, TiledMapPosition pos) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerUnitCastedSpell(Entity entity, Ability ability, TiledMapPosition pos) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerUnitSkipped(Entity entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerUnitSpawned(Entity entity, TiledMapPosition pos) {
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
	public void reset(BattleState activeBattleState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processMove() {
		// TODO Auto-generated method stub

	}

	@Override
	public BattleState getNextBattleState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnemyType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMyTurn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setMyTurn(boolean myTurn) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isAI() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOnlinePlayer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Alliance getAlliance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void synchronizeMultiplayerUnitsWithLocal(HashMap<String, String> teamWithIdMap) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getGameID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void notifyDeploymentDone() {
		// TODO Auto-generated method stub

	}

	@Override
	public UnitTurn getProcessingResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSimulation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDummy() {
		return true;
	}

}
