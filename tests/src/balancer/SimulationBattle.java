package balancer;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.BattleStateModifier;
import com.jelte.norii.ai.MoveGenerator;
import com.jelte.norii.ai.RandomMoveGenerator;
import com.jelte.norii.ai.UnitTurn;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.battle.battleState.Move;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.entities.UnitOwner;

public class SimulationBattle {
	public static final String PLAYER_ONE_NAME = "test1";
	public static final String PLAYER_TWO_NAME = "test2";
	private static final int NUMBER_OF_UNITS_PER_TEAM = 10;
	private static final int MAX_NUMBER_OF_TURNS = 100;

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
		balancer1 = new SimulationPlayer(true);
		balancer2 = new SimulationPlayer(false);

		balancer1.setName(PLAYER_ONE_NAME);
		balancer2.setName(PLAYER_TWO_NAME);

		battleState = new BattleState(20, 20);
		moveGenerator = new RandomMoveGenerator();

		collectRandomUnits();
	}

	private void collectRandomUnits() {
		while (team1.size <= NUMBER_OF_UNITS_PER_TEAM) {
			team1.add(new Entity(EntityTypes.randomEntityType(), balancer1, false));
		}

		while (team2.size <= NUMBER_OF_UNITS_PER_TEAM) {
			team2.add(new Entity(EntityTypes.randomEntityType(), balancer2, false));
		}
	}

	public boolean hasEnded() {
		return (!allyHasWon()) && (!enemyHasWon()) && (turn < MAX_NUMBER_OF_TURNS);
	}

	public void executeTurn() {
		if (allyTurn) {
			// UnitTurn = make random turn for all ap available making a move using all
			// units
			Array<Move> moves = generateMoves(balancer1);
			Entity unit = battleState.getRandomPlayerUnit();

			// make a turn for every action
			UnitTurn turn = new UnitTurn(unit.getEntityID(), generateMoves(unit));

			// apply each in order to the battlestate, no movement so no wating
			battleStateModifier.applyTurnToBattleState(unit, null, battleState);
		}
		turn++;
		allyTurn = !allyTurn;
	}

	private Array<Move> generateMoves(UnitOwner balancer1) {
		return moveGenerator.generateMovesForPlayerFullAp(balancer1, battleState);
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

		return null;
	}

}
