package com.mygdx.game.Battle;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

public class AttackBattleStateTest {

	@InjectMocks
	private static AttackBattleState sut;

	@Mock
	private static BattleManager battleManager;

	@BeforeAll
	public static void setUp() {
		sut = new AttackBattleState(battleManager);
	}

	@Test
	public void testExit() {
		final BattleState battleState = Mockito.mock(BattleState.class);
		doNothing().when(battleState).entry();
		when(battleManager.getActionBattleState()).thenReturn(battleState);
		when(battleManager.getCurrentBattleState()).thenReturn(battleState);
		doNothing().when(battleManager).setCurrentBattleState(Mockito.any(BattleState.class));

		sut.exit();
	}

	@Test
	public void testButtonPressed() {

		final BattleState battleState = Mockito.mock(BattleState.class);
		doNothing().when(battleState).entry();
		when(battleManager.getActionBattleState()).thenReturn(battleState);
		when(battleManager.getCurrentBattleState()).thenReturn(battleState);
		doNothing().when(battleManager).setCurrentBattleState(Mockito.any(BattleState.class));

		sut.buttonPressed(1);
	}
}
