package com.jelte.norii.battle.battlePhase;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.jelte.norii.battle.BattleManager;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.Player;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.map.TiledMapActor;
import com.jelte.norii.particles.ParticleMaker;
import com.jelte.norii.particles.ParticleType;
import com.jelte.norii.utility.TiledMapPosition;

public class DeploymentBattlePhase extends BattlePhase {
	private static final String TAG = DeploymentBattlePhase.class.getSimpleName();

	private final BattleManager battlemanager;
	private int deployingUnitNumber;
	private final List<Entity> playerUnits;

	public DeploymentBattlePhase(final BattleManager battlemanager) {
		this.battlemanager = battlemanager;
		deployingUnitNumber = 0;
		playerUnits = Player.getInstance().getTeam();
		playerUnits.get(0).setInDeploymentPhase(true);
	}

	@Override
	public void exit() {
		ParticleMaker.deactivateAllParticlesOfType(ParticleType.SPAWN);
		battlemanager.setCurrentBattleState(battlemanager.getSelectUnitBattleState());
		battlemanager.getCurrentBattleState().entry();
	}

	@Override
	public void clickedOnTile(final TiledMapActor actor) {
		deployUnit(actor);
	}

	private void deployUnit(final TiledMapActor actor) {
		if (Boolean.TRUE.equals(actor.getIsFreeSpawn())) {
			final TiledMapPosition newPosition = actor.getActorPos();

			if ((playerUnits != null) && (deployingUnitNumber < playerUnits.size())) {
				deployUnit(newPosition);
				actor.setIsFreeSpawn(false);
				ParticleMaker.deactivateParticle(ParticleMaker.getParticle(ParticleType.SPAWN, newPosition));
				nextDeployment();
			} else {
				Gdx.app.debug(TAG, "can't deploy unit, units is null or activeunitindex is > the length of units");
			}
		}
	}

	private void deployUnit(final TiledMapPosition newPosition) {
		final Entity unitToDeploy = playerUnits.get(deployingUnitNumber);
		unitToDeploy.setInDeploymentPhase(false);
		initiateUnitInBattle(unitToDeploy, newPosition);
	}

	private void nextDeployment() {
		deployingUnitNumber++;
		if (deployingUnitNumber < playerUnits.size()) {
			playerUnits.get(deployingUnitNumber).setInDeploymentPhase(true);
		}
		checkIfLastUnit();
	}

	private void initiateUnitInBattle(final Entity unit, final TiledMapPosition pos) {
		unit.setInBattle(true);
		unit.setCurrentPosition(pos);
		unit.getEntityactor().setTouchable(Touchable.enabled);
	}

	private void checkIfLastUnit() {
		if (deployingUnitNumber >= playerUnits.size()) {
			exit();
		}
	}

	@Override
	public void hoveredOnTile(TiledMapActor actor) {
		playerUnits.get(deployingUnitNumber).setCurrentPosition(actor.getActorPos());
	}
}
