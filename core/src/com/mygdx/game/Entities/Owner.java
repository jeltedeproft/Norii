package com.mygdx.game.Entities;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;

public abstract class Owner {
	private List<Entity> team;
	private EntityStage entityStage;
	
	public EntityStage getEntityStage() {
		return entityStage;
	}
	
	public List<Entity> getTeam() {
		return team;
	}

	public void setTeam(List<Entity> team) {
		this.team = team;
	}
	
	public void setStage() {
		if(entityStage != null) this.entityStage.dispose();
		this.entityStage = new EntityStage(team);
	}
	
	public void updateUnits(float delta) {
		for(Entity entity : team) {
			entity.update(delta);
		}
	}
	
	public void renderUnits(Batch batch) {
		for (Entity entity : team) {
			if(entity.isInBattle()) {
				batch.draw(entity.getFrame(), entity.getCurrentPosition().getTileX(), entity.getCurrentPosition().getTileY(), 1f,1f);
			}
		}
	}
	
	public Entity[] getUnitsSortedByIni(){
		Entity[] sortedUnits = team.toArray(new Entity[0]);
		sortUnits(sortedUnits);
		return sortedUnits;
	}

	private void sortUnits(Entity[] sortedUnits) {
		Arrays.sort(sortedUnits, new Comparator<Entity>() {
			   public int compare(Entity e1, Entity e2) {
				   if(e1.getBaseInitiative() > e2.getBaseInitiative()) {
					   return 1;
				   }else if(e1.getBaseInitiative() < e2.getBaseInitiative()) {
					   return -1;
				   }else return 0;
				   
			   }
		});
	}
	
	public void dispose() {
		for (Entity entity : team) {
			entity.dispose();
		}
		entityStage.dispose();
	}
}
