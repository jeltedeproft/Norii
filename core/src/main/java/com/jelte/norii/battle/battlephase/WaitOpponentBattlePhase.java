package com.jelte.norii.battle.battlephase;

import com.jelte.norii.battle.BattleManager;
import com.jelte.norii.battle.MessageToBattleScreen;

public class WaitOpponentBattlePhase extends BattlePhase {
	private final BattleManager battlemanager;

	public WaitOpponentBattlePhase(final BattleManager battlemanager) {
		this.battlemanager = battlemanager;
	}

	@Override
	public void entry() {
		battlemanager.swapTurn();
		battlemanager.sendMessageToBattleScreen(MessageToBattleScreen.LOCK_UI, battlemanager.getActiveUnit());
	}

	@Override
	public void exit() {
		battlemanager.setCurrentBattleState(battlemanager.getSelectUnitBattleState());
		battlemanager.getCurrentBattleState().entry();
	}

}
