package com.mygdx.game.Battle;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.InputController;
import com.mygdx.game.Map.TiledMapActor;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;

import Utility.TiledMapPosition;

public class DeploymentBattleState implements BattleState{
	private static final String TAG = DeploymentBattleState.class.getSimpleName();
	
	private BattleManager battlemanager;
	private int deployingUnitNumber;
	private Entity[] unitsSortedByInitiative;
	

	public DeploymentBattleState(BattleManager battlemanager){
		this.battlemanager = battlemanager;
		this.deployingUnitNumber = 0;
		this.unitsSortedByInitiative = battlemanager.getUnits();
	}

	@Override
	public void entry() {

	}

	@Override
	public void update() {

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
        	
    		if((unitsSortedByInitiative != null) && (deployingUnitNumber < unitsSortedByInitiative.length)) {
    			initiateUnitInBattle(unitsSortedByInitiative[deployingUnitNumber],newPosition);
    			ParticleMaker.deactivateParticle(ParticleMaker.getParticle(ParticleType.SPAWN, newPosition));
    			actor.setIsFreeSpawn(false);
    	
    			deployingUnitNumber++;
    			checkIfLastUnit();
    		}else {
    			Gdx.app.debug(TAG, "can't deploy unit, units is null or activeunitindex is > the length of units");
    		}
        }
    }
    
	private void initiateUnitInBattle(Entity unit, TiledMapPosition pos) {
		unit.setInBattle(true);
		unit.setCurrentPosition(pos);
	}
	
	private void checkIfLastUnit() {
		if (deployingUnitNumber >= unitsSortedByInitiative.length) {
			exit();
		}
	}
}
