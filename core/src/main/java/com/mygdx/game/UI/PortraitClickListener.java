package com.mygdx.game.UI;


import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Entities.Entity;

public class PortraitClickListener extends ClickListener{
	Entity entity;
	
	public PortraitClickListener(Entity entity) {
		this.entity = entity;
	}
	
    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
    	entity.getStatusui().setVisible(true);
    }
    
    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
    	entity.getStatusui().setVisible(false);
    } 

}
