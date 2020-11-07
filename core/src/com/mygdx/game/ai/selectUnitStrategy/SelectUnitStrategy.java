package com.mygdx.game.ai.selectUnitStrategy;

import java.util.List;

import com.mygdx.game.entities.AiEntity;

public interface SelectUnitStrategy {

	public AiEntity selectNextEntity(List<AiEntity> units);
}
