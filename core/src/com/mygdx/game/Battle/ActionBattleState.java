package com.mygdx.game.Battle;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.TiledMapActor;

public class ActionBattleState implements BattleState {
	private static final String TAG = ActionBattleState.class.getSimpleName();
	
	private BattleManager battlemanager;
	

	public ActionBattleState(BattleManager battlemanager){
		this.battlemanager = battlemanager;
	}

	@Override
	public void entry() {
		Gdx.app.debug(TAG, "entering action battle state");
		battlemanager.getActiveUnit().setInActionPhase(true);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
	}

	@Override
	public void exit() {
		Gdx.app.debug(TAG, "exiting action battle state");
		battlemanager.setCurrentBattleState(battlemanager.getWaitOpponentBattleState());
		battlemanager.getCurrentBattleState().entry();
	}

	@Override
	public void clickedOnTile(TiledMapActor actor) {
		Gdx.app.debug(TAG, "clicked on tile in actionbattlestate");
	}

	@Override
	public void clickedOnUnit(Entity entity) {
		// TODO Auto-generated method stub
		
	}

}
