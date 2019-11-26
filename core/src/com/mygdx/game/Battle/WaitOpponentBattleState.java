package com.mygdx.game.Battle;

import com.mygdx.game.Map.TiledMapActor;

import Utility.TiledMapPosition;

public class WaitOpponentBattleState implements BattleState {
	
	private BattleManager battlemanager;
	

	public WaitOpponentBattleState(BattleManager battlemanager){
		this.battlemanager = battlemanager;
	}

	@Override
	public void entry() {
		battlemanager.setCurrentBattleState(battlemanager.getMovementBattleState());
		battlemanager.getMovementBattleState().entry();
	}

	@Override
	public void update() {
		

	}

	@Override
	public void exit() {
		

	}

	@Override
	public void clickedOnTile(TiledMapActor actor) {
		
		
	}

}
