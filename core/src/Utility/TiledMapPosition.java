package Utility;

import java.awt.Point;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.Map.Map;
import com.mygdx.game.Screen.BattleScreen;

public class TiledMapPosition {
	private static final String TAG = TiledMapPosition.class.getSimpleName();
	
	private Point tileCoordinates;
	
	
	public TiledMapPosition() {
		tileCoordinates = new Point(0,0);
	}
	
	public TiledMapPosition setPositionFromTiled(float x, float y){
		tileCoordinates = new Point(Math.round(x  / Map.TILE_WIDTH),Math.round(y / Map.TILE_HEIGHT));
		return this;
	}
	
	public TiledMapPosition setPositionFromTiles(int x, int y){
		tileCoordinates = new Point(x,y);
		return this;
	}
	
	public TiledMapPosition setPositionFromScreen(float x, float y){
		tileCoordinates = new Point(Math.round(x  / (Gdx.graphics.getWidth() / (float) BattleScreen.VISIBLE_WIDTH)),Math.round(y / (Gdx.graphics.getHeight() / (float) BattleScreen.VISIBLE_HEIGHT)));
		return this;
	}
	
	public TiledMapPosition setPositionFromOriginal(float x, float y){
		tileCoordinates = new Point(Math.round(x  / (Gdx.graphics.getWidth() / (float) BattleScreen.VISIBLE_WIDTH)),Math.round(y / (Gdx.graphics.getHeight() / (float) BattleScreen.VISIBLE_HEIGHT)));
		return this;
	}
	
	
	public void scl(float scalingNumber) {
		float oldRealX = this.getRealScreenX();
		float oldRealY = this.getRealScreenY();
		this.setPositionFromScreen(oldRealX * scalingNumber, oldRealY * scalingNumber);
	}
	
	public boolean isTileEqualTo(TiledMapPosition pos) {
		return((pos.tileCoordinates.x == tileCoordinates.x) && (pos.tileCoordinates.y == tileCoordinates.y));
	}
	
	public float getRealScreenX() {
		return (float) tileCoordinates.x * (Gdx.graphics.getWidth() / (float) BattleScreen.VISIBLE_WIDTH);
	}
	
	public float getRealScreenY() {
		return (float) tileCoordinates.y * (Gdx.graphics.getHeight() / (float) BattleScreen.VISIBLE_HEIGHT);
	}
	
	public float getRealTiledX() {
		return (float) (tileCoordinates.x * Map.TILE_WIDTH);
	}
	
	public float getRealTiledY() {
		return (float) (tileCoordinates.y * Map.TILE_HEIGHT);
	}
	
	public float getRealOriginalX() {
		return (float) tileCoordinates.x * (Gdx.graphics.getWidth() / (float) BattleScreen.VISIBLE_WIDTH);
	}
	
	public float getRealOriginalY() {
		return (float) tileCoordinates.y * (Gdx.graphics.getHeight() / (float) BattleScreen.VISIBLE_HEIGHT);
	}
	
	public int getTileX() {
		return tileCoordinates.x;
	}
	
	public int getTileY() {
		return tileCoordinates.y;
	}
}

