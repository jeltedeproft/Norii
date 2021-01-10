package com.jelte.norii.entities;

public class PlayerEntity extends Entity {

	public PlayerEntity(EntityTypes type) {
		super(type);
		isPlayerUnit = true;
	}

	public void setActive(final boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "PLAYER : name : " + entityData.getName() + "   ID:" + entityID + "   pos : (" + currentPlayerPosition.getTileX() + "," + currentPlayerPosition.getTileY() + ")";
	}

}
