package testBalancing;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.jelte.norii.battle.ApFileReader;
import com.jelte.norii.entities.EntityFileReader;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.magic.SpellFileReader;

import testUtilities.GdxTestRunner;

@RunWith(GdxTestRunner.class)
public class TestBattleSimulator {
	private static final int NUMBER_OF_SIMULATIONS_PER_ROUND = 50;
	private static final int NUMBER_OF_ADAPTATION_ROUNDS = 500;
	private static final String FILENAME_END_STATS = "tests/balancer/endStats.txt";
	private static final String FILENAME_BEGIN_STATS = "tests/balancer/beginStats.txt";
	private static final String FILENAME_WIN_RATE = "tests/balancer/winRate.txt";

	private static final DecimalFormat df = new DecimalFormat("0.00");

	private int adaptationCounter = 0;
	private int simulationCounter = 0;
	private SimulationResultKeeper resultKeeper;
	private StatsAdjuster statsAdjuster;

	public TestBattleSimulator() {
		EntityFileReader.loadUnitStatsInMemory();
		SpellFileReader.loadSpellsInMemory();
		ApFileReader.loadApInMemory();
		resultKeeper = new SimulationResultKeeper();
		statsAdjuster = new StatsAdjuster();
	}

	@Test
	public void runSimulations() {
		while (adaptationCounter <= NUMBER_OF_ADAPTATION_ROUNDS) {
			while (simulationCounter <= NUMBER_OF_SIMULATIONS_PER_ROUND) {
				System.out.println("round " + ((adaptationCounter * NUMBER_OF_SIMULATIONS_PER_ROUND) + simulationCounter) + "/" + ((NUMBER_OF_ADAPTATION_ROUNDS + 1) * NUMBER_OF_SIMULATIONS_PER_ROUND));
				runSimulation(simulationCounter);
				simulationCounter++;
			}
			simulationCounter = 0;
			adjustAndStoreUnitStats();
			adaptationCounter++;
		}
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
		Map<EntityTypes, Stats> endingStats = resultKeeper.getEndingStats();

		for (EntityTypes type : participationrates.keySet()) {
			int gamesWon;
			if (wonGames.get(type) == null) {
				gamesWon = 0;
			} else {
				gamesWon = wonGames.get(type);
			}
			Stats adjustedStats = statsAdjuster.returnAdjustedStatsCopy(endingStats.get(type), participationrates.get(type), gamesWon);
			endingStats.put(type, adjustedStats);
		}
	}

	private void compareResults() {
		Map<EntityTypes, Stats> startingStats = resultKeeper.getStartingStats();
		Map<EntityTypes, Stats> endingStats = resultKeeper.getEndingStats();
		Map<EntityTypes, Integer> gamesPlayed = resultKeeper.getGamesPlayed();
		Map<EntityTypes, Integer> winRates = resultKeeper.getWonGames();

		try {
			PrintWriter outBeginStats = new PrintWriter(new BufferedWriter(new FileWriter(FILENAME_BEGIN_STATS)));
			PrintWriter outEndStats = new PrintWriter(new BufferedWriter(new FileWriter(FILENAME_END_STATS)));
			PrintWriter outWinRate = new PrintWriter(new BufferedWriter(new FileWriter(FILENAME_WIN_RATE)));

			for (Stats stats : startingStats.values()) {
				outBeginStats.print(stats);
			}

			for (Stats stats : endingStats.values()) {
				outEndStats.print(stats);
			}

			for (Entry<EntityTypes, Integer> playedGames : gamesPlayed.entrySet()) {
				int played = playedGames.getValue();
				int won = winRates.get(playedGames.getKey());
				float winrate = (float) won / (float) played;
				outWinRate.print(playedGames.getKey() + " : " + df.format(winrate) + "%\n");
			}

			outBeginStats.close();
			outEndStats.close();
			outWinRate.close();
		} catch (FileNotFoundException e) {
			System.err.println(FILENAME_BEGIN_STATS + " or " + FILENAME_END_STATS + "cannot be found.");
		} catch (Exception e) {
			System.err.println(e);
		}
	}

}
