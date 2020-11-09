package com.jelte.norii.ai.selectUnitStrategy;

import java.util.List;
import java.util.Random;

import com.jelte.norii.entities.AiEntity;

public class SelectNextUnitRandomly implements SelectUnitStrategy {
	private final Random random;

	public SelectNextUnitRandomly() {
		random = new Random();
	}

	@Override
	public AiEntity selectNextEntity(List<AiEntity> units) {
		return units.get(random.nextInt(units.size()));
	}

}
