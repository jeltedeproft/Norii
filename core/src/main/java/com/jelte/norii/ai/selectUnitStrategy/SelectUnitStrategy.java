package com.jelte.norii.ai.selectUnitStrategy;

import java.util.List;

import com.jelte.norii.entities.AiEntity;

public interface SelectUnitStrategy {

	public AiEntity selectNextEntity(List<AiEntity> units);
}
