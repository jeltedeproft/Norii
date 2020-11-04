package com.mygdx.game.AI;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.game.AI.SelectUnitStrategy.SelectNextUnitRandomly;
import com.mygdx.game.AI.SelectUnitStrategy.SelectUnitStrategy;
import com.mygdx.game.Entities.AiEntity;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntityObserver.EntityCommand;
import com.mygdx.game.Entities.EntityTypes;
import com.mygdx.game.Entities.PlayerEntity;
import com.mygdx.game.Map.MyPathFinder;

import Utility.TiledMapPosition;

public class AITeamLeader {
	private static final String TAG = AITeamLeader.class.getSimpleName();

	private List<AiEntity> team;
	private final AITeamData aiTeamData;
	private final AIDecisionMaker aiDecisionMaker;
	private MyPathFinder myPathFinder;
	private SelectUnitStrategy selectUnitStrategy;

	public AITeamLeader(final AITeams type) {
		aiTeamData = AITeamFileReader.getAITeamData().get(type.ordinal());
		aiDecisionMaker = new AIDecisionMaker(this);
		selectUnitStrategy = new SelectNextUnitRandomly();
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

	public void act(List<PlayerEntity> playerUnits, List<AiEntity> aiUnits) {
		AiEntity entity = selectUnitStrategy.selectNextEntity(aiUnits);
		entity.notifyEntityObserver(EntityCommand.FOCUS_CAMERA);
		aiUnitAct(entity, playerUnits, aiUnits);
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

	public void aiUnitAct(AiEntity unit, List<PlayerEntity> playerUnits, List<AiEntity> aiUnits) {
		aiDecisionMaker.makeDecision(unit, playerUnits, aiUnits);
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
