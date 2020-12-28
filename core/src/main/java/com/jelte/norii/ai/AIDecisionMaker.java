package com.jelte.norii.ai;

import java.awt.Point;
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
import com.jelte.norii.battle.battleState.BattleStateGridHelperFromUnits;
import com.jelte.norii.battle.battleState.HypotheticalUnit;
import com.jelte.norii.battle.battleState.Move;
import com.jelte.norii.battle.battleState.MoveType;
import com.jelte.norii.battle.battleState.SpellMove;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Modifier;
import com.jelte.norii.magic.ModifiersEnum;
import com.jelte.norii.map.MyPathFinder;
import com.jelte.norii.utility.TiledMapPosition;
import com.jelte.norii.utility.Utility;

public class AIDecisionMaker {
	private final SortedMap<Integer, BattleState> statesWithScores;
	private final BattleStateGridHelperFromUnits battleStateGridHelper;

	private static final int NUMBER_OF_LAYERS = 3;
	private static final String TAG = AIDecisionMaker.class.getSimpleName();

	public AIDecisionMaker() {
		battleStateGridHelper = new BattleStateGridHelperFromUnits();
		statesWithScores = new TreeMap<>();
	}

	public UnitTurn makeDecision(BattleState battleState) {
		System.out.println("1");
		final Array<HypotheticalUnit> players = battleState.getPlayerUnits();
		final Array<HypotheticalUnit> ais = battleState.getAiUnits();
		final Array<BattleState> battleStates = new Array<>();
		final Array<BattleState> battleStatesLevelTwo = new Array<>();
		final Array<BattleState> battleStatesLevelThree = new Array<>();
		// for every aiUnit, generate all his moves and store them
		for (final HypotheticalUnit aiUnit : ais) {
			for (final Ability ability : aiUnit.getAbilities()) {
				final Array<UnitTurn> turns = generateMoves(ability, aiUnit, battleState);
				for (final UnitTurn turn : turns) {
					final BattleState newState = applyTurnToBattleState(aiUnit, turn, battleState);
					newState.setTurn(turn);
					battleStates.add(newState);
				}
			}
		}
		System.out.println("2");
		reduceModifierCount(battleStates);
		for (final BattleState updatedState : battleStates) {
			for (final HypotheticalUnit playerUnit : players) {
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
		System.out.println("3");
		reduceModifierCount(battleStatesLevelTwo);
		System.out.println("3.5");
		System.out.println("number of states = " + battleStatesLevelTwo.size);
		for (final BattleState updatedState : battleStatesLevelTwo) {
			System.out.println("3.5 - inside - level 1");
			for (final HypotheticalUnit aiUnit : ais) {
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
		System.out.println("4");
		reduceModifierCount(battleStatesLevelThree);
		System.out.println("5");
		battleStatesLevelThree.sort();
		System.out.println("6");
		return getmoves(battleStatesLevelThree.get(0));
	}

	private UnitTurn getmoves(BattleState battleState) {
		BattleState initialState = battleState;
		while (initialState.getParentState() != null) {
			initialState = battleState.getParentState();
		}
		return initialState.getTurn();
	}

	private void reduceModifierCount(Array<BattleState> battleStates) {
		for (final BattleState battleState : battleStates) {
			battleState.reduceModifierCounts();
		}

	}

	private BattleState applyTurnToBattleState(HypotheticalUnit aiUnit, UnitTurn turn, BattleState battleState) {
		final BattleState newState = battleState.makeCopy();
		for (final Move move : turn.getMoves()) {
			switch (move.getMoveType()) {
			case SPELL:
				applySpellOnBattleState(aiUnit, (SpellMove) move, newState);
				break;
			case ATTACK:
				applyAttackOnBattleState(aiUnit, move, newState);
				break;
			case MOVE:
				newState.moveUnitTo(aiUnit, move.getLocation());
				break;
			default:
				// do nothing
			}
		}
		return newState;
	}

	private void applyAttackOnBattleState(HypotheticalUnit aiUnit, Move move, BattleState battleState) {
		final Point attackLocation = move.getLocation();
		final int damage = aiUnit.getAttackDamage();
		final int hp = battleState.get(attackLocation.x, attackLocation.y).getUnit().getHp();
		if (damage >= hp) {
			battleState.updateEntity(attackLocation.x, attackLocation.y, 0);
		} else {
			battleState.updateEntity(attackLocation.x, attackLocation.y, hp - damage);
		}
	}

	private void applySpellOnBattleState(HypotheticalUnit aiUnit, SpellMove move, BattleState battleState) {
		final Point caster = new Point(aiUnit.getX(), aiUnit.getY());
		final Array<Point> targets = move.getAffectedUnits();
		switch (move.getAbility().getAbilityEnum()) {
		case FIREBALL:
			for (final Point target : targets) {
				final int damage = move.getAbility().getSpellData().getDamage();
				final int hp = battleState.get(target.x, target.y).getUnit().getHp();
				if (damage >= hp) {
					battleState.updateEntity(target.x, target.y, 0);
				} else {
					battleState.updateEntity(target.x, target.y, hp - damage);
				}
			}
			break;
		case TURN_TO_STONE:
			for (final Point target : targets) {
				battleState.addModifierToUnit(target.x, target.y, new Modifier(ModifiersEnum.STUNNED, 2, 0));
			}
			break;
		case SWAP:
			for (final Point target : targets) {
				final HypotheticalUnit placeHolder = battleState.get(target.x, target.y).getUnit();
				battleState.addEntity(target.x, target.y, battleState.get(caster.x, caster.y).getUnit());
				battleState.addEntity(caster.x, caster.y, placeHolder);
			}
			break;
		case HAMMERBACK:
			// nothing yet
		default:
			// nothing
		}
	}

	private Array<UnitTurn> generateMoves(Ability ability, HypotheticalUnit aiUnit, BattleState battleState) {
		final Array<UnitTurn> unitTurns = new Array<>();

		// get the distance between unit and possible targets
		final TreeMap<Integer, List<HypotheticalUnit>> distancesWithAbilityTargetUnits = (TreeMap<Integer, List<HypotheticalUnit>>) getDistancesToTargets(aiUnit, battleState, ability);

		if (!distancesWithAbilityTargetUnits.isEmpty() && (distancesWithAbilityTargetUnits.firstKey() > (ability.getSpellData().getRange() + ability.getSpellData().getAreaOfEffectRange() + aiUnit.getAp()))) {
			if (distancesWithAbilityTargetUnits.firstKey() > (aiUnit.getAttackRange() + aiUnit.getAp())) {
				// just walk
				final HypotheticalUnit closestUnit = distancesWithAbilityTargetUnits.firstEntry().getValue().get(0);
				final TiledMapPosition closestUnitPos = new TiledMapPosition().setPositionFromTiles(closestUnit.getX(), closestUnit.getY());
				final List<GridCell> path = MyPathFinder.getInstance().pathTowards(new TiledMapPosition().setPositionFromTiles(aiUnit.getX(), aiUnit.getY()), closestUnitPos, aiUnit.getAp());
				final Point goal = new Point(path.get(path.size() - 1).x, path.get(path.size() - 1).y);
				unitTurns.add(new UnitTurn(aiUnit.getEntityId(), new Move(MoveType.MOVE, goal)));
				return unitTurns;
			} else {
				// move attack
				final HypotheticalUnit closestUnit = distancesWithAbilityTargetUnits.firstEntry().getValue().get(0);
				final TiledMapPosition closestUnitPos = new TiledMapPosition().setPositionFromTiles(closestUnit.getX(), closestUnit.getY());
				final List<GridCell> path = MyPathFinder.getInstance().pathTowards(new TiledMapPosition().setPositionFromTiles(aiUnit.getX(), aiUnit.getY()), closestUnitPos, aiUnit.getAp());
				final Point moveGoal = new Point(path.get(path.size() - 1).x, path.get(path.size() - 1).y);
				final Point attackGoal = new Point(distancesWithAbilityTargetUnits.firstEntry().getValue().get(0).getX(), distancesWithAbilityTargetUnits.firstEntry().getValue().get(0).getY());
				final UnitTurn moveAttack = new UnitTurn(aiUnit.getEntityId(), new Move(MoveType.MOVE, moveGoal));
				moveAttack.addMove(new Move(MoveType.ATTACK, attackGoal));
				unitTurns.add(moveAttack);
				return unitTurns;
			}
		}

		// decide where to cast spell
		final Point casterPos = new Point(aiUnit.getX(), aiUnit.getY());
		Array<Point> abilityTargets = getAbilityTargets(ability, casterPos, aiUnit.isPlayerUnit(), battleState);

		// no units found in immediate vicinity, so move
		if (abilityTargets.isEmpty()) {
			Point endPoint = new Point(aiUnit.getX(), aiUnit.getY());
			int ap = aiUnit.getAp();
			while (abilityTargets.isEmpty() && (ap > 0)) {
				final HypotheticalUnit closestUnit = distancesWithAbilityTargetUnits.firstEntry().getValue().get(0);
				final TiledMapPosition closestUnitPos = new TiledMapPosition().setPositionFromTiles(closestUnit.getX(), closestUnit.getY());
				final List<GridCell> path = MyPathFinder.getInstance().pathTowards(new TiledMapPosition().setPositionFromTiles(aiUnit.getX(), aiUnit.getY()), closestUnitPos, aiUnit.getAp());
				endPoint = new Point(path.get(0).x, path.get(0).y);
				ap--;
				abilityTargets = getAbilityTargets(ability, endPoint, aiUnit.isPlayerUnit(), battleState);
			}
			final UnitTurn moveAndSpell = new UnitTurn(aiUnit.getEntityId(), new Move(MoveType.MOVE, endPoint));
			for (final Point target : abilityTargets) {
				final Set<Point> positionsToCastSpell = battleStateGridHelper.getAllPointsWhereTargetIsHit(ability, target, new Point(aiUnit.getX(), aiUnit.getY()), battleState);
				for (final Point point : positionsToCastSpell) {
					final Array<Point> affectedUnits = battleStateGridHelper.getTargetsAbility(ability, point, getUnitPositions(false, ability, battleState));
					moveAndSpell.addMove(new SpellMove(MoveType.SPELL, point, ability, affectedUnits));
					unitTurns.add(moveAndSpell);
				}
			}
		}

		if (!abilityTargets.isEmpty()) {
			for (final Point target : abilityTargets) {
				final Set<Point> positionsToCastSpell = battleStateGridHelper.getAllPointsWhereTargetIsHit(ability, target, new Point(aiUnit.getX(), aiUnit.getY()), battleState);
				final Move moveAfterSpell = decideMove(ability, aiUnit, battleState);
				for (final Point point : positionsToCastSpell) {
					final Array<Point> affectedUnits = battleStateGridHelper.getTargetsAbility(ability, point, getUnitPositions(false, ability, battleState));
					final UnitTurn spellAndMove = new UnitTurn(aiUnit.getEntityId(), new SpellMove(MoveType.SPELL, point, ability, affectedUnits));
					spellAndMove.addMove(moveAfterSpell);
					unitTurns.add(spellAndMove);
				}
			}
		}
		return unitTurns;
	}

	private Move decideMove(Ability ability, HypotheticalUnit aiUnit, BattleState battleState) {
		final Point centerOfGravityEnemies = Utility.getCenterOfGravityPlayers(battleState);
		final Point centerOfGravityAllies = Utility.getCenterOfGravityAi(battleState);
		final Point centerOfGravityAllUnits = Utility.getCenterOfGravityAllUnits(battleState);

		// if low hp run away
		if (aiUnit.getHp() <= ((aiUnit.getMaxHp() / 100.0f) * 10)) {
			final Point originalGoal = MyPathFinder.getInstance().getPositionFurthestAwayFrom(centerOfGravityEnemies);
			final Point trimmedGoal = trimPathConsideringApAndReachable(originalGoal, aiUnit);
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

	private Point trimPathConsideringApAndReachable(Point originalGoal, HypotheticalUnit aiUnit) {
		final TiledMapPosition start = new TiledMapPosition().setPositionFromTiles(aiUnit.getX(), aiUnit.getY());
		final TiledMapPosition goal = new TiledMapPosition().setPositionFromTiles(originalGoal.x, originalGoal.y);
		final List<GridCell> path = MyPathFinder.getInstance().pathTowards(start, goal, aiUnit.getAp());
		return new Point(path.get(path.size() - 1).x, path.get(path.size() - 1).y);
	}

	private Map<Integer, List<HypotheticalUnit>> getDistancesToTargets(HypotheticalUnit unit, BattleState battleState, Ability ability) {
		final Map<Integer, List<HypotheticalUnit>> distances = new TreeMap<>();
		Array<HypotheticalUnit> unitsToCheck = new Array<>();
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

	private Array<Point> getAbilityTargets(Ability ability, Point casterPos, boolean isPlayerUnit, BattleState battleState) {

		final Array<Point> unitPositions = getUnitPositions(isPlayerUnit, ability, battleState);
		return battleStateGridHelper.getTargetPositionsInRangeAbility(casterPos, ability, unitPositions);
	}

	private Array<Point> getUnitPositions(boolean isPlayerUnit, Ability ability, BattleState battleState) {
		switch (ability.getAffectedTeams()) {
		case FRIENDLY:
			if (isPlayerUnit) {
				return collectPoints(battleState.getPlayerUnits());
			} else {
				return collectPoints(battleState.getAiUnits());
			}
		case ENEMY:
			if (!isPlayerUnit) {
				return collectPoints(battleState.getPlayerUnits());
			} else {
				return collectPoints(battleState.getAiUnits());
			}
		case BOTH:
			return collectPoints(battleState.getAllUnits());
		case NONE:
			return new Array<>();
		default:
			Gdx.app.debug(TAG, "ability does not have one of these affected teams : FRIENDLY, ENEMY, BOTH or NONE, returning null");
			return null;
		}
	}

	private Array<Point> collectPoints(Array<HypotheticalUnit> allUnits) {
		final Array<Point> points = new Array<>();
		for (final HypotheticalUnit unit : allUnits) {
			points.add(new Point(unit.getX(), unit.getY()));
		}
		return points;
	}

}
