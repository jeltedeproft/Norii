package com.mygdx.game.Battle;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AttackBattleStateTest {

    @InjectMocks
    private static AttackBattleState sut;

    @Mock
    private static BattleManager battleManager;


    @BeforeClass
    public static void setUp() {
        sut = new AttackBattleState(battleManager);
    }


    @Test
    public void testExit() {
        BattleState battleState = Mockito.mock(BattleState.class);
        doNothing().when(battleState).entry();
        when(battleManager.getActionBattleState())
            .thenReturn(battleState);
        when(battleManager.getCurrentBattleState()).thenReturn(battleState);
        doNothing().when(battleManager).setCurrentBattleState(Mockito.any(BattleState.class));

        sut.exit();
    }

    @Test
    public void testButtonPressed() {

        BattleState battleState = Mockito.mock(BattleState.class);
        doNothing().when(battleState).entry();
        when(battleManager.getActionBattleState())
            .thenReturn(battleState);
        when(battleManager.getCurrentBattleState()).thenReturn(battleState);
        doNothing().when(battleManager).setCurrentBattleState(Mockito.any(BattleState.class));

        sut.buttonPressed(1);
    }
}
