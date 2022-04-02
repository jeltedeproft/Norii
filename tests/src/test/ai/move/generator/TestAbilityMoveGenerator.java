package test.ai.move.generator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.movegenerator.AbilityMoveGenerator;
import com.jelte.norii.battle.ApFileReader;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityFileReader;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.SpellFileReader;

import test.balancing.helpClasses.SimulationPlayer;
import test.utilities.GdxTestRunner;

@RunWith(GdxTestRunner.class)
public class TestAbilityMoveGenerator {
	private static final String PLAYER_ONE_NAME = "test1";
	private static final String PLAYER_TWO_NAME = "test2";

	private AbilityMoveGenerator moveGenerator = new AbilityMoveGenerator();
	private UnitOwner player1;
	private UnitOwner player2;

	Array<Entity> team1;
	Array<Entity> team2;

	@Before
	public void prepareBattleState() {
		EntityFileReader.loadUnitStatsInMemory();
		SpellFileReader.loadSpellsInMemory();
		ApFileReader.loadApInMemory();
		team1 = new Array<>();
		team2 = new Array<>();
		player1 = new SimulationPlayer(true, true);
		player2 = new SimulationPlayer(false, false);
		player1.setName(PLAYER_ONE_NAME);
		player2.setName(PLAYER_TWO_NAME);
	}

	@Test
	public void applyMove() {
		// the class to be tested does nothing yet
	}

}
