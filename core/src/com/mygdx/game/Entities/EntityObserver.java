package com.mygdx.game.Entities;

public interface EntityObserver {
	
    public enum EntityCommand {
        IN_MOVEMENT,
        IN_ATTACK_PHASE,
        UNIT_ACTIVE,
        CLICKED,
        SKIP,
        DIED
    }
    
	void onEntityNotify(EntityCommand command, Entity unit);
}
