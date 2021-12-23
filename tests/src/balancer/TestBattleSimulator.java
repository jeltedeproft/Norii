package balancer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.jelte.norii.entities.EntityTypes;

import HeadlessRunnerTest.GdxTestRunner;

@RunWith(GdxTestRunner.class)
public class TestBattleSimulator {
	private static final int NUMBER_OF_SIMULATIONS = 200;

	private int simulationCounter = 0;
	private SimulationResultKeeper resultKeeper;
	private StatsAdjuster statsAdjuster;

	public TestBattleSimulator() {
		resultKeeper = new SimulationResultKeeper();
		statsAdjuster = new StatsAdjuster();
	}

	@Test
	private void runSimulations() {
		while (simulationCounter <= NUMBER_OF_SIMULATIONS) {
			runSimulation(simulationCounter);
			simulationCounter++;
		}
		simulationCounter = 0;
		adjustAndStoreUnitStats();

		compareResults();
	}

	public void runSimulation(int simulationCounter) {
		SimulationBattle battle = new SimulationBattle();
		resultKeeper.updateParticipationRates(battle.getParticipants());
		while (!battle.hasEnded()) {
			battle.executeTurn();
		}
		resultKeeper.updateWinrates(simulationCounter, battle.getWinningTeam());
	}

	private void adjustAndStoreUnitStats() {
		Map<EntityTypes, Integer> participationrates = resultKeeper.getGamesPlayed();
		Map<EntityTypes, Integer> wonGames = resultKeeper.getWonGames();
		Map<EntityTypes, Stats> startingStats = resultKeeper.getStartingStats();
		Map<EntityTypes, Stats> endingStats = resultKeeper.getEndingStats();

		for (EntityTypes type : participationrates.keySet()) {
			Stats adjustedStats = statsAdjuster.returnAdjustedStatsCopy(startingStats.get(type), participationrates.get(type), wonGames.get(type));
			endingStats.put(type, adjustedStats);
		}
	}

	private void compareResults() {
		Map<EntityTypes, Stats> startingStats = resultKeeper.getStartingStats();
		Map<EntityTypes, Stats> endingStats = resultKeeper.getEndingStats();
		for (EntityTypes type : startingStats.keySet()) {
			assertThat(startingStats.get(type)).isEqualToComparingFieldByFieldRecursively(endingStats.get(type));
		}

	}

}
