package com.mygdx.game.Battle;

import com.badlogic.gdx.Gdx;

public class ActionBattleState implements BattleStates {
	private static final String TAG = ActionBattleState.class.getSimpleName();
	
	private BattleManager battlemanager;
	

	public ActionBattleState(BattleManager battlemanager){
		this.battlemanager = battlemanager;
	}

	@Override
	public void entry() {
		Gdx.app.debug(TAG, "deployment finished, entering action selection of the first unit (highest initiative)");

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub

	}

}
