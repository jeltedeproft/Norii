package com.jelte.norii.entities;

import com.jelte.norii.magic.Ability;
import com.jelte.norii.utility.MyPoint;
import com.jelte.norii.utility.TiledMapPosition;

public interface EntityObserver {

	enum EntityCommand {
		IN_MOVEMENT, IN_ATTACK_PHASE, IN_SPELL_PHASE, UNIT_ACTIVE, CLICKED, SKIP, AI_ACT, DIED, INIT_POSIBILITIES, AI_FINISHED_TURN, UNIT_LOCKED, FOCUS_CAMERA, UPDATE_POS, UPDATE_HP, CAST_SPELL_AI, PLAYER_WINS, AI_WINS
	}

	void onEntityNotify(EntityCommand command, Entity unit);

	void onEntityNotify(EntityCommand command, AiEntity unit);

	void onEntityNotify(EntityCommand command, Entity unit, TiledMapPosition pos);

	void onEntityNotify(EntityCommand command, Entity entity, Ability abilityUsed, MyPoint target);
}
