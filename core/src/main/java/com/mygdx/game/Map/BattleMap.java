
package com.mygdx.game.Map;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Audio.AudioObserver;
import com.mygdx.game.Battle.BattleManager;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;

import Utility.TiledMapPosition;
import Utility.Utility;

public class BattleMap extends Map {
	private static final String TAG = BattleMap.class.getSimpleName();

	private ArrayList<TiledMapPosition> unitSpawnPositions;
	protected Vector2 playerStartPositionRect;
	protected TiledMapPosition convertedUnits;
	protected ArrayList<TiledMapPosition> enemyStartPositions;

	BattleMap(final String mapPath) {
		super(MapFactory.MapType.BATTLE_MAP_THE_TOWN, mapPath);

		if (!Utility.isAssetLoaded(mapPath)) {
			Gdx.app.debug(TAG, "Map not loaded");
			return;
		}

		collisionLayer = currentMap.getLayers().get(MAP_COLLISION_LAYER);
		if (collisionLayer == null) {
			Gdx.app.debug(TAG, "No collision layer!");
		}

		initializeClassVariables();
	}

	private void initializeClassVariables() {
		unitSpawnPositions = new ArrayList<TiledMapPosition>();
		enemyStartPositions = new ArrayList<TiledMapPosition>();
		playerStartPositionRect = new Vector2(0, 0);
		convertedUnits = new TiledMapPosition();

		spawnsLayer = currentMap.getLayers().get(MAP_SPAWNS_LAYER);
		navLayer = (MyNavigationTiledMapLayer) currentMap.getLayers().get(NAVIGATION_LAYER);
		pathfinder = new MyPathFinder(this);
	}

	public void setStage(final BattleManager battlemanager) {
		tiledmapstage = new TiledMapStage(this, ACTOR_LAYER, battlemanager);

		if (spawnsLayer == null) {
			Gdx.app.debug(TAG, "No spawn layer!");
		} else {
			if (unitSpawnPositions.isEmpty()) {
				fillSpawnPositions();
			}
		}
	}

	private void fillSpawnPositions() {
		for (final MapObject object : spawnsLayer.getObjects()) {
			if (object.getName().equalsIgnoreCase(PLAYER_START)) {
				addPositions(object, unitSpawnPositions, true);
			}

			if (object.getName().equalsIgnoreCase(ENEMY_START)) {
				addPositions(object, enemyStartPositions, false);
			}
		}
	}

	private void addPositions(MapObject object, ArrayList<TiledMapPosition> startPositions, boolean isHuman) {
		((RectangleMapObject) object).getRectangle().getPosition(playerStartPositionRect);
		final TiledMapPosition spawnPos = new TiledMapPosition().setPositionFromTiled(playerStartPositionRect.x, playerStartPositionRect.y);
		startPositions.add(spawnPos);

		final TiledMapActor tiledactor = getActorAtScreenCoordinate(spawnPos);

		setSpawns(isHuman, tiledactor);
	}

	private void setSpawns(boolean isHuman, final TiledMapActor tiledactor) {
		if (tiledactor != null) {
			if (isHuman) {
				tiledactor.setIsFreeSpawn(true);
			} else {
				tiledactor.setIsAISpawn(true);
			}
		}
	}

	private TiledMapActor getActorAtScreenCoordinate(final TiledMapPosition pos) {
		return tiledmapstage.getTiledMapActors()[pos.getTileX()][pos.getTileY()];
	}

	public void makeSpawnParticles() {
		for (final TiledMapPosition pos : unitSpawnPositions) {
			ParticleMaker.addParticle(ParticleType.SPAWN, pos);
		}
	}

	public List<TiledMapPosition> getSpawnPositions() {
		return unitSpawnPositions;
	}

	public ArrayList<TiledMapPosition> getEnemyStartPositions() {
		return enemyStartPositions;
	}

	public TiledMapStage getTiledMapStage() {
		return tiledmapstage;
	}

	@Override
	public MyNavigationTiledMapLayer getNavLayer() {
		return navLayer;
	}

	@Override
	public void unloadMusic() {
		notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
	}

	@Override
	public void loadMusic() {
		notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
		notify(AudioObserver.AudioCommand.SOUND_LOAD, AudioObserver.AudioTypeEvent.ATTACK_SOUND);
		notify(AudioObserver.AudioCommand.SOUND_LOAD, AudioObserver.AudioTypeEvent.SPELL_SOUND);
		notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
	}

	public void dispose() {
		initializeClassVariables();
	}
}
