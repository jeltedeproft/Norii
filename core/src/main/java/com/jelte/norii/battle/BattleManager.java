package com.jelte.norii.battle;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.jelte.norii.ai.AITeamLeader;
import com.jelte.norii.battle.battlePhase.ActionBattlePhase;
import com.jelte.norii.battle.battlePhase.AttackBattlePhase;
import com.jelte.norii.battle.battlePhase.BattlePhase;
import com.jelte.norii.battle.battlePhase.DeploymentBattlePhase;
import com.jelte.norii.battle.battlePhase.MovementBattlePhase;
import com.jelte.norii.battle.battlePhase.SelectUnitBattlePhase;
import com.jelte.norii.battle.battlePhase.SpellBattlePhase;
import com.jelte.norii.battleState.BattleState;
import com.jelte.norii.entities.AiEntity;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.PlayerEntity;
import com.jelte.norii.magic.Ability;
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
	private Ability currentSpell;
	private MyPathFinder pathFinder;
	private BattleState stateOfBattle;
	private boolean playerTurn;

	private List<PlayerEntity> playerUnits;
	private List<AiEntity> aiUnits;
	private Entity lockedUnit;

	public BattleManager(final List<PlayerEntity> playerUnits, final List<AiEntity> aiUnits, AITeamLeader aiTeamLeader, int width, int height) {
		initVariables(playerUnits, aiUnits, aiTeamLeader, width, height);

		deploymentBattleState = new DeploymentBattlePhase(this);
		selectUnitBattleState = new SelectUnitBattlePhase(this);
		movementBattleState = new MovementBattlePhase(this);
		attackBattleState = new AttackBattlePhase(this);
		spellBattleState = new SpellBattlePhase(this);
		actionBattleState = new ActionBattlePhase(this);

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
		stateOfBattle = new BattleState(width, height);
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
