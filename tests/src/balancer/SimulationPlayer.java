package balancer;

import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.jelte.norii.ai.AITeamLeader;
import com.jelte.norii.ai.EnemyType;
import com.jelte.norii.ai.UnitTurn;
import com.jelte.norii.battle.BattleManager;
import com.jelte.norii.battle.MessageToBattleScreen;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.entities.Player;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.utility.TiledMapPosition;

public class SimulationPlayer implements UnitOwner {

	private static final String TAG = AITeamLeader.class.getSimpleName();

	private List<Entity> team;
	private BattleManager battleManager;
	private int ap;
	private String name = "simulation";
	private boolean isMyTurn = false;
	private BattleState stateWithNextMove;

	public SimulationPlayer(boolean isMyTurn) {
		this.isMyTurn = isMyTurn;
	}

	@Override
	public void spawnUnits(List<TiledMapPosition> spawnPositions) {
		for (final Entity unit : team) {
			if (!spawnPositions.isEmpty()) {
				unit.setCurrentPosition(spawnPositions.get(0));
				unit.setPlayerUnit(false);
				unit.getVisualComponent().spawn(spawnPositions.get(0));
				spawnPositions.remove(0);
				battleManager.addUnit(unit);
			} else {
				Gdx.app.debug(TAG, "maybe no more room to spawn ai units!");
			}
		}
	}

	@Override
	public void reset(BattleState stateOfBattle) {
		aiDecisionMaker.resetAI(stateOfBattle);
	}

	@Override
	public void processMove() {
		if (aiDecisionMaker.processAi()) {
			stateWithNextMove = getNextBattleState();
			sendMessageToBattleManager(MessageToBattleScreen.FINISHED_PROCESSING_TURN, battleManager.getActiveUnit());
		}
	}

	@Override
	public UnitTurn getProcessingResult() {
		return stateWithNextMove.getTurn();
	}

	@Override
	public BattleState getNextBattleState() {
		return aiDecisionMaker.getResult();
	}

	@Override
	public void updateUnits(final float delta) {
		team.removeIf(Entity::isDead);

		for (final Entity entity : team) {
			entity.update(delta);
		}
	}

	@Override
	public void renderUnits(final Batch batch) {
		for (final Entity entity : team) {
			entity.draw(batch);
		}
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
	public void dispose() {
		for (final Entity entity : team) {
			entity.dispose();
		}
	}

	@Override
	public String toString() {
		return "AITeamLeader with team : " + team;
	}

	@Override
	public void applyModifiers() {
		team.forEach(Entity::applyModifiers);
	}

	@Override
	public boolean isPlayer() {
		return false;
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

	@Override
	public void sendMessageToBattleManager(MessageToBattleScreen message, Entity entity, int damage) {
		battleManager.sendMessageToBattleScreen(message, entity, damage);
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
	public void spawnUnit(EntityTypes entityType, int entityID, TiledMapPosition pos) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerUnitSpawned(Entity entity, TiledMapPosition pos) {
		battleManager.setPlayerTurn(!battleManager.isPlayerTurn());
		Player.getInstance().setMyTurn(true);// turn back to player because AI already placed units
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
		return true;
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
	public void synchronizeMultiplayerUnitsWithLocal(HashMap teamWithIdMap) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getGameID() {
		return 1;
	}

	@Override
	public void notifyDeploymentDone() {
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

}
