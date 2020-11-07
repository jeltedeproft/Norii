package com.mygdx.game.Entities;

import com.badlogic.gdx.scenes.scene2d.Actor;

import Utility.TiledMapPosition;

public class EntityActor extends Actor {
	private Boolean isHovering;
	private Boolean isActionsHovering;
	private final Entity entity;
	private final TiledMapPosition actorPos = new TiledMapPosition();

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
		isActionsHovering = false;
		entity.setEntityactor(this);
	}

	public void setPos() {
		this.setBounds((entity.getCurrentPosition().getTileX()), (entity.getCurrentPosition().getTileY()), 1, 1);
	}

	public TiledMapPosition getActorPos() {
		return actorPos;
	}

	public void setActionsHovering(boolean isActionsHovering) {
		this.isActionsHovering = isActionsHovering;
	}

	public boolean isActionsHovering() {
		return isActionsHovering;
	}

	@Override
	public String toString() {
		return "name : " + entity.getEntityData().getName() + "   pos:" + entity.getCurrentPosition().getTileX() + " , " + entity.getCurrentPosition().getTileY();
	}
}
