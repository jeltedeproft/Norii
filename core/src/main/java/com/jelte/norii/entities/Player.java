package com.jelte.norii.entities;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.jelte.norii.utility.AssetManagerUtility;

public class Player implements UnitOwner {
	private static Player instance;
	private List<PlayerEntity> team;

	private Player() {
		super();
	}

	public static Player getInstance() {
		if (instance == null) {
			instance = new Player();
		}
		return instance;
	}

	public void updateUnits(final float delta) {
		for (final Entity entity : team) {
			entity.update(delta);

			if (!entity.getOldPlayerPosition().isTileEqualTo(entity.getCurrentPosition())) {
				entity.setOldPlayerPosition(entity.getCurrentPosition());
			}
		}
	}

	public void renderUnits(final Batch batch) {
		for (final Entity entity : team) {
			if (entity.isInBattle()) {
				batch.draw(entity.getFrame(), entity.getCurrentPosition().getTileX(), entity.getCurrentPosition().getTileY(), 1.0f, 1.0f);
			}

			if (entity.isLocked()) {
				batch.draw(AssetManagerUtility.getSprite("lock"), entity.getCurrentPosition().getTileX(), entity.getCurrentPosition().getTileY() + 1.0f, 1.0f, 1.0f);
			}
		}
	}

	public List<PlayerEntity> getPlayerUnits() {
		return team;
	}

	public void dispose() {
		for (final Entity entity : team) {
			entity.dispose();
		}
	}

	public void setPlayerUnits(List<PlayerEntity> playerMonsters) {
		team = playerMonsters;
	}

	public void applyModifiers() {
		team.forEach(PlayerEntity::applyModifiers);
	}
}
