package com.jelte.norii.entities;

import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.jelte.norii.entities.EntityAnimation.Direction;

public interface EntityVisualComponentInterface {

	public void update(final float delta);

	public void dispose();

	public void setPos();

	public void setAnimationType(EntityAnimationType type);

	public void removeUnit();

	public void move(List<GridCell> path);

	public void moveAttack(List<GridCell> path, Entity target);

	public void draw(Batch batch);

	public void spawn();

	public void setActive(boolean b);

	public void setDirection(Direction up);

	public void setLocked(boolean b);

	public void setInDeploymentPhase(boolean b);

	public void initiateInBattle();

	public void changeAnimation(EntityAnimation entityAnimation);

	public void setEntityactor(EntityActor entityActor);

	public void restoreAnimation();

	public boolean isActionsHovering();

	public boolean isActive();

	public void setActionsHovering(boolean b);;

	public boolean isHovering();

}
