package testAi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.AIDecisionMaker;
import com.jelte.norii.battle.ApFileReader;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityFileReader;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.SpellFileReader;
import com.jelte.norii.map.BattleMap;
import com.jelte.norii.map.MapFactory.MapType;

import testBalancing.SimulationPlayer;
import testUtilities.GdxTestRunner;
import testUtilities.TestUtil;

import com.jelte.norii.map.MapManager;
import com.jelte.norii.map.MyPathFinder;

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
		mapMgr.loadMap(MapType.BATTLE_MAP_THE_DARK_SWAMP);
		currentMap = (BattleMap) mapMgr.getCurrentMap();
		MyPathFinder.getInstance().setMap(currentMap);
		while (!MyPathFinder.getInstance().isPreprocessingFinished()) {
			System.out.println("preprocesing map");
			MyPathFinder.getInstance().preprocessMap();
		}
		System.out.println("preprocesing finished");
		battleState = new BattleState(currentMap.getMapWidth(), currentMap.getMapHeight());
		balancer1 = new SimulationPlayer(true, true);
		balancer2 = new SimulationPlayer(false, false);
		TestUtil.collectRandomUnits(team1, NUMBER_OF_UNITS_PER_TEAM, balancer1);
		TestUtil.collectRandomUnits(team2, NUMBER_OF_UNITS_PER_TEAM, balancer2);
		battleState.randomlyAddUnitsToBattleState(team1);
		battleState.randomlyAddUnitsToBattleState(team2);
		decisionMaker.resetAI(battleState);
	}

	@Test
	public void testOneRoundOfAiThinking() {
		decisionMaker.processAi();
		resultsToFile();
		regressionTest();
		// assertThat(decisionMaker).isEqualToComparingFieldByFieldRecursively(endingStats.get(type));
	}

	private void resultsToFile() {
		try {
			PrintWriter outFile = new PrintWriter(new BufferedWriter(new FileWriter(FILENAME_ONE_ROUND)));
			outFile.print(decisionMaker);
			outFile.close();
		} catch (FileNotFoundException e) {
			System.err.println(FILENAME_ONE_ROUND + "cannot be found.");
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	private void regressionTest() {
		try {
			FileReader fr1 = new FileReader(FILENAME_ONE_ROUND);
			BufferedReader reader1 = new BufferedReader(fr1);

			FileReader fr2 = new FileReader(FILENAME_ONE_ROUND_OLD);
			BufferedReader reader2 = new BufferedReader(fr2);

			TestUtil.assertReaders(reader1, reader2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
