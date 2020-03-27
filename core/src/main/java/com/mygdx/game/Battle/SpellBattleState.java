package com.mygdx.game.Battle;

import com.badlogic.gdx.Input.Buttons;
import com.mygdx.game.Audio.AudioObserver;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Magic.Ability;
import com.mygdx.game.Map.TiledMapActor;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;

public class SpellBattleState extends BattleState {
	private final BattleManager battlemanager;

	public SpellBattleState(final BattleManager battlemanager) {
		this.battlemanager = battlemanager;
	}

	@Override
	public void clickedOnTile(final TiledMapActor actor) {
		possibleTileSpell(actor);
		ParticleMaker.deactivateAllParticlesOfType(ParticleType.SPELL);
		exit();
	}

	@Override
	public void clickedOnUnit(final Entity entity) {
		possibleUnitTargetSpell(entity);
		ParticleMaker.deactivateAllParticlesOfType(ParticleType.SPELL);
	}

	@Override
	public void exit() {
		battlemanager.setCurrentBattleState(battlemanager.getSpellBattleState());
		battlemanager.getCurrentBattleState().entry();
	}

	private void possibleUnitTargetSpell(final Entity entity) {
		final Ability abilityName = battlemanager.getCurrentSpell();
		final Entity currentUnit = battlemanager.getActiveUnit();
		final Entity possibleTarget = getPossibleTarget(entity);
		if (possibleTarget != null && !(possibleTarget.getName().equalsIgnoreCase(currentUnit.getName()))) {
			//currentUnit.castSpell(possibleTarget);
			notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SPELL_SOUND);
		}
		exit();
	}

	private void possibleTileSpell(final TiledMapActor actor) {
		// TODO Auto-generated method stub
	}

	private Entity getPossibleTarget(final Entity targetEntity) {
		for (final Entity entity : battlemanager.getUnits()) {
			if ((entity.getCurrentPosition().getTileX() == targetEntity.getCurrentPosition().getTileX()) && (entity.getCurrentPosition().getTileY() == targetEntity.getCurrentPosition().getTileY())) {
				return entity;
			}
		}
		return null;
	}

	@Override
	public void buttonPressed(final int button) {
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
