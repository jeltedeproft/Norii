package com.mygdx.game.Battle;

import com.badlogic.gdx.Input.Keys;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntityAnimation.Direction;

public class ActionBattleState extends BattleState {
	private final BattleManager battlemanager;

	public ActionBattleState(final BattleManager battlemanager) {
		this.battlemanager = battlemanager;
	}

	@Override
	public void entry() {
		battlemanager.getActiveUnit().setInActionPhase(true);
	}

	@Override
	public void exit() {
		battlemanager.nextUnitActive();
		entry();
	}

	@Override
	public void keyPressed(final int key) {
		final Entity activeUnit = battlemanager.getActiveUnit();
		switch (key) {
		case Keys.Z:
			activeUnit.setDirection(Direction.UP);
			break;
		case Keys.Q:
			activeUnit.setDirection(Direction.LEFT);
			break;
		case Keys.S:
			activeUnit.setDirection(Direction.DOWN);
			break;
		case Keys.D:
			activeUnit.setDirection(Direction.RIGHT);
			break;
		default:
			break;
		}
	}
}
