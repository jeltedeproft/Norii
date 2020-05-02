package com.mygdx.game.Entities;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;

public abstract class TeamLeader {
	protected List<Entity> team;
	private EntityStage entityStage;

	public EntityStage getEntityStage() {
		return entityStage;
	}

	public List<Entity> getTeam() {
		return team;
	}

	public void setTeam(final List<Entity> team) {
		this.team = team;
	}

	public void setStage(EntityStage stage) {
		if (entityStage != null) {
			entityStage.dispose();
		}
		entityStage = stage;
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
		Arrays.sort(sortedUnits, new Comparator<Entity>() {
			@Override
			public int compare(final Entity e1, final Entity e2) {
				if (e1.getBaseInitiative() > e2.getBaseInitiative()) {
					return 1;
				} else if (e1.getBaseInitiative() < e2.getBaseInitiative()) {
					return -1;
				} else {
					return 0;
				}
			}
		});
	}

	public void dispose() {
		for (final Entity entity : team) {
			entity.dispose();
		}
		entityStage.dispose();
	}
}
