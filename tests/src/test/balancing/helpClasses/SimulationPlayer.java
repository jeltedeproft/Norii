package test.balancing.helpClasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.jelte.norii.ai.EnemyType;
import com.jelte.norii.ai.UnitTurn;
import com.jelte.norii.battle.ApFileReader;
import com.jelte.norii.battle.BattleManager;
import com.jelte.norii.battle.MessageToBattleScreen;
import com.jelte.norii.battle.battlestate.BattleState;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.utility.TiledMapPosition;

public class SimulationPlayer implements UnitOwner {
	private List<Entity> team;
	private int ap;
	private boolean isMyTurn = false;
	private boolean isPlayer;
	private Alliance alliance;

	public SimulationPlayer(boolean isMyTurn, boolean isPlayer) {
		team = new ArrayList<>();
		this.isMyTurn = isMyTurn;
		this.isPlayer = isPlayer;
		ap = ApFileReader.getApData(0);
	}

	@Override
	public void renderUnits(Batch batch) {
		// nothing to do for this class

	}

	@Override
	public void updateUnits(final float delta) {
		team.removeIf(Entity::isDead);

		for (final Entity entity : team) {
			entity.update(delta);
		}
	}

	@Override
	public int getAp() {
		return ap;
	}

	@Override
	public void setAp(int ap) {
		this.ap = ap;
	}

	@Override
	public void setName(String name) {
		// nothing to do for this class

	}

	@Override
	public String getName() {
		// nothing to do for this class
		return null;
	}

	@Override
	public void setTeam(final List<Entity> team) {
		this.team = team;
	}

	@Override
	public List<Entity> getTeam() {
		return team;
	}

	@Override
	public void applyModifiers() {
		Entity[] teamCopy = team.toArray(new Entity[0]);// make copy to prevent concurrentModificationException
		for (Entity entity : teamCopy) {
			entity.applyModifiers();
		}
	}

	@Override
	public boolean isPlayer() {
		return isPlayer;
	}

	@Override
	public void setBattleManager(BattleManager battleManager) {
		// nothing to do for this class

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
	public void spawnUnits(List<TiledMapPosition> spawnPositions) {
		// nothing to do for this class

	}

	@Override
	public void spawnUnit(EntityTypes entityType, int entityID, TiledMapPosition pos) {
		// nothing to do for this class

	}

	@Override
	public void playerUnitMoved(Entity entity, TiledMapPosition pos) {
		// nothing to do for this class

	}

	@Override
	public void playerUnitAttacked(Entity entity, TiledMapPosition pos) {
		// nothing to do for this class

	}

	@Override
	public void playerUnitCastedSpell(Entity entity, Ability ability, TiledMapPosition pos) {
		// nothing to do for this class

	}

	@Override
	public void playerUnitSkipped(Entity entity) {
		// nothing to do for this class

	}

	@Override
	public void playerUnitSpawned(Entity entity, TiledMapPosition pos) {
		// nothing to do for this class

	}

	@Override
	public void sendMessageToBattleManager(MessageToBattleScreen message, Entity entity) {
		// nothing to do for this class

	}

	@Override
	public void sendMessageToBattleManager(MessageToBattleScreen message, Entity entity, TiledMapPosition oldPosition) {
		// nothing to do for this class

	}

	@Override
	public void sendMessageToBattleManager(MessageToBattleScreen message, Entity entity, int damage) {
		// nothing to do for this class

	}

	@Override
	public void dispose() {
		// nothing to do for this class

	}

	@Override
	public void reset(BattleState activeBattleState) {
		// nothing to do for this class

	}

	@Override
	public void processMove() {
		// nothing to do for this class

	}

	@Override
	public BattleState getNextBattleState() {
		return null;
	}

	@Override
	public EnemyType getType() {
		return EnemyType.SIMULATION;
	}

	@Override
	public boolean isMyTurn() {
		return isMyTurn;
	}

	@Override
	public void setMyTurn(boolean myTurn) {
		isMyTurn = myTurn;
	}

	@Override
	public boolean isAI() {
		return !isPlayer;
	}

	@Override
	public boolean isOnlinePlayer() {
		return false;
	}

	@Override
	public Alliance getAlliance() {
		return alliance;
	}

	@Override
	public void synchronizeMultiplayerUnitsWithLocal(HashMap<String, String> teamWithIdMap) {
		// nothing to do for this class

	}

	@Override
	public int getGameID() {
		return 0;
	}

	@Override
	public void notifyDeploymentDone() {
		// nothing to do for this class
	}

	@Override
	public UnitTurn getProcessingResult() {
		return null;
	}

	@Override
	public boolean isSimulation() {
		return true;
	}

	@Override
	public boolean isDummy() {
		return false;
	}

	@Override
	public void setAlliance(Alliance alliance) {
		this.alliance = alliance;
	}

	@Override
	public void giveTeamInformation() {
		// TODO Auto-generated method stub

	}

}
