package com.mygdx.game.Battle;

import com.badlogic.gdx.Input.Buttons;
import com.mygdx.game.Audio.AudioObserver;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntityAnimation;
import com.mygdx.game.Magic.Ability;
import com.mygdx.game.Magic.ModifiersEnum;
import com.mygdx.game.Map.TiledMapActor;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;

import Utility.TiledMapPosition;

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
		battlemanager.setCurrentBattleState(battlemanager.getActionBattleState());
		battlemanager.getCurrentBattleState().entry();
	}

	private void possibleUnitTargetSpell(final Entity entity) {
		final Ability ability = battlemanager.getCurrentSpell();
		final Entity currentUnit = battlemanager.getActiveUnit();
		final Entity possibleTarget = getPossibleTarget(entity);
		if (possibleTarget != null && !(possibleTarget.getEntityID() == currentUnit.getEntityID())) {
			switch (ability.getAbilityEnum()) {
			case FIREBALL:
				castFireBall(currentUnit, possibleTarget, ability);
				break;
			case SWAP:
				castSwap(currentUnit, possibleTarget, ability);
				break;
			case TURN_TO_STONE:
				castTurnToStone(currentUnit, possibleTarget, ability);
				break;
			default:
				break;
			}
		}
		exit();
	}

	private void castFireBall(final Entity caster, final Entity target, final Ability ability) {
		caster.setAp(caster.getAp() - ability.getSpellData().getApCost());
		target.damage(ability.getSpellData().getDamage());
		notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SPELL_SOUND);
		ParticleMaker.addParticle(ParticleType.FIREBALL, target.getCurrentPosition());
	}

	private void castSwap(final Entity caster, final Entity target, final Ability ability) {
		caster.setAp(caster.getAp() - ability.getSpellData().getApCost());
		notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SPELL_SOUND);
		ParticleMaker.addParticle(ParticleType.SWAP, caster.getCurrentPosition());
		final TiledMapPosition posCaster = caster.getCurrentPosition();
		caster.setCurrentPosition(target.getCurrentPosition());
		target.setCurrentPosition(posCaster);
	}

	private void castTurnToStone(final Entity caster, final Entity target, final Ability ability) {
		caster.setAp(caster.getAp() - ability.getSpellData().getApCost());
		notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SPELL_SOUND);
		target.changeAnimation(new EntityAnimation("sprites/characters/rocksheet.png"));
		target.addModifier(ModifiersEnum.IMAGE_CHANGED, 2, 0);
		target.addModifier(ModifiersEnum.STUNNED, 2, 0);
	}

	private void possibleTileSpell(final TiledMapActor actor) {
		// TODO Auto-generated method stub
	}

	private Entity getPossibleTarget(final Entity targetEntity) {
		for (final Entity entity : battlemanager.getUnits()) {
			if ((entity.getCurrentPosition().getTileX() == targetEntity.getCurrentPosition().getTileX())
				&& (entity.getCurrentPosition().getTileY() == targetEntity.getCurrentPosition().getTileY())) {
				return entity;
			}
		}
		return null;
	}

	@Override
	public void buttonPressed(final int button) {
		switch (button) {
		case Buttons.RIGHT:
			ParticleMaker.deactivateAllParticlesOfType(ParticleType.SPELL);
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
