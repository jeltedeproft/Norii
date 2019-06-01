package com.mygdx.game.Entities;

public interface EntityObserver {
	
    public static enum EntityCommand {
        IN_MOVEMENT,
        IN_ATTACK_PHASE,
        DIED
    }
    
	void onNotify(EntityCommand command, Entity unit);
}
