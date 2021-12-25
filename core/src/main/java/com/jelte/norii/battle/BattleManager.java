package com.jelte.norii.battle;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.UnitTurn;
import com.jelte.norii.audio.AudioCommand;
import com.jelte.norii.audio.AudioManager;
import com.jelte.norii.audio.AudioTypeEvent;
import com.jelte.norii.battle.battlePhase.ActionBattlePhase;
import com.jelte.norii.battle.battlePhase.AttackBattlePhase;
import com.jelte.norii.battle.battlePhase.BattlePhase;
import com.jelte.norii.battle.battlePhase.DeploymentBattlePhase;
import com.jelte.norii.battle.battlePhase.MovementBattlePhase;
import com.jelte.norii.battle.battlePhase.SelectUnitBattlePhase;
import com.jelte.norii.battle.battlePhase.SpellBattlePhase;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.battle.battleState.Move;
import com.jelte.norii.battle.battleState.MoveType;
import com.jelte.norii.battle.battleState.SpellMove;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.entities.Player;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Ability.DamageType;
import com.jelte.norii.map.MyPathFinder;
import com.jelte.norii.screen.BattleScreen;
import com.jelte.norii.utility.MyPoint;
import com.jelte.norii.utility.TiledMapPosition;

public class BattleManager {
	private BattlePhase deploymentBattleState;
	private BattlePhase selectUnitBattleState;
	private BattlePhase movementBattleState;
	private BattlePhase attackBattleState;
	private BattlePhase spellBattleState;
	private BattlePhase actionBattleState;
	private BattlePhase currentBattleState;

	private Entity activeUnit;
	private UnitOwner enemyTeamLeader;
	private BattleState activeBattleState;
	private UnitTurn activeTurn;
	private boolean playerTurn;
	private boolean enemyUnitIsDoingSomething = false;
	private boolean enemyIsMakingDecision = false;
	private int unitsDeployed;
	private BattleScreen battleScreen;
	private int turn = 0;

	private static final String TAG = BattleManager.class.getSimpleName();

	public BattleManager(UnitOwner enemyTeamLeader, int width, int height, Array<GridCell> unwalkableNodes, BattleScreen battleScreen) {
		initVariables(enemyTeamLeader, width, height, unwalkableNodes, battleScreen);

		deploymentBattleState = new DeploymentBattlePhase(this);
		selectUnitBattleState = new SelectUnitBattlePhase(this);
		movementBattleState = new MovementBattlePhase(this);
		attackBattleState = new AttackBattlePhase(this);
		spellBattleState = new SpellBattlePhase(this);
		actionBattleState = new ActionBattlePhase(this);

		currentBattleState = deploymentBattleState;
		currentBattleState.entry();
	}

	private void initVariables(UnitOwner enemyTeamLeader, int width, int height, Array<GridCell> unwalkableNodes, BattleScreen battleScreen) {
		this.battleScreen = battleScreen;
		this.enemyTeamLeader = enemyTeamLeader;
		Player.getInstance().setBattleManager(this);
		enemyTeamLeader.setBattleManager(this);
		activeUnit = Player.getInstance().getTeam().get(0);
		playerTurn = !enemyTeamLeader.isMyTurn();
		activeTurn = null;
		enemyIsMakingDecision = !playerTurn;
		activeBattleState = new BattleState(width, height);
		initializeStateOfBattle(unwalkableNodes);
	}

	private void initializeStateOfBattle(Array<GridCell> unwalkableNodes) {
		for (final GridCell cell : unwalkableNodes) {
			activeBattleState.get(cell.x, cell.y).setWalkable(false);
		}
	}

	public void addUnit(Entity unit) {
		activeBattleState.addEntity(unit);
		if (unit.isPlayerUnit()) {
			unitsDeployed++;
		}
	}

	public void justShowUnit(Entity entity) {
		for (final Entity unit : activeBattleState.getAllUnits()) {
			unit.getVisualComponent().setActive(false);
			unit.setFocused(false);
		}
		entity.getVisualComponent().setActive(true);
		entity.setFocused(true);
		sendMessageToBattleScreen(MessageToBattleScreen.UNIT_ACTIVE, entity);
	}

	public void setUnitActive(Entity entity) {
		activeUnit.setFocused(false);
		activeUnit.getVisualComponent().setActive(false);

		activeUnit = entity;
		activeUnit.setFocused(true);
		activeUnit.getVisualComponent().setActive(true);
		sendMessageToBattleScreen(MessageToBattleScreen.UNIT_ACTIVE, entity);
	}

	public void sendMessageToBattleScreen(MessageToBattleScreen message, Entity entity, TiledMapPosition newPosition) {
		switch (message) {
		case UPDATE_POS:
			updateStateOfBattle(entity, newPosition);
			break;
		default:
			break;
		}
	}

	public void sendMessageToBattleScreen(MessageToBattleScreen message, Entity entity, int damage) {
		switch (message) {
		case DAMAGED:
			activeBattleState.unitDamage(entity, damage);
			break;
		default:
			break;
		}
	}

	public void sendMessageToBattleScreen(MessageToBattleScreen message, Entity entity) {
		switch (message) {
		case CLICKED:
			if (!enemyUnitIsDoingSomething) {
				getCurrentBattleState().clickedOnUnit(entity);
			}
			break;
		case UNIT_DIED:
			removeUnit(entity);
			checkVictory();
			break;
		case FINISHED_PROCESSING_TURN:
			enemyIsMakingDecision = false;
			enemyUnitIsDoingSomething = true;
			activeTurn = enemyTeamLeader.getProcessingResult();
			executeTurn();
			setCurrentBattleState(getSelectUnitBattleState());
			getCurrentBattleState().entry();
			break;
		case ACTION_COMPLETED:
			if (!playerTurn && enemyTeamLeader.isAI()) {
				executeTurn();
			}

			if (playerTurn) {
				sendMessageToBattleScreen(MessageToBattleScreen.UNLOCK_UI, activeUnit);
			}
			break;
		default:
			battleScreen.messageFromBattleManager(message, entity);
			break;
		}
	}

	public void swapTurn() {
		Player.getInstance().applyModifiers();
		enemyTeamLeader.applyModifiers();

		playerTurn = !playerTurn;

		if (!playerTurn) {
			Player.getInstance().setAp(ApFileReader.getApData(turn));
			enemyIsMakingDecision = true;
			enemyTeamLeader.reset(activeBattleState);
		} else {
			turn++;
			enemyTeamLeader.setAp(ApFileReader.getApData(turn));
			setCurrentBattleState(getSelectUnitBattleState());
			getCurrentBattleState().entry();
		}

	}

	public void update() {
		if (enemyIsMakingDecision) {
			enemyTeamLeader.processMove();
		}
	}

	private void executeTurn() {
		final int entityID = activeTurn.getEntityID();
		final Entity entity = getEntityByID(entityID);
		final Move move = activeTurn.getNextMove();
		if (move == null) {
			enemyUnitIsDoingSomething = false;
			if (entity.isPlayerUnit()) {
				sendMessageToBattleScreen(MessageToBattleScreen.UNLOCK_UI, activeUnit);
			}
			checkVictory();
			swapTurn();
			enemyTeamLeader.setAp(ApFileReader.getApData(turn));
			return;
		}
		executeMove(entity, move);
		if (!move.getMoveType().equals(MoveType.MOVE)) {
			executeTurn();
		}
	}

	public void executeMove(Entity entity, Move move) {
		switch (move.getMoveType()) {
		case SPELL:
			final SpellMove spellMove = (SpellMove) move;
			final SpellBattlePhase spellState = (SpellBattlePhase) spellBattleState;
			spellState.executeSpellForAi(entity, spellMove.getAbility(), spellMove.getLocation());
			break;
		case MOVE:
			final List<GridCell> path = MyPathFinder.getInstance().pathTowards(entity.getCurrentPosition(), new TiledMapPosition().setPositionFromTiles(move.getLocation().x, move.getLocation().y), entity.getAp());
			activeBattleState.moveUnitTo(entity, new MyPoint(move.getLocation().x, move.getLocation().y));
			entity.move(path);
			// wait for move to complete before executing next one
			break;
		case ATTACK:
			final Entity entityToAttack = getEntityByID(activeBattleState.get(move.getLocation().x, move.getLocation().y).getUnit().getEntityID());
			entity.attack(entityToAttack, DamageType.PHYSICAL);
			sendMessageToBattleScreen(MessageToBattleScreen.UPDATE_UI, entityToAttack);
			AudioManager.getInstance().onNotify(AudioCommand.SOUND_PLAY_ONCE, AudioTypeEvent.ATTACK_SOUND);
			break;
		case SKIP:
			enemyUnitIsDoingSomething = false;
			swapTurn();
			enemyTeamLeader.setAp(ApFileReader.getApData(turn));
			sendMessageToBattleScreen(MessageToBattleScreen.UNLOCK_UI, activeUnit);
			break;
		case DUMMY:
			break;
		default:
			break;
		}
	}

	private void checkVictory() {
		if (activeBattleState.getAiUnits().isEmpty()) {
			sendMessageToBattleScreen(MessageToBattleScreen.PLAYER_WINS, activeUnit);
		}

		if (activeBattleState.getPlayerUnits().isEmpty()) {
			sendMessageToBattleScreen(MessageToBattleScreen.AI_WINS, activeUnit);
		}
	}

	public Entity getEntityByID(int entityID) {
		for (final Entity entity : Player.getInstance().getTeam()) {
			if (entity.getEntityID() == entityID) {
				return entity;
			}
		}

		for (final Entity entity : enemyTeamLeader.getTeam()) {
			if (entity.getEntityID() == entityID) {
				return entity;
			}
		}
		return null;
	}

	private void removeUnit(Entity unit) {
		executeOnDeathEffect(unit);
		Player.getInstance().removeUnit(unit);
		enemyTeamLeader.removeUnit(unit);
		activeBattleState.removeUnit(unit);
	}

	private void executeOnDeathEffect(Entity unit) {
		if (unit.getEntityType().equals(EntityTypes.BOOMERANG)) {
			final Ability ability = unit.getAbility();
			if (ability.getTargetLocation() != null) {
				final SpellBattlePhase spellState = (SpellBattlePhase) spellBattleState;
				spellState.executeSpellForAi(unit, ability, ability.getTargetLocation());
			}

		}
	}

	public void updateStateOfBattle(Entity unit, TiledMapPosition newPos) {
		final MyPoint newPoint = new MyPoint(newPos.getTileX(), newPos.getTileY());
		final MyPoint oldPoint = new MyPoint(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY());
		if (!oldPoint.equals(newPoint)) {
			activeBattleState.moveUnitTo(unit, newPoint);
		}
	}

	public int getTurn() {
		return turn;
	}

	public BattleState getBattleState() {
		return activeBattleState;
	}

	public Entity getActiveUnit() {
		return activeUnit;
	}

	public List<Entity> getPlayerUnits() {
		return Player.getInstance().getTeam();
	}

	public List<Entity> getEnemyUnits() {
		return enemyTeamLeader.getTeam();
	}

	public boolean isPlayerTurn() {
		return playerTurn;
	}

	public void setPlayerTurn(boolean playerTurn) {
		this.playerTurn = playerTurn;
	}

	public BattlePhase getDeploymentBattleState() {
		return deploymentBattleState;
	}

	public void setDeploymentBattleState(final BattlePhase deploymentBattleState) {
		this.deploymentBattleState = deploymentBattleState;
	}

	public BattlePhase getMovementBattleState() {
		return movementBattleState;
	}

	public void setMovementBattleState(final BattlePhase movementBattleState) {
		this.movementBattleState = movementBattleState;
	}

	public BattlePhase getAttackBattleState() {
		return attackBattleState;
	}

	public void setAttackBattleState(final BattlePhase attackBattleState) {
		this.attackBattleState = attackBattleState;
	}

	public BattlePhase getSpellBattleState() {
		return spellBattleState;
	}

	public void setSpellBattleState(final BattlePhase spellBattleState) {
		this.spellBattleState = spellBattleState;
	}

	public BattlePhase getActionBattleState() {
		return actionBattleState;
	}

	public void setActionBattleState(final BattlePhase actionBattleState) {
		this.actionBattleState = actionBattleState;
	}

	public BattlePhase getCurrentBattleState() {
		return currentBattleState;
	}

	public void setCurrentBattleState(final BattlePhase currentBattleState) {
		this.currentBattleState = currentBattleState;
	}

	public BattlePhase getSelectUnitBattleState() {
		return selectUnitBattleState;
	}

	public void setSelectUnitBattleState(BattlePhase selectUnitBattleState) {
		this.selectUnitBattleState = selectUnitBattleState;
	}

	public List<Entity> getUnits() {
		return Stream.concat(Player.getInstance().getTeam().stream(), enemyTeamLeader.getTeam().stream()).collect(Collectors.toList());
	}

	public int getUnitsDeployed() {
		return unitsDeployed;
	}

	public UnitOwner getEnemyTeamLeader() {
		return enemyTeamLeader;
	}
}
