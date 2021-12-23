package com.jelte.norii.ai;

import java.util.List;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.battle.battleState.Move;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.Ability;

public interface MoveGenerator {

	public List<Move> generateMovesForPlayerFullAp(UnitOwner player, BattleState battleState);

	public Array<UnitTurn> generateAllPossibleTurnsForUnit(Ability ability, Entity aiUnit, BattleState battleState);

	public Move generateSingleMoveForPlayer(UnitOwner player, BattleState battleState);

	public Move generateSingleMovementMoveForPlayer(UnitOwner player, BattleState battleState);

	public Move generateSingleSpellMoveForPlayer(UnitOwner player, BattleState battleState);

	public Move generateSingleAttackMoveForPlayer(UnitOwner player, BattleState battleState);
}
