package com.mygdx.game.Battle;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.Map.TiledMapActor;

import Utility.TiledMapPosition;

public class ActionBattleState implements BattleState {
	private static final String TAG = ActionBattleState.class.getSimpleName();
	
	private BattleManager battlemanager;
	

	public ActionBattleState(BattleManager battlemanager){
		this.battlemanager = battlemanager;
	}

	@Override
	public void entry() {
		Gdx.app.debug(TAG, "deployment finished, entering action selection of the first unit (highest initiative)");
		battlemanager.setCurrentBattleState(battlemanager.getWaitOpponentBattleState());
		battlemanager.getWaitOpponentBattleState().entry();
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clickedOnTile(TiledMapActor actor) {
		// TODO Auto-generated method stub
		
	}

}
