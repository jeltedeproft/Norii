package com.jelte.norii.entities;

public class AiEntity extends Entity {
	public AiEntity(EntityTypes type) {
		super(type);
		isPlayerUnit = false;
	}

	@Override
	public void notifyEntityObserver(final EntityObserver.EntityCommand command) {
		for (int i = 0; i < entityObservers.size; i++) {
			entityObservers.get(i).onEntityNotify(command, this);
		}
	}

	@Override
	public String toString() {
		return "AI : name : " + entityData.getName() + "   ID:" + entityID + "   pos : (" + currentPlayerPosition.getTileX() + "," + currentPlayerPosition.getTileY() + ")";
	}

}
