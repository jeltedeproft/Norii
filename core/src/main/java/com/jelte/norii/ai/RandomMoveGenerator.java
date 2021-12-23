package com.jelte.norii.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.battle.battleState.Move;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.Ability;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Move generateSingleSpellMoveForPlayer(UnitOwner player, BattleState battleState) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Move generateSingleAttackMoveForPlayer(UnitOwner player, BattleState battleState) {
		// TODO Auto-generated method stub
		return null;
	}

}
