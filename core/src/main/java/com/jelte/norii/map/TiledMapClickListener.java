package com.jelte.norii.map;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TiledMapClickListener extends ClickListener {

	private final TiledMapActor actor;
	private final TiledMapStage stage;

	public TiledMapClickListener(TiledMapActor actor) {
		this.actor = actor;
		stage = (TiledMapStage) actor.getStage();
	}

	@Override
	public void clicked(InputEvent event, float x, float y) {
		System.out.println("clicking tile : " + actor.getActorPos().getTileX() + " , " + actor.getActorPos().getTileY());
		stage.getBattleScreen().clickedOnTileMapActor(actor);
	}

	@Override
	public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
		actor.setIsHovered(true);
		stage.getBattleScreen().hoveredOnTileMapActor(actor);
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
