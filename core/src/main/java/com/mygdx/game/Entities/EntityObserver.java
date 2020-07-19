package com.mygdx.game.Entities;

import com.mygdx.game.Magic.Ability;

public interface EntityObserver {

	public enum EntityCommand {
		IN_MOVEMENT, IN_ATTACK_PHASE, IN_SPELL_PHASE, UNIT_ACTIVE, CLICKED, SKIP, AI_ACT, DIED, INIT_POSIBILITIES, AI_FINISHED_TURN
	}

	void onEntityNotify(EntityCommand command, Entity unit);

	void onEntityNotify(EntityCommand command, AiEntity unit);

	void onEntityNotify(EntityCommand command, Entity unit, Ability ability);
}
