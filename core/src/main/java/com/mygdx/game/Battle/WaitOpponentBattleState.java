package com.mygdx.game.Battle;

public class WaitOpponentBattleState extends BattleState {
	
	private BattleManager battlemanager;
	

	public WaitOpponentBattleState(BattleManager battlemanager){
		this.battlemanager = battlemanager;
	}

	@Override
	public void entry() {
		battlemanager.nextUnitActive();
		battlemanager.setCurrentBattleState(battlemanager.getActionBattleState());
		battlemanager.getCurrentBattleState().entry();
	}

	@Override
	public void exit() {
		
	}
}
