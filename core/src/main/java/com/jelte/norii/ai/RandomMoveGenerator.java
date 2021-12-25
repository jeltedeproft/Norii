package com.jelte.norii.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.battle.battleState.BattleStateGridHelper;
import com.jelte.norii.battle.battleState.Move;
import com.jelte.norii.battle.battleState.MoveType;
import com.jelte.norii.battle.battleState.SpellMove;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Ability.Target;
import com.jelte.norii.utility.MyPoint;
import com.jelte.norii.utility.Utility;

public class RandomMoveGenerator implements MoveGenerator {

	private static final float CHANCE_OF_MAKING_MOVE = 0.2f;
	private static final float CHANCE_OF_MAKING_ATTACK = 0.1f;
	private static final float CHANCE_OF_MAKING_SPELL = 0.7f;

	private Random random = new Random();

	@Override
	public List<Move> generateMovesForPlayerFullAp(UnitOwner player, BattleState battleState) {
		int ap = player.getAp();
		List<Move> moves = new ArrayList<>();

		while (ap > 0) {
			moves.add(generateSingleMoveForPlayer(player, battleState));
			ap--;
		}

		return moves;
	}

	@Override
	public Array<UnitTurn> generateAllPossibleTurnsForUnit(Ability ability, Entity aiUnit, BattleState battleState) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Move generateSingleMoveForPlayer(UnitOwner player, BattleState battleState) {
		float randomChance = random.nextFloat();

		if (randomChance <= CHANCE_OF_MAKING_MOVE) {
			return generateSingleMovementMoveForPlayer(player, battleState);
		}

		if (randomChance <= (CHANCE_OF_MAKING_MOVE + CHANCE_OF_MAKING_ATTACK)) {
			return generateSingleAttackMoveForPlayer(player, battleState);
		}

		return generateSingleSpellMoveForPlayer(player, battleState);

	}

	@Override
	public Move generateSingleMovementMoveForPlayer(UnitOwner player, BattleState battleState) {
		Entity randomUnit = getRandomUnit(player, battleState);

		MyPoint location = battleState.getRandomMoveSpotForUnit(randomUnit);
		if (location == null) {
			return new Move(MoveType.MOVE, randomUnit.getCurrentPosition().getTilePosAsPoint());
		} else {
			return new Move(MoveType.MOVE, location);
		}

	}

	@Override
	public Move generateSingleAttackMoveForPlayer(UnitOwner player, BattleState battleState) {
		Entity randomUnit = getRandomUnit(player, battleState);
		int attackRange = randomUnit.getAttackRange();
		Array<Entity> neighbours = battleState.getNeighbours(randomUnit.getCurrentPosition().getTilePosAsPoint(), attackRange);
		Move move;
		if (!neighbours.isEmpty()) {
			Entity unitToAttack = Utility.getRandom(neighbours);
			move = new Move(MoveType.ATTACK, unitToAttack.getCurrentPosition().getTilePosAsPoint());
		} else {
			move = new Move(MoveType.DUMMY, new MyPoint(0, 0));
		}

		return move;
	}

	@Override
	public Move generateSingleSpellMoveForPlayer(UnitOwner player, BattleState battleState) {
		Entity randomUnit = getRandomUnit(player, battleState);
		Ability ability = randomUnit.getAbility();
		switch (ability.getTarget()) {
		case CELL_BUT_NO_UNIT:
			return castAbilityOnCell(ability, randomUnit, battleState);
		case NO_TARGET:
		case SELF:
			return castNoTargetOrSelf(ability, randomUnit, battleState);
		case CELL:
		case UNIT:
			return castAbilityOnTarget(player, ability, randomUnit, battleState);
		default:
			return new Move(MoveType.DUMMY, new MyPoint(0, 0));
		}
	}

	private Move castAbilityOnTarget(UnitOwner player, Ability ability, Entity randomUnit, BattleState battleState) {
		final MyPoint casterPos = new MyPoint(randomUnit.getCurrentPosition().getTileX(), randomUnit.getCurrentPosition().getTileY());
		Array<MyPoint> abilityTargets = AIMoveDecider.getAbilityTargets(ability, casterPos, randomUnit.isPlayerUnit(), battleState);
		if (!abilityTargets.isEmpty()) {
			MyPoint castPoint = Utility.getRandom(abilityTargets);
			final Array<MyPoint> affectedUnits = BattleStateGridHelper.getInstance().getTargetsAbility(ability, castPoint, casterPos, AIMoveDecider.getUnitPositions(false, ability, battleState));
			return new SpellMove(MoveType.SPELL, castPoint, ability, affectedUnits);
		} else {
			return generateSingleMovementMoveForPlayer(player, battleState);
		}
	}

	private Move castNoTargetOrSelf(Ability ability, Entity randomUnit, BattleState battleState) {
		return new SpellMove(MoveType.SPELL, new MyPoint(randomUnit.getCurrentPosition().getTileX(), randomUnit.getCurrentPosition().getTileY()), ability, null);
	}

	private Move castAbilityOnCell(Ability ability, Entity randomUnit, BattleState battleState) {
		final MyPoint center = new MyPoint(randomUnit.getCurrentPosition().getTileX(), randomUnit.getCurrentPosition().getTileY());
		final Set<MyPoint> cellsToCastOn = BattleStateGridHelper.getInstance().getAllPointsASpellCanHit(center, ability.getLineOfSight(), ability.getSpellData().getRange(), battleState);

		if (ability.getTarget() == Target.CELL_BUT_NO_UNIT) {
			filterUnits(cellsToCastOn, battleState);
		}

		Optional<MyPoint> point = Utility.getRandom(cellsToCastOn);
		if (point.isPresent()) {
			return new SpellMove(MoveType.SPELL, point.get(), ability, null);
		}

		return new Move(MoveType.DUMMY, new MyPoint(0, 0));
	}

	private void filterUnits(Set<MyPoint> cellsToCastOn, BattleState battleState) {
		for (final Entity unit : battleState.getAllUnits()) {
			cellsToCastOn.remove(unit.getCurrentPosition().getTilePosAsPoint());
		}
	}

	private Entity getRandomUnit(UnitOwner player, BattleState battleState) {
		Entity randomUnit;
		if (player.isAI()) {
			randomUnit = battleState.getRandomAiUnit();
		} else {
			randomUnit = battleState.getRandomPlayerUnit();
		}
		return randomUnit;
	}

}
