package com.mygdx.game.Battle;

import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.TiledMapActor;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;

public class AttackBattleState implements BattleState{
	private static final String TAG = AttackBattleState.class.getSimpleName();
	
	private BattleManager battlemanager;

	public AttackBattleState(BattleManager battlemanager){
		this.battlemanager = battlemanager;
	}
	
	@Override
	public void clickedOnTile(TiledMapActor actor) {
		ParticleMaker.deactivateAllParticlesOfType(ParticleType.ATTACK);
		exit();
	}
	
	@Override
	public void clickedOnUnit(Entity entity) {
		possibleAttack(entity);
		ParticleMaker.deactivateAllParticlesOfType(ParticleType.ATTACK);
	}

	@Override
	public void entry() {

	}

	@Override
	public void update() {
		
	}

	@Override
	public void exit() {
		battlemanager.setCurrentBattleState(battlemanager.getActionBattleState());
		battlemanager.getCurrentBattleState().entry();
	}
	
    private void possibleAttack(Entity entity) {  
    	Entity currentUnit = battlemanager.getActiveUnit();
    	Entity possibleTarget = getPossibleTarget(entity);
    	if(possibleTarget != null && !(possibleTarget.getName().equalsIgnoreCase(currentUnit.getName()))) {
    		currentUnit.attack(possibleTarget);
    	}
    	this.exit();
    }
    
    private Entity getPossibleTarget(Entity targetEntity){
    	for(Entity entity : battlemanager.getUnits()) {
    		if((entity.getCurrentPosition().getTileX() == targetEntity.getCurrentPosition().getTileX()) && (entity.getCurrentPosition().getTileY() == targetEntity.getCurrentPosition().getTileY())) {
    			return entity;
    		}
    	}
    	return null;
    }
}
