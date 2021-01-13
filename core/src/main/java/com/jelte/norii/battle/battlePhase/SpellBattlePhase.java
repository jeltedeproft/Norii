package com.jelte.norii.battle.battlePhase;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.jelte.norii.ai.AIDecisionMaker;
import com.jelte.norii.audio.AudioCommand;
import com.jelte.norii.audio.AudioManager;
import com.jelte.norii.audio.AudioTypeEvent;
import com.jelte.norii.battle.BattleManager;
import com.jelte.norii.battle.MessageToBattleScreen;
import com.jelte.norii.battle.battleState.HypotheticalUnit;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityAnimation;
import com.jelte.norii.entities.EntityAnimation.Direction;
import com.jelte.norii.entities.EntityAnimationType;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Ability.AffectedTeams;
import com.jelte.norii.magic.Ability.LineOfSight;
import com.jelte.norii.magic.Ability.Target;
import com.jelte.norii.magic.ModifiersEnum;
import com.jelte.norii.map.MyPathFinder;
import com.jelte.norii.map.TiledMapActor;
import com.jelte.norii.particles.ParticleMaker;
import com.jelte.norii.particles.ParticleType;
import com.jelte.norii.utility.MyPoint;
import com.jelte.norii.utility.TiledMapPosition;
import com.jelte.norii.utility.Utility;

public class SpellBattlePhase extends BattlePhase {
	private final BattleManager battlemanager;

	public SpellBattlePhase(final BattleManager battlemanager) {
		this.battlemanager = battlemanager;
	}

	@Override
	public void clickedOnTile(final TiledMapActor actor) {
		possibleTileSpell(actor.getActorPos());
		ParticleMaker.deactivateAllParticlesOfType(ParticleType.SPELL);
		exit();
	}

	private void possibleTileSpell(final TiledMapPosition targetPos) {
		final Entity currentUnit = battlemanager.getActiveUnit();

		if (isValidTileTarget(currentUnit, targetPos, ability)) {
			currentUnit.getEntityAnimation().setCurrentAnimationType(EntityAnimationType.WALK);
			selectSpell(null, ability, currentUnit, targetPos);
			currentUnit.getEntityAnimation().setCurrentAnimationType(EntityAnimationType.WALK);
		}
		exit();
	}

	private boolean isValidTileTarget(Entity caster, TiledMapPosition targetPos, Ability ability) {
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
		final Entity currentUnit = battlemanager.getActiveUnit();

		if (isValidUnitTarget(currentUnit, target)) {
			selectSpell(target, ability, currentUnit, target.getCurrentPosition());
			currentUnit.setLocked(true);
			battlemanager.setLockedUnit(currentUnit);
		}
		exit();
	}

	private boolean isValidUnitTarget(Entity caster, Entity target) {
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
		return MyPathFinder.getInstance().lineOfSight(caster, targetPos, battlemanager.getUnits());
	}

	private boolean checkTarget(Ability ability, Target targetType) {
		switch (ability.getTarget()) {
		case CELL:
			return ((targetType == Target.CELL) || (targetType == Target.UNIT) || (targetType == Target.CELL_BUT_NO_UNIT));
		case UNIT:
			return (targetType == Target.UNIT);
		case CELL_BUT_NO_UNIT:
			return ((targetType == Target.CELL) || (targetType == Target.CELL_BUT_NO_UNIT));
		case SELF:
			return (targetType == Target.SELF);
		case NO_TARGET:
			return (targetType == Target.NO_TARGET);
		default:
			Gdx.app.debug("SpellBattlePhase", "ability : " + ability + " has no valid target : " + ability.getTarget());
			return false;
		}
	}

	public void executeSpellForAi(Entity entity, Ability ability, MyPoint target) {
		final TiledMapPosition targetPos = new TiledMapPosition().setPositionFromTiles(target.x, target.y);
		final List<Entity> units = battlemanager.getUnits();
		for (final Entity unit : units) {
			if (unit.getCurrentPosition().isTileEqualTo(targetPos)) {
				selectSpell(unit, ability, entity, targetPos);
			}
		}
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
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_PLAY_ONCE, AudioTypeEvent.FIREBALL_SOUND);
		ParticleMaker.addParticle(ParticleType.FIREBALL, targetPos, 0);

		final Entity possibleTarget = getEntityAtPosition(targetPos);
		if (possibleTarget != null) {
			possibleTarget.damage(ability.getSpellData().getDamage());
			battlemanager.updateHp(possibleTarget);
			battlemanager.sendMessageToBattleScreen(MessageToBattleScreen.UPDATE_UI, possibleTarget);
		}
	}

	private void castSwap(final Entity caster, final Entity target, final Ability ability) {
		caster.setAp(caster.getAp() - ability.getSpellData().getApCost());
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_PLAY_ONCE, AudioTypeEvent.SWAP_SOUND);
		ParticleMaker.addParticle(ParticleType.SWAP, caster.getCurrentPosition(), 0);
		final TiledMapPosition posCaster = caster.getCurrentPosition();
		caster.setCurrentPosition(target.getCurrentPosition());
		target.setCurrentPosition(posCaster);
	}

	private void castTurnToStone(final Entity caster, final Entity target, final Ability ability) {
		caster.setAp(caster.getAp() - ability.getSpellData().getApCost());
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_PLAY_ONCE, AudioTypeEvent.STONE_SOUND);
		target.changeAnimation(new EntityAnimation("Rock"));
		target.addModifier(ModifiersEnum.IMAGE_CHANGED, 2, 0);
		target.addModifier(ModifiersEnum.STUNNED, 2, 0);
	}

	private void castHammerback(final Entity caster, final TiledMapPosition targetPos, final Ability ability) {
		caster.setAp(caster.getAp() - ability.getSpellData().getApCost());
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_PLAY_ONCE, AudioTypeEvent.HAMMER_SOUND);
		final Entity hammerEntity = new Entity(EntityTypes.BOOMERANG, caster.getOwner());
		hammerEntity.setInBattle(true);
		hammerEntity.setCurrentPosition(targetPos);
		hammerEntity.getEntityactor().setTouchable(Touchable.enabled);
		hammerEntity.addModifier(ModifiersEnum.DAMAGE_OVER_TIME, 3, 1);
		battlemanager.addUnit(hammerEntity);
		battlemanager.sendMessageToBattleScreen(MessageToBattleScreen.ADD_UNIT_UI, hammerEntity);
		battlemanager.getBattleState().addEntity(targetPos.getTileX(), targetPos.getTileY(), new HypotheticalUnit(hammerEntity.getEntityID(), hammerEntity.isPlayerUnit(), hammerEntity.getHp(), hammerEntity.getEntityData().getMaxHP(),
				hammerEntity.getAttackRange(), hammerEntity.getEntityData().getAttackPower(), hammerEntity.getAp(), hammerEntity.getModifiers(), hammerEntity.getAbilities(), targetPos.getTileX(), targetPos.getTileY()));
		final List<MyPoint> crossedCells = AIDecisionMaker.findLine(caster.getCurrentPosition().getTileX(), caster.getCurrentPosition().getTileY(), targetPos.getTileX(), targetPos.getTileY());
		// check if correct and apply similar effects in aidecisionMaker
		battlemanager.getBattleState();
		for (final MyPoint point : crossedCells) {
			if (battlemanager.getBattleState().get(point.x, point.y).isOccupied()) {
				HypotheticalUnit unit = battlemanager.getBattleState().get(point.x, point.y).getUnit();
				battlemanager.getEntityByID(unit.getEntityId()).damage(ability.getSpellData().getDamage());
				battlemanager.updateHp(battlemanager.getEntityByID(unit.getEntityId()));
			}
		}
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
	public void setAbility(Ability ability) {
		this.ability = ability;
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
