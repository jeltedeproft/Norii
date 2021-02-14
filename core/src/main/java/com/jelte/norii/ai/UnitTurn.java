package com.jelte.norii.ai;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.battle.battleState.Move;

public class UnitTurn {
	private int entityID;
	private int currentMove;
	private final Array<Move> moves;

	public UnitTurn(int entityID, Move move) {
		moves = new Array<>();
		this.entityID = entityID;
		currentMove = 0;
		moves.add(move);
	}

	public UnitTurn(int entityID, Array<Move> moves) {
		this.moves = moves;
		this.entityID = entityID;
		currentMove = 0;
	}

	public UnitTurn(int entityID) {
		this.moves = new Array<>();
		this.entityID = entityID;
		currentMove = 0;
	}

	public void addMove(Move move) {
		moves.add(move);
	}

	public int getEntityID() {
		return entityID;
	}

	public void setEntityID(int entityID) {
		this.entityID = entityID;
	}

	public Array<Move> getMoves() {
		return moves;
	}

	public Move getNextMove() {
		Move nextMove = null;
		if (currentMove < moves.size) {
			nextMove = moves.get(currentMove);
		}
		currentMove++;
		return nextMove;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (final Move move : moves) {
			sb.append(move.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	public UnitTurn makeCopy() {
		final UnitTurn copy = new UnitTurn(entityID);
		for (final Move move : moves) {
			copy.addMove(move.makeCopy());
		}
		return copy;
	}
}
