package com.jelte.norii.battle;

import java.awt.Point;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.AITeamLeader;
import com.jelte.norii.ai.UnitTurn;
import com.jelte.norii.audio.AudioObserver;
import com.jelte.norii.battle.battlePhase.ActionBattlePhase;
import com.jelte.norii.battle.battlePhase.AttackBattlePhase;
import com.jelte.norii.battle.battlePhase.BattlePhase;
import com.jelte.norii.battle.battlePhase.DeploymentBattlePhase;
import com.jelte.norii.battle.battlePhase.MovementBattlePhase;
import com.jelte.norii.battle.battlePhase.SelectUnitBattlePhase;
import com.jelte.norii.battle.battlePhase.SpellBattlePhase;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.battle.battleState.HypotheticalUnit;
import com.jelte.norii.battle.battleState.Move;
import com.jelte.norii.battle.battleState.SpellMove;
import com.jelte.norii.entities.AiEntity;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.PlayerEntity;
import com.jelte.norii.magic.Modifier;
import com.jelte.norii.map.MyPathFinder;
import com.jelte.norii.utility.TiledMapPosition;

public class BattleManager {
	private BattlePhase deploymentBattleState;
	private BattlePhase selectUnitBattleState;
	private BattlePhase movementBattleState;
	private BattlePhase attackBattleState;
	private BattlePhase spellBattleState;
	private BattlePhase actionBattleState;
	private BattlePhase currentBattleState;

	private PlayerEntity activeUnit;
	private AITeamLeader aiTeamLeader;
	private BattleState stateOfBattle;
	private boolean playerTurn;

	private List<PlayerEntity> playerUnits;
	private List<AiEntity> aiUnits;
	private Entity lockedUnit;

	public BattleManager(final List<PlayerEntity> playerUnits, final List<AiEntity> aiUnits, AITeamLeader aiTeamLeader, int width, int height, Array<GridCell> unwalkableNodes) {
		initVariables(playerUnits, aiUnits, aiTeamLeader, width, height, unwalkableNodes);

		deploymentBattleState = new DeploymentBattlePhase(this);
		selectUnitBattleState = new SelectUnitBattlePhase(this);
		movementBattleState = new MovementBattlePhase(this);
		attackBattleState = new AttackBattlePhase(this);
		spellBattleState = new SpellBattlePhase(this);
		actionBattleState = new ActionBattlePhase(this);

		currentBattleState = deploymentBattleState;
		currentBattleState.entry();
	}

	private void initVariables(final List<PlayerEntity> playerUnits, final List<AiEntity> aiUnits, AITeamLeader aiTeamLeader, int width, int height, Array<GridCell> unwalkableNodes) {
		this.playerUnits = playerUnits;
		this.aiUnits = aiUnits;
		this.aiTeamLeader = aiTeamLeader;
		activeUnit = playerUnits.get(0);
		playerTurn = true;
		lockedUnit = null;
		stateOfBattle = new BattleState(width, height);
		initializeStateOfBattle(playerUnits, aiUnits, unwalkableNodes);
	}

	private void initializeStateOfBattle(final List<PlayerEntity> playerUnits, final List<AiEntity> aiUnits, Array<GridCell> unwalkableNodes) {
		for (final PlayerEntity unit : playerUnits) {
			stateOfBattle.addEntity(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY(), new HypotheticalUnit(unit.getEntityID(), true, unit.getHp(), unit.getEntityData().getMaxHP(),
					unit.getEntityData().getAttackRange(), unit.getEntityData().getAttackPower(), unit.getAp(), unit.getModifiers(), unit.getAbilities()));
			addModifiers(unit);
		}

		for (final AiEntity unit : aiUnits) {
			stateOfBattle.addEntity(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY(), new HypotheticalUnit(unit.getEntityID(), false, unit.getHp(), unit.getEntityData().getMaxHP(),
					unit.getEntityData().getAttackRange(), unit.getEntityData().getAttackPower(), unit.getAp(), unit.getModifiers(), unit.getAbilities()));
			addModifiers(unit);
		}

		for (final GridCell cell : unwalkableNodes) {
			stateOfBattle.get(cell.x, cell.y).setWalkable(false);
		}
	}

	private void addModifiers(final Entity unit) {
		for (final Modifier mod : unit.getModifiers()) {
			stateOfBattle.addModifierToUnit(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY(), mod);
		}
	}

	public void setUnitActive(Entity entity) {
		final PlayerEntity playerEntity = (PlayerEntity) entity;
		activeUnit.setFocused(false);
		activeUnit.setActive(false);

		activeUnit = playerEntity;
		activeUnit.setFocused(true);
		activeUnit.setActive(true);
	}

	public void swapTurn() {
		playerUnits.forEach(PlayerEntity::applyModifiers);
		aiUnits.forEach(AiEntity::applyModifiers);

		playerTurn = !playerTurn;

		if (!playerTurn) {
			final UnitTurn turn = aiTeamLeader.act(stateOfBattle);
			executeMoves(turn);
		}

		setCurrentBattleState(getSelectUnitBattleState());
		getCurrentBattleState().entry();

	}

	private void executeMoves(UnitTurn turn) {
		final int entityID = turn.getEntityID();
		for (final Move move : turn.getMoves()) {
			switch (move.getMoveType()) {
			case SPELL:
				final SpellMove spellMove = (SpellMove) move;
				final SpellBattlePhase spellState = (SpellBattlePhase) spellBattleState;
				spellState.executeSpellForAi(getEntityByID(entityID), spellMove.getAbility(), spellMove.getLocation());
				break;
			case MOVE:
				final Entity entityToMove = getEntityByID(entityID);
				entityToMove.move(MyPathFinder.getInstance().pathTowards(entityToMove.getCurrentPosition(), new TiledMapPosition().setPositionFromTiles(move.getLocation().x, move.getLocation().y), entityToMove.getAp()));
				break;
			case ATTACK:
				final Entity entityAttacking = getEntityByID(entityID);
				final Entity entityToAttack = getEntityByID(stateOfBattle.get(move.getLocation().x, move.getLocation().y).getUnit().getEntityId());
				entityAttacking.attack(entityToAttack);
				attackBattleState.notifyAudio(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.ATTACK_SOUND);
				break;
			default:
				// do nothing
			}
		}
	}

	private Entity getEntityByID(int entityID) {
		for (final PlayerEntity entity : playerUnits) {
			if (entity.getEntityID() == entityID) {
				return entity;
			}
		}

		for (final AiEntity entity : aiUnits) {
			if (entity.getEntityID() == entityID) {
				return entity;
			}
		}
		return null;
	}

	public void updateHp(Entity unit) {
		stateOfBattle.updateEntity(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY(), unit.getHp());
	}

	public void updateStateOfBattle(Entity unit, TiledMapPosition newPos) {
		final Point oldPoint = new Point(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY());
		final Point newPoint = new Point(newPos.getTileX(), newPos.getTileY());
		System.out.println("moving from : (" + oldPoint + ")   to " + newPoint);
		if (!oldPoint.equals(newPoint)) {
			stateOfBattle.moveUnitTo(unit, newPoint);
		}
	}

	public PlayerEntity getActiveUnit() {
		return activeUnit;
	}

	public List<PlayerEntity> getPlayerUnits() {
		return playerUnits;
	}

	public List<AiEntity> getAiUnits() {
		return aiUnits;
	}

	public boolean isPlayerTurn() {
		return playerTurn;
	}

	public void setPlayerTurn(boolean playerTurn) {
		this.playerTurn = playerTurn;
	}

	public Entity getLockedUnit() {
		return lockedUnit;
	}

	public void setLockedUnit(Entity lockedUnit) {
		this.lockedUnit = lockedUnit;
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
		return Stream.concat(playerUnits.stream(), aiUnits.stream()).collect(Collectors.toList());

	}
}
