package com.mygdx.game.Battle;

import java.util.ArrayList;

import com.badlogic.gdx.InputMultiplexer;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.InputController;

import Utility.TiledMapPosition;

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
	
	private Entity[] units;
	
	public BattleManager(InputMultiplexer inputmultiplexer,Entity[] playerSortedUnits) {
		this.multiplexer = inputmultiplexer;
		this.units = playerSortedUnits;
		this.activeUnit = playerSortedUnits[0];
		
		this.deploymentBattleState = new DeploymentBattleState(this);
		this.movementBattleState = new MovementBattleState(this);
		this.actionBattleState = new ActionBattleState(this);
		this.waitOpponentBattleState = new WaitOpponentBattleState(this);
		
		this.currentBattleState = deploymentBattleState;
		this.currentBattleState.entry();
	}
	
	public void giveControlToUnit(Entity unit) {
		_controller.ChangePlayer(unit);
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

	public void setActiveUnit(Entity activeUnit) {
		this.activeUnit = activeUnit;
		giveControlToUnit(activeUnit);
	}
	
	public void setSpawnPoints(ArrayList<TiledMapPosition> spawnPoints2) {
		this.deploymentBattleState.se = spawnPoints2;
	}
	
	public Entity[] getUnits() {
		return units;
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
