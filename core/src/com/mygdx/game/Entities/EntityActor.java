package com.mygdx.game.Entities;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Entities.EntityObserver.EntityCommand;

import Utility.TiledMapPosition;

public class EntityActor extends Actor {
	private static final String TAG = EntityActor.class.getSimpleName();
    
    private Boolean isHovering;
    private Entity entity;
    private TiledMapPosition actorPos = new TiledMapPosition();
    

    public Entity getEntity() {
		return entity;
	}

	public Boolean getIsHovering() {
		return isHovering;
	}

	public void setIsHovering(Boolean isHovering) {
		this.isHovering = isHovering;
	}

    public EntityActor(Entity entity) {
        this.entity = entity;
        this.actorPos.setPositionFromScreen(entity.getX(), entity.getY());
        this.isHovering = false;
        entity.setEntityactor(this);
        
        this.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                setIsHovering(true);
            }
            
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                setIsHovering(false);
            }
        });
    }
    
    public void setPos() {
    	this.setBounds((entity.getCurrentPosition().getTileX()), (entity.getCurrentPosition().getTileY()), 1,1);
    }
    
	public TiledMapPosition getActorPos() {
		return actorPos;
	}  
}
