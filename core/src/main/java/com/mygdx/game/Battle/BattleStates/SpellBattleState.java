package com.mygdx.game.Battle.BattleStates;

import java.util.List;

import com.badlogic.gdx.Input.Buttons;
import com.mygdx.game.Audio.AudioObserver;
import com.mygdx.game.Battle.BattleManager;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.EntityAnimation;
import com.mygdx.game.Entities.EntityAnimation.Direction;
import com.mygdx.game.Entities.EntityAnimationType;
import com.mygdx.game.Entities.EntityObserver.EntityCommand;
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

	private void possibleTileSpell(final TiledMapPosition targetPos) {
		final Ability ability = battlemanager.getCurrentSpell();
		final Entity currentUnit = battlemanager.getActiveUnit();

		if (isValidTileTarget(currentUnit, targetPos)) {
			currentUnit.getEntityAnimation().setCurrentAnimationType(EntityAnimationType.WALK);
			selectSpell(null, ability, currentUnit, targetPos);
			currentUnit.getEntityAnimation().setCurrentAnimationType(EntityAnimationType.WALK);
		}
		exit();
	}

	private boolean isValidTileTarget(Entity caster, TiledMapPosition targetPos) {
		final Ability ability = battlemanager.getCurrentSpell();

		final boolean correctAreaOfEffect = checkAreaOfEffect(caster, targetPos, ability);
		final boolean correctVisibility = checkVisibility(caster, targetPos);
		final boolean correctTarget = checkTarget(ability, Target.CELL);

		return correctAreaOfEffect && correctVisibility && correctTarget;
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

		if (isValidUnitTarget(currentUnit, target)) {
			selectSpell(target, ability, currentUnit, target.getCurrentPosition());
			currentUnit.setLocked(true);
			currentUnit.notifyEntityObserver(EntityCommand.UNIT_LOCKED);
		}
		exit();
	}

	private boolean isValidUnitTarget(Entity caster, Entity target) {
		final Ability ability = battlemanager.getCurrentSpell();
		final AffectedTeams affectedTeams = ability.getAffectedTeams();

		final boolean correctTeam = checkTeams(caster, target, affectedTeams);
		final boolean correctAreaOfEffect = checkAreaOfEffect(caster, target.getCurrentPosition(), ability);
		final boolean correctVisibility = checkVisibility(caster, target.getCurrentPosition());
		final boolean correctTarget = checkTarget(ability, Target.UNIT);

		return correctAreaOfEffect && correctTeam && correctVisibility && correctTarget;
	}

	private boolean checkTeams(Entity caster, Entity target, final AffectedTeams affectedTeams) {
		if (affectedTeams == AffectedTeams.BOTH) {
			return true;
		}

		if (affectedTeams == AffectedTeams.ENEMY) {
			return caster.isPlayerUnit() != target.isPlayerUnit();
		}

		return ((affectedTeams == AffectedTeams.FRIENDLY) && (caster.isPlayerUnit() != target.isPlayerUnit()));
	}

	private boolean checkAreaOfEffect(Entity caster, TiledMapPosition targetPos, Ability ability) {
		final LineOfSight lineOfSight = ability.getLineOfSight();
		switch (lineOfSight) {
		case LINE:
			return checkLine(caster, targetPos, ability);
		case CIRCLE:
			return checkCircle(caster, targetPos, ability);
		case CROSS:
			return checkCross(caster, targetPos, ability);
		default:
			throw new IllegalArgumentException("not a valid line of sight");
		}
	}

	private boolean checkLine(Entity caster, TiledMapPosition targetPos, Ability ability) {
		final Direction direction = caster.getEntityAnimation().getCurrentDirection();
		final int range = ability.getSpellData().getRange();
		return checkIfInLine(caster, targetPos, range, direction);
	}

	private boolean checkIfInLine(final Entity caster, final TiledMapPosition targetPos, final int range, final Direction direction) {
		final TiledMapPosition casterPos = caster.getCurrentPosition();
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

	private boolean checkCircle(Entity caster, TiledMapPosition targetPos, Ability ability) {
		final int range = ability.getSpellData().getRange();
		return Utility.checkIfUnitsWithinDistance(caster, targetPos, range);
	}

	private boolean checkCross(Entity caster, TiledMapPosition targetPos, Ability ability) {
		final int range = ability.getSpellData().getRange();
		final TiledMapPosition casterPos = caster.getCurrentPosition();
		final boolean checkX = Math.abs(casterPos.getTileX() - targetPos.getTileX()) <= range;
		final boolean checkY = Math.abs(casterPos.getTileY() - targetPos.getTileY()) <= range;
		return (checkX && checkY);
	}

	private boolean checkVisibility(Entity caster, TiledMapPosition targetPos) {
		return battlemanager.getPathFinder().lineOfSight(caster, targetPos, battlemanager.getUnits());
	}

	private boolean checkTarget(Ability ability, Target targetType) {
		return (ability.getTarget() == targetType || ability.getTarget() == Target.BOTH);
	}

	private void selectSpell(final Entity target, final Ability ability, final Entity currentUnit, final TiledMapPosition targetPos) {
		switch (ability.getAbilityEnum()) {
		case FIREBALL:
			castFireBall(currentUnit, targetPos, ability);
			break;
		case SWAP:
			castSwap(currentUnit, target, ability);
			break;
		case TURN_TO_STONE:
			castTurnToStone(currentUnit, target, ability);
			break;
		case HAMMERBACK:
			castHammerback(currentUnit, targetPos, ability);
			break;
		default:
			break;
		}
	}

	private void castFireBall(final Entity caster, final TiledMapPosition targetPos, final Ability ability) {
		caster.setAp(caster.getAp() - ability.getSpellData().getApCost());
		notifyAudio(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.FIREBALL_SOUND);
		ParticleMaker.addParticle(ParticleType.FIREBALL, targetPos, 0);

		final Entity possibleTarget = getEntityAtPosition(targetPos);
		if (possibleTarget != null) {
			possibleTarget.damage(ability.getSpellData().getDamage());
		}
	}

	private void castSwap(final Entity caster, final Entity target, final Ability ability) {
		caster.setAp(caster.getAp() - ability.getSpellData().getApCost());
		notifyAudio(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SWAP_SOUND);
		ParticleMaker.addParticle(ParticleType.SWAP, caster.getCurrentPosition(), 0);
		final TiledMapPosition posCaster = caster.getCurrentPosition();
		caster.setCurrentPosition(target.getCurrentPosition());
		target.setCurrentPosition(posCaster);
	}

	private void castTurnToStone(final Entity caster, final Entity target, final Ability ability) {
		caster.setAp(caster.getAp() - ability.getSpellData().getApCost());
		notifyAudio(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.STONE_SOUND);
		target.changeAnimation(new EntityAnimation("sprites/characters/rocksheet.png"));
		target.addModifier(ModifiersEnum.IMAGE_CHANGED, 2, 0);
		target.addModifier(ModifiersEnum.STUNNED, 2, 0);
	}

	private void castHammerback(final Entity caster, final TiledMapPosition targetPos, final Ability ability) {
		caster.setAp(caster.getAp() - ability.getSpellData().getApCost());
		notifyAudio(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.HAMMER_SOUND);
	}

	private Entity getEntityAtPosition(TiledMapPosition targetPos) {
		final List<Entity> units = battlemanager.getUnits();
		for (final Entity unit : units) {
			if (unit.getCurrentPosition().isTileEqualTo(targetPos)) {
				return unit;
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
