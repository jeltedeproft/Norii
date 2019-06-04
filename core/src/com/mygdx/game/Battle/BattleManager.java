package com.mygdx.game.Battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.InputController;

public class BattleManager {
	private static final String TAG = BattleManager.class.getSimpleName();
	
	private BattleState deploymentBattleState;
	private BattleState movementBattleState;
	private BattleState actionBattleState;
	private BattleState waitOpponentBattleState;
	private BattleState currentBattleState;
	
	private InputController _controller;
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
		this.actionBattleState = new ActionBattleState(this);
		this.waitOpponentBattleState = new WaitOpponentBattleState(this);
		
		this.currentBattleState = deploymentBattleState;
		this.currentBattleState.entry();
	}
	
	public void giveControlToNextUnit() {
		activeUnit.setActive(false);
		if(_controller == null) {
			_controller = new InputController(activeUnit);
		}else {
			activeUnitIndex = (activeUnitIndex+1) % numberOfUnits;
			activeUnit = sortedUnits[activeUnitIndex];
			_controller.ChangePlayer(activeUnit);
			
		}
		//activate actions UI
		activeUnit.setActive(true);
	}
	
	public void updateController(float delta) {
		if(_controller != null) {
			_controller.update(delta);
		}
	}
	
	public void dispose() {
		multiplexer.removeProcessor(_controller);
		if(_controller != null) {
			_controller.dispose();
		}
	}

	public Entity getActiveUnit() {
		return activeUnit;
	}
	
	public Entity[] getUnits() {
		return sortedUnits;
	}

	public InputController get_controller() {
		return _controller;
	}

	public void set_controller(InputController _controller) {
		this._controller = _controller;
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
