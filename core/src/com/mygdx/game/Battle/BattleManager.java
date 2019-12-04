package com.mygdx.game.Battle;

import com.badlogic.gdx.InputMultiplexer;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.InputController;

public class BattleManager {
	private static final String TAG = BattleManager.class.getSimpleName();
	
	private BattleState deploymentBattleState;
	private BattleState movementBattleState;
	private BattleState attackBattleState;
	private BattleState actionBattleState;
	private BattleState waitOpponentBattleState;
	private BattleState currentBattleState;
	
	private InputController controller;
	private InputMultiplexer multiplexer;
	private Entity activeUnit;
	private int activeUnitIndex;
	private int numberOfUnits;
	
	private Entity[] sortedUnits;
	
	public BattleManager(InputMultiplexer inputmultiplexer,Entity[] playerSortedUnits) {
		this.multiplexer = inputmultiplexer;
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
	
	public void giveControlToNextUnit() {
		activeUnit.setActive(false);
		activeUnit.setFocused(false);
		if(controller == null) {
			controller = new InputController(activeUnit);
			activeUnit.setFocused(true);
		}else {
			activeUnitIndex = (activeUnitIndex+1) % numberOfUnits;
			activeUnit = sortedUnits[activeUnitIndex];
			controller.changePlayer(activeUnit);
			activeUnit.setFocused(true);
		}
		
		activeUnit.setMp(activeUnit.getMaxMp());
		activeUnit.setActive(true);
	}
	
	public void updateController() {
		if(controller != null) {
			controller.update();
		}
	}
	
	public void dispose() {
		multiplexer.removeProcessor(controller);
		if(controller != null) {
			controller.dispose();
		}
	}

	public Entity getActiveUnit() {
		return activeUnit;
	}
	
	public Entity[] getUnits() {
		return sortedUnits;
	}

	public InputController getController() {
		return controller;
	}

	public void setController(InputController controller) {
		this.controller = controller;
	}

	public InputMultiplexer getMultiplexer() {
		return multiplexer;
	}

	public void setMultiplexer(InputMultiplexer multiplexer) {
		this.multiplexer = multiplexer;
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
