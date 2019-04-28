package com.mygdx.game.Entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
        entity.setEntityactor(this);
    }
    
    public void setPos(TiledMapPosition _currentPlayerPosition) {//bind actor movement with unit
    	this.setPosition(_currentPlayerPosition.getRealX(), _currentPlayerPosition.getRealY());
    	this.setBounds(entity.getCurrentPosition().getRealX() / Map.UNIT_SCALE, entity.getCurrentPosition().getRealY() / Map.UNIT_SCALE, 1 / Map.UNIT_SCALE,1 / Map.UNIT_SCALE);
    }
}
