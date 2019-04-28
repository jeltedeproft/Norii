package com.mygdx.game.Battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.InputController;

public class MovementBattleState implements BattleStates{
	private static final String TAG = MovementBattleState.class.getSimpleName();
	
	private BattleManager battlemanager;
	

	public MovementBattleState(BattleManager battlemanager){
		this.battlemanager = battlemanager;
	}

	@Override
	public void entry() {
		Gdx.app.debug(TAG, "deployment finished, entering movement phase of the first unit (highest initiative)");
		Entity currentlyActiveUnit = battlemanager.getActiveUnit();
		InputController inputcontroller = battlemanager.get_controller();
		InputMultiplexer multiplexer = battlemanager.getMultiplexer();
		inputcontroller = new InputController(currentlyActiveUnit);
		multiplexer.addProcessor(inputcontroller);
		
		//activate actions UI
		currentlyActiveUnit.setActive(true);
		currentlyActiveUnit.getActionsui().update();
		
		//unload spawn particles
		//Utility.unloadAsset("particles/spawn_effect");
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exit() {
		battlemanager.setCurrentBattleState(battlemanager.getActionBattleState());
		battlemanager.getCurrentBattleState().entry();
		
	}

}
