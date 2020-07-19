package com.mygdx.game.AI;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.mygdx.game.Entities.AiEntity;

public enum AIState implements State<AiEntity> {

	RUN_AWAY() {
		@Override
		public void update(AiEntity aiEntity) {
//			if (entity.isSafe()) {
//				entity.stateMachine.changeState(SLEEP);
//			}
//			else {
//				entity.moveAwayFromEnemy();
//			}
		}
	},

	ENGAGE() {
		@Override
		public void update(AiEntity aiEntity) {

		}
	},

	REPOSITION() {
		@Override
		public void update(AiEntity aiEntity) {

		}
	},

	ATTACK() {
		@Override
		public void update(AiEntity aiEntity) {

		}
	};

	@Override
	public void enter(AiEntity aiEntity) {
	}

	@Override
	public void exit(AiEntity aiEntity) {
	}

	@Override
	public boolean onMessage(AiEntity aiEntity, Telegram telegram) {
		// We don't use messaging in this example
		return false;
	}
}
