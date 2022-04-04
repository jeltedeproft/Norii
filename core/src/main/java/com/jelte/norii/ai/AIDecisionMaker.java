package com.jelte.norii.ai;

import java.util.SortedMap;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.movegenerator.MoveGenerator;
import com.jelte.norii.ai.movegenerator.TargetMoveGenerator;
import com.jelte.norii.battle.battlestate.BattleState;
import com.jelte.norii.battle.battlestate.BattleStateModifier;
import com.jelte.norii.battle.battlestate.Move;
import com.jelte.norii.battle.battlestate.SpellMove;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.utility.Utility;

public class AIDecisionMaker {
	private static final String TAG = AIDecisionMaker.class.getSimpleName();

	private static final int NUMBER_OF_LAYERS = 3;
	private static final int MAX_AI_THINKING_TIME = 20000;
	private static final int RANDOMISATION_TOP_X_STATES = 5;
	private static final int SAVE_TOP_X_STATES_EVERY_ROUND = 15;

	private final SortedMap<Integer, BattleState> statesWithScores;
	private final Array<Array<BattleState>> allBattleStates = new Array<>();
	private int turnIndex;
	private int entityIndex;
	private int battleStateIndex;
	private int numberOfBattleStatesThisRound;
	private BattleState currentBattleState;
	private MoveGenerator abilityTargetMoveGenerator;
	private Entity unit;
	private BattleState startingState;
	private Long processingTimeCounter = 0L;
	private Long oldTime = 0L;

	private BattleStateModifier battleStateModifier = new BattleStateModifier();

	public AIDecisionMaker() {
		abilityTargetMoveGenerator = new TargetMoveGenerator();
		statesWithScores = new TreeMap<>();
		for (int i = 0; i < NUMBER_OF_LAYERS; i++) {
			allBattleStates.add(new Array<>());
		}
	}

	public void resetAI(BattleState battleState) {
		battleState.calculateScore();
		currentBattleState = battleState;
		statesWithScores.clear();
		turnIndex = 0;
		numberOfBattleStatesThisRound = 1;
		processingTimeCounter = 0L;
		for (int i = 0; i < NUMBER_OF_LAYERS; i++) {
			allBattleStates.get(i).clear();
		}
	}

	// returns true when finished or time is up
	public boolean processAi() {
		oldTime = System.currentTimeMillis();
		System.out.println("a");
		final Long startingTime = System.currentTimeMillis();

		updateBattlestateAndEntityIfNecessary();
		System.out.println("b");
		oldTime = TargetMoveGenerator.debugTime("\n\ngenerating moves for : " + unit, oldTime);
		generateBattleStatesForUnit(unit, startingState);
		oldTime = TargetMoveGenerator.debugTime("moves generated for unit : " + unit, oldTime);

		entityIndex++;

		nextBattlestateIfAllUnitsDone();// all units done?

		if (isRoundFinished()) {
			oldTime = TargetMoveGenerator.debugTime("\n\nNEXT ROUND", oldTime);
			prepareNextRound();

			if (turnIndex >= NUMBER_OF_LAYERS) {
				oldTime = TargetMoveGenerator.debugTime("\n\nALL STATES DONE", oldTime);
				return true;
			}
		}

		// check timer
		processingTimeCounter += (System.currentTimeMillis() - startingTime);
		if (processingTimeCounter > MAX_AI_THINKING_TIME) {
			oldTime = TargetMoveGenerator.debugTime("\n\nEARLY CUTOFF TO TIME", oldTime);
		}
		return processingTimeCounter > MAX_AI_THINKING_TIME;
	}

	private void updateBattlestateAndEntityIfNecessary() {
		if (turnIndex == 0) {
			startingState = currentBattleState;
		} else {
			startingState = allBattleStates.get(turnIndex - 1).get(battleStateIndex);
		}

		// if turn is even, play AI
		if ((turnIndex % 2) == 0) {
			unit = startingState.getAiUnits().get(entityIndex);
		} else {
			unit = startingState.getPlayerUnits().get(entityIndex);
		}
	}

	private void prepareNextRound() {
		Array<BattleState> statesFromLastRound = allBattleStates.get(turnIndex);
		statesFromLastRound.sort();
		statesFromLastRound = limitNumberOfStatesTo(allBattleStates.get(turnIndex), SAVE_TOP_X_STATES_EVERY_ROUND);
		reduceModifierCount(statesFromLastRound);
		turnIndex++;
		battleStateIndex = 0;
		entityIndex = 0;
		numberOfBattleStatesThisRound = statesFromLastRound.size;
	}

	private Array<BattleState> limitNumberOfStatesTo(Array<BattleState> array, int size) {
		Array<BattleState> smallerArray = new Array<>();
		int i = 0;
		while ((i < size) && (i < array.size)) {
			smallerArray.add(array.get(i));
			i++;
		}
		return smallerArray;
	}

	private void nextBattlestateIfAllUnitsDone() {
		int size = (turnIndex % 2) == 0 ? startingState.getAiUnits().size
										: startingState.getPlayerUnits().size;

		if (entityIndex >= size) {
			oldTime = TargetMoveGenerator.debugTime("\n\nNEXT BATTLESTATE", oldTime);
			battleStateIndex++;
			entityIndex = 0;
		}
	}

	private void generateBattleStatesForUnit(Entity unit, BattleState startingState) {
		int oldScore = startingState.calculateScore();
		Ability ability = unit.getAbility();
		final Array<UnitTurn> turns = abilityTargetMoveGenerator.getAllMovesUnit(ability, unit, startingState);
		for (final UnitTurn turn : turns) {
			String abilityString = "";
			for (Move move : turn.getMoves()) {// this whole block is for the debug line can be removed
				if (move instanceof SpellMove) {
					SpellMove spellMove = (SpellMove) move;
					abilityString = spellMove.getAbility().getName();
				}
			}
			final BattleState newState = battleStateModifier.applyTurnToBattleState(unit, turn, startingState);
			newState.setTurn(turn);
			Gdx.app.debug(TAG, "score went from " + oldScore + " to " + newState.calculateScore() + "for ability: " + abilityString);
			if (turnIndex != 0) {
				newState.setParentState(startingState);
			}
			allBattleStates.get(turnIndex).add(newState);
		}
	}

	private boolean isRoundFinished() {
		return turnIndex == 0	? (battleStateIndex > 0)
								: (battleStateIndex >= (numberOfBattleStatesThisRound - 1));
	}

	public BattleState getResult() {
		Array<BattleState> resultStates = allBattleStates.get(NUMBER_OF_LAYERS - 1);

		int i = 1;
		while (resultStates.isEmpty() && (i <= NUMBER_OF_LAYERS)) {
			i++;
			resultStates = allBattleStates.get(NUMBER_OF_LAYERS - i);
		}
		allBattleStates.get(NUMBER_OF_LAYERS - i).sort();
		Gdx.app.debug(TAG, "RESULTS FOR LAYER : " + (NUMBER_OF_LAYERS - i));
		Gdx.app.debug(TAG, "==========================================================");
		int pos = 1;
		for (BattleState state : allBattleStates.get(NUMBER_OF_LAYERS - i)) {
			Gdx.app.debug("", "current state =  " + state.getTurn() + " --> on level : " + (NUMBER_OF_LAYERS - i));
			for (Move move : getInitialMoves(state).getTurn().getMoves()) {
				if (move instanceof SpellMove) {
					SpellMove spell = (SpellMove) move;
					Gdx.app.debug(TAG, "move " + pos + ") initial spell = " + spell.getAbility() + " with endscore : " + state.getScore() + "\n");

				} else {
					Gdx.app.debug(TAG, "move " + pos + ") initial move = " + move.getLocation() + " with endscore : " + state.getScore() + "\n");

				}
			}
			pos++;
		}

		int stateWePick = selectBattlestateFromResults(i);
		Gdx.app.debug(TAG, "we pick state : " + stateWePick);
		Gdx.app.debug(TAG, "which is : " + getInitialMoves(allBattleStates.get(NUMBER_OF_LAYERS - i).get(stateWePick)).getTurn());
		return getInitialMoves(allBattleStates.get(NUMBER_OF_LAYERS - i).get(stateWePick));
	}

	private int selectBattlestateFromResults(int i) {
		int totalNumberOfStates = allBattleStates.get(NUMBER_OF_LAYERS - i).size;
		return totalNumberOfStates < RANDOMISATION_TOP_X_STATES ? Utility.random.nextInt(totalNumberOfStates)
																: Utility.random.nextInt(RANDOMISATION_TOP_X_STATES);
	}

	private BattleState getInitialMoves(BattleState battleState) {
		BattleState initialState = battleState;
		while (initialState.getParentState() != null) {
			initialState = initialState.getParentState();
		}
		return initialState;
	}

	private void reduceModifierCount(Array<BattleState> battleStates) {
		for (final BattleState battleState : battleStates) {
			battleState.reduceModifierCounts();
		}
	}

	@Override
	public String toString() {
		return "state of the decision maker \n___________________\n\n" + "turn index = " + turnIndex + "\n" + "entity index = " + entityIndex + "\n" + "battleState index = " + battleStateIndex + "\n" + "number of battle states = "
				+ numberOfBattleStatesThisRound + "\n" + "starting state = \n" + startingState + "\n" + "current battlestate = \n" + currentBattleState + "\n" + "unit = " + unit.getEntityData().getName() + "\n";
	}

}
