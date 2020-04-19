package com.mygdx.game.AI;

import java.util.ArrayList;

import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntityStage;
import com.mygdx.game.Entities.EntityTypes;

public class AITeam {
	private final AITeamData aiTeamData;
	private final ArrayList<Entity> units;
	private EntityStage entityStage;

	public AITeam(final AITeams type) {
		units = new ArrayList<Entity>();
		aiTeamData = AITeamFileReader.getAITeamData().get(type.ordinal());
		initiateUnits();
		setStage();
	}

	private void initiateUnits() {
		for (final String name : aiTeamData.getUnits()) {
			for (final EntityTypes type : EntityTypes.values()) {
				if (name.equals(type.getEntityName())) {
					units.add(new Entity(type));
				}
			}
		}
	}

	private void setStage() {
		if (entityStage != null) {
			entityStage.dispose();
		}
		entityStage = new EntityStage(units);
	}

	public String[] getUnitNames() {
		return aiTeamData.getUnits();
	}

	public ArrayList<Entity> getUnits() {
		return units;
	}

	public void dispose() {
		for (final Entity entity : units) {
			entity.dispose();
		}
		entityStage.dispose();
	}
}
