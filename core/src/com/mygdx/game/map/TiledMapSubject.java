package com.mygdx.game.map;


public interface TiledMapSubject {
    public void addTilemapObserver(TiledMapObserver tileMapObserver);
    public void removeObserver(TiledMapObserver tileMapObserver);
    public void removeAllObservers();
    public void notifyTilemapObserver(final TiledMapObserver.TilemapCommand command);
}
