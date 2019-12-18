package com.mygdx.game.Battle;

import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.TiledMapActor;

public class ActionBattleState implements BattleState {
	private BattleManager battlemanager;
	

	public ActionBattleState(BattleManager battlemanager){
		this.battlemanager = battlemanager;
	}

	@Override
	public void entry() {
		battlemanager.getActiveUnit().setInActionPhase(true);
	}

	@Override
	public void update() {
		
	}

	@Override
	public void exit() {
		battlemanager.setCurrentBattleState(battlemanager.getWaitOpponentBattleState());
		battlemanager.getCurrentBattleState().entry();
	}

	@Override
	public void clickedOnTile(TiledMapActor actor) {

	}

	@Override
	public void clickedOnUnit(Entity entity) {
		
	}

	@Override
	public void keyPressed(int key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void buttonPressed(int button) {
		// TODO Auto-generated method stub
		
	}

}
