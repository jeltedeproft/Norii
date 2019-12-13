package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Entities.EntityObserver.EntityCommand;
import com.mygdx.game.Map.TiledMapStage;

public class EntityClickListener extends ClickListener {
	private static final String TAG = EntityClickListener.class.getSimpleName();

    private EntityActor actor;

    public EntityClickListener(EntityActor actor) {
        this.actor = actor;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
    	actor.getEntity().notify(EntityCommand.CLICKED);
    }
    
    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
    	actor.setIsHovering(true);
    	actor.getEntity().getStatusui().setVisible(true);
    	Gdx.app.debug(TAG, "hoverriinnggg");
    }
    
    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
    	actor.setIsHovering(false);
    	actor.getEntity().getStatusui().setVisible(false);
    } 
}

