package com.jelte.norii.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.battle.battleState.BattleStateGridHelper;
import com.jelte.norii.battle.battleState.Move;
import com.jelte.norii.battle.battleState.SpellMove;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.entities.FakeEntityVisualComponent;
import com.jelte.norii.magic.AbilitiesEnum;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Ability.DamageType;
import com.jelte.norii.magic.Modifier;
import com.jelte.norii.magic.ModifiersEnum;
import com.jelte.norii.utility.MyPoint;
import com.jelte.norii.utility.TiledMapPosition;
import com.jelte.norii.utility.Utility;

public class BattleStateModifier {

	public BattleState applyTurnToBattleState(Entity aiUnit, UnitTurn turn, BattleState battleState) {
		final BattleState newState = battleState.makeCopy();
		final Entity copyUnit = newState.get(aiUnit.getCurrentPosition().getTileX(), aiUnit.getCurrentPosition().getTileY()).getUnit();
		for (final Move move : turn.getMoves()) {
			switch (move.getMoveType()) {
			case SPELL:
				applySpellOnBattleState(copyUnit, (SpellMove) move, newState);
				break;
			case ATTACK:
				applyAttackOnBattleState(copyUnit, move, newState);
				break;
			case MOVE:
				newState.moveUnitTo(copyUnit, move.getLocation());
				break;
			case DUMMY:
				// do nothing
			default:
				// do nothing
			}
		}
		return newState;
	}

	private void applyAttackOnBattleState(Entity aiUnit, Move move, BattleState battleState) {
		final MyPoint attackLocation = move.getLocation();
		final int damage = aiUnit.getEntityData().getAttackPower();
		battleState.damageUnit(attackLocation, damage, DamageType.PHYSICAL);
	}

	private void applySpellOnBattleState(Entity unit, SpellMove move, BattleState battleState) {
		final MyPoint casterPos = new MyPoint(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY());
		final Array<MyPoint> targets = move.getAffectedUnits();
		final MyPoint location = move.getLocation();
		final int damage = move.getAbility().getSpellData().getDamage();
		switch (move.getAbility().getAbilityEnum()) {
		case FIREBALL:
			battleState.damageUnit(location, damage, move.getAbility().getDamageType());
			break;
		case LOVE:
			castLove(battleState, casterPos, location, damage);
			break;
		case EXPLOSION:
			castExplosion(unit, move, battleState, location, damage);
			break;
		case HEAL:
			battleState.healUnit(location, damage);
			break;
		case INVISIBLE:
			castInvis(unit, battleState, location, damage);
			break;
		case PUSH:
			battleState.pushOrPullUnit(casterPos, location, damage, false);
			break;
		case PULL:
			battleState.pushOrPullUnit(casterPos, location, damage, true);
			break;
		case ARROW:
			castArrow(move, battleState, targets, damage);
			break;
		case ICEFIELD:
			castIcefield(move, battleState, targets, damage);
			break;
		case TURN_TO_STONE:
			castTurnToStone(battleState, location);
			break;
		case SWAP:
			castSwap(unit, battleState, location);
			break;
		case HAMMERBACK:
			castHammerBack(unit, move, battleState, casterPos, location, damage);
			break;
		case HAMMERBACKBACK:
			castHammerBackBack(move, battleState, casterPos, location, damage);
			break;
		case PORTAL:
			castPortal(unit, battleState, casterPos, location);
			break;
		case TRANSPORT:
			castTransport(unit, battleState);
			break;
		case SUMMON:
			castSummon(unit, battleState, location);
			break;
		case PLANT_SHIELD:
			castPlantShield(unit, battleState, location);
			break;
		case CRACKLE:
			castCrackle(move, battleState, location);
			break;
		default:
			// nothing
		}

	}

	private void castLove(BattleState battleState, final MyPoint casterPos, final MyPoint location, final int damage) {
		final Entity targetToDamage = battleState.get(location.x, location.y).getUnit();
		battleState.linkUnits(casterPos, location);
		battleState.addModifierToUnit(casterPos.x, casterPos.y, new Modifier(ModifiersEnum.LINKED, damage, targetToDamage.getEntityID()));// damage = turns
	}

	private void castExplosion(Entity unit, SpellMove move, BattleState battleState, final MyPoint location, final int damage) {
		final Array<Entity> neighbours = battleState.getNeighbours(location);
		for (final Entity entity : neighbours) {
			battleState.damageUnit(entity.getCurrentPosition().getTilePosAsPoint(), damage, move.getAbility().getDamageType());
		}
		unit.kill();
	}

	private void castInvis(Entity unit, BattleState battleState, final MyPoint location, final int damage) {
		unit.setInvisible(true);
		battleState.addModifierToUnit(location.x, location.y, new Modifier(ModifiersEnum.INVISIBLE, damage, 0));// damage = turns
	}

	private void castArrow(SpellMove move, BattleState battleState, final Array<MyPoint> targets, final int damage) {
		if (targets != null) {
			for (final MyPoint point : targets) {
				battleState.damageUnit(point, damage, move.getAbility().getDamageType());
			}
		}
	}

	private void castIcefield(SpellMove move, BattleState battleState, final Array<MyPoint> targets, final int damage) {
		if (targets != null) {
			for (final MyPoint point : targets) {
				battleState.damageUnit(point, damage, move.getAbility().getDamageType());
			}
		}
	}

	private void castTurnToStone(BattleState battleState, final MyPoint location) {
		battleState.addModifierToUnit(location.x, location.y, new Modifier(ModifiersEnum.STUNNED, 2, 0));
	}

	private void castSwap(Entity unit, BattleState battleState, final MyPoint location) {
		final Entity placeHolder = battleState.get(location.x, location.y).getUnit();
		battleState.swapPositions(unit, placeHolder);
	}

	private void castHammerBack(Entity unit, SpellMove move, BattleState battleState, final MyPoint casterPos, final MyPoint location, final int damage) {
		castHammerBackBack(move, battleState, casterPos, location, damage);
		final Entity hammerBackUnit = new Entity(EntityTypes.BOOMERANG, unit.getOwner(), false);
		hammerBackUnit.setVisualComponent(new FakeEntityVisualComponent());
		hammerBackUnit.setCurrentPosition(new TiledMapPosition().setPositionFromTiles(location.x, location.y));
		battleState.addEntity(hammerBackUnit);
		battleState.get(location.x, location.y).getUnit().addModifier(new Modifier(ModifiersEnum.DAMAGE_OVER_TIME_MAGICAL, 3, 1));
		battleState.get(location.x, location.y).getUnit().addAbility(AbilitiesEnum.HAMMERBACKBACK, casterPos);
	}

	private void castHammerBackBack(SpellMove move, BattleState battleState, final MyPoint casterPos, final MyPoint location, final int damage) {
		final List<MyPoint> crossedCellsBack = findLine(casterPos.x, casterPos.y, location.x, location.y);
		for (final MyPoint point : crossedCellsBack) {
			if (battleState.get(point.x, point.y).isOccupied()) {
				battleState.damageUnit(point, damage, move.getAbility().getDamageType());
			}
		}
	}

	private void castPortal(Entity unit, BattleState battleState, final MyPoint casterPos, final MyPoint location) {
		final boolean playerUnit = unit.isPlayerUnit();
		Array<Entity> entities;
		if (playerUnit) {
			entities = battleState.getPlayerUnits();
		} else {
			entities = battleState.getAiUnits();
		}
		int portalCount = 0;
		for (final Entity entity : entities) {
			if (entity.getEntityType() == EntityTypes.PORTAL) {
				portalCount++;
			}
		}

		if (portalCount < 2) {
			final Entity portalEntity = new Entity(EntityTypes.PORTAL, unit.getOwner(), false);
			unit.getOwner().addUnit(portalEntity);
			portalEntity.setVisualComponent(new FakeEntityVisualComponent());
			portalEntity.setCurrentPosition(new TiledMapPosition().setPositionFromTiles(location.x, location.y));
			battleState.addEntity(portalEntity);
			battleState.get(location.x, location.y).getUnit().addAbility(AbilitiesEnum.TRANSPORT, casterPos);
		}
	}

	private void castTransport(Entity unit, BattleState battleState) {
		final Array<Entity> units = battleState.getAllUnits();
		Entity otherPortal = null;
		final Array<Entity> unitsNextToPortal = new Array<>();

		for (final Entity unitToTransport : units) {
			if (BattleStateGridHelper.getInstance().isNextToButNotSelf(unitToTransport, unit)) {
				unitsNextToPortal.add(unitToTransport);
			}

			if ((unitToTransport.getEntityType() == EntityTypes.PORTAL) && (unitToTransport.getEntityID() != unit.getEntityID())) {
				otherPortal = unitToTransport;
			}
		}

		if (otherPortal != null) {
			for (final Entity unitToTransport : unitsNextToPortal) {
				final TiledMapPosition goal = battleState.findFreeSpotNextTo(otherPortal);
				battleState.get(goal.getTileX(), goal.getTileY()).setUnit(battleState.get(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY()).getUnit());
				battleState.get(unitToTransport.getCurrentPosition().getTileX(), unitToTransport.getCurrentPosition().getTileY()).removeUnit();
				unitToTransport.setCurrentPosition(goal);
				battleState.get(goal.getTileX(), goal.getTileY()).setOccupied(true);
			}
		}
	}

	private void castSummon(Entity unit, BattleState battleState, final MyPoint location) {
		final Entity ghostUnit = new Entity(EntityTypes.GHOST, unit.getOwner(), false);
		ghostUnit.setVisualComponent(new FakeEntityVisualComponent());
		ghostUnit.setCurrentPosition(new TiledMapPosition().setPositionFromTiles(location.x, location.y));
		battleState.addEntity(ghostUnit);
	}

	private void castPlantShield(Entity unit, BattleState battleState, final MyPoint location) {
		final Entity rock = new Entity(EntityTypes.ROCK, unit.getOwner(), false);
		battleState.addModifierToUnit(location.x, location.y, new Modifier(ModifiersEnum.STUNNED, 3, 0));
		battleState.addModifierToUnit(location.x, location.y, new Modifier(ModifiersEnum.PURE_DAMAGE, 3, 334));
		rock.setVisualComponent(new FakeEntityVisualComponent());
		rock.setCurrentPosition(new TiledMapPosition().setPositionFromTiles(location.x, location.y));
		battleState.addEntity(rock);
	}

	private void castCrackle(SpellMove move, BattleState battleState, final MyPoint location) {
		final Array<Entity> usedTargets = new Array<>();
		int entitiesHit = 0;

		// damage target, update ui, increase entities hit and save unit in list
		Entity target = battleState.get(location.x, location.y).getUnit();
		entitiesHit = crackleTarget(move.getAbility(), usedTargets, entitiesHit, target);

		// do same for that unit until enough units hit or no units closeby
		// make sure that unit first cast on is not coming back later on
		TreeMap<Integer, Array<Entity>> distancesToTarget = (TreeMap<Integer, Array<Entity>>) Utility.getDistancesWithTarget(location, battleState.getAllUnits());

		while ((!distancesToTarget.isEmpty()) && (entitiesHit <= 3)) {
			if (distancesToTarget.firstEntry().getValue().size == 0) {
				int j = 5;
			}
			final Array<Entity> closestUnits = getFirstNotNull(distancesToTarget);
			final Entity closestUnit = closestUnits.first();
			if (Utility.checkIfUnitsWithinDistance(closestUnit, target, 4)) {
				if (usedTargets.contains(closestUnit, false)) {
					// skip this unit
					closestUnits.removeIndex(0);
				} else {
					entitiesHit = crackleTarget(move.getAbility(), usedTargets, entitiesHit, closestUnit);
					distancesToTarget = (TreeMap<Integer, Array<Entity>>) Utility.getDistancesWithTarget(closestUnit.getCurrentPosition().getTilePosAsPoint(), battleState.getAllUnits());
					target = closestUnit;
				}
			} else {
				break;
			}
		}
	}

	private Array<Entity> getFirstNotNull(TreeMap<Integer, Array<Entity>> distancesToTarget) {
		for (Map.Entry<Integer, Array<Entity>> entry : distancesToTarget.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				return entry.getValue();
			}
		}
		return null;
	}

	private int crackleTarget(final Ability ability, Array<Entity> usedTargets, int entitiesHit, final Entity target) {
		if (target != null) {
			target.damage(ability.getSpellData().getDamage(), ability.getDamageType());
			usedTargets.add(target);
			entitiesHit++;
		}
		return entitiesHit;
	}

	/** Bresenham algorithm to find all cells crossed by a line **/
	public static List<MyPoint> findLine(int x0, int y0, int x1, int y1) {
		final List<MyPoint> line = new ArrayList<>();

		final int dx = Math.abs(x1 - x0);
		final int dy = Math.abs(y1 - y0);

		final int sx = x0 < x1 ? 1 : -1;
		final int sy = y0 < y1 ? 1 : -1;

		int err = dx - dy;
		int e2;

		while (true) {
			line.add(new MyPoint(x0, y0));

			if ((x0 == x1) && (y0 == y1))
				break;

			e2 = 2 * err;
			if (e2 > -dy) {
				err = err - dy;
				x0 = x0 + sx;
			}

			if (e2 < dx) {
				err = err + dx;
				y0 = y0 + sy;
			}
		}
		return line;
	}

}
