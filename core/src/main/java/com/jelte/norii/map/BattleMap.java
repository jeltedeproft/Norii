
package com.jelte.norii.map;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.jelte.norii.audio.AudioCommand;
import com.jelte.norii.audio.AudioManager;
import com.jelte.norii.audio.AudioTypeEvent;
import com.jelte.norii.map.MapFactory.MapType;
import com.jelte.norii.particles.ParticleMaker;
import com.jelte.norii.particles.ParticleType;
import com.jelte.norii.screen.BattleScreen;
import com.jelte.norii.utility.AssetManagerUtility;
import com.jelte.norii.utility.TiledMapPosition;

public class BattleMap extends Map {
	private static final String TAG = BattleMap.class.getSimpleName();

	private ArrayList<TiledMapPosition> unitSpawnPositions;
	protected Vector2 playerStartPositionRect;
	protected TiledMapPosition convertedUnits;
	protected ArrayList<TiledMapPosition> enemyStartPositions;

	BattleMap(MapType mapType, final String mapPath) {
		super(mapType, mapPath);

		if (!AssetManagerUtility.isAssetLoaded(mapPath)) {
			Gdx.app.debug(TAG, "Map not loaded");
			return;
		}

		initializeClassVariables();
	}

	private void initializeClassVariables() {
		unitSpawnPositions = new ArrayList<>();
		enemyStartPositions = new ArrayList<>();
		playerStartPositionRect = new Vector2(0, 0);
		convertedUnits = new TiledMapPosition();

		spawnsLayer = currentMap.getLayers().get(MAP_SPAWNS_LAYER);
		navLayer = (MyNavigationTiledMapLayer) currentMap.getLayers().get(NAVIGATION_LAYER);
	}

	public void setStage(final BattleScreen battleScreen) {
		tiledmapstage = new TiledMapStage(this, BACKGROUND_LAYER, battleScreen);

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
			ParticleMaker.addParticle(ParticleType.SPAWN, pos, 0);
		}
	}

	public List<TiledMapPosition> getSpawnPositions() {
		return unitSpawnPositions;
	}

	public List<TiledMapPosition> getEnemyStartPositions() {
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
		AudioManager.getInstance().onNotify(AudioCommand.MUSIC_STOP, AudioTypeEvent.MUSIC_BATTLE);
	}

	@Override
	public void loadMusic() {
		AudioManager.getInstance().onNotify(AudioCommand.MUSIC_LOAD, AudioTypeEvent.MUSIC_BATTLE);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_LOAD, AudioTypeEvent.ATTACK_SOUND);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_LOAD, AudioTypeEvent.SPELL_SOUND);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_LOAD, AudioTypeEvent.WALK_LOOP);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_LOAD, AudioTypeEvent.FIREBALL_SOUND);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_LOAD, AudioTypeEvent.EXPLOSION);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_LOAD, AudioTypeEvent.CRACKLE_SOUND);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_LOAD, AudioTypeEvent.ICE);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_LOAD, AudioTypeEvent.PUSH);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_LOAD, AudioTypeEvent.PULL);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_LOAD, AudioTypeEvent.ARROW);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_LOAD, AudioTypeEvent.INVISIBLE);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_LOAD, AudioTypeEvent.HEAL);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_LOAD, AudioTypeEvent.TRANSPORT);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_LOAD, AudioTypeEvent.PORTAL);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_LOAD, AudioTypeEvent.COLD_SNAP);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_LOAD, AudioTypeEvent.SWAP_SOUND);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_LOAD, AudioTypeEvent.STONE_SOUND);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_LOAD, AudioTypeEvent.HAMMER_SOUND);
		AudioManager.getInstance().onNotify(AudioCommand.MUSIC_PLAY_LOOP, AudioTypeEvent.MUSIC_BATTLE);
	}

	public void dispose() {
		initializeClassVariables();
	}

	@Override
	public String toString() {
		return "map name : " + TAG;
	}
}
