package com.mygdx.game.Battle;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.Entities.Entity;
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
		unitsSortedByInitiative[0].setInDeploymentPhase(true);
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
		battlemanager.giveControlToNextUnit();
		battlemanager.setCurrentBattleState(battlemanager.getActionBattleState());
		battlemanager.getCurrentBattleState().entry();
	}

	@Override
	public void clickedOnTile(TiledMapActor actor) {
		deployUnit(actor);
	}
	
    private void deployUnit(TiledMapActor actor){
        if(actor.getIsFreeSpawn()) {
        	TiledMapPosition newPosition = actor.getActorPos();
        	
    		if((unitsSortedByInitiative != null) && (deployingUnitNumber < unitsSortedByInitiative.length)) {
    			deployUnit(newPosition);
    			actor.setIsFreeSpawn(false); 
    			ParticleMaker.deactivateParticle(ParticleMaker.getParticle(ParticleType.SPAWN, newPosition));
    			nextDeployment();
    		}else {
    			Gdx.app.debug(TAG, "can't deploy unit, units is null or activeunitindex is > the length of units");
    		}
        }
    }

	private void deployUnit(TiledMapPosition newPosition) {
		Entity unitToDeploy = unitsSortedByInitiative[deployingUnitNumber];
		unitToDeploy.setInDeploymentPhase(false);
		initiateUnitInBattle(unitToDeploy,newPosition);
	}

	private void nextDeployment() {
		deployingUnitNumber++;
		if(deployingUnitNumber < unitsSortedByInitiative.length) {
			unitsSortedByInitiative[deployingUnitNumber].setInDeploymentPhase(true);
		}
		checkIfLastUnit();
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

	@Override
	public void clickedOnUnit(Entity entity) {
		
	}
}
