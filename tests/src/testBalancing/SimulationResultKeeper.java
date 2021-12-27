package testBalancing;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.entities.UnitOwner;

public class SimulationResultKeeper {
	private Map<EntityTypes, Stats> startingStats;
	private Map<EntityTypes, Stats> endingStats;

	private Map<EntityTypes, Integer> winrates;
	private Map<EntityTypes, Integer> gamesPlayed;

	private final UnitOwner dummyOwner = new DummyOwner();

	public SimulationResultKeeper() {
		winrates = new EnumMap<EntityTypes, Integer>(EntityTypes.class);
		gamesPlayed = new EnumMap<EntityTypes, Integer>(EntityTypes.class);

		startingStats = new EnumMap<EntityTypes, Stats>(EntityTypes.class);
		endingStats = new EnumMap<EntityTypes, Stats>(EntityTypes.class);

		initStatsBeforeSimulation();
	}

	private void initStatsBeforeSimulation() {
		for (EntityTypes type : EntityTypes.values()) {
			Entity dummyEntity = new Entity(type, dummyOwner, false);
			Stats stats = new Stats(dummyEntity);
			startingStats.put(type, stats);
			endingStats.put(type, stats);
		}
	}

	public void updateParticipationRates(Array<Entity> participatingUnits) {
		for (Entity entity : participatingUnits) {
			int count = gamesPlayed.containsKey(entity.getEntityType()) ? gamesPlayed.get(entity.getEntityType()) : 0;
			gamesPlayed.put(entity.getEntityType(), count + 1);
		}
	}

	public void updateWinrates(int round, Array<Entity> winningTeam) {
		for (Entity entity : winningTeam) {
			int count = winrates.containsKey(entity.getEntityType()) ? winrates.get(entity.getEntityType()) : 0;
			winrates.put(entity.getEntityType(), count + 1);
		}
	}

	public Map<EntityTypes, Stats> getStartingStats() {
		return startingStats;
	}

	public Map<EntityTypes, Integer> getWonGames() {
		return winrates;
	}

	public Map<EntityTypes, Integer> getGamesPlayed() {
		return gamesPlayed;
	}

	public Map<EntityTypes, Stats> getEndingStats() {
		return endingStats;
	}
}
