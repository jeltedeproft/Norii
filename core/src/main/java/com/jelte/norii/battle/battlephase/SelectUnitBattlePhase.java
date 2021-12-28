package com.jelte.norii.battle.battlephase;

import com.jelte.norii.battle.BattleManager;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.magic.ModifiersEnum;

public class SelectUnitBattlePhase extends BattlePhase {
	private final BattleManager battlemanager;

	public SelectUnitBattlePhase(final BattleManager battlemanager) {
		this.battlemanager = battlemanager;
	}

	@Override
	public void entry() {
		// no-op
	}

	@Override
	public void clickedOnUnit(Entity entity) {
		if (isUnitSelectable(entity)) {
			battlemanager.setUnitActive(entity);
		} else {
			battlemanager.justShowUnit(entity);
		}
	}

	private boolean isUnitSelectable(Entity entity) {
		if (!entity.isPlayerUnit()) {
			return false;
		}

		return !entity.hasModifier(ModifiersEnum.STUNNED);
	}

	@Override
	public void exit() {
		battlemanager.setCurrentBattleState(battlemanager.getActionBattleState());
		battlemanager.getCurrentBattleState().entry();
	}

}
