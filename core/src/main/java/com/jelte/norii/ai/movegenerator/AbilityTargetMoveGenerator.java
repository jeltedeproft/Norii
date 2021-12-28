package com.jelte.norii.ai.movegenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.UnitTurn;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.battle.battleState.BattleStateGridHelper;
import com.jelte.norii.battle.battleState.Move;
import com.jelte.norii.battle.battleState.MoveType;
import com.jelte.norii.battle.battleState.SpellMove;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Ability.Target;
import com.jelte.norii.map.MyPathFinder;
import com.jelte.norii.utility.MyPoint;
import com.jelte.norii.utility.TiledMapPosition;
import com.jelte.norii.utility.Utility;

public class AbilityTargetMoveGenerator implements MoveGenerator {
	private static final String TAG = AbilityTargetMoveGenerator.class.getSimpleName();
	private static final String WRONG_ABILITY_TEAM = "ability does not have one of these affected teams : FRIENDLY, ENEMY, BOTH or NONE, returning null";
	private static final int FILTER_AMOUNT_OF_CELL_TARGETS = 10;
	private Long oldTime;
	final Random random = new Random();

	@Override
	public Array<UnitTurn> getAllMovesUnit(Ability ability, Entity aiUnit, BattleState battleState) {
		oldTime = System.currentTimeMillis();
		final Array<UnitTurn> unitTurns = new Array<>();
		Gdx.app.debug(TAG, "generating moves for : " + ability);
		Gdx.app.debug(TAG, "===============================================================");

		switch (ability.getTarget()) {
		case CELL_BUT_NO_UNIT:
			castAbilityOnCell(ability, aiUnit, battleState, unitTurns);
			break;
		case NO_TARGET:
		case SELF:
			castNoTargetOrSelf(ability, aiUnit, battleState, unitTurns);
			break;
		case CELL:
		case UNIT:
			castAbilityOnTarget(ability, aiUnit, battleState, unitTurns);
			break;
		default:
			break;
		}

		oldTime = debugTime("the end of this ability", oldTime);
		return unitTurns;
	}

	private void castAbilityOnCell(Ability ability, Entity aiUnit, BattleState battleState, final Array<UnitTurn> unitTurns) {
		oldTime = debugTime("starting ability cast on cell", oldTime);

		final MyPoint center = new MyPoint(aiUnit.getCurrentPosition().getTileX(), aiUnit.getCurrentPosition().getTileY());
		final Set<MyPoint> cellsToCastOn = BattleStateGridHelper.getInstance().getAllPointsASpellCanHit(center, ability.getLineOfSight(), ability.getSpellData().getRange(), battleState);
		oldTime = debugTime("found all points a spell can hit", oldTime);
		if (ability.getTarget() == Target.CELL_BUT_NO_UNIT) {
			filterUnits(cellsToCastOn, battleState);
		}
		oldTime = debugTime("filtered targets", oldTime);

		filterOutNumber(cellsToCastOn, FILTER_AMOUNT_OF_CELL_TARGETS);
		for (final MyPoint point : cellsToCastOn) {
			final UnitTurn spellAndMove = new UnitTurn(aiUnit.getEntityID(), new SpellMove(MoveType.SPELL, point, ability, null));
			spellAndMove.addMove(decideMove(ability, aiUnit, battleState));
			unitTurns.add(spellAndMove);
		}
		oldTime = debugTime("limited number of targets, and created spellmoves", oldTime);
	}

	private void castNoTargetOrSelf(Ability ability, Entity aiUnit, BattleState battleState, final Array<UnitTurn> unitTurns) {
		oldTime = debugTime("starting self/no target spells", oldTime);
		final UnitTurn spellAndMove = new UnitTurn(aiUnit.getEntityID(), new SpellMove(MoveType.SPELL, new MyPoint(aiUnit.getCurrentPosition().getTileX(), aiUnit.getCurrentPosition().getTileY()), ability, null));
		spellAndMove.addMove(decideMove(ability, aiUnit, battleState));
		unitTurns.add(spellAndMove);
	}

	private void castAbilityOnTarget(Ability ability, Entity aiUnit, BattleState battleState, final Array<UnitTurn> unitTurns) {
		final TreeMap<Integer, List<Entity>> distancesToTargets = (TreeMap<Integer, List<Entity>>) getDistancesToTargets(aiUnit, battleState, ability);

		oldTime = debugTime("found distances with ability target units", oldTime);

		if (anyUnitsInReachOfMoveAndSpell(ability, aiUnit, distancesToTargets)) {
			if (anyUnitsInReachOfMoveAndAttack(aiUnit, distancesToTargets)) {
				justMove(aiUnit, battleState, unitTurns, distancesToTargets);
			} else {
				moveAttack(aiUnit, battleState, unitTurns, distancesToTargets);
			}
			return;// no point in looking further for targets
		}

		oldTime = debugTime("there are units in range", oldTime);

		final MyPoint casterPos = new MyPoint(aiUnit.getCurrentPosition().getTileX(), aiUnit.getCurrentPosition().getTileY());
		Array<MyPoint> abilityTargets = getAbilityTargets(ability, casterPos, aiUnit.isPlayerUnit(), battleState);

		oldTime = debugTime("looked for targets", oldTime);

		if (abilityTargets.isEmpty()) {
			oldTime = debugTime("no units found in immediate vicinity, moving", oldTime);
			if (distancesToTargets.isEmpty()) {
				unitTurns.add(new UnitTurn(aiUnit.getEntityID(), new Move(MoveType.DUMMY, new MyPoint(0, 0))));
			} else {
				tryToMoveAndCastSpell(ability, aiUnit, battleState, unitTurns, abilityTargets);
			}
			oldTime = debugTime("targets found without walking", oldTime);
		} else {
			addSpellMovesForEveryTarget(ability, aiUnit, battleState, unitTurns, casterPos, abilityTargets);
		}
	}

	private boolean anyUnitsInReachOfMoveAndAttack(Entity aiUnit, final TreeMap<Integer, List<Entity>> distancesWithAbilityTargetUnits) {
		return distancesWithAbilityTargetUnits.firstKey() > (aiUnit.getAttackRange() + aiUnit.getAp());
	}

	private boolean anyUnitsInReachOfMoveAndSpell(Ability ability, Entity aiUnit, final TreeMap<Integer, List<Entity>> distancesWithAbilityTargetUnits) {
		return !distancesWithAbilityTargetUnits.isEmpty() && (distancesWithAbilityTargetUnits.firstKey() > (ability.getSpellData().getRange() + ability.getSpellData().getAreaOfEffectRange() + aiUnit.getAp()));
	}

	private void justMove(Entity aiUnit, BattleState battleState, final Array<UnitTurn> unitTurns, final TreeMap<Integer, List<Entity>> distancesWithAbilityTargetUnits) {
		oldTime = debugTime("just walking", oldTime);
		final Entity closestUnit = distancesWithAbilityTargetUnits.firstEntry().getValue().get(0);
		final TiledMapPosition closestUnitPos = new TiledMapPosition().setPositionFromTiles(closestUnit.getCurrentPosition().getTileX(), closestUnit.getCurrentPosition().getTileY());
		final List<GridCell> path = MyPathFinder.getInstance().pathTowards(new TiledMapPosition().setPositionFromTiles(aiUnit.getCurrentPosition().getTileX(), aiUnit.getCurrentPosition().getTileY()), closestUnitPos, aiUnit.getAp());
		if (!path.isEmpty()) {
			MyPoint goal = new MyPoint(path.get(path.size() - 1).x, path.get(path.size() - 1).y);
			int i = 2;
			while (checkIfUnitOnPoint(goal, battleState, aiUnit)) {
				if ((path.size() - i) <= 0) {
					return;
				}
				goal = new MyPoint(path.get(path.size() - i).x, path.get(path.size() - i).y);
				i++;
			}
			unitTurns.add(new UnitTurn(aiUnit.getEntityID(), new Move(MoveType.MOVE, goal)));
		} else {
			unitTurns.add(new UnitTurn(aiUnit.getEntityID(), new Move(MoveType.MOVE, aiUnit.getCurrentPosition().getTilePosAsPoint())));
		}
	}

	private void moveAttack(Entity aiUnit, BattleState battleState, final Array<UnitTurn> unitTurns, final TreeMap<Integer, List<Entity>> distancesWithAbilityTargetUnits) {
		oldTime = debugTime("just move and attack", oldTime);
		final Entity closestUnit = distancesWithAbilityTargetUnits.firstEntry().getValue().get(0);
		final TiledMapPosition closestUnitPos = new TiledMapPosition().setPositionFromTiles(closestUnit.getCurrentPosition().getTileX(), closestUnit.getCurrentPosition().getTileY());
		final List<GridCell> path = MyPathFinder.getInstance().pathTowards(new TiledMapPosition().setPositionFromTiles(aiUnit.getCurrentPosition().getTileX(), aiUnit.getCurrentPosition().getTileY()), closestUnitPos, aiUnit.getAp());
		MyPoint moveGoal = new MyPoint(path.get(path.size() - 1).x, path.get(path.size() - 1).y);
		int i = 2;
		while (checkIfUnitOnPoint(moveGoal, battleState, aiUnit)) {
			if ((path.size() - i) <= 0) {
				moveGoal = new MyPoint(aiUnit.getCurrentPosition().getTileX(), aiUnit.getCurrentPosition().getTileY());
				break;
			}
			moveGoal = new MyPoint(path.get(path.size() - i).x, path.get(path.size() - i).y);
			i++;
		}
		final MyPoint attackGoal = new MyPoint(distancesWithAbilityTargetUnits.firstEntry().getValue().get(0).getCurrentPosition().getTileX(), distancesWithAbilityTargetUnits.firstEntry().getValue().get(0).getCurrentPosition().getTileY());
		final UnitTurn moveAttack = new UnitTurn(aiUnit.getEntityID(), new Move(MoveType.MOVE, moveGoal));
		moveAttack.addMove(new Move(MoveType.ATTACK, attackGoal));
		unitTurns.add(moveAttack);
	}

	// if goal is right next to start, then problem ==> just move in random
	// direction? or complicated algorithm that decides move based on start,goal,
	// ability??
	private Array<MyPoint> tryToMoveAndCastSpell(Ability ability, Entity aiUnit, BattleState battleState, final Array<UnitTurn> unitTurns, Array<MyPoint> abilityTargets) {
		MyPoint endPoint = new MyPoint(aiUnit.getCurrentPosition().getTileX(), aiUnit.getCurrentPosition().getTileY());
		final UnitTurn moveAndSpell = new UnitTurn(aiUnit.getEntityID(), new Move(MoveType.MOVE, endPoint));
		int ap = aiUnit.getAp();
		final BattleState copyBattleState = battleState.makeCopy();
		final Entity copyUnit = copyBattleState.get(aiUnit.getCurrentPosition().getTileX(), aiUnit.getCurrentPosition().getTileY()).getUnit();
		List<MyPoint> passedPoints = new ArrayList<>();
		while (abilityTargets.isEmpty() && (ap > 0)) {
			endPoint = getRandomMovePoint(new MyPoint(copyUnit.getCurrentPosition().getTileX(), copyUnit.getCurrentPosition().getTileY()), copyBattleState, copyUnit, passedPoints);
			passedPoints.add(new MyPoint(endPoint.x, endPoint.y));
			copyBattleState.moveUnitTo(copyUnit, endPoint);
			moveAndSpell.addMove(new Move(MoveType.MOVE, endPoint));
			ap--;
			abilityTargets = getAbilityTargets(ability, endPoint, copyUnit.isPlayerUnit(), copyBattleState);
		}
		if (!abilityTargets.isEmpty()) {
			addSpellMovesAfterMovingForEveryTarget(ability, unitTurns, abilityTargets, moveAndSpell, copyBattleState, copyUnit);
		} else {
			// units were in range, but moving did not bring them into line of sight
			unitTurns.add(moveAndSpell);
		}
		oldTime = debugTime("casting spell after move", oldTime);
		return abilityTargets;
	}

	private MyPoint getRandomMovePoint(MyPoint unitPoint, BattleState copyBattleState, Entity copyUnit, List<MyPoint> passedPoints) {
		int randomStartDirection = random.nextInt(4);
		int direction = random.nextInt(4);
		MyPoint endPoint = new MyPoint(unitPoint.x, unitPoint.y);
		do {
			switch (direction) {
			case 0:
				endPoint = new MyPoint(unitPoint.x + 1, unitPoint.y);
				break;
			case 1:
				endPoint = new MyPoint(unitPoint.x - 1, unitPoint.y);
				break;
			case 2:
				endPoint = new MyPoint(unitPoint.x, unitPoint.y + 1);
				break;
			case 3:
				endPoint = new MyPoint(unitPoint.x, unitPoint.y - 1);
				break;
			default:
				break;
			}

			if (checkValidityMovePoint(endPoint, copyBattleState, copyUnit, passedPoints)) {
				return endPoint;
			} else {
				direction = ++direction % 4;
			}
		} while (direction != randomStartDirection);
		return new MyPoint(unitPoint.x, unitPoint.y);
	}

	private boolean checkValidityMovePoint(MyPoint endPoint, BattleState copyBattleState, Entity copyUnit, List<MyPoint> passedPoints) {
		return !checkIfUnitOnPoint(endPoint, copyBattleState, copyUnit) && inBounds(endPoint, copyBattleState) && isAlreadyVisitedPoint(endPoint, passedPoints);
	}

	private boolean isAlreadyVisitedPoint(MyPoint endPoint, List<MyPoint> passedPoints) {
		for (MyPoint point : passedPoints) {
			if (point.equals(endPoint)) {
				return true;
			}
		}
		return false;
	}

	private boolean inBounds(MyPoint point, BattleState copyBattleState) {
		final int maxWidth = copyBattleState.getWidth() - 1;
		final int maxHeight = copyBattleState.getHeight() - 1;
		return (point.x > maxWidth) || (point.y > maxHeight) || (point.x < 0) || (point.y < 0);
	}

	private void addSpellMovesAfterMovingForEveryTarget(Ability ability, final Array<UnitTurn> unitTurns, Array<MyPoint> abilityTargets, final UnitTurn moveAndSpell, final BattleState copyBattleState, final Entity copyUnit) {
		oldTime = debugTime("targets found after walking", oldTime);
		for (final MyPoint target : abilityTargets) {
			final Set<MyPoint> positionsToCastSpell = BattleStateGridHelper.getInstance().getAllCastPointsWhereTargetIsHit(ability, target, new MyPoint(copyUnit.getCurrentPosition().getTileX(), copyUnit.getCurrentPosition().getTileY()),
					copyBattleState);
			for (final MyPoint MyPoint : positionsToCastSpell) {
				final UnitTurn moveAndSpellCopy = moveAndSpell.makeCopy();
				final Array<MyPoint> affectedUnits = BattleStateGridHelper.getInstance().getTargetsAbility(ability, MyPoint, new MyPoint(copyUnit.getCurrentPosition().getTileX(), copyUnit.getCurrentPosition().getTileY()),
						getUnitPositions(false, ability, copyBattleState));
				moveAndSpellCopy.addMove(new SpellMove(MoveType.SPELL, MyPoint, ability, affectedUnits));
				unitTurns.add(moveAndSpellCopy);
			}
		}
	}

	private void addSpellMovesForEveryTarget(Ability ability, Entity aiUnit, BattleState battleState, final Array<UnitTurn> unitTurns, final MyPoint casterPos, Array<MyPoint> abilityTargets) {
		for (final MyPoint target : abilityTargets) {
			final Set<MyPoint> positionsToCastSpell = BattleStateGridHelper.getInstance().getAllCastPointsWhereTargetIsHit(ability, target, new MyPoint(aiUnit.getCurrentPosition().getTileX(), aiUnit.getCurrentPosition().getTileY()),
					battleState);
			oldTime = debugTime("finished 1 target - getcastPointswherehit", oldTime);
			final Move moveAfterSpell = decideMove(ability, aiUnit, battleState);
			oldTime = debugTime("finished 1 target - decideMove", oldTime);
			for (final MyPoint MyPoint : positionsToCastSpell) {
				final Array<MyPoint> affectedUnits = BattleStateGridHelper.getInstance().getTargetsAbility(ability, MyPoint, casterPos, getUnitPositions(false, ability, battleState));
				final UnitTurn spellAndMove = new UnitTurn(aiUnit.getEntityID(), new SpellMove(MoveType.SPELL, MyPoint, ability, affectedUnits));
				spellAndMove.addMove(moveAfterSpell);
				unitTurns.add(spellAndMove);
			}
			oldTime = debugTime("finished 1 target - createspellmove", oldTime);
		}
		oldTime = debugTime("adding spell moves for every target", oldTime);
	}

	private Set<MyPoint> filterOutNumber(Set<MyPoint> cellsToCastOn, int maxSize) {
		final List<MyPoint> pointsAsList = new ArrayList<>();
		pointsAsList.addAll(cellsToCastOn);
		while (pointsAsList.size() > maxSize) {
			pointsAsList.remove(random.nextInt(pointsAsList.size()));
		}
		cellsToCastOn.clear();
		cellsToCastOn.addAll(pointsAsList);
		return cellsToCastOn;
	}

	private void filterUnits(Set<MyPoint> cellsToCastOn, BattleState battleState) {
		for (final Entity unit : battleState.getAllUnits()) {
			cellsToCastOn.remove(unit.getCurrentPosition().getTilePosAsPoint());
		}
	}

	private boolean checkIfUnitOnPoint(MyPoint goal, BattleState battleState, Entity copyUnit) {
		for (final Entity unit : battleState.getAllUnits()) {
			if ((goal.equals(new MyPoint(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY())))
					&& !((copyUnit.getCurrentPosition().getTileX() == unit.getCurrentPosition().getTileX()) && (copyUnit.getCurrentPosition().getTileY() == unit.getCurrentPosition().getTileY()))) {
				return true;
			}
		}
		return false;
	}

	private Move decideMove(Ability ability, Entity aiUnit, BattleState battleState) {
		final MyPoint centerOfGravityEnemies = Utility.getCenterOfGravityPlayers(battleState);
		final MyPoint centerOfGravityAllies = Utility.getCenterOfGravityAi(battleState);
		final MyPoint centerOfGravityAllUnits = Utility.getCenterOfGravityAllUnits(battleState);
		oldTime = debugTime("centersofgravirty", oldTime);
		// if low hp run away
		if (aiUnit.getHp() <= ((aiUnit.getEntityData().getMaxHP() / 100.0f) * 10)) {
			final MyPoint originalGoal = MyPathFinder.getInstance().getPositionFurthestAwayFrom(centerOfGravityEnemies);
			final MyPoint trimmedGoal = trimPathConsideringApAndReachable(originalGoal, aiUnit, battleState);
			return new Move(MoveType.MOVE, trimmedGoal);
		}
		oldTime = debugTime("check low hp run away", oldTime);
		switch (ability.getAffectedTeams()) {
		case FRIENDLY:
			return new Move(MoveType.MOVE, trimPathConsideringApAndReachable(centerOfGravityAllies, aiUnit, battleState));
		case ENEMY:
			return new Move(MoveType.MOVE, trimPathConsideringApAndReachable(centerOfGravityEnemies, aiUnit, battleState));
		case BOTH:
			return new Move(MoveType.MOVE, trimPathConsideringApAndReachable(centerOfGravityAllUnits, aiUnit, battleState));
		case NONE:
			return new Move(MoveType.MOVE, trimPathConsideringApAndReachable(centerOfGravityAllUnits, aiUnit, battleState));
		default:
			Gdx.app.debug(TAG, WRONG_ABILITY_TEAM);
			return null;
		}
	}

	private MyPoint trimPathConsideringApAndReachable(MyPoint originalGoal, Entity aiUnit, BattleState battleState) {
		final TiledMapPosition start = new TiledMapPosition().setPositionFromTiles(aiUnit.getCurrentPosition().getTileX(), aiUnit.getCurrentPosition().getTileY());
		final TiledMapPosition goal = new TiledMapPosition().setPositionFromTiles(originalGoal.x, originalGoal.y);
		final List<GridCell> path = MyPathFinder.getInstance().pathTowards(start, goal, aiUnit.getAp());
		oldTime = debugTime("calculated path", oldTime);
		if (path.isEmpty()) {
			oldTime = debugTime("empty path trimmed", oldTime);
			return new MyPoint(start.getTileX(), start.getTileY());
		}

		int i = 1;
		while (battleState.get(path.get(path.size() - i).x, path.get(path.size() - i).y).isOccupied() && (i < path.size())) {
			i++;
		}
		oldTime = debugTime("path trimmed", oldTime);
		return new MyPoint(path.get(path.size() - i).x, path.get(path.size() - i).y);
	}

	private Map<Integer, List<Entity>> getDistancesToTargets(Entity unit, BattleState battleState, Ability ability) {
		final Map<Integer, List<Entity>> distances = new TreeMap<>();
		switch (ability.getAffectedTeams()) {
		case FRIENDLY:
			addFriendlyUnitsWithDistance(unit, battleState, distances);
			break;
		case ENEMY:
			addEnemyUnitsWithDistance(unit, battleState, distances);
			break;
		case BOTH:
			addAllUnitsWithDistance(unit, battleState, distances);
			break;
		case NONE:
			return distances;
		}
		return distances;
	}

	private void addAllUnitsWithDistance(Entity unit, BattleState battleState, final Map<Integer, List<Entity>> distances) {
		for (final Entity entity : battleState.getAllVisibleUnits()) {
			if (unit.getEntityID() != entity.getEntityID()) {
				distances.computeIfAbsent(calculateDistanceTwoUnits(unit, entity), k -> new ArrayList<Entity>()).add(entity);
			}
		}
	}

	private void addEnemyUnitsWithDistance(Entity unit, BattleState battleState, final Map<Integer, List<Entity>> distances) {
		Array<Entity> unitsToCheck;
		if (unit.isPlayerUnit()) {
			unitsToCheck = battleState.getVisibleAiUnits();
		} else {
			unitsToCheck = battleState.getVisiblePlayerUnits();
		}
		for (final Entity entity : unitsToCheck) {
			if (unit.getEntityID() != entity.getEntityID()) {
				distances.computeIfAbsent(calculateDistanceTwoUnits(unit, entity), k -> new ArrayList<Entity>()).add(entity);
			}
		}
	}

	private void addFriendlyUnitsWithDistance(Entity unit, BattleState battleState, final Map<Integer, List<Entity>> distances) {
		Array<Entity> unitsToCheck;
		if (unit.isPlayerUnit()) {
			unitsToCheck = battleState.getVisiblePlayerUnits();
		} else {
			unitsToCheck = battleState.getVisibleAiUnits();
		}
		for (final Entity entity : unitsToCheck) {
			if (unit.getEntityID() != entity.getEntityID()) {
				distances.computeIfAbsent(calculateDistanceTwoUnits(unit, entity), k -> new ArrayList<Entity>()).add(entity);
			}
		}
	}

	private Integer calculateDistanceTwoUnits(Entity aiUnit, Entity entity) {
		final TiledMapPosition aiUnitPosition = new TiledMapPosition().setPositionFromTiles(aiUnit.getCurrentPosition().getTileX(), aiUnit.getCurrentPosition().getTileY());
		final TiledMapPosition entityPosition = new TiledMapPosition().setPositionFromTiles(entity.getCurrentPosition().getTileX(), entity.getCurrentPosition().getTileY());
		return aiUnitPosition.getDistance(entityPosition);
	}

	public static Array<MyPoint> getAbilityTargets(Ability ability, MyPoint casterPos, boolean isPlayerUnit, BattleState battleState) {

		final Array<MyPoint> unitPositions = getVisibleUnitPositions(isPlayerUnit, ability, battleState);
		final Array<MyPoint> points = BattleStateGridHelper.getInstance().getTargetPositionsInRangeAbility(casterPos, ability, unitPositions);

		if (ability.getTarget() == Target.SELF) {
			points.add(casterPos);
		}
		return points;
	}

	private static Array<MyPoint> getVisibleUnitPositions(boolean isPlayerUnit, Ability ability, BattleState battleState) {
		switch (ability.getAffectedTeams()) {
		case FRIENDLY:
			if (isPlayerUnit) {
				return collectPoints(battleState.getVisiblePlayerUnits());
			} else {
				return collectPoints(battleState.getVisibleAiUnits());
			}
		case ENEMY:
			if (!isPlayerUnit) {
				return collectPoints(battleState.getVisiblePlayerUnits());
			} else {
				return collectPoints(battleState.getVisibleAiUnits());
			}
		case BOTH:
			return collectPoints(battleState.getAllVisibleUnits());
		case NONE:
			return new Array<>();
		default:
			Gdx.app.debug(TAG, WRONG_ABILITY_TEAM);
			return null;
		}
	}

	public static Array<MyPoint> getUnitPositions(boolean isPlayerUnit, Ability ability, BattleState battleState) {
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
			Gdx.app.debug(TAG, WRONG_ABILITY_TEAM);
			return null;
		}
	}

	private static Array<MyPoint> collectPoints(Array<Entity> allUnits) {
		final Array<MyPoint> points = new Array<>();
		for (final Entity unit : allUnits) {
			points.add(new MyPoint(unit.getCurrentPosition().getTileX(), unit.getCurrentPosition().getTileY()));
		}
		return points;
	}

	public static Long debugTime(String log, Long oldTime) {
		final Long newTime = System.currentTimeMillis();
		Gdx.app.debug(TAG, log + " which took : " + (newTime - oldTime) + " ms");
		return newTime;
	}

	@Override
	public Move getMove(UnitOwner player, BattleState battleState) {
		// not applicable for this generator
		return null;
	}

	@Override
	public List<Move> getMoves(UnitOwner player, BattleState battleState, int ap) {
		// not applicable for this generator
		return new ArrayList<>();
	}

}
