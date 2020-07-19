package com.mygdx.game.Entities;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;

public class Player {
	private static Player instance;
	private List<Entity> team;

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
				batch.draw(entity.getFrame(), entity.getCurrentPosition().getTileX(), entity.getCurrentPosition().getTileY(), 1f, 1f);
			}
		}
	}

	public Entity[] getUnitsSortedByIni() {
		final Entity[] sortedUnits = team.toArray(new Entity[0]);
		sortUnits(sortedUnits);
		return sortedUnits;
	}

	private void sortUnits(final Entity[] sortedUnits) {
		Arrays.sort(sortedUnits, (Entity e1, Entity e2) -> {
			if (e1.getEntityData().getBaseInitiative() > e2.getEntityData().getBaseInitiative()) {
				return 1;
			} else if (e1.getEntityData().getBaseInitiative() < e2.getEntityData().getBaseInitiative()) {
				return -1;
			} else {
				return 0;
			}
		});
	}

	public List<Entity> getTeam() {
		return team;
	}

	public void dispose() {
		for (final Entity entity : team) {
			entity.dispose();
		}
	}
}
