package com.jelte.norii.battle.battlePhase;

import com.badlogic.gdx.Input.Buttons;
import com.jelte.norii.audio.AudioObserver;
import com.jelte.norii.battle.BattleManager;
import com.jelte.norii.battle.MessageToBattleScreen;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.map.TiledMapActor;
import com.jelte.norii.particles.ParticleMaker;
import com.jelte.norii.particles.ParticleType;
import com.jelte.norii.utility.Utility;

public class AttackBattlePhase extends BattlePhase {
	private final BattleManager battlemanager;

	public AttackBattlePhase(BattleManager battlemanager) {
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
			battlemanager.updateHp(entity);
			currentUnit.setLocked(true);
			battlemanager.setLockedUnit(entity);
			battlemanager.sendMessageToBattleScreen(MessageToBattleScreen.UPDATE_UI, possibleTarget);
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
