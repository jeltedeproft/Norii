package com.jelte.norii.battle.battlePhase;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.jelte.norii.battle.BattleManager;
import com.jelte.norii.battle.MessageToBattleScreen;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityAnimation.Direction;
import com.jelte.norii.magic.ModifiersEnum;

public class ActionBattlePhase extends BattlePhase {
	private final BattleManager battlemanager;

	public ActionBattlePhase(final BattleManager battlemanager) {
		this.battlemanager = battlemanager;
	}

	@Override
	public void entry() {
		battlemanager.setUnitActive(battlemanager.getActiveUnit());
	}

	@Override
	public void exit() {
		battlemanager.swapTurn();
	}

	@Override
	public void clickedOnUnit(Entity entity) {
		if (isUnitSelectable(entity)) {
			battlemanager.setUnitActive(entity);
		}
	}

	private boolean isUnitSelectable(Entity entity) {
		final Entity lockedEntity = battlemanager.getLockedUnit();

		if (lockedEntity != null) {
			return false;
		}

		if (!entity.isPlayerUnit()) {
			return false;
		}

		return !entity.hasModifier(ModifiersEnum.STUNNED);
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

	@Override
	public void buttonPressed(int button) {
		switch (button) {
		case Buttons.RIGHT:
			battlemanager.sendMessageToBattleScreen(MessageToBattleScreen.HIDE_ACTIONS, battlemanager.getActiveUnit());
			battlemanager.setCurrentBattleState(battlemanager.getSelectUnitBattleState());
			battlemanager.getCurrentBattleState().entry();
			break;
		case Buttons.LEFT:
			break;
		case Buttons.MIDDLE:
			break;
		default:
			break;
		}
	}
}
