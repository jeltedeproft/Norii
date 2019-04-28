package com.mygdx.game.Battle;

import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;

import Utility.TiledMapPosition;

public class DeploymentBattleState implements BattleStates{
	
	private BattleManager battlemanager;
	

	public DeploymentBattleState(BattleManager battlemanager){
		this.battlemanager = battlemanager;
	}

	@Override
	public void entry() {
		for(TiledMapPosition spawnPos : battlemanager.getSpawnPoints()) {
			ParticleMaker.addParticle(ParticleType.SPAWN, spawnPos);
		}
		
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
