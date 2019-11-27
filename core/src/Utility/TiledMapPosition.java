package Utility;

import java.awt.Point;
import com.mygdx.game.Map.Map;

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
		tileCoordinates = new Point(Math.round(x  / Map.TILE_WIDTH_PIXEL),Math.round(y / Map.TILE_HEIGHT_PIXEL));
		return this;
	}
	
	public TiledMapPosition setPositionFromOriginal(float x, float y){
		tileCoordinates = new Point(Math.round(x  / Map.ORIGINAL_TILE_WIDTH_PIXEL),Math.round(y / Map.ORIGINAL_TILE_HEIGHT_PIXEL));
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
	
	public float getRealOriginalX() {
		return (float) tileCoordinates.x * Map.ORIGINAL_TILE_WIDTH_PIXEL;
	}
	
	public float getRealOriginalY() {
		return (float) tileCoordinates.y * Map.ORIGINAL_TILE_HEIGHT_PIXEL;
	}
	
	public int getTileX() {
		return tileCoordinates.x;
	}
	
	public int getTileY() {
		return tileCoordinates.y;
	}
}

