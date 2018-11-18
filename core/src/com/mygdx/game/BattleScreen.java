package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Entity;
import com.mygdx.game.MapManager;
import com.mygdx.game.InputController;

public class BattleScreen implements Screen {
	private static final String TAG = BattleScreen.class.getSimpleName();

	private static class VIEWPORT {
		static float viewportWidth;
		static float viewportHeight;
		static float virtualWidth;
		static float virtualHeight;
		static float physicalWidth;
		static float physicalHeight;
		static float aspectRatio;
	}

	private ArrayList<Owner> _players;

	private OrthogonalTiledMapRenderer _mapRenderer = null;
	private OrthographicCamera _camera = null;
	private static MapManager _mapMgr;
	
	//current map stuff
	private TiledMap tiledMap;
	private MapProperties prop;
	private BattleManager battlemanager;
	
	private int mapWidth;
	private int mapHeight;
	private int tilePixelWidth;
	private int tilePixelHeight;

	public BattleScreen(){
		_mapMgr = new MapManager();
	}

	

	@Override
	public void show() {
		
		//setup map stuff
		tiledMap = _mapMgr.getCurrentMap();
		prop = tiledMap.getProperties();
		battlemanager = new BattleManager();
		
		mapWidth = prop.get("width", Integer.class);
		mapHeight = prop.get("height", Integer.class);
		tilePixelWidth = prop.get("tilewidth", Integer.class);
		tilePixelHeight = prop.get("tileheight", Integer.class);
		
		//_camera setup
		setupViewport(mapWidth, mapHeight);
		
		//get the current size
		_camera = new OrthographicCamera();
		_camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);

		_mapRenderer = new OrthogonalTiledMapRenderer(_mapMgr.getCurrentMap(), MapManager.UNIT_SCALE);
		_mapRenderer.setView(_camera);

		Gdx.app.debug(TAG, "UnitScale value is: " + _mapRenderer.getUnitScale());

		_players = new ArrayList<Owner>();
		
		//init units
		for (Owner owner : _players) {
		    owner.initUnits();
		}
	}

	@Override
	public void hide() {
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//Preferable to lock and center the _camera to the middle of the field
		_camera.position.set(mapWidth/2, mapHeight/2, 0f);
		_camera.update();

		for (Owner owner : _players) {
		    owner.updateUnits(delta);
		}

		battlemanager.updateController(delta);

		//_mapRenderer.getBatch().enableBlending();
		//_mapRenderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		_mapRenderer.setView(_camera);
		_mapRenderer.render();

		_mapRenderer.getBatch().begin();
		
		//draw all units
		for (Owner owner : _players) {
			ArrayList<Entity> units = owner.getTeam();
			for (Entity entity : units) {
				_mapRenderer.getBatch().draw(entity.getFrame(), entity.getFrameSprite().getX(), entity.getFrameSprite().getY(), 1,1);
			}
			
		}
		//draw grid
		renderGrid(Gdx.graphics);
		
		_mapRenderer.getBatch().end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		//dispose all units
		for (Owner owner : _players) {
			ArrayList<Entity> units = owner.getTeam();
			for (Entity entity : units) {
				entity.dispose();
			}	
		}
		battlemanager.dispose();
		Gdx.input.setInputProcessor(null);
		_mapRenderer.dispose();
	}

	private void setupViewport(int width, int height){
		//Make the viewport a percentage of the total display area
		VIEWPORT.virtualWidth = width;
		VIEWPORT.virtualHeight = height;

		//Current viewport dimensions
		VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
		VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;

		//pixel dimensions of display
		VIEWPORT.physicalWidth = Gdx.graphics.getWidth();
		VIEWPORT.physicalHeight = Gdx.graphics.getHeight();

		//aspect ratio for current viewport
		VIEWPORT.aspectRatio = (VIEWPORT.virtualWidth / VIEWPORT.virtualHeight);

		//update viewport if there could be skewing
		if( VIEWPORT.physicalWidth / VIEWPORT.physicalHeight >= VIEWPORT.aspectRatio){
			//Letterbox left and right
			VIEWPORT.viewportWidth = VIEWPORT.viewportHeight * (VIEWPORT.physicalWidth/VIEWPORT.physicalHeight);
			VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;
		}else{
			//letterbox above and below
			VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
			VIEWPORT.viewportHeight = VIEWPORT.viewportWidth * (VIEWPORT.physicalHeight/VIEWPORT.physicalWidth);
		}

		Gdx.app.debug(TAG, "WorldRenderer: virtual: (" + VIEWPORT.virtualWidth + "," + VIEWPORT.virtualHeight + ")" );
		Gdx.app.debug(TAG, "WorldRenderer: viewport: (" + VIEWPORT.viewportWidth + "," + VIEWPORT.viewportHeight + ")" );
		Gdx.app.debug(TAG, "WorldRenderer: physical: (" + VIEWPORT.physicalWidth + "," + VIEWPORT.physicalHeight + ")" );
	}

	public void renderGrid(Graphics g) {
		//create a visible grid
		for(int x = 0; x < mapWidth; x += tilePixelWidth)
			Utility.DrawDebugLine(new Vector2(x,0), new Vector2(x,mapHeight), _camera.combined);
		for(int y = 0; y < mapHeight; y += tilePixelHeight)
			Utility.DrawDebugLine(new Vector2(0,y), new Vector2(mapWidth,y), _camera.combined);
	}


}

