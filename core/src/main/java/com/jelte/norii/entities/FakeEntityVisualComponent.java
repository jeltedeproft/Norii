package com.jelte.norii.entities;

import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.jelte.norii.entities.EntityAnimation.Direction;
import com.jelte.norii.ui.HpBar;
import com.jelte.norii.utility.MyPoint;
import com.jelte.norii.utility.TiledMapPosition;

public class FakeEntityVisualComponent implements EntityVisualComponentInterface {

	@Override
	public void update(float delta) {
		// nothing to do

	}

	@Override
	public void dispose() {
		// nothing to do

	}

	@Override
	public void setAnimationType(EntityAnimationType type) {
		// nothing to do

	}

	@Override
	public void removeUnit() {
		// nothing to do

	}

	@Override
	public void move(List<GridCell> path) {
		// nothing to do

	}

	@Override
	public void moveAttack(List<GridCell> path, Entity target) {
		// nothing to do

	}

	@Override
	public void draw(Batch batch) {
		// nothing to do

	}

	@Override
	public void setActive(boolean b) {
		// nothing to do

	}

	@Override
	public void setDirection(Direction up) {
		// nothing to do

	}

	@Override
	public void setInDeploymentPhase(boolean b) {
		// nothing to do

	}

	@Override
	public void initiateInBattle(TiledMapPosition pos) {
		// nothing to do
	}

	@Override
	public void changeAnimation(EntityAnimation entityAnimation) {
		// nothing to do
	}

	@Override
	public void setEntityactor(EntityActor entityActor) {
		// nothing to do
	}

	@Override
	public void restoreAnimation() {
		// nothing to do
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
		// nothing to do
	}

	@Override
	public boolean isHovering() {
		return false;
	}

	@Override
	public EntityActor getEntityactor() {
		// nothing to do
		return null;
	}

	@Override
	public void spawn(TiledMapPosition tiledMapPosition) {
		// nothing to do

	}

	@Override
	public void setVisualPosition(TiledMapPosition actorPos) {
		// nothing to do

	}

	@Override
	public void updateBattleState(TiledMapPosition oldPosition) {
		// nothing to do

	}

	@Override
	public void pushTo(MyPoint casterNewPosition) {
		// nothing to do

	}

	@Override
	public HpBar getHpBar() {
		// TODO Auto-generated method stub
		return null;
	}

}
