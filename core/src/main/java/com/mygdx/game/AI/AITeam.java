package com.mygdx.game.AI;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntityTypes;
import com.mygdx.game.Entities.TeamLeader;

import Utility.TiledMapPosition;

public class AITeam extends TeamLeader {
	private static final String TAG = AITeam.class.getSimpleName();
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

	public void spawnAiUnits(ArrayList<TiledMapPosition> spawnPositions) {
		for (final Entity unit : getTeam()) {
			if (!spawnPositions.isEmpty()) {
				unit.setCurrentPosition(spawnPositions.get(0));
				unit.setPlayerUnit(false);
				unit.setInBattle(true);
				spawnPositions.remove(0);
			} else {
				Gdx.app.debug(TAG, "maybe no more room to spawn ai units!");
			}
		}
	}

	public void aiUnitAct(Entity unit) {
		aiDecisionMaker.makeDecision(unit);
	}
}
