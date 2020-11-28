package com.jelte.norii.battle;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.jelte.norii.ai.AITeamLeader;
import com.jelte.norii.battle.battleStates.ActionBattleState;
import com.jelte.norii.battle.battleStates.AttackBattleState;
import com.jelte.norii.battle.battleStates.BattleState;
import com.jelte.norii.battle.battleStates.DeploymentBattleState;
import com.jelte.norii.battle.battleStates.MovementBattleState;
import com.jelte.norii.battle.battleStates.SelectUnitBattleState;
import com.jelte.norii.battle.battleStates.SpellBattleState;
import com.jelte.norii.battle.battleStates.StateOfBattle;
import com.jelte.norii.entities.AiEntity;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.PlayerEntity;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.map.MyPathFinder;
import com.jelte.norii.utility.TiledMapPosition;

public class BattleManager {
	private BattleState deploymentBattleState;
	private BattleState selectUnitBattleState;
	private BattleState movementBattleState;
	private BattleState attackBattleState;
	private BattleState spellBattleState;
	private BattleState actionBattleState;
	private BattleState currentBattleState;

	private PlayerEntity activeUnit;
	private AITeamLeader aiTeamLeader;
	private Ability currentSpell;
	private MyPathFinder pathFinder;
	private StateOfBattle stateOfBattle;
	private boolean playerTurn;

	private List<PlayerEntity> playerUnits;
	private List<AiEntity> aiUnits;
	private Entity lockedUnit;

	public BattleManager(final List<PlayerEntity> playerUnits, final List<AiEntity> aiUnits, AITeamLeader aiTeamLeader, int width, int height) {
		initVariables(playerUnits, aiUnits, aiTeamLeader, width, height);

		deploymentBattleState = new DeploymentBattleState(this);
		selectUnitBattleState = new SelectUnitBattleState(this);
		movementBattleState = new MovementBattleState(this);
		attackBattleState = new AttackBattleState(this);
		spellBattleState = new SpellBattleState(this);
		actionBattleState = new ActionBattleState(this);

		currentBattleState = deploymentBattleState;
		currentBattleState.entry();
	}

	private void initVariables(final List<PlayerEntity> playerUnits, final List<AiEntity> aiUnits, AITeamLeader aiTeamLeader, int width, int height) {
		this.playerUnits = playerUnits;
		this.aiUnits = aiUnits;
		this.aiTeamLeader = aiTeamLeader;
		activeUnit = playerUnits.get(0);
		playerTurn = true;
		lockedUnit = null;
		stateOfBattle = new StateOfBattle(width, height);
		initializeStateOfBattle(playerUnits, aiUnits);
	}

	private void initializeStateOfBattle(final List<PlayerEntity> playerUnits, final List<AiEntity> aiUnits) {
		for (final PlayerEntity unit : playerUnits) {
			stateOfBattle.set(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY(), unit.getHp() * (-1));
		}

		for (final AiEntity unit : aiUnits) {
			stateOfBattle.set(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY(), unit.getHp());
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
			aiTeamLeader.act(playerUnits, aiUnits, stateOfBattle);
		}

		setCurrentBattleState(getSelectUnitBattleState());
		getCurrentBattleState().entry();

	}

	public void updateStateOfBattlePos(Entity unit) {
		int factor;
		if (unit.isPlayerUnit()) {
			factor = -1;
		} else {
			factor = 1;
		}
		stateOfBattle.set(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY(), unit.getHp() * factor);
	}

	public void updateStateOfBattlePos(Entity unit, TiledMapPosition oldPos) {
		stateOfBattle.set(oldPos.getTileX(), oldPos.getTileY(), 0);
		updateStateOfBattlePos(unit);
	}

	public void setPathFinder(MyPathFinder myPathFinder) {
		this.pathFinder = myPathFinder;
	}

	public MyPathFinder getPathFinder() {
		return pathFinder;
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

	public Ability getCurrentSpell() {
		return currentSpell;
	}

	public void setCurrentSpell(final Ability currentSpell) {
		this.currentSpell = currentSpell;
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

	public BattleState getDeploymentBattleState() {
		return deploymentBattleState;
	}

	public void setDeploymentBattleState(final BattleState deploymentBattleState) {
		this.deploymentBattleState = deploymentBattleState;
	}

	public BattleState getMovementBattleState() {
		return movementBattleState;
	}

	public void setMovementBattleState(final BattleState movementBattleState) {
		this.movementBattleState = movementBattleState;
	}

	public BattleState getAttackBattleState() {
		return attackBattleState;
	}

	public void setAttackBattleState(final BattleState attackBattleState) {
		this.attackBattleState = attackBattleState;
	}

	public BattleState getSpellBattleState() {
		return spellBattleState;
	}

	public void setSpellBattleState(final BattleState spellBattleState) {
		this.spellBattleState = spellBattleState;
	}

	public BattleState getActionBattleState() {
		return actionBattleState;
	}

	public void setActionBattleState(final BattleState actionBattleState) {
		this.actionBattleState = actionBattleState;
	}

	public BattleState getCurrentBattleState() {
		return currentBattleState;
	}

	public void setCurrentBattleState(final BattleState currentBattleState) {
		this.currentBattleState = currentBattleState;
	}

	public BattleState getSelectUnitBattleState() {
		return selectUnitBattleState;
	}

	public void setSelectUnitBattleState(BattleState selectUnitBattleState) {
		this.selectUnitBattleState = selectUnitBattleState;
	}

	public List<Entity> getUnits() {
		return Stream.concat(playerUnits.stream(), aiUnits.stream()).collect(Collectors.toList());

	}
}
