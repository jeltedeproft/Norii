package com.mygdx.game.battle.battleStates;

import com.badlogic.gdx.Input.Buttons;
import com.mygdx.game.audio.AudioObserver;
import com.mygdx.game.battle.BattleManager;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.EntityObserver.EntityCommand;
import com.mygdx.game.map.TiledMapActor;
import com.mygdx.game.particles.ParticleMaker;
import com.mygdx.game.particles.ParticleType;

import utility.Utility;

public class AttackBattleState extends BattleState {
	private final BattleManager battlemanager;

	public AttackBattleState(BattleManager battlemanager) {
		this.battlemanager = battlemanager;
	}

	@Override
	public void clickedOnTile(TiledMapActor actor) {
		ParticleMaker.deactivateAllParticlesOfType(ParticleType.ATTACK);
		exit();
	}

	@Override
	public void clickedOnUnit(Entity entity) {
		possibleAttack(entity);
		ParticleMaker.deactivateAllParticlesOfType(ParticleType.ATTACK);
	}

	@Override
	public void exit() {
		battlemanager.setCurrentBattleState(battlemanager.getActionBattleState());
		battlemanager.getCurrentBattleState().entry();
	}

	private void possibleAttack(Entity entity) {
		final Entity currentUnit = battlemanager.getActiveUnit();
		final boolean closeEnough = Utility.checkIfUnitsWithinDistance(entity, currentUnit.getCurrentPosition(), currentUnit.getEntityData().getAttackRange());
		if ((entity.isPlayerUnit() != currentUnit.isPlayerUnit()) && closeEnough) {
			currentUnit.attack(entity);
			currentUnit.setLocked(true);
			currentUnit.notifyEntityObserver(EntityCommand.UNIT_LOCKED);
			notifyAudio(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.ATTACK_SOUND);
		}
		this.exit();
	}

	@Override
	public void buttonPressed(int button) {
		switch (button) {
		case Buttons.RIGHT:
			ParticleMaker.deactivateAllParticlesOfType(ParticleType.ATTACK);
			exit();
			break;
		case Buttons.LEFT:
			break;
		case Buttons.MIDDLE:
			break;
		default:
			break;
		}
	}
}
