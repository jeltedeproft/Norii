package testBalancing;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.BattleStateModifier;
import com.jelte.norii.ai.MoveGenerator;
import com.jelte.norii.ai.RandomMoveGenerator;
import com.jelte.norii.battle.ApFileReader;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.battle.battleState.Move;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.utility.Utility;

import testUtilities.TestUtil;

public class SimulationBattle {
	public static final String PLAYER_ONE_NAME = "test1";
	public static final String PLAYER_TWO_NAME = "test2";
	private static final int NUMBER_OF_UNITS_PER_TEAM = 5;
	private static final int MAX_NUMBER_OF_TURNS = 200;

	private UnitOwner balancer1;
	private UnitOwner balancer2;

	Array<Entity> team1;
	Array<Entity> team2;

	private int turn = 0;
	private boolean allyTurn;
	private BattleState battleState;
	private BattleStateModifier battleStateModifier;
	private MoveGenerator moveGenerator;

	public SimulationBattle() {
		team1 = new Array<>();
		team2 = new Array<>();
		balancer1 = new SimulationPlayer(true, true);
		balancer2 = new SimulationPlayer(false, false);

		balancer1.setName(PLAYER_ONE_NAME);
		balancer2.setName(PLAYER_TWO_NAME);

		battleState = new BattleState(5, 5);
		moveGenerator = new RandomMoveGenerator();
		battleStateModifier = new BattleStateModifier();

		TestUtil.collectRandomUnits(team1, NUMBER_OF_UNITS_PER_TEAM, balancer1);
		TestUtil.collectRandomUnits(team2, NUMBER_OF_UNITS_PER_TEAM, balancer2);

		configureEntities();
		battleState.randomlyAddUnitsToBattleState(team1);
		battleState.randomlyAddUnitsToBattleState(team2);
	}

	private void configureEntities() {
		for (Entity unit : team1) {
			unit.setPlayerUnit(true);
		}
		for (Entity unit : team2) {
			unit.setPlayerUnit(false);
		}
	}

	public boolean hasEnded() {
		if (turn >= MAX_NUMBER_OF_TURNS) {
			System.out.println("reached max number of turns in simulation, turn = " + turn);
		}
		return (allyHasWon() || enemyHasWon() || (turn >= MAX_NUMBER_OF_TURNS));
	}

	public void executeTurn() {
		if (allyTurn) {
			while (balancer1.getAp() > 0) {
				Move move = moveGenerator.getMove(balancer1, battleState);
				battleState = battleStateModifier.applyMove(move.getUnit(), move, battleState);
				balancer1.setAp(balancer1.getAp() - 1);
			}
			balancer1.setAp(ApFileReader.getApData(turn + 1));
		} else {
			while (balancer2.getAp() > 0) {
				Move move = moveGenerator.getMove(balancer2, battleState);
				battleState = battleStateModifier.applyMove(move.getUnit(), move, battleState);
				balancer2.setAp(balancer2.getAp() - 1);
			}
			balancer2.setAp(ApFileReader.getApData(turn + 1));
		}
		cleanUpDeadUnits();
		turn++;
		allyTurn = !allyTurn;
	}

	private void cleanUpDeadUnits() {
		for (Entity unit : battleState.getAllUnits()) {
			if (unit.getHp() <= 0) {
				battleState.removeUnit(unit);
			}
		}
	}

	private boolean allyHasWon() {
		return battleState.getPlayerUnits().isEmpty();
	}

	private boolean enemyHasWon() {
		return battleState.getAiUnits().isEmpty();
	}

	public Array<Entity> getParticipants() {
		Array<Entity> allUnits = new Array<>();
		allUnits.addAll(team1);
		allUnits.addAll(team2);
		return allUnits;
	}

	public Array<Entity> getWinningTeam() {
		if (allyHasWon()) {
			return team1;
		}

		if (enemyHasWon()) {
			return team2;
		}

		int totalHpTeam1 = Utility.getTotalHpTeam(team1);
		int totalHpTeam2 = Utility.getTotalHpTeam(team2);

		if (totalHpTeam1 > totalHpTeam2) {
			return team1;
		}

		if (totalHpTeam1 == totalHpTeam2) {
			return new Array<Entity>();
		}

		if (totalHpTeam1 < totalHpTeam2) {
			return team2;
		}

		return null;
	}

	public UnitOwner getWinner() {
		if (allyHasWon()) {
			return balancer1;
		}

		if (enemyHasWon()) {
			return balancer2;
		}

		int totalHpTeam1 = Utility.getTotalHpTeam(team1);
		int totalHpTeam2 = Utility.getTotalHpTeam(team2);

		if (totalHpTeam1 > totalHpTeam2) {
			return balancer1;
		}

		if (totalHpTeam1 == totalHpTeam2) {
			return balancer1;
		}

		if (totalHpTeam1 < totalHpTeam2) {
			return balancer2;
		}

		return null;
	}

}
