package test.ai;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.battle.ApFileReader;
import com.jelte.norii.battle.battlestate.BattleState;
import com.jelte.norii.battle.battlestate.BattleStateModifier;
import com.jelte.norii.battle.battlestate.Move;
import com.jelte.norii.battle.battlestate.MoveType;
import com.jelte.norii.battle.battlestate.SpellMove;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityFileReader;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.AbilitiesEnum;
import com.jelte.norii.magic.SpellFileReader;
import com.jelte.norii.utility.MyPoint;

import test.balancing.helpClasses.SimulationPlayer;
import test.utilities.GdxTestRunner;
import test.utilities.TestUtil;

@RunWith(GdxTestRunner.class)
public class TestAiBattleStateModifier {
	private static final String BASE_NEW = "tests/ai/BattleStateModifier/new/";
	private static final String BASE_ORIGINAL = "tests/ai/BattleStateModifier/original/";
	private static final String FILENAME_MOVE = BASE_NEW + "move.txt";
	private static final String FILENAME_MOVE_OLD = BASE_ORIGINAL + "move.txt";
	private static final String FILENAME_ATTACK = BASE_NEW + "attack.txt";
	private static final String FILENAME_ATTACK_OLD = BASE_ORIGINAL + "attack.txt";
	private static final String FILENAME_FIREBALL = BASE_NEW + "fireball.txt";
	private static final String FILENAME_FIREBALL_OLD = BASE_ORIGINAL + "fireball.txt";
	private static final String FILENAME_TURN_TO_STONE = BASE_NEW + "stone.txt";
	private static final String FILENAME_TURN_TO_STONE_OLD = BASE_ORIGINAL + "stone.txt";
	private static final String FILENAME_SWAP = BASE_NEW + "swap.txt";
	private static final String FILENAME_SWAP_OLD = BASE_ORIGINAL + "swap.txt";
	private static final String FILENAME_ARROW = BASE_NEW + "arrow.txt";
	private static final String FILENAME_ARROW_OLD = BASE_ORIGINAL + "arrow.txt";
	private static final String PLAYER_ONE_NAME = "test1";
	private static final String PLAYER_TWO_NAME = "test2";
	private static final int BATTLESTATE_SIZE = 5; // must be bigger than units per team

	private BattleStateModifier battleStateModifier;
	private UnitOwner balancer1;
	private UnitOwner balancer2;

	Array<Entity> team1;
	Array<Entity> team2;

	@Before
	public void prepareBattleState() {
		EntityFileReader.loadUnitStatsInMemory();
		SpellFileReader.loadSpellsInMemory();
		ApFileReader.loadApInMemory();
		battleStateModifier = new BattleStateModifier();
		team1 = new Array<>();
		team2 = new Array<>();
		balancer1 = new SimulationPlayer(true, true);
		balancer2 = new SimulationPlayer(false, false);
		balancer1.setName(PLAYER_ONE_NAME);
		balancer2.setName(PLAYER_TWO_NAME);
	}

	@Test
	public void applyMove() {
		// setup
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		Entity playerUnit = new Entity(EntityTypes.ARTIST, balancer1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, balancer2, false);
		team2.add(enemyUnit);
		battleState.placeUnitOnSpecificSpot(playerUnit, 0, 0);
		battleState.placeUnitOnSpecificSpot(enemyUnit, BATTLESTATE_SIZE - 1, 0);

		// move
		Move move = new Move(MoveType.MOVE, playerUnit.getCurrentPosition().getTilePosAsPoint().incrementX());
		BattleState copyBbattleState = battleStateModifier.applyMove(playerUnit, move, battleState);
		TestUtil.resultsToFile(FILENAME_MOVE, copyBbattleState);
		TestUtil.regressionTest(FILENAME_MOVE, FILENAME_MOVE_OLD);
	}

	@Test
	public void applyAttack() {
		// setup
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		Entity playerUnit = new Entity(EntityTypes.ARTIST, balancer1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, balancer2, false);
		team2.add(enemyUnit);
		battleState.placeUnitOnSpecificSpot(playerUnit, 0, 0);
		battleState.placeUnitOnSpecificSpot(enemyUnit, 0, 1);

		// attack
		Move attackMove = new Move(MoveType.ATTACK, enemyUnit.getCurrentPosition().getTilePosAsPoint());
		BattleState copyBattleState = battleStateModifier.applyMove(playerUnit, attackMove, battleState);
		TestUtil.resultsToFile(FILENAME_ATTACK, copyBattleState);
		TestUtil.regressionTest(FILENAME_ATTACK, FILENAME_ATTACK_OLD);
	}

	@Test
	public void applyFireball() {
		// setup
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		Entity playerUnit = new Entity(EntityTypes.ARTIST, balancer1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, balancer2, false);
		team2.add(enemyUnit);
		battleState.placeUnitOnSpecificSpot(playerUnit, 0, 0);
		battleState.placeUnitOnSpecificSpot(enemyUnit, 0, 4);

		// fireball
		playerUnit.setAbility(AbilitiesEnum.FIREBALL);
		Move spellMove = new SpellMove(MoveType.SPELL, enemyUnit.getCurrentPosition().getTilePosAsPoint(), playerUnit.getAbility(), null);
		BattleState copyBattleState = battleStateModifier.applyMove(playerUnit, spellMove, battleState);
		TestUtil.resultsToFile(FILENAME_FIREBALL, copyBattleState);
		TestUtil.regressionTest(FILENAME_FIREBALL, FILENAME_FIREBALL_OLD);
	}

	@Test
	public void applyTurnToSTone() {
		// setup
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		Entity playerUnit = new Entity(EntityTypes.ARTIST, balancer1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, balancer2, false);
		team2.add(enemyUnit);
		battleState.placeUnitOnSpecificSpot(playerUnit, 0, 0);
		battleState.placeUnitOnSpecificSpot(enemyUnit, 0, 4);

		// turn to stone
		playerUnit.setAbility(AbilitiesEnum.TURN_TO_STONE);
		Move spellMove = new SpellMove(MoveType.SPELL, enemyUnit.getCurrentPosition().getTilePosAsPoint(), playerUnit.getAbility(), null);
		BattleState copyBattleState = battleStateModifier.applyMove(playerUnit, spellMove, battleState);
		TestUtil.resultsToFile(FILENAME_TURN_TO_STONE, copyBattleState);
		TestUtil.regressionTest(FILENAME_TURN_TO_STONE, FILENAME_TURN_TO_STONE_OLD);
	}

	@Test
	public void applySwap() {
		// setup
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		Entity playerUnit = new Entity(EntityTypes.ARTIST, balancer1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, balancer2, false);
		team2.add(enemyUnit);
		battleState.placeUnitOnSpecificSpot(playerUnit, 0, 0);
		battleState.placeUnitOnSpecificSpot(enemyUnit, 0, 4);

		// swap
		playerUnit.setAbility(AbilitiesEnum.SWAP);
		Move spellMove = new SpellMove(MoveType.SPELL, enemyUnit.getCurrentPosition().getTilePosAsPoint(), playerUnit.getAbility(), null);
		BattleState copyBattleState = battleStateModifier.applyMove(playerUnit, spellMove, battleState);
		TestUtil.resultsToFile(FILENAME_SWAP, copyBattleState);
		TestUtil.regressionTest(FILENAME_SWAP, FILENAME_SWAP_OLD);
	}

	@Test
	public void applyArrow() {
		// setup
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		Entity playerUnit = new Entity(EntityTypes.ARTIST, balancer1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, balancer2, false);
		team2.add(enemyUnit);
		battleState.placeUnitOnSpecificSpot(playerUnit, 0, 0);
		battleState.placeUnitOnSpecificSpot(enemyUnit, 4, 4);

		// swap
		playerUnit.setAbility(AbilitiesEnum.ARROW);
		final Array<MyPoint> affectedUnits = new Array<>();
		affectedUnits.add(new MyPoint(4, 4));
		Move spellMove = new SpellMove(MoveType.SPELL, enemyUnit.getCurrentPosition().getTilePosAsPoint(), playerUnit.getAbility(), affectedUnits);
		BattleState copyBattleState = battleStateModifier.applyMove(playerUnit, spellMove, battleState);
		TestUtil.resultsToFile(FILENAME_ARROW, copyBattleState);
		TestUtil.regressionTest(FILENAME_ARROW, FILENAME_ARROW_OLD);
	}

}
