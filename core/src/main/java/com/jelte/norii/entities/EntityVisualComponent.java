package com.jelte.norii.entities;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.jelte.norii.audio.AudioCommand;
import com.jelte.norii.audio.AudioManager;
import com.jelte.norii.audio.AudioTypeEvent;
import com.jelte.norii.battle.MessageToBattleScreen;
import com.jelte.norii.entities.EntityAnimation.Direction;
import com.jelte.norii.utility.AssetManagerUtility;
import com.jelte.norii.utility.MyPoint;
import com.jelte.norii.utility.TiledMapPosition;

public class EntityVisualComponent implements EntityVisualComponentInterface {
	protected boolean isInAttackPhase;
	protected boolean isActive;
	protected boolean inBattle;
	protected boolean locked;
	private boolean isMoving;

	protected EntityAnimation entityAnimation;
	protected EntityAnimation entityTemporaryAnimation;
	protected EntityActor entityactor;

	private Runnable updatePositionAction;
	private Runnable stopWalkAction;
	private Runnable cleanup;

	private final Entity entity;

	public EntityVisualComponent(Entity entity) {
		this.entity = entity;
		isInAttackPhase = false;
		locked = false;
		inBattle = false;
		entityAnimation = new EntityAnimation(entity.getEntityData().getEntitySpriteName());
		initActions();
	}

	private void initActions() {
		updatePositionAction = this::updatePositionFromActor;
		stopWalkAction = this::stopWalkingAction;
		cleanup = this::cleanUpDeadUnit;
	}

	@Override
	public void update(final float delta) {
		entityAnimation.update(delta);
	}

	@Override
	public void dispose() {
		AssetManagerUtility.unloadAsset(entityAnimation.getSpriteName());
	}

	@Override
	public EntityActor getEntityactor() {
		return entityactor;
	}

	@Override
	public void setEntityactor(final EntityActor entityactor) {
		this.entityactor = entityactor;
	}

	@Override
	public void removeUnit() {
		setAnimationType(EntityAnimationType.WALK);
		final SequenceAction sequence = Actions.sequence();
		sequence.addAction(Actions.fadeOut(1));
		sequence.addAction(run(cleanup));
		getEntityactor().addAction(sequence);
		entity.getOwner().sendMessageToBattleManager(MessageToBattleScreen.REMOVE_HUD_UNIT, entity);
		entity.getOwner().sendMessageToBattleManager(MessageToBattleScreen.UNIT_DIED, entity);
	}

	public void cleanUpDeadUnit() {
		isActive = false;
		inBattle = false;
		getEntityactor().setPosition(-100, -100);
		getEntityactor().remove();
	}

	@Override
	public void setActive(final boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	public boolean isInAttackPhase() {
		return isInAttackPhase;
	}

	public boolean isLocked() {
		return locked;
	}

	@Override
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isInBattle() {
		return inBattle;
	}

	public void setInBattle(final boolean inBattle) {
		this.inBattle = inBattle;
	}

	@Override
	public void setInDeploymentPhase(final boolean isInDeploymentPhase) {
		if (isInDeploymentPhase) {
			setInBattle(true);
			entityactor.setTouchable(Touchable.disabled);
			entity.getOwner().sendMessageToBattleManager(MessageToBattleScreen.SET_CHARACTER_HUD, entity);
		}
	}

	public TextureRegion getFrame() {
		return entityAnimation.getFrame();
	}

	@Override
	public void setDirection(final Direction direction) {
		entityAnimation.setDirection(direction);
	}

	@Override
	public void initiateInBattle(TiledMapPosition pos) {
		setInBattle(true);
		getEntityactor().setTouchable(Touchable.enabled);
		spawn(pos);
	}

	public EntityAnimation getEntityAnimation() {
		return entityAnimation;
	}

	@Override
	public void changeAnimation(final EntityAnimation tempAnimation) {
		entityTemporaryAnimation = entityAnimation;
		entityAnimation = tempAnimation;
	}

	@Override
	public void restoreAnimation() {
		entityAnimation = entityTemporaryAnimation;
	}

	private float decideRotation(GridCell oldCell, GridCell cell) {
		if ((oldCell.x == cell.x) && (oldCell.y > cell.y)) {
			return 0.0f;
		} else if ((oldCell.x == cell.x) && (oldCell.y < cell.y)) {
			return 180.0f;
		} else if ((oldCell.x > cell.x) && (oldCell.y == cell.y)) {
			return 270.0f;
		}
		return 90.0f;
	}

	private void updatePositionFromActor() {
		setDirection(decideDirection(this.getEntityactor().getRotation()));
	}

	private void stopWalkingAction() {
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_STOP, AudioTypeEvent.WALK_LOOP);
		setAnimationType(EntityAnimationType.WALK);
		isMoving = false;
		entity.getOwner().sendMessageToBattleManager(MessageToBattleScreen.ACTION_COMPLETED, entity);
	}

	@Override
	public void setAnimationType(EntityAnimationType type) {
		getEntityAnimation().setCurrentAnimationType(type);
	}

	private Direction decideDirection(float rotation) {
		if ((rotation >= 45) && (rotation < 135)) {
			return Direction.RIGHT;
		} else if ((rotation >= 135) && (rotation < 225)) {
			return Direction.UP;
		} else if ((rotation >= 225) && (rotation < 315)) {
			return Direction.LEFT;
		}
		return Direction.DOWN;
	}

	@Override
	public void move(List<GridCell> path) {
		if (!path.isEmpty()) {
			isMoving = true;
			final SequenceAction sequence = createMoveSequence(path);
			getEntityactor().addAction(sequence);
		}
	}

	@Override
	public void moveAttack(List<GridCell> path, Entity target) {
		if (!path.isEmpty()) {
			isMoving = true;
			final SequenceAction sequence = createMoveSequence(path);
			sequence.addAction(new AttackAction(entity, target));
			getEntityactor().addAction(sequence);
		} else {
			getEntityactor().addAction(new AttackAction(entity, target));
		}
	}

	public SequenceAction createMoveSequence(List<GridCell> path) {
		getEntityactor().setOrigin(getEntityactor().getWidth() / 2, getEntityactor().getHeight() / 2);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_PLAY_LOOP, AudioTypeEvent.WALK_LOOP);
		setAnimationType(EntityAnimationType.WALK);
		GridCell oldCell = new GridCell(entity.getCurrentPosition().getTileX(), entity.getCurrentPosition().getTileY());
		final SequenceAction sequence = Actions.sequence();
		for (final GridCell cell : path) {
			sequence.addAction(Actions.rotateTo(decideRotation(oldCell, cell), 0.05f, Interpolation.swingIn));
			sequence.addAction(run(updatePositionAction));
			sequence.addAction(moveTo(cell.getX(), cell.getY(), 0.05f));
			oldCell = cell;
		}
		sequence.addAction(run(stopWalkAction));
		return sequence;
	}

	@Override
	public void draw(Batch batch) {
		if (isInBattle()) {
			final Color temp = batch.getColor();
			batch.setColor(new Color(temp.r, temp.g, temp.b, getEntityactor().getColor().a));
			batch.draw(getFrame(), getEntityactor().getX(), getEntityactor().getY(), 1.0f, 1.0f);
			batch.setColor(temp);
		}

		if (isLocked()) {
			batch.draw(AssetManagerUtility.getSprite("lock"), getEntityactor().getX(), getEntityactor().getY() + 1.0f, 1.0f, 1.0f);
		}
	}

	@Override
	public void spawn(TiledMapPosition pos) {
		setInBattle(true);
		getEntityactor().setBounds(pos.getTileX(), pos.getTileY(), 1, 1);
	}

	@Override
	public boolean isActionsHovering() {
		return entityactor.isActionsHovering();
	}

	@Override
	public void setActionsHovering(boolean b) {
		entityactor.setActionsHovering(b);
	}

	@Override
	public boolean isHovering() {
		return entityactor.getIsHovering();
	}

	@Override
	public void setVisualPosition(TiledMapPosition pos) {
		getEntityactor().setBounds(pos.getTileX(), pos.getTileY(), 1, 1);
	}

	@Override
	public void updateBattleState(TiledMapPosition newPosition) {
		entity.getOwner().sendMessageToBattleManager(MessageToBattleScreen.UPDATE_POS, entity, newPosition);
	}

	@Override
	public void pushTo(MyPoint casterNewPosition) {
		getEntityactor().setOrigin(getEntityactor().getWidth() / 2, getEntityactor().getHeight() / 2);
		final SequenceAction sequence = Actions.sequence();
		sequence.addAction(run(updatePositionAction));
		sequence.addAction(moveTo(casterNewPosition.x, casterNewPosition.y, 0.05f));
		getEntityactor().addAction(sequence);
	}

}
