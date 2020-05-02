package com.mygdx.game.AI;

import java.util.ArrayList;

import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntityTypes;
import com.mygdx.game.Entities.TeamLeader;

public class AITeam extends TeamLeader {
	private final AITeamData aiTeamData;
	private final AIDecisionMaker aiDecisionMaker;

	public AITeam(final AITeams type) {
		aiTeamData = AITeamFileReader.getAITeamData().get(type.ordinal());
		aiDecisionMaker = new AIDecisionMaker();
		initiateUnits();
	}

	private void initiateUnits() {
		team = new ArrayList<Entity>();
		for (final String name : aiTeamData.getUnits()) {
			for (final EntityTypes type : EntityTypes.values()) {
				if (name.equals(type.getEntityName())) {
					final Entity entity = new Entity(type);
					entity.setPlayerUnit(false);
					team.add(entity);
				}
			}
		}
	}

	public void aiUnitAct(Entity unit) {
		aiDecisionMaker.makeDecision(unit);
	}
}
