package com.jelte.norii.entities;

public interface EntitySubject {
	public void addEntityObserver(EntityObserver entityObserver);

	public void removeObserver(EntityObserver entityObserver);

	public void removeAllObservers();

	public void notifyEntityObserver(final EntityObserver.EntityCommand command);
}
