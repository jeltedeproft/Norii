package com.mygdx.game.Entities;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Map.Map;

import Utility.TiledMapPosition;

public class EntityActor extends Actor {
	private static final String TAG = EntityActor.class.getSimpleName();
    
    private Boolean isHovering;
    private Entity entity;
    

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
    
    public void setPos(TiledMapPosition _currentPlayerPosition) {//bind actor movement with unit
    	this.setPosition(_currentPlayerPosition.getRealStageX(), _currentPlayerPosition.getRealStageY());
    	this.setBounds(entity.getCurrentPosition().getRealStageX(), entity.getCurrentPosition().getRealStageY(), 1 / Map.UNIT_SCALE,1 / Map.UNIT_SCALE);
    }
    
    
}
