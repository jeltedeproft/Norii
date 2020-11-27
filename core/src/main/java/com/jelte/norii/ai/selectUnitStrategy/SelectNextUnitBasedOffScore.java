package com.jelte.norii.ai.selectUnitStrategy;

import java.util.List;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Sort;
import com.jelte.norii.entities.AiEntity;

public class SelectNextUnitBasedOffScore implements SelectUnitStrategy {

	public SelectNextUnitBasedOffScore() {

	}

	@Override
	public AiEntity selectNextEntity(List<AiEntity> units) {
		final Array<Integer> scores = new Array<>();
		for (final AiEntity aiunit : units) {
			scores.add(aiunit.getNextMoveScore());
		}

		Sort.instance().sort(scores);
	}

}
