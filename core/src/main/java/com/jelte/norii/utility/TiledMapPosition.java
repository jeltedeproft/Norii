package com.jelte.norii.utility;

import com.badlogic.gdx.Gdx;
import com.jelte.norii.map.Map;
import com.jelte.norii.screen.BattleScreen;

public class TiledMapPosition {
	private MyPoint tileCoordinates;

	public TiledMapPosition() {
		tileCoordinates = new MyPoint(0, 0);
	}

	public TiledMapPosition setPositionFromTiled(float x, float y) {
		tileCoordinates = new MyPoint(Math.round(x / Map.TILE_WIDTH), Math.round(y / Map.TILE_HEIGHT));
		return this;
	}

	public TiledMapPosition setPositionFromTiles(int x, int y) {
		tileCoordinates = new MyPoint(x, y);
		return this;
	}

	public TiledMapPosition setPositionFromScreen(float x, float y) {
		tileCoordinates = new MyPoint(Math.round(x / (Gdx.graphics.getWidth() / (float) BattleScreen.VISIBLE_WIDTH)), Math.round(y / (Gdx.graphics.getHeight() / (float) BattleScreen.VISIBLE_HEIGHT)));
		return this;
	}

	public boolean isTileEqualTo(TiledMapPosition pos) {
		return ((pos.tileCoordinates.x == tileCoordinates.x) && (pos.tileCoordinates.y == tileCoordinates.y));
	}

	public boolean isTileEqualTo(MyPoint myPoint) {
		return ((myPoint.x == tileCoordinates.x) && (myPoint.y == tileCoordinates.y));
	}

	public boolean isTileEqualTo(int x, int y) {
		return ((x == tileCoordinates.x) && (y == tileCoordinates.y));
	}

	public int getDistance(TiledMapPosition pos) {
		return (Math.abs(tileCoordinates.x - pos.getTileX()) + (Math.abs(tileCoordinates.y - pos.getTileY())));
	}

	public float getRealTiledX() {
		return (float) tileCoordinates.x * Map.TILE_WIDTH;
	}

	public float getRealTiledY() {
		return (float) tileCoordinates.y * Map.TILE_HEIGHT;
	}

	public int getTileX() {
		return tileCoordinates.x;
	}

	public int getTileY() {
		return tileCoordinates.y;
	}

	public TiledMapPosition changeX(int change) {
		tileCoordinates.x += change;
		return this;
	}

	public TiledMapPosition changeY(int change) {
		tileCoordinates.y += change;
		return this;
	}

	public MyPoint getTilePosAsPoint() {
		return new MyPoint(tileCoordinates.x, tileCoordinates.y);
	}

	public TiledMapPosition setPosFromString(String pos) {
		String[] coordinates = pos.split(",");
		int x = Integer.parseInt(coordinates[0].substring(1));
		int y = Integer.parseInt(coordinates[1].substring(0, coordinates[1].length() - 1));
		tileCoordinates = new MyPoint(x, y);
		return this;
	}

	@Override
	public String toString() {
		return "[" + tileCoordinates.x + "," + tileCoordinates.y + "]";
	}
}
