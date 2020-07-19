package com.mygdx.game.Entities;

import com.badlogic.gdx.ai.fsm.StateMachine;
import com.mygdx.game.AI.AIState;

public class AiEntity extends Entity {
	private StateMachine<Entity, AIState> stateMachine;

	public AiEntity(EntityTypes type) {
		super(type);
		isPlayerUnit = false;
	}

}
