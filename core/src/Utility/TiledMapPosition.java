package Utility;

import java.awt.Point;
import java.awt.geom.Point2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Map.Map;
import com.mygdx.game.Screen.BattleScreen;

public class TiledMapPosition {
	private static final String TAG = TiledMapPosition.class.getSimpleName();
	
	private Point2D realScreenCoordinates;
	private Point2D realTiledCoordinates;
	
	private Point tileCoordinates;
	
	
	public TiledMapPosition() {
		realTiledCoordinates = new Point2D.Float(0, 0);
		realScreenCoordinates = new Point2D.Float(0,0);
		
		tileCoordinates = new Point(0,0);
	}
	
	public TiledMapPosition setPositionFromTiled(float x, float y){
		realTiledCoordinates = new Point2D.Float(x, y);
		realScreenCoordinates = new Point2D.Float(x / Map.TILE_WIDTH * Map.TILE_WIDTH_PIXEL, y / Map.TILE_HEIGHT * Map.TILE_HEIGHT_PIXEL);
		
		tileCoordinates = new Point(Math.round(x  / Map.TILE_WIDTH),Math.round(y / Map.TILE_HEIGHT));
		return this;
	}
	
	public TiledMapPosition setPositionFromTiles(int x, int y){
		realTiledCoordinates = new Point2D.Float((float) (x * Map.TILE_WIDTH),(float) (y * Map.TILE_HEIGHT));
		realScreenCoordinates = new Point2D.Float(x * Map.TILE_WIDTH_PIXEL, y * Map.TILE_HEIGHT_PIXEL);
		
		tileCoordinates = new Point(x,y);
		return this;
	}
	
	public TiledMapPosition setPositionFromScreen(float x, float y){
		realScreenCoordinates = new Point2D.Float(x, y);
		realTiledCoordinates = new Point2D.Float(x / Map.TILE_WIDTH_PIXEL * Map.TILE_WIDTH, y / Map.TILE_HEIGHT_PIXEL * Map.TILE_HEIGHT);
		
		tileCoordinates = new Point(Math.round(x  / Map.TILE_WIDTH_PIXEL),Math.round(y / Map.TILE_HEIGHT_PIXEL));
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
		return (float) tileCoordinates.x * Map.TILE_WIDTH_PIXEL;
	}
	
	public float getRealScreenY() {
		return (float) tileCoordinates.y * Map.TILE_HEIGHT_PIXEL;
	}
	
	public float getRealTiledX() {
		return (float) (tileCoordinates.x * Map.TILE_WIDTH);
	}
	
	public float getRealTiledY() {
		return (float) (tileCoordinates.y * Map.TILE_HEIGHT);
	}
	
	public int getTileX() {
		return tileCoordinates.x;
	}
	
	public int getTileY() {
		return tileCoordinates.y;
	}
}

