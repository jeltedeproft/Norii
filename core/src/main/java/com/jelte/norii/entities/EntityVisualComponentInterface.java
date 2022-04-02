package com.jelte.norii.entities;

import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.jelte.norii.entities.EntityAnimation.Direction;
import com.jelte.norii.ui.HpBar;
import com.jelte.norii.utility.MyPoint;
import com.jelte.norii.utility.TiledMapPosition;

public interface EntityVisualComponentInterface {

	public void initiateInBattle(TiledMapPosition pos);

	public void update(final float delta);

	public void move(List<GridCell> path);

	public void moveAttack(List<GridCell> path, Entity target);

	public void draw(Batch batch);

	public void spawn(TiledMapPosition tiledMapPosition);

	public void removeUnit();

	public void dispose();

	public void setAnimationType(EntityAnimationType type);

	public void setActive(boolean b);

	public void setDirection(Direction up);

	public void setInDeploymentPhase(boolean b);

	public void changeAnimation(EntityAnimation entityAnimation);

	public void setEntityactor(EntityActor entityActor);

	public void restoreAnimation();

	public boolean isActionsHovering();

	public boolean isActive();

	public void setActionsHovering(boolean b);;

	public boolean isHovering();

	public EntityActor getEntityactor();

	public void setVisualPosition(TiledMapPosition actorPos);

	public void updateBattleState(TiledMapPosition oldPlayerPosition);

	public void pushTo(MyPoint casterNewPosition);

	public HpBar getHpBar();

}
