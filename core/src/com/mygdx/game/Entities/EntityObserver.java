package com.mygdx.game.Entities;

public interface EntityObserver {
	
    public enum EntityCommand {
        IN_MOVEMENT,
        IN_ATTACK_PHASE,
        IN_ACTION_PHASE,
        CLICKED,
        SKIP,
        DIED
    }
    
	void onNotify(EntityCommand command, Entity unit);
}
