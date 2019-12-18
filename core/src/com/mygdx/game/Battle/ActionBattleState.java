package com.mygdx.game.Battle;

public class ActionBattleState extends BattleState {
	private BattleManager battlemanager;
	

	public ActionBattleState(BattleManager battlemanager){
		this.battlemanager = battlemanager;
	}

	@Override
	public void entry() {
		battlemanager.getActiveUnit().setInActionPhase(true);
	}

	@Override
	public void exit() {
		battlemanager.setCurrentBattleState(battlemanager.getWaitOpponentBattleState());
		battlemanager.getCurrentBattleState().entry();
	}
}
