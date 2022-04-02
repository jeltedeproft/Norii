package test.ai.move.generator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.UnitTurn;
import com.jelte.norii.ai.movegenerator.TargetMoveGenerator;
import com.jelte.norii.battle.ApFileReader;
import com.jelte.norii.battle.battlestate.BattleState;
import com.jelte.norii.battle.battlestate.Move;
import com.jelte.norii.battle.battlestate.MoveType;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityFileReader;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.SpellFileReader;
import com.jelte.norii.utility.MyPoint;

import test.balancing.helpClasses.SimulationPlayer;
import test.utilities.GdxTestRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(GdxTestRunner.class)
public class TestTargetMoveGenerator {
	private static final String PLAYER_ONE_NAME = "test1";
	private static final String PLAYER_TWO_NAME = "test2";
	private static final int BATTLESTATE_SIZE = 5; // must be bigger than units per team

	private TargetMoveGenerator moveGenerator = new TargetMoveGenerator();
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
	public void testGetMoveDemon() {
		// setup
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		Entity playerUnit = new Entity(EntityTypes.DEMON, player1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, player2, false);
		team2.add(enemyUnit);
		battleState.placeUnitOnSpecificSpot(playerUnit, 0, 0);
		battleState.placeUnitOnSpecificSpot(enemyUnit, BATTLESTATE_SIZE - 1, 0);

		// expected result
		Array<Move> expectedMoves = new Array<>();
		expectedMoves.add(new Move(MoveType.SPELL, new MyPoint(BATTLESTATE_SIZE - 1, 0), enemyUnit));
		Array<UnitTurn> expectedResult = new Array<>();
		expectedResult.add(new UnitTurn(playerUnit.getEntityID(), expectedMoves));

		// move
		Array<UnitTurn> turns = moveGenerator.getAllMovesUnit(playerUnit.getAbility(), playerUnit, battleState);
	}

}
