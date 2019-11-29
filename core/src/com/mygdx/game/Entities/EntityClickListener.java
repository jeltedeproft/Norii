package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class EntityClickListener extends ClickListener {
	private static final String TAG = EntityClickListener.class.getSimpleName();

    private EntityActor actor;

    public EntityClickListener(EntityActor actor) {
        this.actor = actor;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
    	Gdx.app.debug(TAG, "clicking on actor");
    }
    
    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
    	actor.setIsHovering(true);
    	actor.getEntity().getStatusui().setVisible(true);
    }
    
    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
    	actor.setIsHovering(false);
    	actor.getEntity().getStatusui().setVisible(false);
    } 
}

