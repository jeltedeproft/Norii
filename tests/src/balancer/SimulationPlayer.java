package balancer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.jelte.norii.ai.AITeamLeader;
import com.jelte.norii.ai.EnemyType;
import com.jelte.norii.ai.UnitTurn;
import com.jelte.norii.battle.ApFileReader;
import com.jelte.norii.battle.BattleManager;
import com.jelte.norii.battle.MessageToBattleScreen;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.utility.TiledMapPosition;

public class SimulationPlayer implements UnitOwner {

	private static final String TAG = AITeamLeader.class.getSimpleName();

	private List<Entity> team;
	private int ap;
	private boolean isMyTurn = false;
	private boolean isPlayer;
	private Alliance alliance;

	public SimulationPlayer(boolean isMyTurn, boolean isPlayer) {
		team = new ArrayList<Entity>();
		this.isMyTurn = isMyTurn;
		this.isPlayer = isPlayer;
		ap = ApFileReader.getApData(0);
	}

	@Override
	public void renderUnits(Batch batch) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPlayer() {
		return isPlayer;
	}

	@Override
	public void setBattleManager(BattleManager battleManager) {
		// TODO Auto-generated method stub

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

}
