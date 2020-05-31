package com.mygdx.game.Entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class EntityStage extends Stage {
	private final List<Entity> entities;

	public EntityStage(final Entity[] entities) {
		this.entities = new ArrayList<Entity>(Arrays.asList(entities));
		createActorsForEntities();
	}

	private void createActorsForEntities() {
		for (int x = 0; x < entities.size(); x++) {
			final Entity entity = entities.get(x);
			final EntityActor actor = new EntityActor(entity);
			initializeActor(actor);
		}
	}

	private void initializeActor(final EntityActor actor) {
		final Entity entity = actor.getEntity();
		// DANGER : I assume every entity has a 32x32 size (map tile width x map tile height)
		actor.setBounds(entity.getCurrentPosition().getTileX(), entity.getCurrentPosition().getTileY(), 1, 1);
		addActor(actor);
		final EventListener eventListener = new EntityClickListener(actor);
		actor.addListener(eventListener);
	}

	public void drawEntitiesDebug() {
		final Array<Actor> actors = getActors();
		final ShapeRenderer debugRenderer = new ShapeRenderer();
		debugRenderer.setProjectionMatrix(getCamera().combined);
		debugRenderer.setColor(Color.RED);
		debugRenderer.begin(ShapeType.Line);
		for (final Actor actor : actors) {
			actor.debug();
			actor.drawDebug(debugRenderer);
		}
		debugRenderer.end();
	}
}
