package com.jelte.norii.battle.battlePhase;

import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.jelte.norii.battle.BattleManager;
import com.jelte.norii.battle.MessageToBattleScreen;
import com.jelte.norii.entities.Entity;
import com.jelte.norii.map.MyPathFinder;
import com.jelte.norii.map.TiledMapActor;
import com.jelte.norii.particles.ParticleMaker;
import com.jelte.norii.particles.ParticleType;
import com.jelte.norii.utility.TiledMapPosition;

public class MovementBattlePhase extends BattlePhase {
	private final BattleManager battlemanager;

	public MovementBattlePhase(BattleManager battlemanager) {
		this.battlemanager = battlemanager;
	}

	@Override
	public void clickedOnTile(TiledMapActor actor) {
		possibleMove(actor);
	}

	@Override
	public void entry() {
		//
	}

	@Override
	public void exit() {
		battlemanager.setCurrentBattleState(battlemanager.getActionBattleState());
		battlemanager.getCurrentBattleState().entry();
	}

	private void possibleMove(TiledMapActor actor) {
		if (MyPathFinder.getInstance().canUnitWalkTo(battlemanager.getActiveUnit(), actor.getActorPos())) {
			moveUnit(actor);
		} else {
			battlemanager.sendMessageToBattleScreen(MessageToBattleScreen.INVALID_MOVE, battlemanager.getActiveUnit());
		}
	}

	private void moveUnit(TiledMapActor actor) {
		battlemanager.sendMessageToBattleScreen(MessageToBattleScreen.LOCK_UI, battlemanager.getActiveUnit());
		ParticleMaker.deactivateAllParticlesOfType(ParticleType.MOVE);
		battlemanager.getActiveUnit().getVisualComponent().setLocked(true);
		battlemanager.setLockedUnit(battlemanager.getActiveUnit());
		updateUnit(actor);
		battlemanager.getCurrentBattleState().exit();
	}

	private void updateUnit(TiledMapActor actor) {
		final Entity currentUnit = battlemanager.getActiveUnit();
		final TiledMapPosition newUnitPos = new TiledMapPosition().setPositionFromTiles(actor.getActorPos().getTileX(), actor.getActorPos().getTileY());
		final List<GridCell> path = MyPathFinder.getInstance().pathTowards(currentUnit.getCurrentPosition(), newUnitPos, currentUnit.getAp());
		battlemanager.getBattleState().moveUnitTo(currentUnit, newUnitPos);
		currentUnit.move(path);
	}

	@Override
	public void keyPressed(int key) {
		switch (key) {
		case Keys.Q:
			break;
		default:
			break;
		}
	}

	@Override
	public void buttonPressed(int button) {
		switch (button) {
		case Buttons.RIGHT:
			ParticleMaker.deactivateAllParticlesOfType(ParticleType.MOVE);
			exit();
			break;
		case Buttons.LEFT:
			break;
		case Buttons.MIDDLE:
			break;
		default:
			break;
		}
	}
}
