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
    
    public void setPos(TiledMapPosition _currentPlayerPosition) {
    	//this.setBounds((entity.getCurrentPosition().getRealScreenX()), (entity.getCurrentPosition().getRealScreenY()), Map.TILE_WIDTH_PIXEL,Map.TILE_HEIGHT_PIXEL);
    	this.setBounds((entity.getCurrentPosition().getRealOriginalX()), (entity.getCurrentPosition().getRealOriginalY()), Map.ORIGINAL_TILE_WIDTH_PIXEL,Map.ORIGINAL_TILE_HEIGHT_PIXEL);
    }
    
    
}
