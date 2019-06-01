package com.mygdx.game.Entities;

public interface EntitySubject {
    public void addObserver(EntityObserver entityObserver);
    public void removeObserver(EntityObserver entityObserver);
    public void removeAllObservers();
    public void notify(final EntityObserver.EntityCommand command);
}
