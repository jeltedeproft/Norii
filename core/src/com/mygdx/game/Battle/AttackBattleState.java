package com.mygdx.game.Battle;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.TiledMapActor;

public class AttackBattleState implements BattleState{
	private static final String TAG = AttackBattleState.class.getSimpleName();
	
	private BattleManager battlemanager;
	private Entity currentUnit;
	

	public AttackBattleState(BattleManager battlemanager){
		this.battlemanager = battlemanager;
	}
	
	public void clickedOnTile(TiledMapActor actor) {
		possibleAttack(actor);
	}

	@Override
	public void entry() {
		Gdx.app.debug(TAG, "entering attack phase");
	}

	@Override
	public void update() {
		
	}

	@Override
	public void exit() {
		Gdx.app.debug(TAG, "exiting attack phase");
		battlemanager.setCurrentBattleState(battlemanager.getActionBattleState());
		battlemanager.getCurrentBattleState().entry();
	}
	
    private void possibleAttack(TiledMapActor actor) {  	
    	Entity possibleTarget = getPossibleTarget(actor);
    	if(possibleTarget != null) {
    		currentUnit.attack(possibleTarget);
    	}
    	this.exit();
    }
    
    private Entity getPossibleTarget(TiledMapActor actor){
    	for(Entity entity : battlemanager.getUnits()) {
    		if((entity.getCurrentPosition().getTileX() == actor.getActorPos().getTileX()) && (entity.getCurrentPosition().getTileY() == actor.getActorPos().getTileY())) {
    			return entity;
    		}
    	}
    	return null;
    }
}
