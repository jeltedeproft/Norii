package com.jelte.norii.screen;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jelte.norii.ai.AITeamLeader;
import com.jelte.norii.ai.AITeams;
import com.jelte.norii.audio.AudioObserver;
import com.jelte.norii.battle.BattleManager;
import com.jelte.norii.battle.BattleScreenInputProcessor;
import com.jelte.norii.battle.battleState.HypotheticalUnit;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityStage;
import com.jelte.norii.entities.Player;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.ModifiersEnum;
import com.jelte.norii.map.BattleMap;
import com.jelte.norii.map.Map;
import com.jelte.norii.map.MapManager;
import com.jelte.norii.map.MyPathFinder;
import com.jelte.norii.map.TiledMapActor;
import com.jelte.norii.particles.ParticleMaker;
import com.jelte.norii.particles.ParticleType;
import com.jelte.norii.profile.ProfileManager;
import com.jelte.norii.ui.ActionsUi;
import com.jelte.norii.ui.Hud;
import com.jelte.norii.ui.MessageToBattleScreen;
import com.jelte.norii.ui.StatusUi;
import com.jelte.norii.utility.AssetManagerUtility;
import com.jelte.norii.utility.TiledMapPosition;
import com.jelte.norii.utility.Utility;

public class BattleScreen extends GameScreen {
	public static final int VISIBLE_WIDTH = 25;
	public static final int VISIBLE_HEIGHT = 25;
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
	private Hud newHud;
	private PauseMenuScreen pauseMenu;

	private EntityStage entityStage;
	private final String fpsTitle = "fps = ";

	private boolean isPaused;

	public BattleScreen(AITeams aiTeams) {
		initializeVariables();
		initializeAI(aiTeams);
		initializeEntityStage();
		initializeHUD();
		initializePauseMenu();
		initializeInput();
		initializeMap();
		initializeObservers();
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
		entityStage = new EntityStage(Stream.concat(Player.getInstance().getTeam().stream(), aiTeamLeader.getTeam().stream()).collect(Collectors.toList()));
	}

	private void initializeHUD() {
		hudCamera = new OrthographicCamera();
		hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		newHud = new Hud(Player.getInstance().getTeam(), aiTeamLeader.getTeam(), spriteBatch, currentMap.getMapWidth(), currentMap.getMapHeight(), this);
	}

	private void initializePauseMenu() {
		pauseMenu = new PauseMenuScreen(hudCamera, this, spriteBatch);
	}

	private void initializeInput() {
		battlescreenInputProcessor = new BattleScreenInputProcessor(this, mapCamera);
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(battlescreenInputProcessor);
		multiplexer.addProcessor(newHud.getStage());
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

	private void initializeObservers() {
		ProfileManager.getInstance().addObserver(newHud);
	}

	public BattleManager getBattlemanager() {
		return battlemanager;
	}

	@Override
	public void show() {
		notifyAudio(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_TITLE2);
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
		newHud.update(battlemanager.getUnits());
		battlescreenInputProcessor.update();
		battlemanager.getCurrentBattleState().update();
		updateAI(delta);
		updateUnits(delta);
		updateUIHover();
		updateStages();
		updateCameras();
	}

	private void updateAI(float delta) {
		GdxAI.getTimepiece().update(delta);
		MessageManager.getInstance().update();
	}

	private void updateUnits(final float delta) {
		Player.getInstance().updateUnits(delta);
		aiTeamLeader.updateUnits(delta);
	}

	// TODO check this
	private void updateUIHover() {
		boolean hoverResult = false;
		for (final Entity unit : Player.getInstance().getTeam()) {
			if (unit.getEntityactor().isActionsHovering()) {
				hoverResult = true;
			}
		}
		for (final StatusUi ui : newHud.getStatusUIs()) {
			ui.setActionsUIHovering(hoverResult);
		}
	}

	private void updateStages() {
		entityStage.act();
		newHud.getStage().act();
		currentMap.getTiledMapStage().act();
	}

	private void updateCameras() {
		mapCamera.position.x = Utility.clamp(mapCamera.position.x, currentMap.getTilemapWidthInTiles() - (mapCamera.viewportWidth / 2), 0 + (mapCamera.viewportWidth / 2));
		mapCamera.position.y = Utility.clamp(mapCamera.position.y, currentMap.getTilemapHeightInTiles() - (mapCamera.viewportHeight / 2), 0 + (mapCamera.viewportHeight / 2));
		mapCamera.update();
		hudCamera.update();
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
		if (battlemanager.getActiveUnit().isActive()) {
			spriteBatch.draw(AssetManagerUtility.getSprite("purple"), battlemanager.getActiveUnit().getCurrentPosition().getTileX(), battlemanager.getActiveUnit().getCurrentPosition().getTileY(), 1.0f, 1.0f);
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
		spriteBatch.setProjectionMatrix(newHud.getStage().getCamera().combined);
		newHud.getStage().getViewport().apply();
		newHud.render(delta);
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

		newHud.resize(width, height);
		pauseMenu.resize(width, height);
	}

	@Override
	public void pause() {
		isPaused = true;
		notifyAudio(AudioObserver.AudioCommand.MUSIC_PAUSE, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
		notifyAudio(AudioObserver.AudioCommand.SOUND_STOP, AudioObserver.AudioTypeEvent.WALK_LOOP);
		pauseMenu.setVisible(true);
	}

	@Override
	public void resume() {
		isPaused = false;
		notifyAudio(AudioObserver.AudioCommand.MUSIC_RESUME, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
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
		final List<GridCell> path = MyPathFinder.getInstance().getCellsWithinCircle(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY(), unit.getAp());
		for (final GridCell cell : path) {
			if (!isUnitOnCell(cell)) {
				final TiledMapPosition positionToPutMoveParticle = new TiledMapPosition().setPositionFromTiles(cell.x, cell.y);
				ParticleMaker.addParticle(ParticleType.MOVE, positionToPutMoveParticle, 0);
				battlemanager.setCurrentBattleState(battlemanager.getMovementBattleState());
			}
		}
	}

	private void prepareAttack(final Entity unit) {
		final List<GridCell> attackPath = MyPathFinder.getInstance().getCellsWithinCircle(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY(), unit.getEntityData().getAttackRange());
		for (final GridCell cell : attackPath) {
			final TiledMapPosition positionToPutAttackParticle = new TiledMapPosition().setPositionFromTiles(cell.x, cell.y);
			ParticleMaker.addParticle(ParticleType.ATTACK, positionToPutAttackParticle, 0);
		}
		battlemanager.setCurrentBattleState(battlemanager.getAttackBattleState());
	}

	private void prepareSpell(final Entity unit, final Ability ability) {
		final List<TiledMapPosition> positions = battlemanager.getUnits().stream().map(Entity::getCurrentPosition).collect(Collectors.toList());
		final List<GridCell> spellPath = calculateSpellPath(unit, ability, positions);

		for (final GridCell cell : spellPath) {
			final TiledMapPosition positionToPutSpellParticle = new TiledMapPosition().setPositionFromTiles(cell.x, cell.y);
			ParticleMaker.addParticle(ParticleType.SPELL, positionToPutSpellParticle, 0);
		}

		battlemanager.getSpellBattleState().setAbility(ability);
		battlemanager.setCurrentBattleState(battlemanager.getSpellBattleState());
	}

	private List<GridCell> calculateSpellPath(final Entity unit, final Ability ability, final List<TiledMapPosition> positions) {
		List<GridCell> spellPath = null;

		switch (ability.getLineOfSight()) {
		case CIRCLE:
			spellPath = MyPathFinder.getInstance().getLineOfSightWithinCircle(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY(), ability.getSpellData().getRange(), positions);
			break;
		case CROSS:
			// TODO
			break;
		case LINE:
			spellPath = MyPathFinder.getInstance().getLineOfSightWithinLine(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY(), ability.getSpellData().getRange(), unit.getEntityAnimation().getCurrentDirection(),
					positions);
			break;
		default:
			break;
		}
		return spellPath;
	}

	private boolean isUnitOnCell(final GridCell cell) {
		final TiledMapPosition cellToTiled = new TiledMapPosition().setPositionFromTiles(cell.x, cell.y);
		for (final HypotheticalUnit entity : battlemanager.getBattleState().getAllUnits()) {
			if (entity.getPositionAsTiledMapPosition().isTileEqualTo(cellToTiled)) {
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
		newHud.setPositionTileHover(actor.getActorPos().getTileX(), actor.getActorPos().getTileY());
	}

	public void messageFromUi(MessageToBattleScreen message, int entityID, Ability ability) {
		switch (message) {
		case CLICKED_ON_SKIP:
			final Entity skipEntity = battlemanager.getEntityByID(entityID);
			skipEntity.setActive(false);
			skipEntity.setFocused(false);
			skipEntity.setLocked(false);
			skipEntity.setAp(skipEntity.getEntityData().getMaxAP());
			battlemanager.setLockedUnit(null);
			battlemanager.setCurrentBattleState(battlemanager.getActionBattleState());
			battlemanager.getCurrentBattleState().exit();
			break;
		case CLICKED_ON_MOVE:
			final Entity moveEntity = battlemanager.getEntityByID(entityID);
			if (moveEntity.canMove()) {
				prepareMove(moveEntity);
			}
			break;
		case CLICKED_ON_ATTACK:
			final Entity attackEntity = battlemanager.getEntityByID(entityID);
			if ((attackEntity.getAp() >= attackEntity.getEntityData().getBasicAttackCost()) && attackEntity.canAttack()) {
				prepareAttack(attackEntity);
			}
			break;
		case CLICKED_ON_ABILITY:
			final Entity spellEntity = battlemanager.getEntityByID(entityID);
			if (spellEntity.getAp() >= ability.getSpellData().getApCost()) {
				prepareSpell(spellEntity, ability);
			}
			break;
		}
	}

	public void messageFromBattleManager(com.jelte.norii.battle.MessageToBattleScreen message, Entity entity) {
		switch (message) {
		case UNIT_ACTIVE:
			final ActionsUi actionsUI = newHud.getEntityIdWithActionUi().get(entity.getEntityID());
			actionsUI.update(entity);
			actionsUI.setVisible(!entity.hasModifier(ModifiersEnum.STUNNED));
			break;
		case HIDE_ACTIONS:
			final ActionsUi actionsUIToHide = newHud.getEntityIdWithActionUi().get(entity.getEntityID());
			actionsUIToHide.setVisible(false);
			break;
		case UPDATE_UI:
			newHud.update(battlemanager.getUnits());
			break;
		case SET_CHARACTER_HUD:
			newHud.getPortraitAndStats().setHero(entity);
			break;
		case UNSET_CHARACTER_HUD:
			newHud.getPortraitAndStats().setHero(null);
			break;
		case AI_WINS:
			newHud.showAiWin();
			break;
		case PLAYER_WINS:
			newHud.showPlayerWin();
			break;
		case FOCUS_CAMERA:
			mapCamera.position.set(entity.getCurrentPosition().getTileX(), entity.getCurrentPosition().getTileY(), 0f);
			break;
		case SHOW_STATUS_UI:
			newHud.getEntityIdWithStatusUi().get(entity.getEntityID()).setVisible(true);
			break;
		case HIDE_STATUS_UI:
			newHud.getEntityIdWithStatusUi().get(entity.getEntityID()).setVisible(false);
			break;
		case REMOVE_HUD_UNIT:
			newHud.removeUnit(entity);
			break;
		case ADD_UNIT_UI:
			newHud.addUnit(entity);
			break;
		default:
			break;
		}
	}
}
