package com.jelte.norii.battle.battlephase;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.jelte.norii.battle.BattleManager;
import com.jelte.norii.battle.MessageToBattleScreen;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.Player;
import com.jelte.norii.entities.UnitOwner.Alliance;
import com.jelte.norii.map.TiledMapActor;
import com.jelte.norii.particles.ParticleMaker;
import com.jelte.norii.particles.ParticleType;
import com.jelte.norii.utility.TiledMapPosition;

public class DeploymentBattlePhase extends BattlePhase {
	private static final String TAG = DeploymentBattlePhase.class.getSimpleName();

	private final BattleManager battlemanager;
	private final List<Entity> playerUnits;
	private int deployingUnitNumber;

	public DeploymentBattlePhase(final BattleManager battlemanager) {
		this.battlemanager = battlemanager;
		deployingUnitNumber = 0;
		playerUnits = Player.getInstance().getTeam();
		playerUnits.get(0).getVisualComponent().setInDeploymentPhase(true);
	}

	@Override
	public void exit() {
		removeSpawnParticles();
		battlemanager.sendMessageToBattleScreen(MessageToBattleScreen.DEPLOYMENT_FINISHED, battlemanager.getActiveUnit());
		battlemanager.setCurrentBattleState(battlemanager.getSelectUnitBattleState());
		battlemanager.getCurrentBattleState().entry();
	}

	private void removeSpawnParticles() {
		if (Player.getInstance().getAlliance().equals(Alliance.TEAM_BLUE)) {
			ParticleMaker.deactivateAllParticlesOfType(ParticleType.SPAWN);
		}
		if (Player.getInstance().getAlliance().equals(Alliance.TEAM_RED)) {
			ParticleMaker.deactivateAllParticlesOfType(ParticleType.PURPLE_SQUARE);
		}
	}

	@Override
	public void clickedOnTile(final TiledMapActor actor) {
		if (battlemanager.isPlayerTurn()) {
			deployUnit(actor);
		}
	}

	private void deployUnit(final TiledMapActor actor) {
		boolean isPossibleSpawnPoint = (Player.getInstance().getAlliance() == Alliance.TEAM_BLUE)	? actor.getIsFreeSpawn()
																									: actor.getIsAISpawn();
		if (Boolean.TRUE.equals(isPossibleSpawnPoint)) {
			final TiledMapPosition newPosition = actor.getActorPos();

			if ((playerUnits != null) && (deployingUnitNumber < playerUnits.size())) {
				Player.getInstance().setMyTurn(false);
				battlemanager.setPlayerTurn(!battlemanager.isPlayerTurn());
				deployUnit(newPosition);
				actor.setIsFreeSpawn(false);
				ParticleMaker.deactivateParticle(ParticleMaker.getParticle(ParticleType.SPAWN, newPosition));
				ParticleMaker.deactivateParticle(ParticleMaker.getParticle(ParticleType.PURPLE_SQUARE, newPosition));
				battlemanager.sendMessageToBattleScreen(MessageToBattleScreen.UNIT_DEPLOYED, battlemanager.getActiveUnit());
				battlemanager.getEnemyTeamLeader().playerUnitSpawned(playerUnits.get(deployingUnitNumber), newPosition);
				nextDeployment();
			} else {
				Gdx.app.debug(TAG, "can't deploy unit, units is null or activeunitindex is > the length of units");
			}
		} else {
			battlemanager.sendMessageToBattleScreen(MessageToBattleScreen.INVALID_SPAWN_POINT, battlemanager.getActiveUnit());
		}
	}

	private void deployUnit(final TiledMapPosition newPosition) {
		final Entity unitToDeploy = playerUnits.get(deployingUnitNumber);
		unitToDeploy.getVisualComponent().setInDeploymentPhase(false);
		initiateUnitInBattle(unitToDeploy, newPosition);
	}

	private void initiateUnitInBattle(final Entity unit, final TiledMapPosition pos) {
		unit.setCurrentPosition(pos);
		battlemanager.addUnit(unit);
		unit.getVisualComponent().initiateInBattle(pos);
	}

	private void nextDeployment() {
		deployingUnitNumber++;
		if (deployingUnitNumber < playerUnits.size()) {
			playerUnits.get(deployingUnitNumber).getVisualComponent().setInDeploymentPhase(true);
		}
		checkIfLastUnit();
	}

	private void checkIfLastUnit() {
		if (deployingUnitNumber >= playerUnits.size()) {
			exit();
		}
	}

	public int getDeployedUnitNumber() {
		return deployingUnitNumber;
	}

	@Override
	public void hoveredOnTile(TiledMapActor actor) {
		playerUnits.get(deployingUnitNumber).getVisualComponent().setVisualPosition(actor.getActorPos());
	}
}
