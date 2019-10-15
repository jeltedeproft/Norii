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
		ParticleMaker.deactivateAllParticlesOfType(ParticleType.SPAWN);
		battlemanager.setCurrentBattleState(battlemanager.getMovementBattleState());
		battlemanager.getMovementBattleState().entry();
	}

	@Override
	public void clickedOnTile(TiledMapActor actor) {
		deployUnit(actor);
	}
	
    private void deployUnit(TiledMapActor actor){
        if(actor.getIsFreeSpawn()) {
        	TiledMapPosition newPosition = actor.getActorPos();
        	
    		if((unitsSortedByInitiative != null) && (deployingUnitNumber < unitsSortedByInitiative.length)) {
    			//deploy unit
    			Entity unitToDeploy = unitsSortedByInitiative[deployingUnitNumber];
    			initiateUnitInBattle(unitToDeploy,newPosition);
    			
    			//adjust tile
    			actor.setIsFreeSpawn(false);
    			
    			// Set actor position not walkable
    			int centreX = unitToDeploy.getCurrentPosition().getTileX();
    	    	int centreY = unitToDeploy.getCurrentPosition().getTileY();
    			actor.getTiledMap().get_navLayer().getCell(centreX,centreY).setWalkable(false);
    			
    			//update for next
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
