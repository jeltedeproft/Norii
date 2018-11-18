package com.mygdx.game;

import java.util.ArrayList;

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
}
