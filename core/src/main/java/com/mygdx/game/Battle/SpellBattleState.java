package com.mygdx.game.Battle;

import com.badlogic.gdx.Input.Buttons;
import com.mygdx.game.Audio.AudioObserver;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntityAnimation;
import com.mygdx.game.Entities.EntityAnimation.Direction;
import com.mygdx.game.Magic.Ability;
import com.mygdx.game.Magic.Ability.AffectedTeams;
import com.mygdx.game.Magic.Ability.LineOfSight;
import com.mygdx.game.Magic.Ability.Target;
import com.mygdx.game.Magic.ModifiersEnum;
import com.mygdx.game.Map.TiledMapActor;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;

import Utility.TiledMapPosition;
import Utility.Utility;

public class SpellBattleState extends BattleState {
	private final BattleManager battlemanager;

	public SpellBattleState(final BattleManager battlemanager) {
		this.battlemanager = battlemanager;
	}

	@Override
	public void clickedOnTile(final TiledMapActor actor) {
		possibleTileSpell(actor.getActorPos());
		ParticleMaker.deactivateAllParticlesOfType(ParticleType.SPELL);
		exit();
	}

	private void possibleTileSpell(final TiledMapPosition pos) {
		// todo
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

	private void possibleUnitTargetSpell(final Entity target) {
		final Ability ability = battlemanager.getCurrentSpell();
		final Entity currentUnit = battlemanager.getActiveUnit();

		if (isValidTarget(currentUnit, target)) {
			switch (ability.getAbilityEnum()) {
			case FIREBALL:
				castFireBall(currentUnit, target, ability);
				break;
			case SWAP:
				castSwap(currentUnit, target, ability);
				break;
			case TURN_TO_STONE:
				castTurnToStone(currentUnit, target, ability);
				break;
			default:
				break;
			}
		}
		exit();
	}

	private boolean isValidTarget(Entity caster, Entity target) {
		final Ability ability = battlemanager.getCurrentSpell();
		final AffectedTeams affectedTeams = ability.getAffectedTeams();

		final boolean correctTeam = checkTeams(caster, target, affectedTeams);
		final boolean correctAreaOfEffect = checkAreaOfEffect(caster, target, ability);
		final boolean correctVisibility = checkVisibility(caster, target, ability);
		final boolean correctTarget = checkTarget(ability);

		return correctAreaOfEffect && correctTeam && correctVisibility && correctTarget;
	}

	private boolean checkTeams(Entity caster, Entity target, final AffectedTeams affectedTeams) {
		if ((affectedTeams == AffectedTeams.ENEMY) && (caster.isPlayerUnit() == target.isPlayerUnit())) {
			return false;
		}

		return ((affectedTeams == AffectedTeams.FRIENDLY) && (caster.isPlayerUnit() != target.isPlayerUnit()));
	}

	private boolean checkAreaOfEffect(Entity caster, Entity target, Ability ability) {
		final LineOfSight lineOfSight = ability.getLineOfSight();
		switch (lineOfSight) {
		case LINE:
			return checkLine(caster, target, ability);
		case CIRCLE:
			return checkCircle(caster, target, ability);
		case CROSS:
			return checkCross(caster, target, ability);
		default:
			throw new IllegalArgumentException("not a valid line of sight");
		}
	}

	private boolean checkLine(Entity caster, Entity target, Ability ability) {
		final Direction direction = caster.getEntityAnimation().getCurrentDirection();
		final int range = ability.getSpellData().getRange();
		return checkIfInLine(caster, target, range, direction);
	}

	private boolean checkIfInLine(final Entity caster, final Entity target, final int range, final Direction direction) {
		final TiledMapPosition casterPos = caster.getCurrentPosition();
		final TiledMapPosition targetPos = target.getCurrentPosition();
		if ((Math.abs(casterPos.getTileX() - targetPos.getTileX()) + (Math.abs(casterPos.getTileY() - targetPos.getTileY()))) <= range) {
			switch (direction) {
			case UP:
				return (casterPos.getTileX() == targetPos.getTileX()) && (casterPos.getTileY() <= targetPos.getTileY());
			case DOWN:
				return (casterPos.getTileX() == targetPos.getTileX()) && (casterPos.getTileY() >= targetPos.getTileY());
			case LEFT:
				return (casterPos.getTileX() >= targetPos.getTileX()) && (casterPos.getTileY() == targetPos.getTileY());
			case RIGHT:
				return (casterPos.getTileX() <= targetPos.getTileX()) && (casterPos.getTileY() == targetPos.getTileY());
			default:
				return false;
			}
		}
		return false;
	}

	private boolean checkCircle(Entity caster, Entity target, Ability ability) {
		final int range = ability.getSpellData().getRange();
		return Utility.checkIfUnitsWithinDistance(caster, target, range);
	}

	private boolean checkCross(Entity caster, Entity target, Ability ability) {
		final int range = ability.getSpellData().getRange();
		final TiledMapPosition casterPos = caster.getCurrentPosition();
		final TiledMapPosition targetPos = target.getCurrentPosition();
		final boolean checkX = Math.abs(casterPos.getTileX() - targetPos.getTileX()) <= range;
		final boolean checkY = Math.abs(casterPos.getTileY() - targetPos.getTileY()) <= range;
		return (checkX && checkY);
	}

	private boolean checkVisibility(Entity caster, Entity target, Ability ability) {
		return battlemanager.getPathFinder().lineOfSight(caster, target, battlemanager.getUnits());
	}

	private boolean checkTarget(Ability ability) {
		return (ability.getTarget() == Target.UNIT);
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
