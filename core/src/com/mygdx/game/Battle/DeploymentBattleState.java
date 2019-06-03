package com.mygdx.game.Battle;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.TiledMapActor;
import com.mygdx.game.Map.TiledMapStage;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;

import Utility.TiledMapPosition;

public class DeploymentBattleState implements BattleState{
	private static final String TAG = DeploymentBattleState.class.getSimpleName();
	
	private BattleManager battlemanager;
	private int deployingUnitNumber;
	private ArrayList<TiledMapPosition> spawnPoints;
	

	public DeploymentBattleState(BattleManager battlemanager){
		this.battlemanager = battlemanager;
		this.deployingUnitNumber = 0;
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
		battlemanager.getMovementBattleState().entry();
	}

	@Override
	public void clickedOnTile(TiledMapActor actor) {
		deployUnit(actor);
	}
	
    private void deployUnit(TiledMapActor actor){
        if(actor.getIsFreeSpawn()) {
        	TiledMapPosition newPosition = new TiledMapPosition(actor.getX(),actor.getY());
        	TiledMapStage stage = (TiledMapStage) actor.getStage();
        	Entity[] units = stage.getBattlemanager().getUnits();
        	
    		if((units != null) && deployingUnitNumber < units.length) {
    			initiateUnitInBattle(newPosition);
    			ParticleMaker.deactivateParticle(ParticleMaker.getParticle(ParticleType.SPAWN, pos));
    			removeSpawnPoint(newPosition);
    	
    			deployingUnitNumber++;
    			checkIfLastUnit();
    		}else {
    			Gdx.app.debug(TAG, "can't deploy unit, units is null or activeunitindex is > the length of units");
    		}
    		
        	actor.setIsFreeSpawn(false);
        }
    }
    
	private void initiateUnitInBattle(TiledMapPosition pos) {
		units[deployingUnitNumber].setInBattle(true);
		units[deployingUnitNumber].setCurrentPosition(pos);
	}
	
	private void checkIfLastUnit() {
		if (deployingUnitNumber >= units.length) {
			currentBattleState.exit();
		}
	}
	
	private void removeSpawnPoint(TiledMapPosition pos) {
		for(int i = 0; i < spawnPoints.size(); i++) {
			if((spawnPoints.get(i).isEqualTo(pos))) {
				spawnPoints.remove(i);
			}
		}
	}
	
	public ArrayList<TiledMapPosition> getSpawnPoints() {
		return spawnPoints;
	}

	public void setSpawnPoints(ArrayList<TiledMapPosition> spawnPoints2) {
		this.spawnPoints = spawnPoints2;
	}
}
