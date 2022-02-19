package com.jelte.norii.battle;

public enum MessageToBattleScreen {
	UNIT_ACTIVE,
	UPDATE_UI,
	SET_CHARACTER_HUD,
	UNSET_CHARACTER_HUD,
	CLICKED,
	PLAYER_WINS,
	AI_WINS,
	FOCUS_CAMERA,
	UPDATE_POS,
	ADD_UNIT_UI,
	ADD_UNIT_ENTITYSTAGE,
	UPDATE_HP_UNIT,
	MOVING_ENTITY,
	UNIT_DIED,
	ACTION_COMPLETED,
	LOCK_UI,
	UNLOCK_UI,
	FINISHED_PROCESSING_TURN,
	INVALID_SPAWN_POINT,
	UNIT_DEPLOYED,
	INVALID_SPELL_TARGET,
	INVALID_MOVE,
	INVALID_ATTACK_TARGET,
	DEPLOYMENT_FINISHED,
	DAMAGED,
	ENEMY_TURN,
	PLAYER_TURN
}
