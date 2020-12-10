package com.jelte.norii.ai;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.entities.AiEntity;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.entities.PlayerEntity;
import com.jelte.norii.map.MyPathFinder;
import com.jelte.norii.utility.TiledMapPosition;

public class AITeamLeader {
	private static final String TAG = AITeamLeader.class.getSimpleName();

	private List<AiEntity> team;
	private final AITeamData aiTeamData;
	private final AIDecisionMaker aiDecisionMaker;
	private MyPathFinder myPathFinder;

	public AITeamLeader(final AITeams type) {
		aiTeamData = AITeamFileReader.getAITeamData().get(type.ordinal());
		aiDecisionMaker = new AIDecisionMaker(this);
		initiateUnits();
	}

	private void initiateUnits() {
		team = new ArrayList<>();
		for (final String name : aiTeamData.getUnits()) {
			for (final EntityTypes type : EntityTypes.values()) {
				if (name.equals(type.getEntityName())) {
					final AiEntity entity = new AiEntity(type);
					entity.setPlayerUnit(false);
					team.add(entity);
				}
			}
		}
	}

	public void spawnAiUnits(List<TiledMapPosition> spawnPositions) {
		for (final AiEntity unit : team) {
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

	public void act(List<PlayerEntity> playerUnits, List<AiEntity> aiUnits, BattleState stateOfBattle) {
		aiDecisionMaker.makeDecision(playerUnits, aiUnits, stateOfBattle);
	}

	public void updateUnits(final float delta) {
		team.removeIf(Entity::isDead);

		for (final Entity entity : team) {
			entity.update(delta);
		}
	}

	public void renderUnits(final Batch batch) {
		for (final Entity entity : team) {
			if (entity.isInBattle()) {
				batch.draw(entity.getFrame(), entity.getCurrentPosition().getTileX(), entity.getCurrentPosition().getTileY() - 0.0f, 1.0f, 1.0f);
			}
		}
	}

	public void setPathFinder(MyPathFinder pathfinder) {
		this.myPathFinder = pathfinder;
	}

	public MyPathFinder getMyPathFinder() {
		return myPathFinder;
	}

	public void setTeam(final List<AiEntity> team) {
		this.team = team;
	}

	public List<AiEntity> getTeam() {
		return team;
	}

	public void dispose() {
		for (final Entity entity : team) {
			entity.dispose();
		}
	}

	@Override
	public String toString() {
		return "AITeamLeader with team : " + team;
	}
}
