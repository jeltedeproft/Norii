package com.mygdx.game.Entities;

import com.mygdx.game.Magic.Ability;

public interface EntityObserver {

	public enum EntityCommand {
		IN_MOVEMENT,
		IN_ATTACK_PHASE,
		IN_SPELL_PHASE,
		UNIT_ACTIVE,
		CLICKED,
		SKIP,
		DIED
	}

	void onEntityNotify(EntityCommand command, Entity unit);

	void onEntityNotify(EntityCommand command, Entity unit, Ability ability);
}
