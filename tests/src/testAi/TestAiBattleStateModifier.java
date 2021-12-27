package testAi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.BattleStateModifier;
import com.jelte.norii.ai.MoveGenerator;
import com.jelte.norii.ai.RandomMoveGenerator;
import com.jelte.norii.battle.ApFileReader;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.battle.battleState.Move;
import com.jelte.norii.battle.battleState.MoveType;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityFileReader;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.SpellFileReader;
import com.jelte.norii.utility.MyPoint;

import testBalancing.helpClasses.SimulationPlayer;
import testUtilities.GdxTestRunner;
import testUtilities.TestUtil;

@RunWith(GdxTestRunner.class)
public class TestAiBattleStateModifier {
	private static final String FILENAME_MOVE = "tests/ai/BattleStateModifier/move.txt";
	private static final String FILENAME_MOVE_OLD = "tests/ai/BattleStateModifier/move_old.txt";
	private static final String FILENAME_ATTACK = "tests/ai/BattleStateModifier/attack.txt";
	private static final String FILENAME_ATTACK_OLD = "tests/ai/BattleStateModifier/attack_old.txt";
	private static final String PLAYER_ONE_NAME = "test1";
	private static final String PLAYER_TWO_NAME = "test2";
	private static final int BATTLESTATE_SIZE = 5; // must be bigger than units per team

	private BattleStateModifier battleStateModifier;
	private UnitOwner balancer1;
	private UnitOwner balancer2;

	Array<Entity> team1;
	Array<Entity> team2;

	private int turn = 0;
	private boolean allyTurn;
	private BattleState battleState;
	private MoveGenerator moveGenerator;

	@Before
	public void prepareBattleState() {
		EntityFileReader.loadUnitStatsInMemory();
		SpellFileReader.loadSpellsInMemory();
		ApFileReader.loadApInMemory();
		team1 = new Array<>();
		team2 = new Array<>();
		balancer1 = new SimulationPlayer(true, true);
		balancer2 = new SimulationPlayer(false, false);

		balancer1.setName(PLAYER_ONE_NAME);
		balancer2.setName(PLAYER_TWO_NAME);

		battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		moveGenerator = new RandomMoveGenerator();
		battleStateModifier = new BattleStateModifier();

		team1.add(new Entity(EntityTypes.ARTIST, balancer1, false));
		team2.add(new Entity(EntityTypes.ARTIST, balancer2, false));

		configureEntities();
		specificallyPlaceUnits();
	}

	private void specificallyPlaceUnits() {
		for (int i = 0; i < team1.size; i++) {
			battleState.placeUnitOnSpecificSpot(team1.get(i), 0, i);
		}
		for (int i = 0; i < team2.size; i++) {
			battleState.placeUnitOnSpecificSpot(team2.get(i), BATTLESTATE_SIZE - 1, i);
		}
	}

	private void configureEntities() {
		for (Entity unit : team1) {
			unit.setPlayerUnit(true);
		}
		for (Entity unit : team2) {
			unit.setPlayerUnit(false);
		}
	}

	@Test
	public void applyMove() {
		Entity unit = battleState.get(0, 0).getUnit();
		Move move = new Move(MoveType.MOVE, unit.getCurrentPosition().getTilePosAsPoint().incrementX());
		BattleState copyBbattleState = battleStateModifier.applyMove(unit, move, battleState);
		TestUtil.resultsToFile(FILENAME_MOVE, copyBbattleState);
		TestUtil.regressionTest(FILENAME_MOVE, FILENAME_MOVE_OLD);
	}

	@Test
	public void applyAttack() {
		Entity unit = battleState.get(0, 0).getUnit();
		Entity enemy = battleState.get(BATTLESTATE_SIZE - 1, 0).getUnit();
		MyPoint pointToMoveTo = enemy.getCurrentPosition().getTilePosAsPoint().decrementX();
		Move move = new Move(MoveType.MOVE, pointToMoveTo);
		BattleState copyBattleState = battleStateModifier.applyMove(unit, move, battleState);

		unit = copyBattleState.get(pointToMoveTo.x, pointToMoveTo.y).getUnit();
		Move attackMove = new Move(MoveType.ATTACK, enemy.getCurrentPosition().getTilePosAsPoint());
		copyBattleState = battleStateModifier.applyMove(unit, attackMove, copyBattleState);
		TestUtil.resultsToFile(FILENAME_ATTACK, copyBattleState);
		TestUtil.regressionTest(FILENAME_ATTACK, FILENAME_ATTACK_OLD);
	}

}
