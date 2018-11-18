package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class BattleManager {
	//this class organizes the flow of battle : unit ordering and action ordering
	private BattleState battlestate = BattleState.UNIT_PLACEMENT;
	private InputController _controller;
	
	//TO-DO : at the start of battle decide on unit ordering and initiate the inputprocessor
	
	public void InitBattleManager(Entity unit) {
		_controller = new InputController(unit);
		Gdx.input.setInputProcessor(_controller);
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
		//_controller.update(delta);
	}
	
	public void dispose() {
		_controller.dispose();
	}
}
