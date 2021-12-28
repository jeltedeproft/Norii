package com.jelte.norii.battle.battlephase;

import com.jelte.norii.entities.Entity;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.map.TiledMapActor;

public abstract class BattlePhase {
	protected Ability ability;

	public abstract void exit();

	protected BattlePhase() {
	}

	public void entry() {

	}

	public void update() {

	}

	public void clickedOnTile(TiledMapActor actor) {

	}

	public void clickedOnUnit(Entity entity) {

	}

	public void keyPressed(int key) {

	}

	public void buttonPressed(int button) {

	}

	public void hoveredOnTile(TiledMapActor actor) {
		// no-op
	}

	public void setAbility(Ability ability) {
		this.ability = ability;
	}
}
