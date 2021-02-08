package com.jelte.norii.entities;

import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.jelte.norii.entities.EntityAnimation.Direction;

public class FakeEntityVisualComponent implements EntityVisualComponentInterface {

	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPos() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAnimationType(EntityAnimationType type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeUnit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(List<GridCell> path) {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveAttack(List<GridCell> path, Entity target) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Batch batch) {
		// TODO Auto-generated method stub

	}

	@Override
	public void spawn() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setActive(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDirection(Direction up) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLocked(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setInDeploymentPhase(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initiateInBattle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeAnimation(EntityAnimation entityAnimation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEntityactor(EntityActor entityActor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void restoreAnimation() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isActionsHovering() {
		return false;
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public void setActionsHovering(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isHovering() {
		return false;
	}

}
