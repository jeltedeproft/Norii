package com.jelte.norii.ai;

import com.badlogic.gdx.utils.Array;
import com.jelte.norii.battle.battleState.Move;

public class UnitTurn {
	private int entityID;
	private Array<Move> moves;

	public UnitTurn(int entityID, Move move) {
		moves = new Array<>();
		this.entityID = entityID;
		moves.add(move);
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Move move : moves) {
			sb.append(move.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
