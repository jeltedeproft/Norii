package com.jelte.norii.battle;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.AITeamLeader;
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
import com.jelte.norii.battle.battleState.SpellMove;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.Player;
import com.jelte.norii.magic.Ability;
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
	private AITeamLeader aiTeamLeader;
	private BattleState activeBattleState;
	private boolean playerTurn;
	private Entity lockedUnit;
	private BattleScreen battleScreen;

	public BattleManager(AITeamLeader aiTeamLeader, int width, int height, Array<GridCell> unwalkableNodes, BattleScreen battleScreen) {
		initVariables(aiTeamLeader, width, height, unwalkableNodes, battleScreen);

		deploymentBattleState = new DeploymentBattlePhase(this);
		selectUnitBattleState = new SelectUnitBattlePhase(this);
		movementBattleState = new MovementBattlePhase(this);
		attackBattleState = new AttackBattlePhase(this);
		spellBattleState = new SpellBattlePhase(this);
		actionBattleState = new ActionBattlePhase(this);

		currentBattleState = deploymentBattleState;
		currentBattleState.entry();
	}

	private void initVariables(AITeamLeader aiTeamLeader, int width, int height, Array<GridCell> unwalkableNodes, BattleScreen battleScreen) {
		this.battleScreen = battleScreen;
		this.aiTeamLeader = aiTeamLeader;
		Player.getInstance().setBattleManager(this);
		aiTeamLeader.setBattleManager(this);
		activeUnit = Player.getInstance().getTeam().get(0);
		playerTurn = true;
		lockedUnit = null;
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
	}

	public void setUnitActive(Entity entity) {
		activeUnit.setFocused(false);
		activeUnit.getVisualComponent().setActive(false);
		sendMessageToBattleScreen(MessageToBattleScreen.HIDE_ACTIONS, entity);

		activeUnit = entity;
		activeUnit.setFocused(true);
		activeUnit.getVisualComponent().setActive(true);
		sendMessageToBattleScreen(MessageToBattleScreen.UNIT_ACTIVE, entity);

	}

	public void sendMessageToBattleScreen(MessageToBattleScreen message, Entity entity) {
		switch (message) {
		case CLICKED:
			getCurrentBattleState().clickedOnUnit(entity);
			break;
		case AI_FINISHED_TURN:
			swapTurn();
			break;
		case UPDATE_POS:
			updateStateOfBattle(entity, entity.getCurrentPosition());
			break;
		case UNIT_DIED:
			removeUnit(entity);
			break;
		default:
			battleScreen.messageFromBattleManager(message, entity);
			break;
		}
	}

	public void swapTurn() {
		Player.getInstance().applyModifiers();
		aiTeamLeader.applyModifiers();

		playerTurn = !playerTurn;

		if (!playerTurn) {
			final BattleState newState = aiTeamLeader.act(activeBattleState);
			setStateOfBattle(newState);
			executeMoves(newState.getTurn());
		}

		setCurrentBattleState(getSelectUnitBattleState());
		getCurrentBattleState().entry();

	}

	public void setStateOfBattle(BattleState stateOfBattle) {
		this.activeBattleState = stateOfBattle;
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
				final List<GridCell> path = MyPathFinder.getInstance().pathTowards(entityToMove.getCurrentPosition(), new TiledMapPosition().setPositionFromTiles(move.getLocation().x, move.getLocation().y), entityToMove.getAp());
				activeBattleState.moveUnitTo(entityToMove, new MyPoint(move.getLocation().x, move.getLocation().y));
				entityToMove.move(path);
				break;
			case ATTACK:
				final Entity entityAttacking = getEntityByID(entityID);
				final Entity entityToAttack = getEntityByID(activeBattleState.get(move.getLocation().x, move.getLocation().y).getUnit().getEntityID());
				entityAttacking.attack(entityToAttack);
				sendMessageToBattleScreen(MessageToBattleScreen.UPDATE_UI, entityToAttack);
				AudioManager.getInstance().onNotify(AudioCommand.SOUND_PLAY_ONCE, AudioTypeEvent.ATTACK_SOUND);
				break;
			case DUMMY:
				// do nothing
			default:
				// do nothing
			}
			checkVictory();
		}
		final Entity entity = getEntityByID(entityID);
		entity.endTurn();
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

		for (final Entity entity : aiTeamLeader.getTeam()) {
			if (entity.getEntityID() == entityID) {
				return entity;
			}
		}
		return null;
	}

	private void removeUnit(Entity unit) {
		executeOnDeathEffect(unit);
		Player.getInstance().removeUnit(unit);
		aiTeamLeader.removeUnit(unit);
		activeBattleState.removeUnit(unit);
	}

	@SuppressWarnings("incomplete-switch")
	private void executeOnDeathEffect(Entity unit) {
		switch (unit.getEntityType()) {
		case BOOMERANG:
			for (final Ability ability : unit.getAbilities()) {
				if (ability.getTargetLocation() != null) {
					final SpellBattlePhase spellState = (SpellBattlePhase) spellBattleState;
					spellState.executeSpellForAi(unit, ability, ability.getTargetLocation());
				}
			}
		}
	}

	public void updateStateOfBattle(Entity unit, TiledMapPosition newPos) {
		final MyPoint oldMyPoint = new MyPoint(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY());
		final MyPoint newMyPoint = new MyPoint(newPos.getTileX(), newPos.getTileY());
		if (!oldMyPoint.equals(newMyPoint)) {
			activeBattleState.moveUnitTo(unit, newMyPoint);
		}
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

	public List<Entity> getAiUnits() {
		return aiTeamLeader.getTeam();
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
		return Stream.concat(Player.getInstance().getTeam().stream(), aiTeamLeader.getTeam().stream()).collect(Collectors.toList());
	}

}
