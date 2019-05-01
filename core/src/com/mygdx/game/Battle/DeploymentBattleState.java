package com.mygdx.game.Battle;

public class DeploymentBattleState implements BattleStates{
	private static final String TAG = DeploymentBattleState.class.getSimpleName();
	
	private BattleManager battlemanager;
	

	public DeploymentBattleState(BattleManager battlemanager){
		this.battlemanager = battlemanager;
	}

	@Override
	public void entry() {

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exit() {
		battlemanager.setCurrentBattleState(battlemanager.getMovementBattleState());
		battlemanager.getCurrentBattleState().entry();
		
	}

}
