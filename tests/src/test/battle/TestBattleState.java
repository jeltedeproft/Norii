package test.battle;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.battle.ApFileReader;
import com.jelte.norii.battle.battlestate.BattleCell;
import com.jelte.norii.battle.battlestate.BattleState;
import com.jelte.norii.battle.battlestate.BattleStateModifier;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityFileReader;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.SpellFileReader;
import com.jelte.norii.utility.MyPoint;

import test.balancing.helpClasses.SimulationPlayer;
import test.utilities.GdxTestRunner;

@RunWith(GdxTestRunner.class)
public class TestBattleState {
	private static final String BASE_NEW = "tests/battleState/new/";
	private static final String BASE_ORIGINAL = "tests/battleState/original/";
	private static final String FILENAME_MOVE = BASE_NEW + "move.txt";
	private static final String FILENAME_MOVE_OLD = BASE_ORIGINAL + "move.txt";
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
	public void testBasicConstructor() {
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		for (int i = 0; i < BATTLESTATE_SIZE; i++) {
			for (int j = 0; j < BATTLESTATE_SIZE; j++) {
				BattleCell cell = battleState.get(i, j);
				assertTrue("cell should not be empty", cell != null);
				assertTrue("cell should not be occupied", !cell.isOccupied());
				assertTrue("cell should be walkable", cell.isWalkable());
				assertTrue("cell should not have a unit", cell.getUnit() == null);
			}
		}
	}

	@Test
	public void testMoveExistingUnit() {
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		Entity playerUnit = new Entity(EntityTypes.ARTIST, balancer1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, balancer2, false);
		team2.add(enemyUnit);
		battleState.placeUnitOnSpecificSpot(playerUnit, 0, 0);
		battleState.placeUnitOnSpecificSpot(enemyUnit, BATTLESTATE_SIZE - 1, 0);
		battleState.moveUnitAndCreateIfNecessary(playerUnit, new MyPoint(0, 1));
		BattleCell emptyCell = battleState.get(0, 0);
		assertTrue("cell should not be empty", emptyCell != null);
		assertTrue("cell should not be occupied", !emptyCell.isOccupied());
		assertTrue("cell should be walkable", emptyCell.isWalkable());
		assertTrue("cell should not have a unit", emptyCell.getUnit() == null);
		BattleCell cellWithUnit = battleState.get(0, 1);
		assertTrue("cell should not be empty", cellWithUnit != null);
		assertTrue("cell should be occupied", cellWithUnit.isOccupied());
		assertTrue("cell should be walkable", cellWithUnit.isWalkable());
		assertTrue("cell should have a unit", cellWithUnit.getUnit() != null);
	}

	@Test
	public void testMoveNonExistingUnit() {
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		Entity playerUnit = new Entity(EntityTypes.ARTIST, balancer1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, balancer2, false);
		team2.add(enemyUnit);
		battleState.placeUnitOnSpecificSpot(enemyUnit, BATTLESTATE_SIZE - 1, 0);
		battleState.moveUnitAndCreateIfNecessary(playerUnit, new MyPoint(0, 1));
		BattleCell emptyCell = battleState.get(0, 0);
		assertTrue("cell should not be empty", emptyCell != null);
		assertTrue("cell should not be occupied", !emptyCell.isOccupied());
		assertTrue("cell should be walkable", emptyCell.isWalkable());
		assertTrue("cell should not have a unit", emptyCell.getUnit() == null);
		BattleCell cellWithUnit = battleState.get(0, 1);
		assertTrue("cell should not be empty", cellWithUnit != null);
		assertTrue("cell should be occupied", cellWithUnit.isOccupied());
		assertTrue("cell should be walkable", cellWithUnit.isWalkable());
		assertTrue("cell should have a unit", cellWithUnit.getUnit() != null);
	}

	@Test
	public void testSwap() {
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		Entity playerUnit = new Entity(EntityTypes.ARTIST, balancer1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, balancer2, false);
		team2.add(enemyUnit);
		battleState.placeUnitOnSpecificSpot(playerUnit, 0, 0);
		battleState.placeUnitOnSpecificSpot(enemyUnit, BATTLESTATE_SIZE - 1, 0);
		battleState.swapPositions(playerUnit, enemyUnit);
		BattleCell emptyCell = battleState.get(0, 0);
		assertTrue("cell should not be empty", emptyCell != null);
		assertTrue("cell should be occupied", emptyCell.isOccupied());
		assertTrue("cell should be walkable", emptyCell.isWalkable());
		assertTrue("cell should have the swapped unit", emptyCell.getUnit().getEntityType().equals(EntityTypes.BLACK_CAT));
		BattleCell cellWithUnit = battleState.get(BATTLESTATE_SIZE - 1, 0);
		assertTrue("cell should not be empty", cellWithUnit != null);
		assertTrue("cell should be occupied", cellWithUnit.isOccupied());
		assertTrue("cell should be walkable", cellWithUnit.isWalkable());
		assertTrue("cell should have the swapped unit", cellWithUnit.getUnit().getEntityType().equals(EntityTypes.ARTIST));
	}

	@Test
	public void testPush() {
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		Entity playerUnit = new Entity(EntityTypes.ARTIST, balancer1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, balancer2, false);
		team2.add(enemyUnit);
		battleState.placeUnitOnSpecificSpot(playerUnit, 0, 0);
		battleState.placeUnitOnSpecificSpot(enemyUnit, BATTLESTATE_SIZE - 1, 0);
		battleState.pushOrPullUnit(playerUnit.getCurrentPositionPoint(), enemyUnit.getCurrentPositionPoint(), 3, false);
		BattleCell casterCell = battleState.get(0, 0);
		assertTrue("cell should not be empty", casterCell != null);
		assertTrue("cell should be occupied", casterCell.isOccupied());
		assertTrue("cell should be walkable", casterCell.isWalkable());
		assertTrue("cell should have the original unit", casterCell.getUnit().getEntityType().equals(EntityTypes.ARTIST));
		BattleCell targetCell = battleState.get(BATTLESTATE_SIZE - 1, 0);
		assertTrue("cell should not be empty", targetCell != null);
		assertTrue("cell should be occupied", targetCell.isOccupied());
		assertTrue("cell should be walkable", targetCell.isWalkable());
		assertTrue("unit should bump against the wall and stay in the same place", targetCell.getUnit().getEntityType().equals(EntityTypes.BLACK_CAT));
	}

	@Test
	public void testPull() {
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		Entity playerUnit = new Entity(EntityTypes.ARTIST, balancer1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, balancer2, false);
		team2.add(enemyUnit);
		battleState.placeUnitOnSpecificSpot(playerUnit, 0, 0);
		battleState.placeUnitOnSpecificSpot(enemyUnit, BATTLESTATE_SIZE - 1, 0);
		battleState.pushOrPullUnit(playerUnit.getCurrentPositionPoint(), enemyUnit.getCurrentPositionPoint(), 3, true);
		BattleCell casterCell = battleState.get(0, 0);
		assertTrue("cell should not be empty", casterCell != null);
		assertTrue("cell should be occupied", casterCell.isOccupied());
		assertTrue("cell should be walkable", casterCell.isWalkable());
		assertTrue("cell should have the original unit", casterCell.getUnit().getEntityType().equals(EntityTypes.ARTIST));
		BattleCell targetCell = battleState.get(1, 0);
		assertTrue("cell should not be empty", targetCell != null);
		assertTrue("cell should be occupied", targetCell.isOccupied());
		assertTrue("cell should be walkable", targetCell.isWalkable());
		assertTrue("unit should be pulled here", targetCell.getUnit().getEntityType().equals(EntityTypes.BLACK_CAT));
	}

	@Test
	public void testPullIntoObstacle() {
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		Entity playerUnit = new Entity(EntityTypes.ARTIST, balancer1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, balancer2, false);
		team2.add(enemyUnit);
		battleState.get(3, 0).setWalkable(false);
		battleState.placeUnitOnSpecificSpot(playerUnit, 0, 0);
		battleState.placeUnitOnSpecificSpot(enemyUnit, BATTLESTATE_SIZE - 1, 0);
		battleState.pushOrPullUnit(playerUnit.getCurrentPositionPoint(), enemyUnit.getCurrentPositionPoint(), 3, true);
		BattleCell casterCell = battleState.get(0, 0);
		assertTrue("cell should not be empty", casterCell != null);
		assertTrue("cell should be occupied", casterCell.isOccupied());
		assertTrue("cell should be walkable", casterCell.isWalkable());
		assertTrue("cell should have the original unit", casterCell.getUnit().getEntityType().equals(EntityTypes.ARTIST));
		BattleCell targetCell = battleState.get(4, 0);
		assertTrue("cell should not be empty", targetCell != null);
		assertTrue("cell should be occupied", targetCell.isOccupied());
		assertTrue("cell should be walkable", targetCell.isWalkable());
		assertTrue("unit should be pulled here, blocked by obstacle", targetCell.getUnit().getEntityType().equals(EntityTypes.BLACK_CAT));
	}

	@Test
	public void testPushLimit() {
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		Entity playerUnit = new Entity(EntityTypes.ARTIST, balancer1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, balancer2, false);
		team2.add(enemyUnit);
		battleState.placeUnitOnSpecificSpot(playerUnit, 0, 0);
		battleState.placeUnitOnSpecificSpot(enemyUnit, 1, 0);
		battleState.pushOrPullUnit(playerUnit.getCurrentPositionPoint(), enemyUnit.getCurrentPositionPoint(), 2, false);
		BattleCell casterCell = battleState.get(0, 0);
		assertTrue("cell should not be empty", casterCell != null);
		assertTrue("cell should be occupied", casterCell.isOccupied());
		assertTrue("cell should be walkable", casterCell.isWalkable());
		assertTrue("cell should have the original unit", casterCell.getUnit().getEntityType().equals(EntityTypes.ARTIST));
		BattleCell targetCell = battleState.get(3, 0);
		assertTrue("cell should not be empty", targetCell != null);
		assertTrue("cell should be occupied", targetCell.isOccupied());
		assertTrue("cell should be walkable", targetCell.isWalkable());
		assertTrue("unit should be pulled here, blocked by obstacle", targetCell.getUnit().getEntityType().equals(EntityTypes.BLACK_CAT));
	}

	@Test
	public void testPushVertical() {
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		Entity playerUnit = new Entity(EntityTypes.ARTIST, balancer1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, balancer2, false);
		team2.add(enemyUnit);
		battleState.placeUnitOnSpecificSpot(playerUnit, 0, 4);
		battleState.placeUnitOnSpecificSpot(enemyUnit, 0, 3);
		battleState.pushOrPullUnit(playerUnit.getCurrentPositionPoint(), enemyUnit.getCurrentPositionPoint(), 2, false);
		BattleCell casterCell = battleState.get(0, 4);
		assertTrue("cell should not be empty", casterCell != null);
		assertTrue("cell should be occupied", casterCell.isOccupied());
		assertTrue("cell should be walkable", casterCell.isWalkable());
		assertTrue("cell should have the original unit", casterCell.getUnit().getEntityType().equals(EntityTypes.ARTIST));
		BattleCell targetCell = battleState.get(0, 1);
		assertTrue("cell should not be empty", targetCell != null);
		assertTrue("cell should be occupied", targetCell.isOccupied());
		assertTrue("cell should be walkable", targetCell.isWalkable());
		assertTrue("unit should bump against the wall and stay in the same place", targetCell.getUnit().getEntityType().equals(EntityTypes.BLACK_CAT));
	}

	@Test
	public void testPullVertical() {
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		Entity playerUnit = new Entity(EntityTypes.ARTIST, balancer1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, balancer2, false);
		team2.add(enemyUnit);
		battleState.placeUnitOnSpecificSpot(playerUnit, 0, 4);
		battleState.placeUnitOnSpecificSpot(enemyUnit, 0, 0);
		battleState.pushOrPullUnit(playerUnit.getCurrentPositionPoint(), enemyUnit.getCurrentPositionPoint(), 3, true);
		BattleCell casterCell = battleState.get(0, 4);
		assertTrue("cell should not be empty", casterCell != null);
		assertTrue("cell should be occupied", casterCell.isOccupied());
		assertTrue("cell should be walkable", casterCell.isWalkable());
		assertTrue("cell should have the original unit", casterCell.getUnit().getEntityType().equals(EntityTypes.ARTIST));
		BattleCell targetCell = battleState.get(0, 3);
		assertTrue("cell should not be empty", targetCell != null);
		assertTrue("cell should be occupied", targetCell.isOccupied());
		assertTrue("cell should be walkable", targetCell.isWalkable());
		assertTrue("unit should be pulled here", targetCell.getUnit().getEntityType().equals(EntityTypes.BLACK_CAT));
	}

	@Test
	public void testPullIntoObstacleVertical() {
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		Entity playerUnit = new Entity(EntityTypes.ARTIST, balancer1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, balancer2, false);
		team2.add(enemyUnit);
		battleState.get(0, 2).setWalkable(false);
		battleState.placeUnitOnSpecificSpot(playerUnit, 0, 4);
		battleState.placeUnitOnSpecificSpot(enemyUnit, 0, 0);
		battleState.pushOrPullUnit(playerUnit.getCurrentPositionPoint(), enemyUnit.getCurrentPositionPoint(), 3, true);
		BattleCell casterCell = battleState.get(0, 4);
		assertTrue("cell should not be empty", casterCell != null);
		assertTrue("cell should be occupied", casterCell.isOccupied());
		assertTrue("cell should be walkable", casterCell.isWalkable());
		assertTrue("cell should have the original unit", casterCell.getUnit().getEntityType().equals(EntityTypes.ARTIST));
		BattleCell targetCell = battleState.get(0, 1);
		assertTrue("cell should not be empty", targetCell != null);
		assertTrue("cell should be occupied", targetCell.isOccupied());
		assertTrue("cell should be walkable", targetCell.isWalkable());
		assertTrue("unit should be pulled here, blocked by obstacle", targetCell.getUnit().getEntityType().equals(EntityTypes.BLACK_CAT));
	}

	@Test
	public void testPushLimitVertical() {
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		Entity playerUnit = new Entity(EntityTypes.ARTIST, balancer1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, balancer2, false);
		team2.add(enemyUnit);
		battleState.placeUnitOnSpecificSpot(playerUnit, 0, 4);
		battleState.placeUnitOnSpecificSpot(enemyUnit, 0, 3);
		battleState.pushOrPullUnit(playerUnit.getCurrentPositionPoint(), enemyUnit.getCurrentPositionPoint(), 2, false);
		BattleCell casterCell = battleState.get(0, 4);
		assertTrue("cell should not be empty", casterCell != null);
		assertTrue("cell should be occupied", casterCell.isOccupied());
		assertTrue("cell should be walkable", casterCell.isWalkable());
		assertTrue("cell should have the original unit", casterCell.getUnit().getEntityType().equals(EntityTypes.ARTIST));
		BattleCell targetCell = battleState.get(0, 1);
		assertTrue("cell should not be empty", targetCell != null);
		assertTrue("cell should be occupied", targetCell.isOccupied());
		assertTrue("cell should be walkable", targetCell.isWalkable());
		assertTrue("unit should be pulled here, blocked by obstacle", targetCell.getUnit().getEntityType().equals(EntityTypes.BLACK_CAT));
	}

	@Test
	public void testPushLimitWayTooHigh() {
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		Entity playerUnit = new Entity(EntityTypes.ARTIST, balancer1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, balancer2, false);
		team2.add(enemyUnit);
		battleState.placeUnitOnSpecificSpot(playerUnit, 0, 0);
		battleState.placeUnitOnSpecificSpot(enemyUnit, 1, 0);
		battleState.pushOrPullUnit(playerUnit.getCurrentPositionPoint(), enemyUnit.getCurrentPositionPoint(), 99, false);
		BattleCell casterCell = battleState.get(0, 0);
		assertTrue("cell should not be empty", casterCell != null);
		assertTrue("cell should be occupied", casterCell.isOccupied());
		assertTrue("cell should be walkable", casterCell.isWalkable());
		assertTrue("cell should have the original unit", casterCell.getUnit().getEntityType().equals(EntityTypes.ARTIST));
		BattleCell targetCell = battleState.get(4, 0);
		assertTrue("cell should not be empty", targetCell != null);
		assertTrue("cell should be occupied", targetCell.isOccupied());
		assertTrue("cell should be walkable", targetCell.isWalkable());
		assertTrue("unit should be pulled here, blocked by obstacle", targetCell.getUnit().getEntityType().equals(EntityTypes.BLACK_CAT));
	}

	@Test
	public void testPush0Distance() {
		BattleState battleState = new BattleState(BATTLESTATE_SIZE, BATTLESTATE_SIZE);
		Entity playerUnit = new Entity(EntityTypes.ARTIST, balancer1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, balancer2, false);
		team2.add(enemyUnit);
		battleState.placeUnitOnSpecificSpot(playerUnit, 0, 0);
		battleState.placeUnitOnSpecificSpot(enemyUnit, 1, 0);
		battleState.pushOrPullUnit(playerUnit.getCurrentPositionPoint(), enemyUnit.getCurrentPositionPoint(), 0, false);
		BattleCell casterCell = battleState.get(0, 0);
		assertTrue("cell should not be empty", casterCell != null);
		assertTrue("cell should be occupied", casterCell.isOccupied());
		assertTrue("cell should be walkable", casterCell.isWalkable());
		assertTrue("cell should have the original unit", casterCell.getUnit().getEntityType().equals(EntityTypes.ARTIST));
		BattleCell targetCell = battleState.get(1, 0);
		assertTrue("cell should not be empty", targetCell != null);
		assertTrue("cell should be occupied", targetCell.isOccupied());
		assertTrue("cell should be walkable", targetCell.isWalkable());
		assertTrue("unit should be pulled here, blocked by obstacle", targetCell.getUnit().getEntityType().equals(EntityTypes.BLACK_CAT));
	}

}
