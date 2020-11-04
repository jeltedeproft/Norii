package com.mygdx.game.AI.SelectUnitStrategy;

import java.util.List;

import com.mygdx.game.Entities.AiEntity;

public interface SelectUnitStrategy {

	public AiEntity selectNextEntity(List<AiEntity> units);
}
