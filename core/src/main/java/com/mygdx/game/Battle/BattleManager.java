package com.mygdx.game.Battle;

import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Magic.Ability;
import com.mygdx.game.Map.MyPathFinder;

public class BattleManager {
	private BattleState deploymentBattleState;
	private BattleState movementBattleState;
	private BattleState attackBattleState;
	private BattleState spellBattleState;
	private BattleState actionBattleState;
	private BattleState currentBattleState;

	private Entity activeUnit;
	private int activeUnitIndex;
	private final int numberOfUnits;
	private Ability currentSpell;
	private MyPathFinder pathFinder;

	private final Entity[] sortedUnits;

	public BattleManager(final Entity[] allSortedUnits) {
		sortedUnits = allSortedUnits;
		activeUnitIndex = 0;
		numberOfUnits = sortedUnits.length;
		activeUnit = allSortedUnits[activeUnitIndex];

		deploymentBattleState = new DeploymentBattleState(this);
		movementBattleState = new MovementBattleState(this);
		attackBattleState = new AttackBattleState(this);
		spellBattleState = new SpellBattleState(this);
		actionBattleState = new ActionBattleState(this);

		currentBattleState = deploymentBattleState;
		currentBattleState.entry();

	}

	public void nextUnitActive() {
		activeUnit.setActive(false);
		activeUnit.setFocused(false);

		activeUnitIndex = (activeUnitIndex + 1) % numberOfUnits;
		activeUnit = sortedUnits[activeUnitIndex];

		startUnitTurn();
	}

	public void startUnitTurn() {
		activeUnit.setFocused(true);
		activeUnit.setAp(activeUnit.getEntityData().getMaxAP());
		activeUnit.setActive(true);
		activeUnit.applyModifiers();
	}

	public void setPathFinder(MyPathFinder myPathFinder) {
		this.pathFinder = myPathFinder;
	}

	public MyPathFinder getPathFinder() {
		return pathFinder;
	}

	public Entity getActiveUnit() {
		return activeUnit;
	}

	public Entity[] getUnits() {
		return sortedUnits;
	}

	public Ability getCurrentSpell() {
		return currentSpell;
	}

	public void setCurrentSpell(final Ability currentSpell) {
		this.currentSpell = currentSpell;
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
}
