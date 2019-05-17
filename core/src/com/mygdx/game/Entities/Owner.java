package com.mygdx.game.Entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.Screen.BattleScreen;

public abstract class Owner {
	private static final String TAG = Owner.class.getSimpleName();
	
	private ArrayList<Entity> team;
	private EntityStage entityStage;
	
	public EntityStage getEntityStage() {
		return entityStage;
	}
	
	public ArrayList<Entity> getTeam() {
		return team;
	}

	public void setTeam(ArrayList<Entity> team) {
		this.team = team;
		
		if(entityStage == null) {
			this.entityStage = new EntityStage(team);
		}else {
			this.entityStage.dispose();
			this.entityStage = new EntityStage(team);
		}
		
	}
	
	public void updateUnits(float delta) {
		for(Entity entity : team) {
			entity.update(delta);
		}
	}
	
	public Entity[] getUnitsSortedByIni(){
		Entity[] sortedUnits = team.toArray(new Entity[0]);
		Arrays.sort(sortedUnits, new Comparator<Entity>() {
			   public int compare(Entity e1, Entity e2) {
				   if(e1.getIni() > e2.getIni()) {
					   return 1;
				   }else if(e1.getIni() < e2.getIni()) {
					   return -1;
				   }else return 0;
				   
			   }
		});
		return sortedUnits;
	}
	
	public void dispose() {
		for (Entity entity : team) {
			entity.dispose();
		}
	}
}
