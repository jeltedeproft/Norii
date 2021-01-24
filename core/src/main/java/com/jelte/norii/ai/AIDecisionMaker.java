package com.jelte.norii.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.battle.battleState.BattleStateGridHelper;
import com.jelte.norii.battle.battleState.HypotheticalUnit;
import com.jelte.norii.battle.battleState.Move;
import com.jelte.norii.battle.battleState.MoveType;
import com.jelte.norii.battle.battleState.SpellMove;
import com.jelte.norii.entities.EntityData;
import com.jelte.norii.entities.EntityFileReader;
import com.jelte.norii.entities.EntityTypes;
import com.jelte.norii.magic.AbilitiesEnum;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Ability.Target;
import com.jelte.norii.magic.Modifier;
import com.jelte.norii.magic.ModifiersEnum;
import com.jelte.norii.map.MyPathFinder;
import com.jelte.norii.utility.MyPoint;
import com.jelte.norii.utility.TiledMapPosition;
import com.jelte.norii.utility.Utility;

public class AIDecisionMaker {
	private final SortedMap<Integer, BattleState> statesWithScores;

	private static final int NUMBER_OF_LAYERS = 3;
	private static final String TAG = AIDecisionMaker.class.getSimpleName();

	public AIDecisionMaker() {
		statesWithScores = new TreeMap<>();
	}

	public BattleState makeDecision(BattleState battleState) {
		System.out.println("1");
		final Array<BattleState> battleStates = new Array<>();
		final Array<BattleState> battleStatesLevelTwo = new Array<>();
		final Array<BattleState> battleStatesLevelThree = new Array<>();
		// for every aiUnit, generate all his moves and store them
		for (final HypotheticalUnit aiUnit : battleState.getAiUnits()) {
			for (final Ability ability : aiUnit.getAbilities()) {
				final Array<UnitTurn> turns = generateMoves(ability, aiUnit, battleState);
				for (final UnitTurn turn : turns) {
					final BattleState newState = applyTurnToBattleState(aiUnit, turn, battleState);
					newState.setTurn(turn);
					battleStates.add(newState);
				}
			}
		}
		System.out.println("number of states in lvl 1 = " + battleStates.size);
		System.out.println("2");
		reduceModifierCount(battleStates);
		for (final BattleState updatedState : battleStates) {
			if (checkEndConditions(updatedState)) {
				battleStatesLevelThree.add(updatedState.makeCopyWithTurn());
			} else {
				for (final HypotheticalUnit playerUnit : updatedState.getPlayerUnits()) {
					for (final Ability ability : playerUnit.getAbilities()) {
						final Array<UnitTurn> turns = generateMoves(ability, playerUnit, updatedState);
						for (final UnitTurn turn : turns) {
							final BattleState newState = applyTurnToBattleState(playerUnit, turn, updatedState);
							newState.setParentState(updatedState);
							newState.setTurn(turn);
							battleStatesLevelTwo.add(newState);
						}
					}
				}
			}
		}
		System.out.println("3");
		reduceModifierCount(battleStatesLevelTwo);
		System.out.println("3.5");
		System.out.println("number of states in lvl 2 = " + battleStatesLevelTwo.size);
		for (final BattleState updatedState : battleStatesLevelTwo) {
			if (checkEndConditions(updatedState)) {
				battleStatesLevelThree.add(updatedState.makeCopyWithTurn());
			} else {
				System.out.println("3.5 - inside - level 1");
				for (final HypotheticalUnit aiUnit : updatedState.getAiUnits()) {
					System.out.println("3.5 - inside - level 2");
					for (final Ability ability : aiUnit.getAbilities()) {
						System.out.println("3.5 - inside - level 3");
						final Array<UnitTurn> turns = generateMoves(ability, aiUnit, updatedState);
						for (final UnitTurn turn : turns) {
							System.out.println("3.5 - inside - level 4");
							final BattleState newState = applyTurnToBattleState(aiUnit, turn, updatedState);
							newState.setParentState(updatedState);
							newState.setTurn(turn);
							battleStatesLevelThree.add(newState);
						}
					}
				}
			}
		}
		System.out.println("number of states in lvl 3 = " + battleStatesLevelThree.size);
		System.out.println("4");
		reduceModifierCount(battleStatesLevelThree);
		System.out.println("5");
		battleStatesLevelThree.sort();
		System.out.println("6");
		return getInitialMoves(battleStatesLevelThree.get(0));
	}

	private boolean checkEndConditions(BattleState battleState) {
		return battleState.getPlayerUnits().isEmpty() || battleState.getAiUnits().isEmpty();
	}

	private BattleState getInitialMoves(BattleState battleState) {
		BattleState initialState = battleState;
		while (initialState.getParentState() != null) {
			initialState = initialState.getParentState();
		}
		return initialState;
	}

	private void reduceModifierCount(Array<BattleState> battleStates) {
		for (final BattleState battleState : battleStates) {
			battleState.reduceModifierCounts();
		}
	}

	private BattleState applyTurnToBattleState(HypotheticalUnit aiUnit, UnitTurn turn, BattleState battleState) {
		final BattleState newState = battleState.makeCopy();
		final HypotheticalUnit copyUnit = newState.get(aiUnit.getX(), aiUnit.getY()).getUnit();
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

	private void applyAttackOnBattleState(HypotheticalUnit aiUnit, Move move, BattleState battleState) {
		final MyPoint attackLocation = move.getLocation();
		final int damage = aiUnit.getAttackDamage();
		final int hp = battleState.get(attackLocation.x, attackLocation.y).getUnit().getHp();
		if (damage >= hp) {
			battleState.updateEntity(attackLocation.x, attackLocation.y, 0);
		} else {
			battleState.updateEntity(attackLocation.x, attackLocation.y, hp - damage);
		}
	}

	private void applySpellOnBattleState(HypotheticalUnit unit, SpellMove move, BattleState battleState) {
		final MyPoint caster = new MyPoint(unit.getX(), unit.getY());
		final Array<MyPoint> targets = move.getAffectedUnits();
		final MyPoint location = move.getLocation();
		final int damage = move.getAbility().getSpellData().getDamage();
		switch (move.getAbility().getAbilityEnum()) {
		case FIREBALL:

			final int hp = battleState.get(location.x, location.y).getUnit().getHp();
			if (damage >= hp) {
				battleState.updateEntity(location.x, location.y, 0);
			} else {
				battleState.updateEntity(location.x, location.y, hp - damage);
			}

			break;
		case TURN_TO_STONE:
			battleState.addModifierToUnit(location.x, location.y, new Modifier(ModifiersEnum.STUNNED, 2, 0));
			break;
		case SWAP:
			final HypotheticalUnit placeHolder = battleState.get(location.x, location.y).getUnit();
			battleState.addEntity(location.x, location.y, battleState.get(caster.x, caster.y).getUnit());
			battleState.addEntity(caster.x, caster.y, placeHolder);
			break;
		case HAMMERBACK:
			final List<MyPoint> crossedCells = findLine(caster.x, caster.y, location.x, location.y);
			for (final MyPoint point : crossedCells) {
				if (battleState.get(point.x, point.y).isOccupied()) {
					final int unitHp = battleState.get(point.x, point.y).getUnit().getHp();
					if (damage >= unitHp) {
						battleState.updateEntity(point.x, point.y, 0);
					} else {
						battleState.updateEntity(point.x, point.y, unitHp - damage);
					}
				}
			}
			final EntityData entityData = EntityFileReader.getUnitData().get(EntityTypes.BOOMERANG.ordinal());
			battleState.addEntity(location.x, location.y, new HypotheticalUnit(0, unit.isPlayerUnit(), entityData.getMaxHP(), entityData.getMaxHP(), entityData.getAttackRange(), entityData.getAttackPower(), entityData.getMaxAP(),
					new ArrayList<>(), new ArrayList<>(), location.x, location.y));
			battleState.get(location.x, location.y).getUnit().setEntityId(java.lang.System.identityHashCode(battleState.get(location.x, location.y).getUnit()));
			battleState.get(location.x, location.y).getUnit().addModifier(new Modifier(ModifiersEnum.DAMAGE_OVER_TIME, 3, 1));
			battleState.get(location.x, location.y).getUnit().addAbility(AbilitiesEnum.HAMMERBACKBACK, caster);
			break;
		case HAMMERBACKBACK:
			final List<MyPoint> crossedCellsBack = findLine(caster.x, caster.y, location.x, location.y);
			for (final MyPoint point : crossedCellsBack) {
				if (battleState.get(point.x, point.y).isOccupied()) {
					final int unitHp = battleState.get(point.x, point.y).getUnit().getHp();
					if (damage >= unitHp) {
						battleState.updateEntity(point.x, point.y, 0);
					} else {
						battleState.updateEntity(point.x, point.y, unitHp - damage);
					}
				}
			}
			break;
		default:
			// nothing
		}
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

	private Array<UnitTurn> generateMoves(Ability ability, HypotheticalUnit unit, BattleState battleState) {
		final Array<UnitTurn> unitTurns = new Array<>();

		// if the ability is cast on self or no target, just cast it and move

		if ((ability.getTarget() == Target.NO_TARGET) || (ability.getTarget() == Target.SELF)) {
			final UnitTurn spellAndMove = new UnitTurn(unit.getEntityId(), new SpellMove(MoveType.SPELL, new MyPoint(unit.getX(), unit.getY()), ability));
			spellAndMove.addMove(decideMove(ability, unit, battleState));
			unitTurns.add(spellAndMove);
		}

		// if the ability has cell targets, try out all of them + move and make a new
		// state for each
		if ((ability.getTarget() == Target.CELL) || (ability.getTarget() == Target.CELL_BUT_NO_UNIT)) {
			final MyPoint center = new MyPoint(unit.getX(), unit.getY());
			final Set<MyPoint> cellsToCastOn = BattleStateGridHelper.getInstance().getAllPointsASpellCanHit(center, ability.getLineOfSight(), ability.getSpellData().getRange(), battleState);
			if (ability.getTarget() == Target.CELL_BUT_NO_UNIT) {
				filterUnits(cellsToCastOn, battleState);
			}
			for (final MyPoint point : cellsToCastOn) {
				final UnitTurn spellAndMove = new UnitTurn(unit.getEntityId(), new SpellMove(MoveType.SPELL, point, ability));
				spellAndMove.addMove(decideMove(ability, unit, battleState));
				unitTurns.add(spellAndMove);
			}
		}

		// get the distance between unit and possible targets
		final TreeMap<Integer, List<HypotheticalUnit>> distancesWithAbilityTargetUnits = (TreeMap<Integer, List<HypotheticalUnit>>) getDistancesToTargets(unit, battleState, ability);

		if (!distancesWithAbilityTargetUnits.isEmpty() && (distancesWithAbilityTargetUnits.firstKey() > (ability.getSpellData().getRange() + ability.getSpellData().getAreaOfEffectRange() + unit.getAp()))) {
			if (distancesWithAbilityTargetUnits.firstKey() > (unit.getAttackRange() + unit.getAp())) {
				// just walk
				final HypotheticalUnit closestUnit = distancesWithAbilityTargetUnits.firstEntry().getValue().get(0);
				final TiledMapPosition closestUnitPos = new TiledMapPosition().setPositionFromTiles(closestUnit.getX(), closestUnit.getY());
				final List<GridCell> path = MyPathFinder.getInstance().pathTowards(new TiledMapPosition().setPositionFromTiles(unit.getX(), unit.getY()), closestUnitPos, unit.getAp());
				MyPoint goal = new MyPoint(path.get(path.size() - 1).x, path.get(path.size() - 1).y);
				int i = 2;
				while (checkIfUnitOnPoint(goal, battleState, unit)) {
					if ((path.size() - i) <= 0) {
						return unitTurns;
					}
					goal = new MyPoint(path.get(path.size() - i).x, path.get(path.size() - i).y);
					i++;
				}
				unitTurns.add(new UnitTurn(unit.getEntityId(), new Move(MoveType.MOVE, goal)));
				return unitTurns;
			} else {
				// move attack
				final HypotheticalUnit closestUnit = distancesWithAbilityTargetUnits.firstEntry().getValue().get(0);
				final TiledMapPosition closestUnitPos = new TiledMapPosition().setPositionFromTiles(closestUnit.getX(), closestUnit.getY());
				final List<GridCell> path = MyPathFinder.getInstance().pathTowards(new TiledMapPosition().setPositionFromTiles(unit.getX(), unit.getY()), closestUnitPos, unit.getAp());
				MyPoint moveGoal = new MyPoint(path.get(path.size() - 1).x, path.get(path.size() - 1).y);
				int i = 2;
				while (checkIfUnitOnPoint(moveGoal, battleState, unit)) {
					if ((path.size() - i) <= 0) {
						moveGoal = new MyPoint(unit.getX(), unit.getY());
						break;
					}
					moveGoal = new MyPoint(path.get(path.size() - i).x, path.get(path.size() - i).y);
					i++;
				}
				final MyPoint attackGoal = new MyPoint(distancesWithAbilityTargetUnits.firstEntry().getValue().get(0).getX(), distancesWithAbilityTargetUnits.firstEntry().getValue().get(0).getY());
				final UnitTurn moveAttack = new UnitTurn(unit.getEntityId(), new Move(MoveType.MOVE, moveGoal));
				moveAttack.addMove(new Move(MoveType.ATTACK, attackGoal));
				unitTurns.add(moveAttack);
				return unitTurns;
			}
		}

		// decide where to cast spell
		final MyPoint casterPos = new MyPoint(unit.getX(), unit.getY());
		Array<MyPoint> abilityTargets = getAbilityTargets(ability, casterPos, unit.isPlayerUnit(), battleState);

		// no units found in immediate vicinity, so move
		if (abilityTargets.isEmpty()) {
			MyPoint endMyPoint = new MyPoint(unit.getX(), unit.getY());
			final UnitTurn moveAndSpell = new UnitTurn(unit.getEntityId(), new Move(MoveType.MOVE, endMyPoint));
			int ap = unit.getAp();
			final BattleState copyBattleState = battleState.makeCopy();
			final HypotheticalUnit copyUnit = copyBattleState.get(unit.getX(), unit.getY()).getUnit();
			while (abilityTargets.isEmpty() && (ap > 0)) {
				final HypotheticalUnit closestUnit = distancesWithAbilityTargetUnits.firstEntry().getValue().get(0);
				final TiledMapPosition closestUnitPos = new TiledMapPosition().setPositionFromTiles(closestUnit.getX(), closestUnit.getY());
				final List<GridCell> path = MyPathFinder.getInstance().pathTowards(new TiledMapPosition().setPositionFromTiles(copyUnit.getX(), copyUnit.getY()), closestUnitPos, copyUnit.getAp());
				endMyPoint = new MyPoint(path.get(0).x, path.get(0).y);
				int i = 0;
				while (checkIfUnitOnPoint(endMyPoint, copyBattleState, copyUnit)) {
					endMyPoint = tryAdjacentPoint(i, new MyPoint(copyUnit.getX(), copyUnit.getY()), new MyPoint(closestUnit.getX(), closestUnit.getY()));
					i++;
				}
				copyBattleState.moveUnitTo(copyUnit, endMyPoint);
				moveAndSpell.addMove(new Move(MoveType.MOVE, endMyPoint));
				ap--;
				abilityTargets = getAbilityTargets(ability, endMyPoint, copyUnit.isPlayerUnit(), copyBattleState);
			}
			if (!abilityTargets.isEmpty()) {
				for (final MyPoint target : abilityTargets) {
					final Set<MyPoint> positionsToCastSpell = BattleStateGridHelper.getInstance().getAllCastPointsWhereTargetIsHit(ability, target, new MyPoint(copyUnit.getX(), copyUnit.getY()), copyBattleState);
					for (final MyPoint MyPoint : positionsToCastSpell) {
						final UnitTurn moveAndSpellCopy = moveAndSpell.makeCopy();
						final Array<MyPoint> affectedUnits = BattleStateGridHelper.getInstance().getTargetsAbility(ability, MyPoint, getUnitPositions(false, ability, copyBattleState));
						moveAndSpellCopy.addMove(new SpellMove(MoveType.SPELL, MyPoint, ability, affectedUnits));
						unitTurns.add(moveAndSpellCopy);
					}
				}
			} else {
				// units were in range, but moving did not bring them into line of sight
				unitTurns.add(moveAndSpell);
			}
		}

		if (!abilityTargets.isEmpty()) {
			for (final MyPoint target : abilityTargets) {
				final Set<MyPoint> positionsToCastSpell = BattleStateGridHelper.getInstance().getAllCastPointsWhereTargetIsHit(ability, target, new MyPoint(unit.getX(), unit.getY()), battleState);
				final Move moveAfterSpell = decideMove(ability, unit, battleState);
				for (final MyPoint MyPoint : positionsToCastSpell) {
					final Array<MyPoint> affectedUnits = BattleStateGridHelper.getInstance().getTargetsAbility(ability, MyPoint, getUnitPositions(false, ability, battleState));
					final UnitTurn spellAndMove = new UnitTurn(unit.getEntityId(), new SpellMove(MoveType.SPELL, MyPoint, ability, affectedUnits));
					spellAndMove.addMove(moveAfterSpell);
					unitTurns.add(spellAndMove);
				}
			}
		}
		return unitTurns;
	}

	private void filterUnits(Set<MyPoint> cellsToCastOn, BattleState battleState) {
		for (final HypotheticalUnit unit : battleState.getAllUnits()) {
			if (cellsToCastOn.contains(unit.getPositionAsPoint())) {
				cellsToCastOn.remove(unit.getPositionAsPoint());
			}
		}
	}

	private MyPoint tryAdjacentPoint(int i, MyPoint unitPoint, MyPoint target) {
		if (i == 0) {
			if (target.x < unitPoint.x) {
				i++;
			} else {
				return new MyPoint(unitPoint.x + 1, unitPoint.y);
			}
		}

		if (i == 1) {
			if (target.x > unitPoint.x) {
				i++;
			} else {
				return new MyPoint(unitPoint.x - 1, unitPoint.y);
			}
		}

		if (i == 2) {
			if (target.y < unitPoint.y) {
				i++;
			} else {
				return new MyPoint(unitPoint.x, unitPoint.y + 1);
			}
		}

		if (i == 3) {
			return new MyPoint(unitPoint.x, unitPoint.y - 1);
		} else {
			return new MyPoint(unitPoint.x, unitPoint.y);
		}
	}

	private boolean checkIfUnitOnPoint(MyPoint goal, BattleState battleState, HypotheticalUnit copyUnit) {
		for (final HypotheticalUnit unit : battleState.getAllUnits()) {
			if ((goal.equals(new MyPoint(unit.getX(), unit.getY()))) && !((copyUnit.getX() == unit.getX()) && (copyUnit.getY() == unit.getY()))) {
				return true;
			}
		}
		return false;
	}

	private Move decideMove(Ability ability, HypotheticalUnit aiUnit, BattleState battleState) {
		final MyPoint centerOfGravityEnemies = Utility.getCenterOfGravityPlayers(battleState);
		final MyPoint centerOfGravityAllies = Utility.getCenterOfGravityAi(battleState);
		final MyPoint centerOfGravityAllUnits = Utility.getCenterOfGravityAllUnits(battleState);

		// if low hp run away
		if (aiUnit.getHp() <= ((aiUnit.getMaxHp() / 100.0f) * 10)) {
			final MyPoint originalGoal = MyPathFinder.getInstance().getPositionFurthestAwayFrom(centerOfGravityEnemies);
			final MyPoint trimmedGoal = trimPathConsideringApAndReachable(originalGoal, aiUnit);
			return new Move(MoveType.MOVE, trimmedGoal);
		}

		switch (ability.getAffectedTeams()) {
		case FRIENDLY:
			return new Move(MoveType.MOVE, trimPathConsideringApAndReachable(centerOfGravityAllies, aiUnit));
		case ENEMY:
			return new Move(MoveType.MOVE, trimPathConsideringApAndReachable(centerOfGravityEnemies, aiUnit));
		case BOTH:
			return new Move(MoveType.MOVE, trimPathConsideringApAndReachable(centerOfGravityAllUnits, aiUnit));
		case NONE:
			return new Move(MoveType.MOVE, trimPathConsideringApAndReachable(centerOfGravityAllUnits, aiUnit));
		default:
			Gdx.app.debug(TAG, "ability does not have one of these affected teams : FRIENDLY, ENEMY, BOTH or NONE, returning null");
			return null;
		}
	}

	private MyPoint trimPathConsideringApAndReachable(MyPoint originalGoal, HypotheticalUnit aiUnit) {
		final TiledMapPosition start = new TiledMapPosition().setPositionFromTiles(aiUnit.getX(), aiUnit.getY());
		final TiledMapPosition goal = new TiledMapPosition().setPositionFromTiles(originalGoal.x, originalGoal.y);
		System.out.println("moving from " + start + "  to  : " + goal);
		final List<GridCell> path = MyPathFinder.getInstance().pathTowards(start, goal, aiUnit.getAp());
		System.out.println("after trimming : " + path + "  with length  : " + path.size());
		if (path.isEmpty()) {
			return new MyPoint(start.getTileX(), start.getTileY());
		}

		return new MyPoint(path.get(path.size() - 1).x, path.get(path.size() - 1).y);
	}

	private Map<Integer, List<HypotheticalUnit>> getDistancesToTargets(HypotheticalUnit unit, BattleState battleState, Ability ability) {
		final Map<Integer, List<HypotheticalUnit>> distances = new TreeMap<>();
		Array<HypotheticalUnit> unitsToCheck;
		switch (ability.getAffectedTeams()) {
		case FRIENDLY:
			if (unit.isPlayerUnit()) {
				unitsToCheck = battleState.getPlayerUnits();
			} else {
				unitsToCheck = battleState.getAiUnits();
			}
			for (final HypotheticalUnit entity : unitsToCheck) {
				if (unit.getEntityId() != entity.getEntityId()) {
					distances.computeIfAbsent(calculateDistanceTwoUnits(unit, entity), k -> new ArrayList<HypotheticalUnit>()).add(entity);
				}
			}
			break;
		case ENEMY:
			if (unit.isPlayerUnit()) {
				unitsToCheck = battleState.getAiUnits();
			} else {
				unitsToCheck = battleState.getPlayerUnits();
			}
			for (final HypotheticalUnit entity : unitsToCheck) {
				if (unit.getEntityId() != entity.getEntityId()) {
					distances.computeIfAbsent(calculateDistanceTwoUnits(unit, entity), k -> new ArrayList<HypotheticalUnit>()).add(entity);
				}
			}
			break;
		case BOTH:
			for (final HypotheticalUnit entity : battleState.getAllUnits()) {
				if (unit.getEntityId() != entity.getEntityId()) {
					distances.computeIfAbsent(calculateDistanceTwoUnits(unit, entity), k -> new ArrayList<HypotheticalUnit>()).add(entity);
				}
			}
			break;
		case NONE:
			return distances;
		}
		return distances;
	}

	private Integer calculateDistanceTwoUnits(HypotheticalUnit aiUnit, HypotheticalUnit entity) {
		final TiledMapPosition aiUnitPosition = new TiledMapPosition().setPositionFromTiles(aiUnit.getX(), aiUnit.getY());
		final TiledMapPosition entityPosition = new TiledMapPosition().setPositionFromTiles(entity.getX(), entity.getY());
		return aiUnitPosition.getDistance(entityPosition);
	}

	private Array<MyPoint> getAbilityTargets(Ability ability, MyPoint casterPos, boolean isPlayerUnit, BattleState battleState) {

		final Array<MyPoint> unitPositions = getUnitPositions(isPlayerUnit, ability, battleState);
		return BattleStateGridHelper.getInstance().getTargetPositionsInRangeAbility(casterPos, ability, unitPositions);
	}

	private Array<MyPoint> getUnitPositions(boolean isPlayerUnit, Ability ability, BattleState battleState) {
		switch (ability.getAffectedTeams()) {
		case FRIENDLY:
			if (isPlayerUnit) {
				return collectMyPoints(battleState.getPlayerUnits());
			} else {
				return collectMyPoints(battleState.getAiUnits());
			}
		case ENEMY:
			if (!isPlayerUnit) {
				return collectMyPoints(battleState.getPlayerUnits());
			} else {
				return collectMyPoints(battleState.getAiUnits());
			}
		case BOTH:
			return collectMyPoints(battleState.getAllUnits());
		case NONE:
			return new Array<>();
		default:
			Gdx.app.debug(TAG, "ability does not have one of these affected teams : FRIENDLY, ENEMY, BOTH or NONE, returning null");
			return null;
		}
	}

	private Array<MyPoint> collectMyPoints(Array<HypotheticalUnit> allUnits) {
		final Array<MyPoint> MyPoints = new Array<>();
		for (final HypotheticalUnit unit : allUnits) {
			MyPoints.add(new MyPoint(unit.getX(), unit.getY()));
		}
		return MyPoints;
	}

}
