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
import com.jelte.norii.battle.battleState.BattleStateGridHelperFromUnits;
import com.jelte.norii.battle.battleState.HypotheticalUnit;
import com.jelte.norii.battle.battleState.Move;
import com.jelte.norii.battle.battleState.MoveType;
import com.jelte.norii.battle.battleState.SpellMove;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Modifier;
import com.jelte.norii.magic.ModifiersEnum;
import com.jelte.norii.map.MyPathFinder;
import com.jelte.norii.utility.MyPoint;
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
		final MyPoint attackLocation = move.getLocation();
		final int damage = aiUnit.getAttackDamage();
		final int hp = battleState.get(attackLocation.x, attackLocation.y).getUnit().getHp();
		if (damage >= hp) {
			battleState.updateEntity(attackLocation.x, attackLocation.y, 0);
		} else {
			battleState.updateEntity(attackLocation.x, attackLocation.y, hp - damage);
		}
	}

	private void applySpellOnBattleState(HypotheticalUnit aiUnit, SpellMove move, BattleState battleState) {
		final MyPoint caster = new MyPoint(aiUnit.getX(), aiUnit.getY());
		final Array<MyPoint> targets = move.getAffectedUnits();
		switch (move.getAbility().getAbilityEnum()) {
		case FIREBALL:
			for (final MyPoint target : targets) {
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
			for (final MyPoint target : targets) {
				battleState.addModifierToUnit(target.x, target.y, new Modifier(ModifiersEnum.STUNNED, 2, 0));
			}
			break;
		case SWAP:
			for (final MyPoint target : targets) {
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

	private Array<UnitTurn> generateMoves(Ability ability, HypotheticalUnit unit, BattleState battleState) {
		final Array<UnitTurn> unitTurns = new Array<>();

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
				while (checkIfUnitOnPoint(goal, battleState)) {
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
				while (checkIfUnitOnPoint(moveGoal, battleState)) {
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
			while (abilityTargets.isEmpty() && (ap > 0)) {
				final HypotheticalUnit closestUnit = distancesWithAbilityTargetUnits.firstEntry().getValue().get(0);
				final TiledMapPosition closestUnitPos = new TiledMapPosition().setPositionFromTiles(closestUnit.getX(), closestUnit.getY());
				final List<GridCell> path = MyPathFinder.getInstance().pathTowards(new TiledMapPosition().setPositionFromTiles(unit.getX(), unit.getY()), closestUnitPos, unit.getAp());
				endMyPoint = new MyPoint(path.get(0).x, path.get(0).y);
				int i = 0;
				while (checkIfUnitOnPoint(endMyPoint, battleState)) {
					endMyPoint = tryAdjacentPoint(i, new MyPoint(unit.getX(), unit.getY()));
					i++;
				}
				battleState.moveUnitTo(unit, endMyPoint);
				moveAndSpell.addMove(new Move(MoveType.MOVE, endMyPoint));
				ap--;
				abilityTargets = getAbilityTargets(ability, endMyPoint, unit.isPlayerUnit(), battleState);
			}
			if (!abilityTargets.isEmpty()) {
				for (final MyPoint target : abilityTargets) {
					final Set<MyPoint> positionsToCastSpell = battleStateGridHelper.getAllCastPointsWhereTargetIsHit(ability, target, new MyPoint(unit.getX(), unit.getY()), battleState);
					for (final MyPoint MyPoint : positionsToCastSpell) {
						final Array<MyPoint> affectedUnits = battleStateGridHelper.getTargetsAbility(ability, MyPoint, getUnitPositions(false, ability, battleState));
						moveAndSpell.addMove(new SpellMove(MoveType.SPELL, MyPoint, ability, affectedUnits));
						unitTurns.add(moveAndSpell);
					}
				}
			}
		}

		if (!abilityTargets.isEmpty()) {
			for (final MyPoint target : abilityTargets) {
				final Set<MyPoint> positionsToCastSpell = battleStateGridHelper.getAllCastPointsWhereTargetIsHit(ability, target, new MyPoint(unit.getX(), unit.getY()), battleState);
				final Move moveAfterSpell = decideMove(ability, unit, battleState);
				for (final MyPoint MyPoint : positionsToCastSpell) {
					final Array<MyPoint> affectedUnits = battleStateGridHelper.getTargetsAbility(ability, MyPoint, getUnitPositions(false, ability, battleState));
					final UnitTurn spellAndMove = new UnitTurn(unit.getEntityId(), new SpellMove(MoveType.SPELL, MyPoint, ability, affectedUnits));
					spellAndMove.addMove(moveAfterSpell);
					unitTurns.add(spellAndMove);
				}
			}
		}
		return unitTurns;
	}

	private MyPoint tryAdjacentPoint(int i, MyPoint unitPoint) {
		if (i == 0) {
			return new MyPoint(unitPoint.x + 1, unitPoint.y);
		}

		if (i == 1) {
			return new MyPoint(unitPoint.x - 1, unitPoint.y);
		}

		if (i == 2) {
			return new MyPoint(unitPoint.x, unitPoint.y + 1);
		}

		if (i == 3) {
			return new MyPoint(unitPoint.x, unitPoint.y - 1);
		} else {
			return new MyPoint(unitPoint.x, unitPoint.y);
		}
	}

	private boolean checkIfUnitOnPoint(MyPoint goal, BattleState battleState) {
		for (final HypotheticalUnit unit : battleState.getAllUnits()) {
			if (goal.equals(new MyPoint(unit.getX(), unit.getY()))) {
				return false;
			}
		}
		return true;
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
		final List<GridCell> path = MyPathFinder.getInstance().pathTowards(start, goal, aiUnit.getAp());
		return new MyPoint(path.get(path.size() - 1).x, path.get(path.size() - 1).y);
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

	private Array<MyPoint> getAbilityTargets(Ability ability, MyPoint casterPos, boolean isPlayerUnit, BattleState battleState) {

		final Array<MyPoint> unitPositions = getUnitPositions(isPlayerUnit, ability, battleState);
		return battleStateGridHelper.getTargetPositionsInRangeAbility(casterPos, ability, unitPositions);
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
