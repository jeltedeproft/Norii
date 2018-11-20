package com.mygdx.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public abstract class Owner {
	private ArrayList<Entity> team;
	
	public ArrayList<Entity> getTeam() {
		return team;
	}

	public void setTeam(ArrayList<Entity> team) {
		this.team = team;
	}
	
	public void initUnits() {
		for(Entity entity : team) {
			entity.init();
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
