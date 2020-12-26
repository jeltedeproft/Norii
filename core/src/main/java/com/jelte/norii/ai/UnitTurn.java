package com.jelte.norii.ai;

import java.util.List;

import com.jelte.norii.battle.battleState.Move;

public class UnitTurn {
	private int entityID;
	private List<Move> moves;

	public UnitTurn(int entityID, Move move) {
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

	public List<Move> getMoves() {
		return moves;
	}
}
