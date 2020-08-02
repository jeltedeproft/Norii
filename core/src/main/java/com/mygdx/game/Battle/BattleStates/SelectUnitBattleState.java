package com.mygdx.game.Battle.BattleStates;

import com.mygdx.game.Battle.BattleManager;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Magic.ModifiersEnum;

public class SelectUnitBattleState extends BattleState {
	private final BattleManager battlemanager;

	public SelectUnitBattleState(final BattleManager battlemanager) {
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
			exit();
		}
	}

	private boolean isUnitSelectable(Entity entity) {
		Entity lockedEntity = battlemanager.getLockedUnit();
		if ((lockedEntity != null) && (lockedEntity.getEntityID() != entity.getEntityID())) {
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
