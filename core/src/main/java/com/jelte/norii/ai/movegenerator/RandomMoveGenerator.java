package com.jelte.norii.ai.movegenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.UnitTurn;
import com.jelte.norii.battle.battlestate.BattleState;
import com.jelte.norii.battle.battlestate.BattleStateGridHelper;
import com.jelte.norii.battle.battlestate.Move;
import com.jelte.norii.battle.battlestate.MoveType;
import com.jelte.norii.battle.battlestate.SpellMove;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Ability.Target;
import com.jelte.norii.utility.MyPoint;
import com.jelte.norii.utility.Utility;

public class RandomMoveGenerator implements MoveGenerator {

	private static final float CHANCE_OF_MAKING_MOVE = 0.2f;
	private static final float CHANCE_OF_MAKING_ATTACK = 0.1f;

	@Override
	public List<Move> getMoves(UnitOwner player, BattleState battleState, int ap) {
		List<Move> moves = new ArrayList<>();

		while (ap > 0) {
			moves.add(getMove(player, battleState));
			ap--;
		}

		return moves;
	}

	@Override
	public Array<UnitTurn> getAllMovesUnit(Ability ability, Entity aiUnit, BattleState battleState) {
		return null; // not applicable for this generator
	}

	@Override
	public Move getMove(UnitOwner player, BattleState battleState) {
		float randomChance = Utility.random.nextFloat();

		if (randomChance <= CHANCE_OF_MAKING_MOVE) {
			return generateSingleMovementMoveForPlayer(player, battleState);
		}

		if (randomChance <= (CHANCE_OF_MAKING_MOVE + CHANCE_OF_MAKING_ATTACK)) {
			return generateSingleAttackMoveForPlayer(player, battleState);
		}

		return generateSingleSpellMoveForPlayer(player, battleState);

	}

	public Move generateSingleMovementMoveForPlayer(UnitOwner player, BattleState battleState) {
		Entity randomUnit = getRandomUnit(player, battleState);

		MyPoint location = battleState.getRandomMoveSpotForUnit(randomUnit);
		if (location == null) {
			return new Move(MoveType.MOVE, randomUnit.getCurrentPosition().getTilePosAsPoint(), randomUnit);
		} else {
			return new Move(MoveType.MOVE, location, randomUnit);
		}

	}

	public Move generateSingleAttackMoveForPlayer(UnitOwner player, BattleState battleState) {
		Entity randomUnit = getRandomUnit(player, battleState);
		int attackRange = randomUnit.getAttackRange();
		Array<Entity> neighbours = battleState.getNeighbours(randomUnit.getCurrentPosition().getTilePosAsPoint(), attackRange);
		return neighbours.isEmpty() ? new Move(MoveType.DUMMY, new MyPoint(0, 0), randomUnit)
									: new Move(MoveType.ATTACK, Utility.getRandom(neighbours).getCurrentPosition().getTilePosAsPoint(), randomUnit);
	}

	public Move generateSingleSpellMoveForPlayer(UnitOwner player, BattleState battleState) {
		Entity randomUnit = getRandomUnit(player, battleState);
		Ability ability = randomUnit.getAbility();
		switch (ability.getTarget()) {
		case CELL_BUT_NO_UNIT:
			return castAbilityOnCell(ability, randomUnit, battleState);
		case NO_TARGET:
		case SELF:
			return castNoTargetOrSelf(ability, randomUnit);
		case CELL:
		case UNIT:
			return castAbilityOnTarget(player, ability, randomUnit, battleState);
		default:
			return new Move(MoveType.DUMMY, new MyPoint(0, 0), randomUnit);
		}
	}

	private Move castAbilityOnTarget(UnitOwner player, Ability ability, Entity randomUnit, BattleState battleState) {
		final MyPoint casterPos = new MyPoint(randomUnit.getCurrentPosition().getTileX(), randomUnit.getCurrentPosition().getTileY());
		Array<MyPoint> abilityTargets = TargetMoveGenerator.getAbilityTargets(ability, casterPos, randomUnit.isPlayerUnit(), battleState);
		if (!abilityTargets.isEmpty()) {
			MyPoint castPoint = Utility.getRandom(abilityTargets);
			final Array<MyPoint> affectedUnits = BattleStateGridHelper.getInstance().getTargetsAbility(ability, castPoint, casterPos, TargetMoveGenerator.getUnitPositions(false, ability, battleState));
			return new SpellMove(MoveType.SPELL, castPoint, ability, affectedUnits, randomUnit);
		} else {
			return generateSingleMovementMoveForPlayer(player, battleState);
		}
	}

	private Move castNoTargetOrSelf(Ability ability, Entity randomUnit) {
		return new SpellMove(MoveType.SPELL, new MyPoint(randomUnit.getCurrentPosition().getTileX(), randomUnit.getCurrentPosition().getTileY()), ability, null, randomUnit);
	}

	private Move castAbilityOnCell(Ability ability, Entity randomUnit, BattleState battleState) {
		final MyPoint center = new MyPoint(randomUnit.getCurrentPosition().getTileX(), randomUnit.getCurrentPosition().getTileY());
		final Set<MyPoint> cellsToCastOn = BattleStateGridHelper.getInstance().getAllPointsASpellCanHit(center, ability.getLineOfSight(), ability.getSpellData().getRange(), battleState);

		if (ability.getTarget() == Target.CELL_BUT_NO_UNIT) {
			filterUnits(cellsToCastOn, battleState);
		}

		Optional<MyPoint> point = Utility.getRandom(cellsToCastOn);
		if (point.isPresent()) {
			return new SpellMove(MoveType.SPELL, point.get(), ability, null, randomUnit);
		}

		return new Move(MoveType.DUMMY, new MyPoint(0, 0), randomUnit);
	}

	private void filterUnits(Set<MyPoint> cellsToCastOn, BattleState battleState) {
		for (final Entity unit : battleState.getAllUnits()) {
			cellsToCastOn.remove(unit.getCurrentPosition().getTilePosAsPoint());
		}
	}

	private Entity getRandomUnit(UnitOwner player, BattleState battleState) {
		return player.isAI()	? battleState.getRandomAiUnit()
								: battleState.getRandomPlayerUnit();
	}

}
