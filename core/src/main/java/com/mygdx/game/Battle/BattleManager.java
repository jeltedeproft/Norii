package com.mygdx.game.Battle;


import com.mygdx.game.Entities.Entity;

public class BattleManager {
	private BattleState deploymentBattleState;
	private BattleState movementBattleState;
	private BattleState attackBattleState;
	private BattleState actionBattleState;
	private BattleState waitOpponentBattleState;
	private BattleState currentBattleState;
	
	private Entity activeUnit;
	private int activeUnitIndex;
	private int numberOfUnits;
	
	private Entity[] sortedUnits;
	
	public BattleManager(Entity[] playerSortedUnits) {
		this.sortedUnits = playerSortedUnits;
		this.activeUnitIndex = 0;
		this.numberOfUnits = sortedUnits.length;
		this.activeUnit = playerSortedUnits[activeUnitIndex];
		
		this.deploymentBattleState = new DeploymentBattleState(this);
		this.movementBattleState = new MovementBattleState(this);
		this.attackBattleState = new AttackBattleState(this);
		this.actionBattleState = new ActionBattleState(this);
		this.waitOpponentBattleState = new WaitOpponentBattleState(this);
		
		this.currentBattleState = deploymentBattleState;
		this.currentBattleState.entry();
		
	}
	
	public void nextUnitActive() {
		activeUnit.setActive(false);
		activeUnit.setFocused(false);
		
		activeUnitIndex = (activeUnitIndex+1) % numberOfUnits;
		activeUnit = sortedUnits[activeUnitIndex];
		
		startUnitTurn();
	}
	
	public void startUnitTurn() {
		activeUnit.setFocused(true);
		activeUnit.setAp(activeUnit.getMaxAp());
		activeUnit.setActive(true);
	}

	public Entity getActiveUnit() {
		return activeUnit;
	}
	
	public Entity[] getUnits() {
		return sortedUnits;
	}

	public BattleState getDeploymentBattleState() {
		return deploymentBattleState;
	}

	public void setDeploymentBattleState(BattleState deploymentBattleState) {
		this.deploymentBattleState = deploymentBattleState;
	}

	public BattleState getMovementBattleState() {
		return movementBattleState;
	}

	public void setMovementBattleState(BattleState movementBattleState) {
		this.movementBattleState = movementBattleState;
	}
	
	public BattleState getAttackBattleState() {
		return attackBattleState;
	}
	
	public void setAttackBattleState(BattleState attackBattleState) {
		this.attackBattleState = attackBattleState;
	}

	public BattleState getActionBattleState() {
		return actionBattleState;
	}

	public void setActionBattleState(BattleState actionBattleState) {
		this.actionBattleState = actionBattleState;
	}

	public BattleState getWaitOpponentBattleState() {
		return waitOpponentBattleState;
	}

	public void setWaitOpponentBattleState(BattleState waitOpponentBattleState) {
		this.waitOpponentBattleState = waitOpponentBattleState;
	}

	public BattleState getCurrentBattleState() {
		return currentBattleState;
	}

	public void setCurrentBattleState(BattleState currentBattleState) {
		this.currentBattleState = currentBattleState;
	}
	
	
}
