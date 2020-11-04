package com.mygdx.game.Map;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TiledMapClickListener extends ClickListener {

	private TiledMapActor actor;
	private TiledMapStage stage;

	public TiledMapClickListener(TiledMapActor actor) {
		this.actor = actor;
		stage = (TiledMapStage) actor.getStage();
	}

	@Override
	public void clicked(InputEvent event, float x, float y) {
		stage.getBattlemanager().getCurrentBattleState().clickedOnTile(actor);
	}

	@Override
	public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
		actor.setIsHovered(true);
	}

	@Override
	public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
		actor.setIsHovered(false);
	}

	@Override
	public String toString() {
		return "tiledMapClickListener : (" + actor.getX() + "," + actor.getY() + ")";
	}
}
