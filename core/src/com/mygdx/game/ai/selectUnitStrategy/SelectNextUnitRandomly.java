package com.mygdx.game.ai.selectUnitStrategy;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import com.mygdx.game.entities.AiEntity;

public class SelectNextUnitRandomly implements SelectUnitStrategy {
	private Random random;

	public SelectNextUnitRandomly() {
		try {
			random = SecureRandom.getInstanceStrong();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	@Override
	public AiEntity selectNextEntity(List<AiEntity> units) {
		return units.get(random.nextInt(units.size()));
	}

}
