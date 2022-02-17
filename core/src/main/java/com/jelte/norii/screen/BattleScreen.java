package com.jelte.norii.screen;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.WaterDistortionEffect;
import com.jelte.norii.ai.AITeamLeader;
import com.jelte.norii.ai.EnemyType;
import com.jelte.norii.audio.AudioCommand;
import com.jelte.norii.audio.AudioManager;
import com.jelte.norii.audio.AudioTypeEvent;
import com.jelte.norii.battle.BattleManager;
import com.jelte.norii.battle.BattleScreenInputProcessor;
import com.jelte.norii.battle.battlestate.BattleStateGridHelper;
import com.jelte.norii.battle.battlestate.Move;
import com.jelte.norii.battle.battlestate.MoveType;
import com.jelte.norii.battle.battlestate.SpellMove;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityStage;
import com.jelte.norii.entities.Player;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.entities.UnitOwner.Alliance;
import com.jelte.norii.magic.AbilitiesEnum;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Ability.LineOfSight;
import com.jelte.norii.map.BattleMap;
import com.jelte.norii.map.Map;
import com.jelte.norii.map.MapManager;
import com.jelte.norii.map.MyPathFinder;
import com.jelte.norii.map.TiledMapActor;
import com.jelte.norii.multiplayer.NetworkMessage;
import com.jelte.norii.multiplayer.ServerCommunicator;
import com.jelte.norii.particles.ParticleMaker;
import com.jelte.norii.particles.ParticleType;
import com.jelte.norii.shaders.PixelScalerEffect;
import com.jelte.norii.ui.Hud;
import com.jelte.norii.ui.HudMessageTypes;
import com.jelte.norii.ui.MessageToBattleScreen;
import com.jelte.norii.utility.AssetManagerUtility;
import com.jelte.norii.utility.GraphicalUtility;
import com.jelte.norii.utility.MyPoint;
import com.jelte.norii.utility.TiledMapPosition;
import com.jelte.norii.utility.Utility;

public class BattleScreen extends GameScreen {
	private static final String TAG = BattleScreen.class.getSimpleName();
	private static final String FPS_TITLE = "fps = ";
	public static final int VISIBLE_WIDTH = 21;
	public static final int VISIBLE_HEIGHT = 21;
	private static OrthographicCamera mapCamera = null;

	private OrthogonalTiledMapRenderer mapRenderer = null;
	private SpriteBatch spriteBatch;
	private MapManager mapMgr;
	private BattleMap currentMap;
	private BattleManager battlemanager;
	private UnitOwner enemyTeamLeader;
	private InputMultiplexer multiplexer;
	private BattleScreenInputProcessor battlescreenInputProcessor;
	private OrthographicCamera hudCamera;
	private Hud hud;
	private PauseMenuScreen pauseMenu;
	private Json json;
	private EntityStage entityStage;
	private FrameBuffer fbo;
	private ShaderProgram shader;
	private String vertexShader;
	private String fragmentShader;
	private VfxManager vfxManager;
	private PixelScalerEffect vfxEffect;
	private WaterDistortionEffect effect;

	private boolean isPaused;

	public BattleScreen(UnitOwner enemy, MapManager mapMgr) {
		initializeVariables(enemy, mapMgr);
		initializeEntityStage();
		initializeHUD();
		initializePauseMenu();
		initializeInput();
		initializeMap();
		spawnEnemyUnits();
	}

	private void initializeVariables(UnitOwner enemy, MapManager mapMgr) {
		mapCamera = new OrthographicCamera();
		mapCamera.setToOrtho(false, VISIBLE_WIDTH, VISIBLE_HEIGHT);
		fbo = new FrameBuffer(Pixmap.Format.RGBA8888, 672, 672, false);
		spriteBatch = new SpriteBatch(900);
		isPaused = false;
		this.mapMgr = mapMgr;
		currentMap = (BattleMap) mapMgr.getCurrentMap();
		enemyTeamLeader = enemy;
		json = new Json();
		initTeams();
		vfxManager = new VfxManager(Format.RGBA8888);
		vfxEffect = new PixelScalerEffect();
		vfxManager.setBlendingEnabled(true);
		vfxManager.addEffect(vfxEffect);
		fragmentShader = Gdx.files.internal("shaders/pixelScaler.frag").readString();
		shader = new ShaderProgram(spriteBatch.getShader().getVertexShaderSource(), fragmentShader);
		shader.pedantic = false;
		Gdx.app.debug(TAG, shader.getLog());
		if (!shader.isCompiled()) {
			Gdx.app.debug(TAG, shader.getLog());
		}
	}

	private void initTeams() {
		if (enemyTeamLeader.isAI()) {
			Player.getInstance().setAlliance(Alliance.TEAM_BLUE);
			enemyTeamLeader.setAlliance(Alliance.TEAM_RED);
		}
	}

	private void initializeEntityStage() {
		Player.getInstance().initializeTeam();

		if (enemyTeamLeader.getType().equals(EnemyType.ONLINE_PLAYER)) {
			sendTeamToOnlineEnemy();
		}

		entityStage = new EntityStage(Stream.concat(Player.getInstance().getTeam().stream(), enemyTeamLeader.getTeam().stream()).collect(Collectors.toList()));
	}

	private void sendTeamToOnlineEnemy() {
		HashMap<Integer, String> teamWithId = new HashMap<>();
		for (Entity unit : Player.getInstance().getTeam()) {
			teamWithId.put(unit.getEntityID(), unit.getEntityData().getName());
		}
		String serializedTeamWithId = json.toJson(teamWithId);
		NetworkMessage message = new NetworkMessage();
		message.makeInitEnemyTeamMessage(enemyTeamLeader.getGameID(), serializedTeamWithId);
		ServerCommunicator.getInstance().sendMessage(message);
	}

	private void initializeHUD() {
		hudCamera = new OrthographicCamera();
		hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		hud = new Hud(Player.getInstance().getTeam(), enemyTeamLeader.getTeam(), spriteBatch, currentMap.getMapWidth(), currentMap.getMapHeight(), this, enemyTeamLeader.getType());
		if (enemyTeamLeader.getType() == EnemyType.AI) {
			AITeamLeader leader = (AITeamLeader) enemyTeamLeader;
			if (leader.getAiTeamData().getName().equals("TeamTutorial")) {
				hud.setTutorial(true);
				hud.getHudMessages().showInfoWindow(HudMessageTypes.DEPLOY_UNITS_INFO);
			}
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
		battlemanager = new BattleManager(enemyTeamLeader, currentMap.getMapWidth(), currentMap.getMapHeight(), currentMap.getNavLayer().getUnwalkableNodes(), this);
		battlescreenInputProcessor.setBattleManager(battlemanager);
		currentMap.setStage(this);
	}

	private void spawnEnemyUnits() {
		final List<TiledMapPosition> enemyStartPositions = currentMap.getEnemyStartPositions();
		enemyTeamLeader.spawnUnits(enemyStartPositions);
		hud.setLocked(enemyTeamLeader.isMyTurn());
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

		mapCamera.position.set(currentMap.getMapWidth() * 0.5f, currentMap.getMapHeight() * 0.5f, 0f);
		mapRenderer = new OrthogonalTiledMapRenderer(mapMgr.getCurrentTiledMap(), Map.UNIT_SCALE, spriteBatch);
		mapRenderer.setView(mapCamera);
		currentMap.makeSpawnParticles();

		final FitViewport vp = new FitViewport(VISIBLE_WIDTH, VISIBLE_HEIGHT, mapCamera);
		currentMap.getTiledMapStage().setViewport(vp);
		entityStage.setViewport(vp);
		currentMap.getTiledMapStage().getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
	}

	@Override
	public void hide() {
		// no-op
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
		hud.update(battlemanager.getUnits());
		hud.updateApBar(Player.getInstance().getAp());
		battlescreenInputProcessor.update();
		battlemanager.getCurrentBattleState().update();
		updateUnits(delta);
		updateStages();
		updateCameras();
		processAi();
		processMessagesFromServer();
	}

	private void updateUnits(final float delta) {
		Player.getInstance().updateUnits(delta);
		enemyTeamLeader.updateUnits(delta);
	}

	private void updateStages() {
		entityStage.act();
		hud.getStage().act();
		currentMap.getTiledMapStage().act();
	}

	private void updateCameras() {
		mapCamera.position.x = Utility.clamp(mapCamera.position.x, currentMap.getTilemapWidthInTiles() - (mapCamera.viewportWidth * 0.5f), 0 + (mapCamera.viewportWidth * 0.5f));
		mapCamera.position.y = Utility.clamp(mapCamera.position.y, currentMap.getTilemapHeightInTiles() - (mapCamera.viewportHeight * 0.5f), 0 + (mapCamera.viewportHeight * 0.5f));
		mapCamera.update();
		hudCamera.update();
	}

	private void processAi() {
		if (enemyTeamLeader.isAI()) {
			battlemanager.update();
		}
	}

	@SuppressWarnings("unchecked")
	private void processMessagesFromServer() {
		if (ServerCommunicator.getInstance().isNewMessage()) {
			NetworkMessage message = ServerCommunicator.getInstance().getOldestMessageFromServer();
			switch (message.getType()) {
			case UNIT_DEPLOYED:
				TiledMapPosition pos = message.getPos();
				enemyTeamLeader.spawnUnit(message.getUnitType(), message.getUnitID(), pos);
				ParticleMaker.deactivateParticle(ParticleMaker.getParticle(ParticleType.SPAWN, pos));
				ParticleMaker.deactivateParticle(ParticleMaker.getParticle(ParticleType.PURPLE_SQUARE, pos));
				hud.setLocked(false);
				battlemanager.setPlayerTurn(true);
				Player.getInstance().setMyTurn(true);
				break;
			case SYNCHRONIZE_UNIT_IDS:
				enemyTeamLeader.synchronizeMultiplayerUnitsWithLocal(message.getTeamWithIdMap());
				break;
			case DEPLOYMENT_FINISHED:
				deploymentfinished();
				break;
			case UNIT_MOVED:
			case UNIT_ATTACKED:
			case UNIT_SKIPPED:
				executeMove(message);
				break;
			case UNIT_CASTED_SPELL:
				TiledMapPosition position = message.getPos();
				MoveType type = message.getMoveType();
				AbilitiesEnum abilityEnum = message.getAbility();
				Ability ability = new Ability(abilityEnum, position.getTilePosAsPoint());
				Array<MyPoint> affectedUnits = message.getAffectedUnits();
				Move move = new SpellMove(type, position.getTilePosAsPoint(), ability, affectedUnits);
				int unitID = message.getUnitID();
				final Entity entity = battlemanager.getEntityByID(unitID);
				battlemanager.executeMove(entity, move);
				break;
			case TURN_FINISHED:
				break;
			default:
				break;
			}
		}
	}

	private void deploymentfinished() {
		Alliance alliance = Player.getInstance().getAlliance();
		if (alliance.equals(Alliance.TEAM_BLUE)) {
			ParticleMaker.deactivateAllParticlesOfType(ParticleType.PURPLE_SQUARE);
		}
		if (alliance.equals(Alliance.TEAM_RED)) {
			ParticleMaker.deactivateAllParticlesOfType(ParticleType.SPAWN);
		}
		hud.setLocked(false);
	}

	private void executeMove(NetworkMessage message) {
		int unitID = message.getUnitID();
		final Entity entity = battlemanager.getEntityByID(unitID);
		TiledMapPosition position = message.getPos();
		MoveType type = message.getMoveType();
		Move move = new Move(type, position.getTilePosAsPoint());
		battlemanager.executeMove(entity, move);
	}

	private void renderElements(final float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// fbo.begin();

		// Clean up internal buffers, as we don't need any information from the last
		// render.
		vfxManager.cleanUpBuffers();

		// Begin render to an off-screen buffer.
		vfxManager.beginInputCapture();

//		Gdx.gl.glClearColor(0, 0, 0, 0);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.graphics.setTitle(FPS_TITLE + Gdx.graphics.getFramesPerSecond() + "(" + Gdx.graphics.getWidth() + "," + Gdx.graphics.getHeight() + ")");
		renderMap();
		renderUnits();
		renderParticles(delta);
		renderGrid();
		renderHUD(delta);

		// End render to an off-screen buffer.
		vfxManager.endInputCapture();
		vfxManager.update(Gdx.graphics.getDeltaTime());

		// Apply the effects chain to the captured frame.
		// In our case, only one effect (gaussian blur) will be applied.
		vfxManager.applyEffects();

		// Render result to the screen.
		vfxManager.renderToScreen();

//		fbo.end();
//		spriteBatch.setShader(shader);
//		spriteBatch.begin();
//		Texture texture = fbo.getColorBufferTexture();
//		TextureRegion textureRegion = new TextureRegion(texture);
//		textureRegion.flip(false, true);
//		mapCamera.setToOrtho(false, fbo.getWidth(), fbo.getHeight());
//		mapCamera.update();
//		spriteBatch.setProjectionMatrix(mapCamera.combined);
//		spriteBatch.draw(textureRegion, 0, 0, fbo.getWidth(), fbo.getHeight());
//		spriteBatch.end();
//		mapCamera.setToOrtho(false, VISIBLE_WIDTH, VISIBLE_HEIGHT);
//		mapCamera.update();
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
		Player.getInstance().renderUnits(spriteBatch);
		enemyTeamLeader.renderUnits(spriteBatch);
	}

	private void renderParticles(final float delta) {
		ParticleMaker.drawAllActiveParticles(spriteBatch, delta);
		spriteBatch.end();
	}

	private void renderHUD(final float delta) {
		hud.getStage().getViewport().apply();
		spriteBatch.setProjectionMatrix(hud.getStage().getCamera().combined);
		hud.render(delta);
	}

	public void renderGrid() {
		for (int x = 0; x < currentMap.getMapWidth(); x += 1)
			GraphicalUtility.drawDebugLine(new Vector2(x, 0), new Vector2(x, currentMap.getMapHeight()), mapCamera.combined);
		for (int y = 0; y < currentMap.getMapHeight(); y += 1)
			GraphicalUtility.drawDebugLine(new Vector2(0, y), new Vector2(currentMap.getMapWidth(), y), mapCamera.combined);
	}

	@Override
	public void resize(final int width, final int height) {
		vfxManager.resize(width, height);
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
		enemyTeamLeader.dispose();
		mapRenderer.dispose();
		pauseMenu.dispose();
		currentMap.dispose();
		vfxManager.dispose();
		vfxEffect.dispose();
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
				skipUnit(entityID);
				break;
			case CLICKED_ON_MOVE:
				moveUnit(entityID);
				break;
			case CLICKED_ON_ATTACK:
				attackUnit(entityID);
				break;
			case CLICKED_ON_ABILITY:
				abilityUnit(entityID, ability);
				break;
			case HOVERED_ON_MOVE:
				hoveredOnMove(entityID);
				break;
			case STOP_HOVERED_ON_MOVE:
				ParticleMaker.deactivateAllParticlesOfType(ParticleType.MOVE);
				break;
			}
		}
	}

	private void skipUnit(int entityID) {
		ParticleMaker.deactivateAllParticles();
		final Entity skipEntity = battlemanager.getEntityByID(entityID);
		skipEntity.getVisualComponent().setActive(false);
		skipEntity.setFocused(false);
		enemyTeamLeader.playerUnitSkipped(skipEntity);
		battlemanager.setCurrentBattleState(battlemanager.getWaitOpponentBattleState());
		battlemanager.getCurrentBattleState().entry();
	}

	private void moveUnit(int entityID) {
		final Entity moveEntity = battlemanager.getEntityByID(entityID);
		if (moveEntity.canMove()) {
			prepareMove(moveEntity);
		} else {
			hud.getHudMessages().showPopup(HudMessageTypes.NOT_ENOUGH_AP);
		}
	}

	private void prepareMove(final Entity unit) {
		final Set<MyPoint> pointsToMove = BattleStateGridHelper.getInstance().getPossibleCenterCellsFiltered(unit.getCurrentPosition().getTilePosAsPoint(), LineOfSight.CIRCLE, unit.getAp(), battlemanager.getBattleState());
		MyPathFinder.getInstance().filterPositionsByWalkability(unit, pointsToMove);
		ParticleMaker.deactivateAllParticles();
		for (final MyPoint cell : pointsToMove) {
			if (!isUnitOnCell(cell)) {
				final TiledMapPosition positionToPutMoveParticle = new TiledMapPosition().setPositionFromTiles(cell.x, cell.y);
				ParticleMaker.addParticle(ParticleType.MOVE, positionToPutMoveParticle, 0);
				battlemanager.setCurrentBattleState(battlemanager.getMovementBattleState());
			}
		}
	}

	private boolean isUnitOnCell(final MyPoint cell) {
		for (final Entity entity : battlemanager.getBattleState().getAllUnits()) {
			if (entity.getCurrentPosition().isTileEqualTo(cell)) {
				return true;
			}
		}
		return false;
	}

	private void attackUnit(int entityID) {
		final Entity attackEntity = battlemanager.getEntityByID(entityID);
		if (attackEntity.canAttack()) {
			prepareAttack(attackEntity);
		} else {
			hud.getHudMessages().showPopup(HudMessageTypes.NOT_ENOUGH_AP);
		}
	}

	private void prepareAttack(final Entity unit) {
		ParticleMaker.deactivateAllParticles();
		final Set<MyPoint> pointsToAttack = BattleStateGridHelper.getInstance().getPossibleCenterCells(unit.getCurrentPosition().getTilePosAsPoint(), LineOfSight.CIRCLE, unit.getEntityData().getAttackRange());
		for (final MyPoint cell : pointsToAttack) {
			final TiledMapPosition positionToPutAttackParticle = new TiledMapPosition().setPositionFromTiles(cell.x, cell.y);
			ParticleMaker.addParticle(ParticleType.ATTACK, positionToPutAttackParticle, 0);
		}
		battlemanager.setCurrentBattleState(battlemanager.getAttackBattleState());
	}

	private void abilityUnit(int entityID, Ability ability) {
		final Entity spellEntity = battlemanager.getEntityByID(entityID);
		if (ability != null) {
			if (spellEntity.canCastSpell(ability)) {
				hud.setLocked(true);
				prepareSpell(spellEntity, ability);
				hud.setLocked(false);
			} else {
				hud.getHudMessages().showPopup(HudMessageTypes.NOT_ENOUGH_AP);
			}
		}
	}

	private void prepareSpell(final Entity unit, final Ability ability) {
		ParticleMaker.deactivateAllParticles();
		final List<TiledMapPosition> positions = battlemanager.getUnits().stream().map(Entity::getCurrentPosition).collect(Collectors.toList());
		final Set<MyPoint> spellPath = BattleStateGridHelper.getInstance().calculateSpellPath(unit, ability, positions, battlemanager.getBattleState());

		for (final MyPoint cell : spellPath) {
			final TiledMapPosition positionToPutSpellParticle = new TiledMapPosition().setPositionFromTiles(cell.x, cell.y);
			ParticleMaker.addParticle(ParticleType.SPELL, positionToPutSpellParticle, 0);
		}

		battlemanager.getSpellBattleState().setAbility(ability);
		battlemanager.setCurrentBattleState(battlemanager.getSpellBattleState());
	}

	private void hoveredOnMove(int entityID) {
		final Entity showMoveEntity = battlemanager.getEntityByID(entityID);
		final Set<MyPoint> pointsToMove = BattleStateGridHelper.getInstance().getPossibleCenterCellsFiltered(showMoveEntity.getCurrentPosition().getTilePosAsPoint(), LineOfSight.CIRCLE, showMoveEntity.getAp(),
				battlemanager.getBattleState());
		MyPathFinder.getInstance().filterPositionsByWalkability(showMoveEntity, pointsToMove);
		for (final MyPoint cell : pointsToMove) {
			if (!isUnitOnCell(cell)) {
				final TiledMapPosition positionToPutMoveParticle = new TiledMapPosition().setPositionFromTiles(cell.x, cell.y);
				ParticleMaker.addParticle(ParticleType.MOVE, positionToPutMoveParticle, 0);
				battlemanager.setCurrentBattleState(battlemanager.getMovementBattleState());
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
			hud.setLocked(true);
			break;
		case DEPLOYMENT_FINISHED:
			hud.getHudMessages().hideInfoWindow(HudMessageTypes.NUMBER_OF_UNITS_DEPLOYED);
			hud.getHudMessages().showNextTutorialMessage();
			hud.getPortraitAndStats().setActionsVisible(true);
			enemyTeamLeader.notifyDeploymentDone();
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
		case PLAYER_TURN:
			hud.getHudMessages().showPopup(HudMessageTypes.PLAYER_TURN);
			break;
		case ENEMY_TURN:
			hud.getHudMessages().showPopup(HudMessageTypes.ENEMY_TURN);
			break;
		default:
			break;
		}
	}

	public static OrthographicCamera getCamera() {
		return mapCamera;
	}

	public boolean isPaused() {
		return isPaused;
	}
}
