package com.mygdx.game.Screen;

import java.util.ArrayList;
import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Battle.BattleManager;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntityObserver;
import com.mygdx.game.Entities.Owner;
import com.mygdx.game.Entities.Player;
import com.mygdx.game.Map.BattleMap;
import com.mygdx.game.Map.Map;
import com.mygdx.game.Map.MapManager;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;
import com.mygdx.game.Profile.ProfileManager;
import com.mygdx.game.UI.PlayerBattleHUD;

import Utility.TiledMapPosition;
import Utility.Utility;

public class BattleScreen extends GameScreen implements EntityObserver {
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

	private ArrayList<Owner> players;
	private OrthogonalTiledMapRenderer mapRenderer = null;
	private OrthographicCamera camera = null;
	private MapManager mapMgr;
	private BattleMap map;
	private BattleManager battlemanager;
	private Entity[] playerSortedUnits;
	private InputMultiplexer multiplexer;
	private OrthographicCamera hudCamera;
	private PlayerBattleHUD playerBattleHUD;

	public BattleScreen(Object... params){
		initializeVariables();
		initializeHUD();
		initializeInput(); 
		initializeMap();
		initializeUnits(params);
		initializeObservers();
	}

	private void initializeVariables() {
		playerSortedUnits = Player.getInstance().getUnitsSortedByIni(); 
	}
	
	private void initializeHUD() {
		hudCamera = new OrthographicCamera();
		hudCamera.setToOrtho(false, VIEWPORT.physicalWidth, VIEWPORT.physicalHeight);
		playerBattleHUD = new PlayerBattleHUD(hudCamera,playerSortedUnits);
	}

	private void initializeInput() {
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(Player.getInstance().getEntityStage()); 
		battlemanager = new BattleManager(multiplexer,playerSortedUnits);
		multiplexer.addProcessor(playerBattleHUD.getStage());
	}
	
	private void initializeMap() {
		mapMgr = new MapManager();
		map = (BattleMap) mapMgr.get_currentMap();
		map.setStage(battlemanager);
	}
	
	private void initializeUnits(Object... params) {
		int index = ScreenManager.ScreenParams.ARRAYLIST_OF_OWNERS.ordinal();
		if(params[index] != null) {
			players = (ArrayList<Owner>) params[index];
		}else players = new ArrayList<Owner>();
	}
	
	private void initializeObservers() {
		ProfileManager.getInstance().addObserver(playerBattleHUD);
		
		for(Owner player : players) {
			for(Entity unit : player.getTeam()){
				unit.addObserver(this);
			}
		}
	}

	@Override
	public void show() {	
		mapMgr.getCurrentTiledMap();
		
		multiplexer.addProcessor(map.getTiledMapStage());
		Gdx.input.setInputProcessor(multiplexer);
		
		setupViewport(map.getMapWidth(), map.getMapHeight());
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, map.getMapWidth(), map.getMapHeight());
		
		mapRenderer = new OrthogonalTiledMapRenderer(mapMgr.getCurrentTiledMap(), Map.UNIT_SCALE);
		mapRenderer.setView(camera);
		
		map.makeSpawnParticles();
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void render(float delta) {
		/////////////////////////// UPDATE GAME ELEMENTS///////////////////////////////////////////
		for (Owner owner : players) {
		    owner.updateUnits(delta);
		}

		battlemanager.updateController(delta);
		
		//act stages
		Player.getInstance().getEntityStage().act();
		playerBattleHUD.getStage().act();
		map.getTiledMapStage().act();

		//lock camera in middle field
		camera.position.set(map.getMapWidth()/2f, map.getMapHeight()/2f, 0f);
		camera.update();
		
		hudCamera.update();
		
		////////////////////////////////RENDERING//////////////////////////////////////////////////
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		mapRenderer.setView(camera);
		map.getTiledMapStage().getViewport().apply();
		mapRenderer.render();
		mapRenderer.getBatch().begin();
		
		//draw all units
		Player.getInstance().getEntityStage().getViewport().apply();
		for (Owner owner : players) {
			ArrayList<Entity> units = owner.getTeam();
			for (Entity entity : units) {
				if(entity.isInBattle()) {
					mapRenderer.getBatch().draw(entity.getFrame(), entity.getCurrentPosition().getTileX(), entity.getCurrentPosition().getTileY(), 1f,1f);
				}
			}	
		}
		
		mapRenderer.getBatch().end();
		
		//draw grid
		renderGrid();
		
		//draw particles
		camera.update();
		
		
		playerBattleHUD.getStage().getViewport().apply();
		Player.getInstance().getEntityStage().getViewport().apply();
		mapRenderer.getBatch().begin();
		ParticleMaker.drawAllActiveParticles((SpriteBatch) mapRenderer.getBatch(), delta);
		mapRenderer.getBatch().end();
		
		//render HUD
		playerBattleHUD.getStage().getViewport().apply();
		//playerBattleHUD.getStage().getBatch().setProjectionMatrix(mapRenderer.getBatch().getProjectionMatrix());
		playerBattleHUD.render(delta);
			
	}

	@Override
	public void resize(int width, int height) {
		Player.getInstance().getEntityStage().getViewport().update(width, height, false);
		playerBattleHUD.resize(width, height);
		map.getTiledMapStage().getViewport().update(width, height, false);
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
		for (Owner owner : players) {
			owner.dispose();
		}
		battlemanager.dispose();
		Gdx.input.setInputProcessor(null);
		mapRenderer.dispose();
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

	public void renderGrid() {
		//create a visible grid
		for(int x = 0; x < map.getMapWidth(); x += 1)
			Utility.DrawDebugLine(new Vector2(x,0), new Vector2(x,map.getMapHeight()), camera.combined);
		for(int y = 0; y < map.getMapHeight(); y += 1)
			Utility.DrawDebugLine(new Vector2(0,y), new Vector2(map.getMapWidth(),y), camera.combined);
	}

	@Override
	public void onNotify(EntityCommand command,Entity unit) {
		switch(command){
		case IN_MOVEMENT:
			List<GridCell> path = map.getPathfinder().getCellsWithin(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY(), unit.getMp());
			for(GridCell cell : path) {
				TiledMapPosition positionToPutMoveParticle = new TiledMapPosition(cell.x,cell.y);
				ParticleMaker.addParticle(ParticleType.MOVE,positionToPutMoveParticle );
			}
			break;
		default:
			break;
		}	
	}
}

