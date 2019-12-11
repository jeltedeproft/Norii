package com.mygdx.game.Screen;

import java.util.ArrayList;
import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.Battle.BattleManager;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntityObserver;
import com.mygdx.game.Entities.Owner;
import com.mygdx.game.Entities.Player;
import com.mygdx.game.Map.BattleMap;
import com.mygdx.game.Map.Map;
import com.mygdx.game.Map.MapManager;
import com.mygdx.game.Map.TiledMapActor;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;
import com.mygdx.game.Profile.ProfileManager;
import com.mygdx.game.UI.PauseMenuUI;
import com.mygdx.game.UI.PlayerBattleHUD;

import Utility.TiledMapPosition;

public class BattleScreen extends GameScreen implements EntityObserver {
	private static final String TAG = BattleScreen.class.getSimpleName();
	
	public static final int VISIBLE_WIDTH = 50; 
	public static final int VISIBLE_HEIGHT = 50; 

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
	private PauseMenuUI pauseMenu;
	
	private static class VIEWPORT {
		static float viewportWidth;
		static float viewportHeight;
		static float virtualWidth;
		static float virtualHeight;
		static float physicalWidth;
		static float physicalHeight;
		static float aspectRatio;
	}

	public BattleScreen(Object... params){
		initializeVariables();
		initializeHUD();
		initializePauseMenu();
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
	
	private void initializePauseMenu() {
		pauseMenu = new PauseMenuUI(hudCamera);
	}

	private void initializeInput() {
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(Player.getInstance().getEntityStage()); 
		battlemanager = new BattleManager(multiplexer,playerSortedUnits);
		multiplexer.addProcessor(playerBattleHUD.getStage());
		multiplexer.addProcessor(pauseMenu.getStage());
	}
	
	private void initializeMap() {
		mapMgr = new MapManager();
		map = (BattleMap) mapMgr.getCurrentMap();
		map.setStage(battlemanager);
	}
	
	@SuppressWarnings("unchecked")
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
	
	private void handleInput() {
		if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
			pauseMenu.setVisible(pauseMenu.getVisible());
		}
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
			float x = camera.position.x;
			
			float y = camera.position.y + 1;
			float z = camera.position.z;
			camera.position.set(x, y, z);
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
			float x = camera.position.x;
			float y = camera.position.y - 1;
			float z = camera.position.z;
			camera.position.set(x, y, z);
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
			float x = camera.position.x - 1;
			float y = camera.position.y;
			float z = camera.position.z;
			camera.position.set(x, y, z);
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
			float x = camera.position.x + 1;
			float y = camera.position.y;
			float z = camera.position.z;
			camera.position.set(x, y, z);
		}
	}

	@Override
	public void show() {	
		resize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		mapMgr.getCurrentTiledMap();
		
		multiplexer.addProcessor(map.getTiledMapStage());
		Gdx.input.setInputProcessor(multiplexer);
		
		setupViewport(VISIBLE_WIDTH, VISIBLE_HEIGHT);
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, VISIBLE_WIDTH, VISIBLE_HEIGHT);
		camera.position.set(map.getMapWidth()/2f, map.getMapHeight()/2f, 0f);
		
		mapRenderer = new OrthogonalTiledMapRenderer(mapMgr.getCurrentTiledMap(), Map.UNIT_SCALE);
		mapRenderer.setView(camera);
		
		map.makeSpawnParticles();
		StretchViewport vp = new StretchViewport(VISIBLE_WIDTH, VISIBLE_HEIGHT, camera);
		map.getTiledMapStage().setViewport(vp);
		playerBattleHUD.getStage().setViewport(vp);

	}

	@Override
	public void hide() {
		
	}

	@Override
	public void render(float delta) {
		updateElements(delta);
		renderElements(delta);
		handleInput();
	}

	private void updateElements(float delta) {
		updateUnits(delta);	
		updateStages();
		updateCameras();
	}
	
	private void updateUnits(float delta) {
		for (Owner owner : players) {
		    owner.updateUnits(delta);
		}

		battlemanager.updateController();
	}
	
	private void updateStages() {
		Player.getInstance().getEntityStage().act();
		playerBattleHUD.getStage().act();
		map.getTiledMapStage().act();
		pauseMenu.getStage().act();
	}

	private void updateCameras() {
		camera.position.x = clamp(camera.position.x, Map.TILEMAP_WIDTH_IN_TILES - (camera.viewportWidth / 2), 0 + (camera.viewportWidth / 2));
		camera.position.y = clamp(camera.position.y, Map.TILEMAP_HEIGHT_IN_TILES - (camera.viewportWidth / 2), 0 + (camera.viewportWidth / 2));
		camera.update();
		hudCamera.update();
	}
	
	private float clamp(float var, float max, float min) {
	    if(var > min) {
	        if(var < max) {
	            return var;
	        } else return max;
	    } else return min;
	}
	
	private void renderElements(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		renderMap();
		renderUnits();
		renderParticles(delta);
		renderHUD(delta);
	}

	private void renderMap() {
		mapRenderer.setView(camera);
		map.getTiledMapStage().getViewport().apply();
		mapRenderer.render();
	}

	private void renderUnits() {
		mapRenderer.getBatch().begin();
		Player.getInstance().getEntityStage().getViewport().apply();
		for (Owner owner : players) {
			ArrayList<Entity> units = (ArrayList<Entity>) owner.getTeam();
			for (Entity entity : units) {
				if(entity.isInBattle()) {
					mapRenderer.getBatch().draw(entity.getFrame(), entity.getCurrentPosition().getTileX(), entity.getCurrentPosition().getTileY(), 1f,1f);
				}
			}	
		}
		mapRenderer.getBatch().end();
	}
	
	private void renderParticles(float delta) {
		mapRenderer.getBatch().begin();
		ParticleMaker.drawAllActiveParticles((SpriteBatch) mapRenderer.getBatch(), delta);
		mapRenderer.getBatch().end();
	}

	private void renderHUD(float delta) {
		renderTileHover();
		playerBattleHUD.getStage().getViewport().apply();
		playerBattleHUD.render(delta);
		pauseMenu.getStage().getViewport().apply();
		pauseMenu.render(delta);
	}
	
	private void renderTileHover() {
		for(Actor actor :  map.getTiledMapStage().getActors()) {
			TiledMapActor tiledActor = (TiledMapActor) actor;
			if(tiledActor.getIsHovered()) {
				playerBattleHUD.getTileHoverImage().setPosition(tiledActor.getActorPos().getRealScreenX(), tiledActor.getActorPos().getRealScreenY());
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		map.updatePixelDimensions();
		map.getTiledMapStage().getViewport().update(width, height, false);
		
		Player.getInstance().getEntityStage().getViewport().update(width, height, false);
		playerBattleHUD.resize(width, height);
		pauseMenu.resize(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		for (Owner owner : players) {
			owner.dispose();
		}
		battlemanager.dispose();
		mapRenderer.dispose();
		pauseMenu.dispose();
		map.dispose();
	}

	private static void setupViewport(int width, int height){
		//part of display
		VIEWPORT.virtualWidth = width;
		VIEWPORT.virtualHeight = height;

		//Current
		VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
		VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;

		//pixels screen
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
	}

	@Override
	public void onNotify(EntityCommand command,Entity unit) {
		switch(command){
		case IN_MOVEMENT:
			prepareMove(unit);
			break;
		case IN_ATTACK_PHASE:
			prepareAttack(unit);
			break;
		case IN_ACTION_PHASE:
			playerBattleHUD.getPortraits().updateBorders(unit);
			break;
		case CLICKED:
			battlemanager.getCurrentBattleState().clickedOnUnit(unit);
			break;
		case SKIP:
			battlemanager.setCurrentBattleState(battlemanager.getActionBattleState());
			battlemanager.getCurrentBattleState().exit();
			break;
		default:
			break;
		}	

	}

	private void prepareMove(Entity unit) {
		List<GridCell> path = map.getPathfinder().getCellsWithin(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY(), unit.getMp());
		for(GridCell cell : path) {
			if(!isUnitOnCell(cell)) {
				TiledMapPosition positionToPutMoveParticle = new TiledMapPosition().setPositionFromTiles(cell.x,cell.y);
				ParticleMaker.addParticle(ParticleType.MOVE,positionToPutMoveParticle );
				battlemanager.setCurrentBattleState(battlemanager.getMovementBattleState());
			}
		}
	}

	private void prepareAttack(Entity unit) {
		List<GridCell> attackPath = map.getPathfinder().getCellsWithin(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY(), unit.getAttackRange());
		for(GridCell cell : attackPath) {
			TiledMapPosition positionToPutAttackParticle = new TiledMapPosition().setPositionFromTiles(cell.x,cell.y);
			ParticleMaker.addParticle(ParticleType.ATTACK,positionToPutAttackParticle);
			battlemanager.setCurrentBattleState(battlemanager.getAttackBattleState());
		}
	}
	
	private boolean isUnitOnCell(GridCell cell) {
		TiledMapPosition cellToTiled = new TiledMapPosition().setPositionFromTiles(cell.x,cell.y);
		for(Entity entity : battlemanager.getUnits()) {
			if(entity.getCurrentPosition().isTileEqualTo(cellToTiled)) {
				return true;
			}
		}
		return false; 
	}
}

