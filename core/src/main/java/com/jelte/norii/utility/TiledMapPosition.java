package com.jelte.norii.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

	public boolean isTileEqualTo(MyPoint MyPoint) {
		return ((MyPoint.x == tileCoordinates.x) && (MyPoint.y == tileCoordinates.y));
	}

	public int getDistance(TiledMapPosition pos) {
		return (Math.abs(tileCoordinates.x - pos.getTileX()) + (Math.abs(tileCoordinates.y - pos.getTileY())));
	}

	public float getCameraX() {
		final OrthographicCamera cam = BattleScreen.getCamera();
		final float xDifference = cam.position.x - (cam.viewportWidth / 2);
		return (tileCoordinates.x - xDifference) * (Gdx.graphics.getWidth() / (float) BattleScreen.VISIBLE_WIDTH);
	}

	public float getCameraY() {
		final OrthographicCamera cam = BattleScreen.getCamera();
		final float yDifference = cam.position.y - (cam.viewportHeight / 2);
		return (tileCoordinates.y - yDifference) * (Gdx.graphics.getHeight() / (float) BattleScreen.VISIBLE_HEIGHT);
	}

	public float getRealTiledX() {
		return tileCoordinates.x * Map.TILE_WIDTH;
	}

	public float getRealTiledY() {
		return tileCoordinates.y * Map.TILE_HEIGHT;
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

	@Override
	public String toString() {
		return "[" + tileCoordinates.x + "," + tileCoordinates.y + "]";
	}
}
