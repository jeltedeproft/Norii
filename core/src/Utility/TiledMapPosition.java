package Utility;

import java.awt.Point;
import java.awt.geom.Point2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Map.Map;
import com.mygdx.game.Screen.BattleScreen;

public class TiledMapPosition {
	private static final String TAG = TiledMapPosition.class.getSimpleName();
	
	private Point2D realCoordinates;
	private Point tileCoordinates;
	
	public TiledMapPosition() {
		
	}
	
	public TiledMapPosition(float x,float y) {
		realCoordinates = new Point2D.Float(x,y);
		tileCoordinates = new Point((int) (x * Map.UNIT_SCALE),(int) (y * Map.UNIT_SCALE));
	}
	
	
	public TiledMapPosition(int x,int y) {
		realCoordinates = new Point2D.Float((float) (x / Map.UNIT_SCALE),(float) (y / Map.UNIT_SCALE));
		tileCoordinates = new Point(x,y);
	}
	
	public TiledMapPosition(Vector2 pos) {
		realCoordinates = new Point2D.Float(pos.x,pos.y);
		tileCoordinates = new Point((int) (pos.x * Map.UNIT_SCALE),(int) (pos.y * Map.UNIT_SCALE));
	}

	public void setPosition(float x, float y) {
		realCoordinates = new Point2D.Float(x,y);
		tileCoordinates = new Point((int) (x * Map.UNIT_SCALE),(int) (y * Map.UNIT_SCALE));
	}
	
	public void setPosition(int x, int y) {
		realCoordinates = new Point2D.Float((float) (x / Map.UNIT_SCALE),(float) (y / Map.UNIT_SCALE));
		tileCoordinates = new Point(x,y);
	}
	
	public Point2D getPositionAsRealCoordinates() {
		return realCoordinates;
	}
	
	public Point getPositionAsTileCoordinates() {
		return tileCoordinates;
	}
	
	public void moveStepsUpward(int steps){
		float currentRealX = (float) realCoordinates.getX();
		float currentRealY = (float) realCoordinates.getY();
		realCoordinates.setLocation(currentRealX, currentRealY + steps / Map.UNIT_SCALE);
		
		int currentTileX = tileCoordinates.x;
		int currentTileY = tileCoordinates.y;
		tileCoordinates.setLocation(currentTileX, currentTileY + steps);
	}
	
	public void moveStepsDownward(int steps){
		float currentRealX = (float) realCoordinates.getX();
		float currentRealY = (float) realCoordinates.getY();
		realCoordinates.setLocation(currentRealX, currentRealY - steps / Map.UNIT_SCALE);
		
		int currentTileX = tileCoordinates.x;
		int currentTileY = tileCoordinates.y;
		tileCoordinates.setLocation(currentTileX, currentTileY - steps);
	}
	
	public void moveStepsLeft(int steps){
		float currentRealX = (float) realCoordinates.getX();
		float currentRealY = (float) realCoordinates.getY();
		realCoordinates.setLocation(currentRealX - steps / Map.UNIT_SCALE, currentRealY);
		
		int currentTileX = tileCoordinates.x;
		int currentTileY = tileCoordinates.y;
		tileCoordinates.setLocation(currentTileX - steps, currentTileY);
	}
	
	public void moveStepsRight(int steps){
		float currentRealX = (float) realCoordinates.getX();
		float currentRealY = (float) realCoordinates.getY();
		realCoordinates.setLocation(currentRealX + steps / Map.UNIT_SCALE, currentRealY);
		
		int currentTileX = tileCoordinates.x;
		int currentTileY = tileCoordinates.y;
		tileCoordinates.setLocation(currentTileX + steps, currentTileY);
	}
	
	public void scl(float scalingNumber) {
		float oldRealX = this.getRealX();
		float oldRealY = this.getRealY();
		this.setPosition(oldRealX * scalingNumber, oldRealY * scalingNumber);
	}
	
	public boolean isTileEqualTo(TiledMapPosition pos) {
		Gdx.app.debug(TAG, "testing if positions are equal");
		Gdx.app.debug(TAG, "x : " + pos.tileCoordinates.x + " , " + tileCoordinates.x);
		Gdx.app.debug(TAG, "y : " + pos.tileCoordinates.y + " , " + tileCoordinates.y);
		return((pos.tileCoordinates.x == tileCoordinates.x) && (pos.tileCoordinates.y == tileCoordinates.y));
	}
	
	public boolean isTileEqualToScaled(TiledMapPosition pos) {
		Gdx.app.debug(TAG, "testing if positions are equal");
		Gdx.app.debug(TAG, "x : " + pos.tileCoordinates.x + " , " + tileCoordinates.x);
		Gdx.app.debug(TAG, "y : " + pos.tileCoordinates.y + " , " + tileCoordinates.y);
		return((pos.tileCoordinates.x == tileCoordinates.x) && (pos.tileCoordinates.y == tileCoordinates.y));
	}
	
	public float getRealX() {
		return (float) realCoordinates.getX();
	}
	
	public float getRealY() {
		return (float) realCoordinates.getY();
	}
	
	public int getTileX() {
		return tileCoordinates.x;
	}
	
	public int getTileY() {
		return tileCoordinates.y;
	}
	
	public static float getDownScaledX(float coor) {
		return ((coor / (float) Map.TILE_WIDTH_PIXEL) / (float) Map.UNIT_SCALE);
	}
	
	public static float getDownScaledY(float coor) {
		return ((coor / (float) Map.TILE_HEIGHT_PIXEL) / (float) Map.UNIT_SCALE);
	}
	
	public static float getUpScaledX(float coor) {
		return ((coor * (float) Map.TILE_WIDTH_PIXEL) * (float) Map.UNIT_SCALE);
	}
	
	public static float getUpScaledY(float coor) {
		return ((coor * (float) Map.TILE_HEIGHT_PIXEL) * (float) Map.UNIT_SCALE);
	}
}

