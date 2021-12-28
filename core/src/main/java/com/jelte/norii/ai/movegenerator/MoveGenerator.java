package com.jelte.norii.ai.movegenerator;

import java.util.List;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.ai.UnitTurn;
import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.battle.battleState.Move;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.magic.Ability;

public interface MoveGenerator {

	public Move getMove(UnitOwner player, BattleState battleState);

	public List<Move> getMoves(UnitOwner player, BattleState battleState, int ap);

	public Array<UnitTurn> getAllMovesUnit(Ability ability, Entity unit, BattleState battleState);

}
