package com.jelte.norii.ai;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.battle.battleStates.StateOfBattle;
import com.jelte.norii.entities.AiEntity;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Ability.AffectedTeams;
import com.jelte.norii.magic.Ability.AreaOfEffect;
import com.jelte.norii.magic.Ability.LineOfSight;
import com.jelte.norii.magic.Ability.Target;
import com.jelte.norii.utility.TiledMapPosition;

public class AIDecisionMaker {
	private final AITeamLeader aiTeam;
	private final boolean actionTaken = false;
	private final SortedMap<Integer, StateOfBattle> statesWithScores;

	private static final int NUMBER_OF_LAYERS = 3;

	public AIDecisionMaker(final AITeamLeader aiTeam) {
		this.aiTeam = aiTeam;
		statesWithScores = new TreeMap<>();
	}

	public void makeDecision(List<AiEntity> aiUnits, StateOfBattle stateOfBattle) {
		int iteration = NUMBER_OF_LAYERS;
		while (iteration > 0) {
			for (final AiEntity ai : aiUnits) {
				generatePossibleMoves(ai, stateOfBattle);
			}
			iteration--;
		}

		// entity.notifyEntityObserver(EntityCommand.FOCUS_CAMERA);

	}

	private void generatePossibleMoves(AiEntity ai, StateOfBattle stateOfBattle) {
		for (final Ability ability : ai.getAbilities()) {
			final StateOfBattle newState = performActionOnState(ai, stateOfBattle, ability);
			statesWithScores.put(newState.getScore(), newState);
		}
	}

	private StateOfBattle performActionOnState(AiEntity ai, StateOfBattle stateOfBattle, Ability ability) {
		final Array<Entity> targets = findTargets(ai.getCurrentPosition(), ability, stateOfBattle);
	}

	private Array<Entity> findTargets(TiledMapPosition center, Ability ability, StateOfBattle stateOfBattle) {
		final AffectedTeams affectedTeams = ability.getAffectedTeams();
		final AreaOfEffect area = ability.getAreaOfEffect();
		final LineOfSight lineOfSight = ability.getLineOfSight();
		final Target target = ability.getTarget();
		final int range = ability.getSpellData().getRange();

		final Array<Integer> possibleCenterCells = getPossibleCenterCells(center, LineOfSight);
	}

	private Array<Integer> getPossibleCenterCells(TiledMapPosition center, LineOfSight lineOfSight) {
		switch (lineOfSight) {
		case LINE:

		}
	}

	private TiledMapPosition calculateCenterOfGravity(final List<TiledMapPosition> positions) {
		final int numberOfElements = positions.size();
		int sumX = 0;
		int sumY = 0;

		for (int i = 0; i < numberOfElements; i++) {
			sumX += positions.get(i).getTileX();
			sumY += positions.get(i).getTileY();
		}

		return new TiledMapPosition().setPositionFromTiles(sumX / numberOfElements, sumY / numberOfElements);
	}

}
