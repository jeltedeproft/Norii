package balancer;

import java.util.List;

import com.jelte.norii.battle.battleState.BattleState;
import com.jelte.norii.entities.Entity;

public class BattleSimulator {
	private final BattleState battleState;

	public BattleSimulator() {
		battleState = new BattleState(20, 20);
	}

	public void initEntities(List<Entity> heroes) {
		for (final Entity unit : heroes) {
			battleState.addEntity(unit);
		}
	}

	public void playRound() {
		int turn = 0;
		boolean allyTurn = true;
		while ((!allyHasWon()) && (!enemyHasWon()) && (turn < 200)) {
			playATurn(allyTurn);
			turn++;
			allyTurn = !allyTurn;
		}
	}

	private boolean allyHasWon() {
		return battleState.getPlayerUnits().isEmpty();
	}

	private boolean enemyHasWon() {
		return battleState.getAiUnits().isEmpty();
	}

	private void playATurn(boolean allyTurn) {
		if (allyTurn) {

		}
	}

}
