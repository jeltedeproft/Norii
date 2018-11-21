package com.mygdx.game;

import com.badlogic.gdx.InputMultiplexer;

public class BattleManager {
	//this class organizes the flow of battle : unit ordering and action ordering
	private BattleState battlestate = BattleState.UNIT_PLACEMENT;
	private InputController _controller;
	private InputMultiplexer multiplexer;
	private Entity activeUnit;
	
	//TO-DO : at the start of battle decide on unit ordering and initiate the inputprocessor
	public BattleManager(InputMultiplexer inputmultiplexer) {
		multiplexer = inputmultiplexer;
	}
	
	private void startNextPhase() {
		if (battlestate == BattleState.UNIT_PLACEMENT) {
			battlestate = BattleState.MOVEMENT_PHASE;
			_controller = new InputController(activeUnit);
			multiplexer.addProcessor(_controller);
		}
	}
	
	public BattleState getBattleState() {
		return battlestate;
	}
	
	public void setBattleState(BattleState battleState) {
		battlestate = battleState;
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
	}
	
	public void startDeploy(Entity[] playerSortedUnits) {
		for(int i=0; i<playerSortedUnits.length;i++) {
			Entity _currentUnit = playerSortedUnits[i];
			
		}
	}
}
