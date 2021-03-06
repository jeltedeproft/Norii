package com.jelte.norii.screen;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jelte.norii.ai.AITeamLeader;
import com.jelte.norii.ai.AITeams;
import com.jelte.norii.audio.AudioCommand;
import com.jelte.norii.audio.AudioManager;
import com.jelte.norii.audio.AudioTypeEvent;
import com.jelte.norii.battle.BattleManager;
import com.jelte.norii.battle.BattleScreenInputProcessor;
import com.jelte.norii.battle.battleState.BattleStateGridHelper;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityStage;
import com.jelte.norii.entities.Player;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Ability.LineOfSight;
import com.jelte.norii.map.BattleMap;
import com.jelte.norii.map.Map;
import com.jelte.norii.map.MapManager;
import com.jelte.norii.map.MyPathFinder;
import com.jelte.norii.map.TiledMapActor;
import com.jelte.norii.particles.ParticleMaker;
import com.jelte.norii.particles.ParticleType;
import com.jelte.norii.ui.Hud;
import com.jelte.norii.ui.HudMessageTypes;
import com.jelte.norii.ui.MessageToBattleScreen;
import com.jelte.norii.utility.AssetManagerUtility;
import com.jelte.norii.utility.MyPoint;
import com.jelte.norii.utility.TiledMapPosition;
import com.jelte.norii.utility.Utility;

public class BattleScreen extends GameScreen {
	public static final int VISIBLE_WIDTH = 21;
	public static final int VISIBLE_HEIGHT = 21;
	private static OrthographicCamera mapCamera = null;

	private OrthogonalTiledMapRenderer mapRenderer = null;
	private SpriteBatch spriteBatch;
	private MapManager mapMgr;
	private BattleMap currentMap;
	private BattleManager battlemanager;
	private AITeamLeader aiTeamLeader;
	private InputMultiplexer multiplexer;
	private BattleScreenInputProcessor battlescreenInputProcessor;
	private OrthographicCamera hudCamera;
	private Hud hud;
	private PauseMenuScreen pauseMenu;

	private EntityStage entityStage;
	private final String fpsTitle = "fps = ";

	private boolean isPaused;

	public BattleScreen(AITeams aiTeams) {
		initializeVariables();
		initializeAI(aiTeams);
		initializeEntityStage();
		initializeHUD(aiTeams);
		initializePauseMenu();
		initializeInput();
		initializeMap();
		spawnAI();
	}

	private void initializeVariables() {
		mapCamera = new OrthographicCamera();
		mapCamera.setToOrtho(false, VISIBLE_WIDTH, VISIBLE_HEIGHT);
		spriteBatch = new SpriteBatch(900);
		isPaused = false;
		mapMgr = new MapManager();
		currentMap = (BattleMap) mapMgr.getCurrentMap();
	}

	private void initializeAI(AITeams ai) {
		aiTeamLeader = new AITeamLeader(ai);
	}

	private void initializeEntityStage() {
		Player.getInstance().initializeTeam();
		entityStage = new EntityStage(Stream.concat(Player.getInstance().getTeam().stream(), aiTeamLeader.getTeam().stream()).collect(Collectors.toList()));
	}

	private void initializeHUD(AITeams aiTeams) {
		hudCamera = new OrthographicCamera();
		hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		hud = new Hud(Player.getInstance().getTeam(), aiTeamLeader.getTeam(), spriteBatch, currentMap.getMapWidth(), currentMap.getMapHeight(), this, (aiTeams == AITeams.TUTORIAL));
		if (aiTeams == AITeams.TUTORIAL) {
			hud.getHudMessages().showInfoWindow(HudMessageTypes.DEPLOY_UNITS_INFO);
		}
	}

	private void initializePauseMenu() {
		pauseMenu = new PauseMenuScreen(hudCamera, this, spriteBatch);
	}

	private void initializeInput() {
		battlescreenInputProcessor = new BattleScreenInputProcessor(this, mapCamera);
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(battlescreenInputProcessor);
		multiplexer.addProcessor(hud.getStage());
		multiplexer.addProcessor(entityStage);
		multiplexer.addProcessor(pauseMenu.getStage());
	}

	private void initializeMap() {
		battlemanager = new BattleManager(aiTeamLeader, currentMap.getMapWidth(), currentMap.getMapHeight(), currentMap.getNavLayer().getUnwalkableNodes(), this);
		battlescreenInputProcessor.setBattleManager(battlemanager);
		currentMap.setStage(this);
		MyPathFinder.getInstance().setMap(currentMap);
	}

	private void spawnAI() {
		final List<TiledMapPosition> enemyStartPositions = currentMap.getEnemyStartPositions();
		aiTeamLeader.spawnAiUnits(enemyStartPositions);
	}

	public BattleManager getBattlemanager() {
		return battlemanager;
	}

	@Override
	public void show() {
		AudioManager.getInstance().onNotify(AudioCommand.MUSIC_STOP, AudioTypeEvent.MUSIC_TITLE2);
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		mapMgr.getCurrentTiledMap();

		multiplexer.addProcessor(currentMap.getTiledMapStage());
		Gdx.input.setInputProcessor(multiplexer);

		mapCamera.position.set(currentMap.getMapWidth() / 2f, currentMap.getMapHeight() / 2f, 0f);
		mapRenderer = new OrthogonalTiledMapRenderer(mapMgr.getCurrentTiledMap(), Map.UNIT_SCALE, spriteBatch);
		mapRenderer.setView(mapCamera);
		currentMap.makeSpawnParticles();

		final FitViewport vp = new FitViewport(VISIBLE_WIDTH, VISIBLE_HEIGHT, mapCamera);
		currentMap.getTiledMapStage().setViewport(vp);
		entityStage.setViewport(vp);
	}

	@Override
	public void hide() {
		// no-op
	}

	@Override
	public void render(final float delta) {
		// System.out.println("max sprites in batch : " +
		// spriteBatch.maxSpritesInBatch);
		// System.out.println("spritebatch render calls : " + spriteBatch.renderCalls);
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
		hud.update(battlemanager.getUnits());
		hud.updateApBar(Player.getInstance().getAp());
		battlescreenInputProcessor.update();
		battlemanager.getCurrentBattleState().update();
		updateUnits(delta);
		updateStages();
		updateCameras();
		processAi();
	}

	private void updateUnits(final float delta) {
		Player.getInstance().updateUnits(delta);
		aiTeamLeader.updateUnits(delta);
	}

	private void updateStages() {
		entityStage.act();
		hud.getStage().act();
		currentMap.getTiledMapStage().act();
	}

	private void updateCameras() {
		mapCamera.position.x = Utility.clamp(mapCamera.position.x, currentMap.getTilemapWidthInTiles() - (mapCamera.viewportWidth / 2), 0 + (mapCamera.viewportWidth / 2));
		mapCamera.position.y = Utility.clamp(mapCamera.position.y, currentMap.getTilemapHeightInTiles() - (mapCamera.viewportHeight / 2), 0 + (mapCamera.viewportHeight / 2));
		mapCamera.update();
		hudCamera.update();
	}

	private void processAi() {
		battlemanager.processAI();
	}

	private void renderElements(final float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.graphics.setTitle(fpsTitle + Gdx.graphics.getFramesPerSecond());
		renderMap();
		renderUnits();
		renderParticles(delta);
		renderGrid();
		renderHUD(delta);
	}

	private void renderMap() {
		spriteBatch.setProjectionMatrix(mapCamera.combined);
		mapRenderer.setView(mapCamera);
		currentMap.getTiledMapStage().getViewport().apply();
		mapRenderer.render();
	}

	private void renderUnits() {
		spriteBatch.begin();

		if (battlemanager.getActiveUnit().getVisualComponent().isActive()) {
			spriteBatch.draw(AssetManagerUtility.getSprite("purple"), battlemanager.getActiveUnit().getVisualComponent().getEntityactor().getX(), battlemanager.getActiveUnit().getVisualComponent().getEntityactor().getY(), 1.0f, 1.0f);
		}
		entityStage.getViewport().apply();
		Player.getInstance().renderUnits(spriteBatch);
		aiTeamLeader.renderUnits(spriteBatch);
	}

	private void renderParticles(final float delta) {
		ParticleMaker.drawAllActiveParticles(spriteBatch, delta);
		spriteBatch.end();
	}

	private void renderHUD(final float delta) {
		spriteBatch.setProjectionMatrix(hud.getStage().getCamera().combined);
		hud.getStage().getViewport().apply();
		hud.render(delta);
	}

	public void renderGrid() {
		for (int x = 0; x < currentMap.getMapWidth(); x += 1)
			Utility.drawDebugLine(new Vector2(x, 0), new Vector2(x, currentMap.getMapHeight()), mapCamera.combined);
		for (int y = 0; y < currentMap.getMapHeight(); y += 1)
			Utility.drawDebugLine(new Vector2(0, y), new Vector2(currentMap.getMapWidth(), y), mapCamera.combined);
	}

	@Override
	public void resize(final int width, final int height) {
		currentMap.getTiledMapStage().getViewport().update(width, height, false);
		entityStage.getViewport().update(width, height, false);

		hud.resize(width, height);
		pauseMenu.resize(width, height);
	}

	@Override
	public void pause() {
		isPaused = true;
		AudioManager.getInstance().onNotify(AudioCommand.MUSIC_PAUSE, AudioTypeEvent.MUSIC_BATTLE);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_STOP, AudioTypeEvent.WALK_LOOP);
		pauseMenu.setVisible(true);
	}

	@Override
	public void resume() {
		isPaused = false;
		AudioManager.getInstance().onNotify(AudioCommand.MUSIC_RESUME, AudioTypeEvent.MUSIC_BATTLE);
		pauseMenu.setVisible(false);
	}

	@Override
	public void dispose() {
		Player.getInstance().dispose();
		aiTeamLeader.dispose();
		mapRenderer.dispose();
		pauseMenu.dispose();
		currentMap.dispose();
	}

	private void prepareMove(final Entity unit) {
		final Set<MyPoint> pointsToMove = BattleStateGridHelper.getInstance().getPossibleCenterCellsFiltered(unit.getCurrentPosition().getTilePosAsPoint(), LineOfSight.CIRCLE, unit.getAp(), battlemanager.getBattleState());
		MyPathFinder.getInstance().filterPositionsByWalkability(unit, pointsToMove);
		for (final MyPoint cell : pointsToMove) {
			if (!isUnitOnCell(cell)) {
				final TiledMapPosition positionToPutMoveParticle = new TiledMapPosition().setPositionFromTiles(cell.x, cell.y);
				ParticleMaker.addParticle(ParticleType.MOVE, positionToPutMoveParticle, 0);
				battlemanager.setCurrentBattleState(battlemanager.getMovementBattleState());
			}
		}
	}

	private void prepareAttack(final Entity unit) {
		final Set<MyPoint> pointsToAttack = BattleStateGridHelper.getInstance().getPossibleCenterCells(unit.getCurrentPosition().getTilePosAsPoint(), LineOfSight.CIRCLE, unit.getEntityData().getAttackRange());
		for (final MyPoint cell : pointsToAttack) {
			final TiledMapPosition positionToPutAttackParticle = new TiledMapPosition().setPositionFromTiles(cell.x, cell.y);
			ParticleMaker.addParticle(ParticleType.ATTACK, positionToPutAttackParticle, 0);
		}
		battlemanager.setCurrentBattleState(battlemanager.getAttackBattleState());
	}

	private void prepareSpell(final Entity unit, final Ability ability) {
		final List<TiledMapPosition> positions = battlemanager.getUnits().stream().map(Entity::getCurrentPosition).collect(Collectors.toList());
		final Set<MyPoint> spellPath = BattleStateGridHelper.getInstance().calculateSpellPath(unit, ability, positions, battlemanager.getBattleState());

		for (final MyPoint cell : spellPath) {
			final TiledMapPosition positionToPutSpellParticle = new TiledMapPosition().setPositionFromTiles(cell.x, cell.y);
			ParticleMaker.addParticle(ParticleType.SPELL, positionToPutSpellParticle, 0);
		}

		battlemanager.getSpellBattleState().setAbility(ability);
		battlemanager.setCurrentBattleState(battlemanager.getSpellBattleState());
	}

	private boolean isUnitOnCell(final MyPoint cell) {
		for (final Entity entity : battlemanager.getBattleState().getAllUnits()) {
			if (entity.getCurrentPosition().isTileEqualTo(cell)) {
				return true;
			}
		}
		return false;
	}

	public static OrthographicCamera getCamera() {
		return mapCamera;
	}

	public boolean isPaused() {
		return isPaused;
	}

	public void clickedOnTileMapActor(TiledMapActor actor) {
		battlemanager.getCurrentBattleState().clickedOnTile(actor);
	}

	public void hoveredOnTileMapActor(TiledMapActor actor) {
		battlemanager.getCurrentBattleState().hoveredOnTile(actor);
		hud.setPositionTileHover(actor.getActorPos().getTileX(), actor.getActorPos().getTileY());
	}

	public void messageFromUi(MessageToBattleScreen message, int entityID, Ability ability) {
		if (!hud.isLocked()) {
			switch (message) {
			case CLICKED_ON_SKIP:
				final Entity skipEntity = battlemanager.getEntityByID(entityID);
				skipEntity.getVisualComponent().setActive(false);
				skipEntity.setFocused(false);
				skipEntity.getVisualComponent().setLocked(false);
				battlemanager.setLockedUnit(null);
				battlemanager.setCurrentBattleState(battlemanager.getActionBattleState());
				battlemanager.getCurrentBattleState().exit();
				break;
			case CLICKED_ON_MOVE:
				final Entity moveEntity = battlemanager.getEntityByID(entityID);
				if (moveEntity.canMove()) {
					prepareMove(moveEntity);
				} else {
					hud.getHudMessages().showPopup(HudMessageTypes.NOT_ENOUGH_AP);
				}
				break;
			case CLICKED_ON_ATTACK:
				final Entity attackEntity = battlemanager.getEntityByID(entityID);
				if ((attackEntity.getAp() >= attackEntity.getEntityData().getBasicAttackCost()) && attackEntity.canAttack()) {
					prepareAttack(attackEntity);
				} else {
					hud.getHudMessages().showPopup(HudMessageTypes.NOT_ENOUGH_AP);
				}
				break;
			case CLICKED_ON_ABILITY:
				final Entity spellEntity = battlemanager.getEntityByID(entityID);
				if (spellEntity.getAp() >= ability.getSpellData().getApCost()) {
					hud.setLocked(true);
					prepareSpell(spellEntity, ability);
					hud.setLocked(false);
				} else {
					hud.getHudMessages().showPopup(HudMessageTypes.NOT_ENOUGH_AP);
				}
				break;
			case HOVERED_ON_MOVE:
				final Entity showMoveEntity = battlemanager.getEntityByID(entityID);
				final Set<MyPoint> pointsToMove = BattleStateGridHelper.getInstance().getPossibleCenterCellsFiltered(showMoveEntity.getCurrentPosition().getTilePosAsPoint(), LineOfSight.CIRCLE, showMoveEntity.getAp(), battlemanager.getBattleState());
				MyPathFinder.getInstance().filterPositionsByWalkability(showMoveEntity, pointsToMove);
				for (final MyPoint cell : pointsToMove) {
					if (!isUnitOnCell(cell)) {
						final TiledMapPosition positionToPutMoveParticle = new TiledMapPosition().setPositionFromTiles(cell.x, cell.y);
						ParticleMaker.addParticle(ParticleType.MOVE, positionToPutMoveParticle, 0);
						battlemanager.setCurrentBattleState(battlemanager.getMovementBattleState());
					}
				}
				break;
			case STOP_HOVERED_ON_MOVE:
				ParticleMaker.deactivateAllParticlesOfType(ParticleType.MOVE);
				break;
			}
		}
	}

	public void messageFromBattleManager(com.jelte.norii.battle.MessageToBattleScreen message, Entity entity) {
		switch (message) {
		case UNIT_ACTIVE:
			hud.getHudMessages().showNextTutorialMessage();
			hud.updateBottomBar(entity);
			break;
		case UPDATE_UI:
			hud.update(battlemanager.getUnits());
			hud.updateBottomBar(entity);
			break;
		case SET_CHARACTER_HUD:
			hud.getPortraitAndStats().setHero(entity);
			break;
		case UNSET_CHARACTER_HUD:
			hud.getPortraitAndStats().setHero(null);
			break;
		case INVALID_SPAWN_POINT:
			hud.getHudMessages().showPopup(HudMessageTypes.INVALID_SPAWN_POINT);
			break;
		case UNIT_DEPLOYED:
			hud.getHudMessages().updateNumberOfDeployedUnits(battlemanager.getUnitsDeployed(), battlemanager.getPlayerUnits().size());
			break;
		case DEPLOYMENT_FINISHED:
			hud.getHudMessages().hideInfoWindow(HudMessageTypes.NUMBER_OF_UNITS_DEPLOYED);
			hud.getHudMessages().showNextTutorialMessage();
			break;
		case INVALID_SPELL_TARGET:
			hud.getHudMessages().showPopup(HudMessageTypes.INVALID_SPELL_TARGET);
			break;
		case INVALID_ATTACK_TARGET:
			hud.getHudMessages().showPopup(HudMessageTypes.INVALID_ATTACK_TARGET);
			break;
		case INVALID_MOVE:
			hud.getHudMessages().showPopup(HudMessageTypes.INVALID_MOVE);
			break;
		case AI_WINS:
			hud.getHudMessages().showPopup(HudMessageTypes.AI_VICTORY);
			break;
		case PLAYER_WINS:
			hud.getHudMessages().showPopup(HudMessageTypes.PLAYER_VICTORY);
			break;
		case FOCUS_CAMERA:
			mapCamera.position.set(entity.getCurrentPosition().getTileX(), entity.getCurrentPosition().getTileY(), 0f);
			break;
		case REMOVE_HUD_UNIT:
			hud.removeUnit(entity);
			break;
		case ADD_UNIT_UI:
			hud.addUnit(entity);
			break;
		case ADD_UNIT_ENTITYSTAGE:
			entityStage.addActor(entity);
			break;
		case UNLOCK_UI:
			hud.setLocked(false);
			break;
		case LOCK_UI:
			hud.setLocked(true);
			break;
		default:
			break;
		}
	}
}
