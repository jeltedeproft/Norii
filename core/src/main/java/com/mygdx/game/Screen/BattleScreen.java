package com.mygdx.game.Screen;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.AI.AITeam;
import com.mygdx.game.AI.AITeams;
import com.mygdx.game.Audio.AudioObserver;
import com.mygdx.game.Battle.BattleManager;
import com.mygdx.game.Battle.BattleScreenInputProcessor;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntityObserver;
import com.mygdx.game.Entities.EntityStage;
import com.mygdx.game.Entities.Player;
import com.mygdx.game.Entities.TeamLeader;
import com.mygdx.game.Magic.Ability;
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
import Utility.Utility;

public class BattleScreen extends GameScreen implements EntityObserver, TiledMapObserver {
	private static final String TAG = BattleScreen.class.getSimpleName();
	public static final int VISIBLE_WIDTH = 20;
	public static final int VISIBLE_HEIGHT = 20;
	private static OrthographicCamera mapCamera = null;

	private ArrayList<TeamLeader> players;
	private OrthogonalTiledMapRenderer mapRenderer = null;
	private MapManager mapMgr;
	private BattleMap currentMap;
	private BattleManager battlemanager;
	private AITeam aiTeam;
	private Entity[] playerSortedUnits;
	private InputMultiplexer multiplexer;
	private BattleScreenInputProcessor battlescreenInputProcessor;
	private OrthographicCamera hudCamera;
	private PlayerBattleHUD playerBattleHUD;
	private PauseMenuUI pauseMenu;
	private Entity[] allUnits;
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

	public BattleScreen(ArrayList<TeamLeader> players, AITeams aiTeams) {
		initializeVariables();
		initializeAI(aiTeams);
		initializeEntityStage();
		initializeHUD();
		initializePauseMenu();
		initializeInput();
		initializeUnits(players);
		initializeMap();
		initializeObservers();
		spawnAI();
	}

	private void initializeVariables() {
		playerSortedUnits = Player.getInstance().getUnitsSortedByIni();
		mapCamera = new OrthographicCamera();
		mapCamera.setToOrtho(false, VISIBLE_WIDTH, VISIBLE_HEIGHT);
		isPaused = false;
	}

	private void initializeAI(AITeams ai) {
		aiTeam = new AITeam(ai);
	}

	private void initializeEntityStage() {
		allUnits = (Entity[]) ArrayUtils.addAll(playerSortedUnits, aiTeam.getTeam().toArray());
		final EntityStage stage = new EntityStage(allUnits);
		Player.getInstance().setStage(stage);
		aiTeam.setStage(stage);
	}

	private void initializeHUD() {
		hudCamera = new OrthographicCamera();
		hudCamera.setToOrtho(false, VIEWPORT.physicalWidth, VIEWPORT.physicalHeight);
		playerBattleHUD = new PlayerBattleHUD(hudCamera, Utility.sortUnits(allUnits));
	}

	private void initializePauseMenu() {
		pauseMenu = new PauseMenuUI(hudCamera);
	}

	private void initializeInput() {
		battlescreenInputProcessor = new BattleScreenInputProcessor(this, mapCamera);
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(battlescreenInputProcessor);
		multiplexer.addProcessor(playerBattleHUD.getStage());
		multiplexer.addProcessor(Player.getInstance().getEntityStage());
		multiplexer.addProcessor(pauseMenu.getStage());
	}

	private void initializeMap() {
		battlemanager = new BattleManager(allUnits);
		battlescreenInputProcessor.setBattleManager(battlemanager);
		mapMgr = new MapManager();
		currentMap = (BattleMap) mapMgr.getCurrentMap();
		currentMap.setStage(battlemanager);
		battlemanager.setPathFinder(currentMap.getPathfinder());
	}

	private void spawnAI() {
		final ArrayList<TiledMapPosition> enemyStartPositions = currentMap.getEnemyStartPositions();
		aiTeam.spawnAiUnits(enemyStartPositions);
	}

	private void initializeUnits(ArrayList<TeamLeader> players) {
		this.players = players;
		this.players.add(aiTeam);

		for (final TeamLeader player : players) {
			for (final Entity unit : player.getTeam()) {
				unit.addEntityObserver(this);
			}
		}
	}

	private void initializeObservers() {
		ProfileManager.getInstance().addObserver(playerBattleHUD);

		for (final TiledMapActor[] tmpa : currentMap.getTiledMapStage().getTiledMapActors()) {
			for (final TiledMapActor actor : tmpa) {
				actor.addTilemapObserver(this);
			}
		}
	}

	public BattleManager getBattlemanager() {
		return battlemanager;
	}

	@Override
	public void show() {
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		mapMgr.getCurrentTiledMap();

		multiplexer.addProcessor(currentMap.getTiledMapStage());
		Gdx.input.setInputProcessor(multiplexer);

		setupViewport(VISIBLE_WIDTH, VISIBLE_HEIGHT);
		mapCamera.position.set(currentMap.getMapWidth() / 2f, currentMap.getMapHeight() / 2f, 0f);
		mapRenderer = new OrthogonalTiledMapRenderer(mapMgr.getCurrentTiledMap(), Map.UNIT_SCALE);
		mapRenderer.setView(mapCamera);
		currentMap.makeSpawnParticles();

		final StretchViewport vp = new StretchViewport(VISIBLE_WIDTH, VISIBLE_HEIGHT, mapCamera);
		currentMap.getTiledMapStage().setViewport(vp);
		Player.getInstance().getEntityStage().setViewport(vp);
		aiTeam.getEntityStage().setViewport(vp);
	}

	@Override
	public void hide() {

	}

	@Override
	public void render(final float delta) {
		if (isPaused) {
			updatePauseMenu();
			renderPauseMenu(delta);
		} else {
			updateElements(delta);
			renderElements(delta);
		}
	}

	private void updatePauseMenu() {
		pauseMenu.getStage().act();
	}

	private void renderPauseMenu(final float delta) {
		pauseMenu.getStage().getViewport().apply();
		pauseMenu.render(delta);
	}

	private void updateElements(final float delta) {
		playerBattleHUD.update();
		battlescreenInputProcessor.update();
		updateUnits(delta);
		updateStages();
		updateCameras();
	}

	private void updateUnits(final float delta) {
		for (final TeamLeader owner : players) {
			owner.updateUnits(delta);
		}
	}

	private void updateStages() {
		Player.getInstance().getEntityStage().act();
		aiTeam.getEntityStage().act();
		playerBattleHUD.getStage().act();
		currentMap.getTiledMapStage().act();
	}

	private void updateCameras() {
		mapCamera.position.x = Utility.clamp(mapCamera.position.x, currentMap.getTilemapWidthInTiles() - (mapCamera.viewportWidth / 2), 0 + (mapCamera.viewportWidth / 2));
		mapCamera.position.y = Utility.clamp(mapCamera.position.y, currentMap.getTilemapHeightInTiles() - (mapCamera.viewportWidth / 2), 0 + (mapCamera.viewportWidth / 2));
		mapCamera.update();
		hudCamera.update();
	}

	private void renderElements(final float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		renderMap();
		renderUnits();
		renderParticles(delta);
		renderHUD(delta);
	}

	private void renderMap() {
		mapRenderer.setView(mapCamera);
		currentMap.getTiledMapStage().getViewport().apply();
		mapRenderer.render();
	}

	private void renderUnits() {
		mapRenderer.getBatch().begin();
		Player.getInstance().getEntityStage().getViewport().apply();
		aiTeam.getEntityStage().getViewport().apply();
		for (final TeamLeader owner : players) {
			owner.renderUnits(mapRenderer.getBatch());
		}
		mapRenderer.getBatch().end();
	}

	private void renderParticles(final float delta) {
		mapRenderer.getBatch().begin();
		ParticleMaker.drawAllActiveParticles((SpriteBatch) mapRenderer.getBatch(), delta);
		mapRenderer.getBatch().end();
	}

	private void renderHUD(final float delta) {
		playerBattleHUD.getStage().getViewport().apply();
		playerBattleHUD.render(delta);
	}

	@Override
	public void resize(final int width, final int height) {
		currentMap.getTiledMapStage().getViewport().update(width, height, false);
		Player.getInstance().getEntityStage().getViewport().update(width, height, false);
		aiTeam.getEntityStage().getViewport().update(width, height, false);

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
		for (final TeamLeader owner : players) {
			owner.dispose();
		}
		mapRenderer.dispose();
		pauseMenu.dispose();
		currentMap.dispose();
	}

	private static void setupViewport(final int width, final int height) {
		VIEWPORT.virtualWidth = width;
		VIEWPORT.virtualHeight = height;

		VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
		VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;

		VIEWPORT.physicalWidth = Gdx.graphics.getWidth();
		VIEWPORT.physicalHeight = Gdx.graphics.getHeight();

		VIEWPORT.aspectRatio = (VIEWPORT.virtualWidth / VIEWPORT.virtualHeight);

		// letterbox
		if (VIEWPORT.physicalWidth / VIEWPORT.physicalHeight >= VIEWPORT.aspectRatio) {
			VIEWPORT.viewportWidth = VIEWPORT.viewportHeight * (VIEWPORT.physicalWidth / VIEWPORT.physicalHeight);
			VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;
		} else {
			VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
			VIEWPORT.viewportHeight = VIEWPORT.viewportWidth * (VIEWPORT.physicalHeight / VIEWPORT.physicalWidth);
		}
	}

	@Override
	public void onEntityNotify(final EntityCommand command, final Entity unit) {
		switch (command) {
		case IN_MOVEMENT:
			prepareMove(unit);
			break;
		case IN_ATTACK_PHASE:
			prepareAttack(unit);
			break;
		case UNIT_ACTIVE:
			playerBattleHUD.getPortraits().updateBorders(unit);
			break;
		case CLICKED:
			battlemanager.getCurrentBattleState().clickedOnUnit(unit);
			break;
		case SKIP:
			battlemanager.setCurrentBattleState(battlemanager.getActionBattleState());
			battlemanager.getCurrentBattleState().exit();
			break;
		case AI_ACT:
			aiTeam.aiUnitAct(unit);
			battlemanager.getCurrentBattleState().exit();
			break;
		default:
			break;
		}
	}

	@Override
	public void onEntityNotify(final EntityCommand command, final Entity unit, final Ability ability) {
		switch (command) {
		case IN_SPELL_PHASE:
			prepareSpell(unit, ability);
			break;
		default:
			break;
		}
	}

	private void prepareMove(final Entity unit) {
		final List<GridCell> path = currentMap.getPathfinder().getCellsWithinCircle(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY(), unit.getAp());
		for (final GridCell cell : path) {
			if (!isUnitOnCell(cell)) {
				final TiledMapPosition positionToPutMoveParticle = new TiledMapPosition().setPositionFromTiles(cell.x, cell.y);
				ParticleMaker.addParticle(ParticleType.MOVE, positionToPutMoveParticle);
				battlemanager.setCurrentBattleState(battlemanager.getMovementBattleState());
			}
		}
	}

	private void prepareAttack(final Entity unit) {
		final List<GridCell> attackPath = currentMap.getPathfinder().getCellsWithinCircle(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY(),
			unit.getEntityData().getAttackRange());
		for (final GridCell cell : attackPath) {
			final TiledMapPosition positionToPutAttackParticle = new TiledMapPosition().setPositionFromTiles(cell.x, cell.y);
			ParticleMaker.addParticle(ParticleType.ATTACK, positionToPutAttackParticle);
			battlemanager.setCurrentBattleState(battlemanager.getAttackBattleState());
		}
	}

	private void prepareSpell(final Entity unit, final Ability ability) {
		final ArrayList<TiledMapPosition> positions = Utility.collectPositionsUnits(players);
		final List<GridCell> spellPath = calculateSpellPath(unit, ability, positions);

		for (final GridCell cell : spellPath) {
			final TiledMapPosition positionToPutSpellParticle = new TiledMapPosition().setPositionFromTiles(cell.x, cell.y);
			ParticleMaker.addParticle(ParticleType.SPELL, positionToPutSpellParticle);
		}

		battlemanager.setCurrentSpell(ability);
		battlemanager.setCurrentBattleState(battlemanager.getSpellBattleState());
	}

	private List<GridCell> calculateSpellPath(final Entity unit, final Ability ability, final ArrayList<TiledMapPosition> positions) {
		List<GridCell> spellPath = null;

		switch (ability.getLineOfSight()) {
		case CIRCLE:
			spellPath = currentMap.getPathfinder().getLineOfSightWithinCircle(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY(),
				ability.getSpellData().getRange(), positions);
			break;
		case CROSS:
			// TODO
			break;
		case LINE:
			spellPath = currentMap.getPathfinder().getLineOfSightWithinLine(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY(),
				ability.getSpellData().getRange(), unit.getEntityAnimation().getCurrentDirection(), positions);
			break;
		default:
			break;
		}
		return spellPath;
	}

	private boolean isUnitOnCell(final GridCell cell) {
		final TiledMapPosition cellToTiled = new TiledMapPosition().setPositionFromTiles(cell.x, cell.y);
		for (final Entity entity : allUnits) {
			if (entity.getCurrentPosition().isTileEqualTo(cellToTiled)) {
				return true;
			}
		}
		return false;
	}

	public static OrthographicCamera getCamera() {
		return mapCamera;
	}

	@Override
	public void onTiledMapNotify(final TilemapCommand command, final TiledMapPosition pos) {
		switch (command) {
		case HOVER_CHANGED:
			playerBattleHUD.getTileHoverImage().setPosition(pos.getCameraX(), pos.getCameraY());
			break;
		}
	}
}
