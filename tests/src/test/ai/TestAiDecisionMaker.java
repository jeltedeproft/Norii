package test.ai;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.AIDecisionMaker;
import com.jelte.norii.battle.ApFileReader;
import com.jelte.norii.battle.battlestate.BattleState;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityFileReader;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.SpellFileReader;
import com.jelte.norii.map.BattleMap;
import com.jelte.norii.map.MapFactory.MapType;
import com.jelte.norii.map.MapManager;
import com.jelte.norii.map.MyPathFinder;

import test.balancing.helpClasses.SimulationPlayer;
import test.utilities.GdxTestRunner;
import test.utilities.TestUtil;

@RunWith(GdxTestRunner.class)
public class TestAiDecisionMaker {
	private static final String FILENAME_ONE_ROUND = "tests/ai/oneRound.txt";
	private static final String FILENAME_ONE_ROUND_OLD = "tests/ai/oneRound_old.txt";
	private static final int NUMBER_OF_UNITS_PER_TEAM = 5;

	private AIDecisionMaker decisionMaker = new AIDecisionMaker();
	private BattleState battleState;
	private MapManager mapMgr;
	private BattleMap currentMap;

	private UnitOwner balancer1;
	private UnitOwner balancer2;

	Array<Entity> team1 = new Array<>();
	Array<Entity> team2 = new Array<>();

	@Before
	public void prepareBattleState() {
		EntityFileReader.loadUnitStatsInMemory();
		SpellFileReader.loadSpellsInMemory();
		ApFileReader.loadApInMemory();
		mapMgr = new MapManager();
		mapMgr.loadMap(MapType.TEST_MAP);
		currentMap = (BattleMap) mapMgr.getCurrentMap();
		MyPathFinder.getInstance().setMap(currentMap);
		while (!MyPathFinder.getInstance().isPreprocessingFinished()) {
			System.out.println("preprocesing map");
			MyPathFinder.getInstance().preprocessMap();
		}
		System.out.println("preprocesing finished");
		balancer1 = new SimulationPlayer(true, true);
		balancer2 = new SimulationPlayer(false, false);
	}

	@Test
	public void testOneRoundOfAiThinking() {
		// setup
		battleState = new BattleState(currentMap.getMapWidth(), currentMap.getMapHeight());
		Entity playerUnit = new Entity(EntityTypes.ARTIST, balancer1, false);
		team1.add(playerUnit);
		Entity enemyUnit = new Entity(EntityTypes.BLACK_CAT, balancer2, false);
		team2.add(enemyUnit);
		battleState.placeUnitOnSpecificSpot(playerUnit, 0, 0);
		battleState.placeUnitOnSpecificSpot(enemyUnit, 0, 4);
		decisionMaker.resetAI(battleState);

		// test
		decisionMaker.processAi();
		TestUtil.resultsToFile(FILENAME_ONE_ROUND, decisionMaker);
		TestUtil.regressionTest(FILENAME_ONE_ROUND, FILENAME_ONE_ROUND_OLD);
	}
}
