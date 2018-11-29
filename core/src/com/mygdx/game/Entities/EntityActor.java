package com.mygdx.game.Entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.Map.Map;

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
    
    public void setPos(Vector2 pos) {//bind actor met unit en zorg ervoor dat als unit beweegt, actor meebeweegt!!
    	this.setPosition(pos.x, pos.y);
    	this.setBounds(entity.getCurrentPosition().x / Map.UNIT_SCALE, entity.getCurrentPosition().y / Map.UNIT_SCALE, 1 / Map.UNIT_SCALE,1 / Map.UNIT_SCALE);
    }
}
