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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.Audio.AudioObserver;
import com.mygdx.game.Battle.BattleManager;
import com.mygdx.game.Battle.BattleScreenInputProcessor;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntityObserver;
import com.mygdx.game.Entities.Owner;
import com.mygdx.game.Entities.Player;
import com.mygdx.game.Map.BattleMap;
import com.mygdx.game.Map.Map;
import com.mygdx.game.Map.MapManager;
import com.mygdx.game.Map.TiledMapActor;
import com.mygdx.game.Map.TiledMapObserver;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;
import com.mygdx.game.Profile.ProfileManager;
import com.mygdx.game.UI.PauseMenuUI;
import com.mygdx.game.UI.PlayerBattleHUD;

import Utility.TiledMapPosition;

public class BattleScreen extends GameScreen implements EntityObserver,TiledMapObserver {
	public static final int VISIBLE_WIDTH = 20; 
	public static final int VISIBLE_HEIGHT = 20; 

	private ArrayList<Owner> players;
	private OrthogonalTiledMapRenderer mapRenderer = null;
	private static OrthographicCamera mapCamera = null;
	private MapManager mapMgr;
	private BattleMap map;
	private BattleManager battlemanager;
	private Entity[] playerSortedUnits;
	private InputMultiplexer multiplexer;
	private BattleScreenInputProcessor battlescreenInputProcessor;
	private OrthographicCamera hudCamera;
	private PlayerBattleHUD playerBattleHUD;
	private PauseMenuUI pauseMenu;
	private boolean isPaused;
	
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
		initializePlayerStage();
		initializeHUD();
		initializePauseMenu();
		initializeInput(); 
		initializeMap();
		initializeUnits(params);
		initializeObservers();
	}

	private void initializeVariables() {
		playerSortedUnits = Player.getInstance().getUnitsSortedByIni();
		mapCamera = new OrthographicCamera();
		mapCamera.setToOrtho(false, VISIBLE_WIDTH, VISIBLE_HEIGHT);
		isPaused = false;
	}
	
	private void initializePlayerStage() {
		Player.getInstance().setStage();
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
		battlescreenInputProcessor = new BattleScreenInputProcessor(this,mapCamera);
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(battlescreenInputProcessor);
		multiplexer.addProcessor(Player.getInstance().getEntityStage()); 
		multiplexer.addProcessor(playerBattleHUD.getStage());
		multiplexer.addProcessor(pauseMenu.getStage());
	}
	
	private void initializeMap() {
		battlemanager = new BattleManager(multiplexer,playerSortedUnits);
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
				unit.addEntityObserver(this);
			}
		}
		
		for(TiledMapActor[] tmpa : map.getTiledMapStage().getTiledMapActors()){
			for(TiledMapActor actor : tmpa) {
				actor.addTilemapObserver(this);
			}
		}
	}
	
	@Override
	public void show() {	
		resize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		mapMgr.getCurrentTiledMap();
		
		multiplexer.addProcessor(map.getTiledMapStage());
		Gdx.input.setInputProcessor(multiplexer);
		
		setupViewport(VISIBLE_WIDTH, VISIBLE_HEIGHT);	
		mapCamera.position.set(map.getMapWidth()/2f, map.getMapHeight()/2f, 0f);
		mapRenderer = new OrthogonalTiledMapRenderer(mapMgr.getCurrentTiledMap(), Map.UNIT_SCALE);
		mapRenderer.setView(mapCamera);
		map.makeSpawnParticles();
		
		StretchViewport vp = new StretchViewport(VISIBLE_WIDTH, VISIBLE_HEIGHT, mapCamera);
		map.getTiledMapStage().setViewport(vp);
		Player.getInstance().getEntityStage().setViewport(vp);
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void render(float delta) {
		if(isPaused) {
			updatePauseMenu();
			renderPauseMenu(delta);
		}else {
			Player.getInstance().getEntityStage().drawEntitiesDebug();
			updateElements(delta);
			renderElements(delta);
		}
	}
	
	private void updatePauseMenu() {
		pauseMenu.getStage().act();
	}
	
	private void renderPauseMenu(float delta) {
		pauseMenu.getStage().getViewport().apply();
		pauseMenu.render(delta);
	}

	private void updateElements(float delta) {
		playerBattleHUD.update();
		battlescreenInputProcessor.update();
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
	}

	private void updateCameras() {
		mapCamera.position.x = clamp(mapCamera.position.x, map.getTilemapWidthInTiles() - (mapCamera.viewportWidth / 2), 0 + (mapCamera.viewportWidth / 2));
		mapCamera.position.y = clamp(mapCamera.position.y, map.getTilemapHeightInTiles() - (mapCamera.viewportWidth / 2), 0 + (mapCamera.viewportWidth / 2));
		mapCamera.update();
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
		mapRenderer.setView(mapCamera);
		map.getTiledMapStage().getViewport().apply();
		mapRenderer.render();
	}

	private void renderUnits() {
		mapRenderer.getBatch().begin();
		Player.getInstance().getEntityStage().getViewport().apply();
		for (Owner owner : players) {
			owner.renderUnits(mapRenderer.getBatch());
		}
		mapRenderer.getBatch().end();
	}
	
	private void renderParticles(float delta) {
		mapRenderer.getBatch().begin();
		ParticleMaker.drawAllActiveParticles((SpriteBatch) mapRenderer.getBatch(), delta);
		mapRenderer.getBatch().end();
	}

	private void renderHUD(float delta) {
		playerBattleHUD.getStage().getViewport().apply();
		playerBattleHUD.render(delta);
	}

	@Override
	public void resize(int width, int height) {
		map.getTiledMapStage().getViewport().update(width, height, false);
		
		Player.getInstance().getEntityStage().getViewport().update(width, height, false);
		playerBattleHUD.resize(width, height);
		pauseMenu.resize(width, height);
	}

	@Override
	public void pause() {
		isPaused = true;
		notify(AudioObserver.AudioCommand.MUSIC_PAUSE, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
		pauseMenu.setVisible(true);
	}

	@Override
	public void resume() {
		isPaused = false;
		notify(AudioObserver.AudioCommand.MUSIC_RESUME, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
		pauseMenu.setVisible(false);
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
		VIEWPORT.virtualWidth = width;
		VIEWPORT.virtualHeight = height;

		VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
		VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;

		
		VIEWPORT.physicalWidth = Gdx.graphics.getWidth();
		VIEWPORT.physicalHeight = Gdx.graphics.getHeight();

		VIEWPORT.aspectRatio = (VIEWPORT.virtualWidth / VIEWPORT.virtualHeight);

		//letterbox
		if( VIEWPORT.physicalWidth / VIEWPORT.physicalHeight >= VIEWPORT.aspectRatio){
			VIEWPORT.viewportWidth = VIEWPORT.viewportHeight * (VIEWPORT.physicalWidth/VIEWPORT.physicalHeight);
			VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;
		}else{
			VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
			VIEWPORT.viewportHeight = VIEWPORT.viewportWidth * (VIEWPORT.physicalHeight/VIEWPORT.physicalWidth);
		}
	}

	@Override
	public void onEntityNotify(EntityCommand command,Entity unit) {
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
		List<GridCell> path = map.getPathfinder().getCellsWithin(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY(), unit.getAp());
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
	
	public static OrthographicCamera getCamera() {
		return mapCamera;
	}

	@Override
	public void onTiledMapNotify(TilemapCommand command, TiledMapPosition pos) {
		switch(command){
			case HOVER_CHANGED:
				playerBattleHUD.getTileHoverImage().setPosition(pos.getCameraX(), pos.getCameraY());
				break;
		}
	}
}

