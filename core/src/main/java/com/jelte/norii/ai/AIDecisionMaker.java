package com.jelte.norii.ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.battle.battleState.BattleStateGridHelperFromUnits;
import com.jelte.norii.battle.battleState.HypotheticalUnit;
import com.jelte.norii.battle.battleState.Move;
import com.jelte.norii.battle.battleState.MoveType;
import com.jelte.norii.entities.AiEntity;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.PlayerEntity;
import com.jelte.norii.magic.Ability;
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

	public void makeDecision(BattleState battleState) {
		Array<HypotheticalUnit> players = battleState.getPlayerUnits();
		Array<HypotheticalUnit> ais = battleState.getAiUnits();
		// for every aiUnit, generate all his moves and store them
		for (HypotheticalUnit aiUnit : ais) {
			for (Ability ability : aiUnit.getAbilities()) {
				generateMoves(ability, aiUnit, battleState);
			}
		}

		// generate 2 more moves for each of them (only keep changes to score)

		// calculate score

		// sort

		// return move for highest score
	}

	private Array<UnitTurn> generateMoves(Ability ability, AiEntity aiUnit, BattleState battleState) {
		Array<UnitTurn> unitTurns = new Array<>();

		// get the distance between unit and possible targets
		TreeMap<Integer, HypotheticalUnit> distancesWithAbilityTargetUnits = (TreeMap<Integer, HypotheticalUnit>) getDistancesToTargets(aiUnit, battleState, ability);

		if (!distancesWithAbilityTargetUnits.isEmpty() && (distancesWithAbilityTargetUnits.firstKey() > (ability.getSpellData().getRange() + ability.getSpellData().getAreaOfEffectRange() + aiUnit.getAp()))) {
			if (distancesWithAbilityTargetUnits.firstKey() > (aiUnit.getAttackRange() + aiUnit.getAp())) {
				// just walk
				HypotheticalUnit closestUnit = distancesWithAbilityTargetUnits.firstEntry().getValue();
				TiledMapPosition closestUnitPos = new TiledMapPosition().setPositionFromTiles(closestUnit.getX(), closestUnit.getY());
				List<GridCell> path = MyPathFinder.getInstance().pathTowards(aiUnit.getCurrentPosition(), closestUnitPos, aiUnit.getAp());
				Point goal = new Point(path.get(path.size() - 1).x, path.get(path.size() - 1).y);
				unitTurns.add(new UnitTurn(aiUnit.getEntityID(),new Move(MoveType.MOVE,goal)));
				return unitTurns;
			}else {
				//move attack
				HypotheticalUnit closestUnit = distancesWithAbilityTargetUnits.firstEntry().getValue();
				TiledMapPosition closestUnitPos = new TiledMapPosition().setPositionFromTiles(closestUnit.getX(), closestUnit.getY());
				List<GridCell> path = MyPathFinder.getInstance().pathTowards(aiUnit.getCurrentPosition(), closestUnitPos, aiUnit.getAp());
				Point moveGoal = new Point(path.get(path.size() - 1).x, path.get(path.size() - 1).y);
				Point attackGoal = new Point(distancesWithAbilityTargetUnits.firstEntry().getValue().getX(), distancesWithAbilityTargetUnits.firstEntry().getValue().getY());
				UnitTurn moveAttack = new UnitTurn(aiUnit.getEntityID(),new Move(MoveType.MOVE,moveGoal));
				moveAttack.addMove(new Move(MoveType.ATTACK,attackGoal));
				unitTurns.add(moveAttack);
				return unitTurns;
			}
		}

		// decide where to cast spell
		Array<Point> abilityTargets = getAbilityTargets(ability, aiUnit, battleState);
		if(!abilityTargets.isEmpty()) {
			for(Point target : abilityTargets) {
				Set<Point> positionsToCastSpell = battleStateGridHelper.getAllPointsWhereTargetIsHit(ability,target,new Point(aiUnit.getCurrentPosition().getTileX(),aiUnit.getCurrentPosition().getTileY()),battleState);
				Move moveAfterSpell = decideMove(ability, aiUnit, battleState);
				for(Point point : positionsToCastSpell) {
					UnitTurn spellAndMove = new UnitTurn(aiUnit.getEntityID(),new Move(MoveType.SPELL,positionToCastSpell));
					spellAndMove.addMove(MoveType.MOVE,);
				}
			}
		}


		while (abilityTargets.isEmpty() && (aiUnit.getAp() > 0)) {

		}
		// decide where to move

		// decide who to attack (if possible)

	}

	private Move decideMove(Ability ability, AiEntity aiUnit, BattleState battleState) {
		Point centerOfGravityEnemies = Utility.getCenterOfGravityPlayers(battleState);
		Point centerOfGravityAllies = Utility.getCenterOfGravityAi(battleState);
		Point centerOfGravityAllUnits = Utility.getCenterOfGravityAllUnits(battleState);

		// if low hp run away
		if (aiUnit.getHp() <= ((aiUnit.getEntityData().getMaxHP() / 100.0f) * 10)) {
			Point originalGoal = MyPathFinder.getInstance().getPositionFurthestAwayFrom(centerOfGravityEnemies);
			Point trimmedGoal = trimPathConsideringApAndReachable(originalGoal, aiUnit);
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

	private Point trimPathConsideringApAndReachable(Point originalGoal, AiEntity aiUnit) {
		TiledMapPosition start = new TiledMapPosition().setPositionFromTiles(aiUnit.getCurrentPosition().getTileX(), aiUnit.getCurrentPosition().getTileY());
		TiledMapPosition goal = new TiledMapPosition().setPositionFromTiles(originalGoal.x, originalGoal.y);
		List<GridCell> path = MyPathFinder.getInstance().pathTowards(start, goal, aiUnit.getAp());
		return new Point(path.get(path.size() - 1).x, path.get(path.size() - 1).y);
	}

	private Map<Integer, HypotheticalUnit> getDistancesToTargets(AiEntity aiUnit, BattleState battleState, Ability ability) {
		Map<Integer, HypotheticalUnit> distances = new TreeMap<>();
		switch (ability.getAffectedTeams()) {
		case FRIENDLY:
			for (HypotheticalUnit entity : battleState.getAiUnits()) {
				distances.put(calculateDistanceTwoUnits(aiUnit, entity), entity);
			}
			break;
		case ENEMY:
			for (HypotheticalUnit entity : battleState.getPlayerUnits()) {
				distances.put(calculateDistanceTwoUnits(aiUnit, entity), entity);
			}
			break;
		case BOTH:
			for (HypotheticalUnit entity : battleState.getAllUnits()) {
				distances.put(calculateDistanceTwoUnits(aiUnit, entity), entity);
			}
			break;
		case NONE:
			return distances;
		}
		return distances;
	}

	private Integer calculateDistanceTwoUnits(AiEntity aiUnit, HypotheticalUnit entity) {
		TiledMapPosition aiUnitPosition = aiUnit.getCurrentPosition();
		TiledMapPosition entityPosition = new TiledMapPosition().setPositionFromTiles(entity.getX(), entity.getY());
		return aiUnitPosition.getDistance(entityPosition);
	}

	private Array<Point> getAbilityTargets(Ability ability, AiEntity aiUnit, BattleState battleState) {
		Point casterPos = new Point(aiUnit.getCurrentPosition().getTileX(), aiUnit.getCurrentPosition().getTileY());
		Array<Point> unitPositions = getUnitPositions(aiUnit, ability, battleState);
		return battleStateGridHelper.getTargetPositionsInRangeAbility(casterPos, ability, unitPositions);
	}

	private Array<Point> getUnitPositions(Entity unit, Ability ability, BattleState battleState) {
		switch (ability.getAffectedTeams()) {
		case FRIENDLY:
			if (unit.isPlayerUnit()) {
				return collectPoints(battleState.getPlayerUnits());
			} else {
				return collectPoints(battleState.getAiUnits());
			}
		case ENEMY:
			if (!unit.isPlayerUnit()) {
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
		Array<Point> points = new Array<>();
		for (HypotheticalUnit unit : allUnits) {
			points.add(new Point(unit.getX(), unit.getY()));
		}
		return points;
	}

}
