package com.jelte.norii.entities;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

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
import com.jelte.norii.utility.TiledMapPosition;

public class EntityVisualComponent implements EntityVisualComponentInterface {
	protected boolean isInAttackPhase;
	protected boolean isActive;
	protected boolean inBattle;
	protected boolean locked;
	protected boolean statsChanged;

	protected EntityAnimation entityAnimation;// weg
	protected EntityAnimation entityTemporaryAnimation;// weg
	protected EntityActor entityactor;// weg

	private Runnable updatePositionAction;// weg
	private Runnable stopWalkAction;// weg
	private Runnable cleanup;// weg

	private Entity entity;

	public EntityVisualComponent(Entity entity) {
		this.entity = entity;
		isInAttackPhase = false;
		statsChanged = true;
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
	public void setPos() {
		entityactor.setPos();
	}

	public EntityActor getEntityactor() {
		return entityactor;
	}

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

	public boolean isStatsChanged() {
		return statsChanged;
	}

	public void setStatsChanged(boolean statsChanged) {
		this.statsChanged = statsChanged;
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

	public void initiateInBattle() {
		setInBattle(true);
		getEntityactor().setTouchable(Touchable.enabled);
	}

	public EntityAnimation getEntityAnimation() {
		return entityAnimation;
	}

	public void changeAnimation(final EntityAnimation tempAnimation) {
		entityTemporaryAnimation = entityAnimation;
		entityAnimation = tempAnimation;
	}

	public void restoreAnimation() {
		entityAnimation = entityTemporaryAnimation;
	}

	public SequenceAction createMoveSequence(List<GridCell> path) {
		getEntityactor().setOrigin(getEntityactor().getWidth() / 2, getEntityactor().getHeight() / 2);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_PLAY_LOOP, AudioTypeEvent.WALK_LOOP);
		setAnimationType(EntityAnimationType.WALK);
		GridCell oldCell = new GridCell(entity.getCurrentPosition().getTileX(), entity.getCurrentPosition().getTileY());
		final SequenceAction sequence = Actions.sequence();
		for (final GridCell cell : path) {
			sequence.addAction(Actions.rotateTo(decideRotation(oldCell, cell), 0.05f, Interpolation.swingIn));
			sequence.addAction(moveTo(cell.getX(), cell.getY(), 0.05f));
			sequence.addAction(run(updatePositionAction));
			oldCell = cell;
		}
		sequence.addAction(run(stopWalkAction));
		return sequence;
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
		entity.setCurrentPosition(new TiledMapPosition().setPositionFromTiles((int) this.getEntityactor().getX(), (int) this.getEntityactor().getY()));
		setDirection(decideDirection(this.getEntityactor().getRotation()));
	}

	private void stopWalkingAction() {
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_STOP, AudioTypeEvent.WALK_LOOP);
		setAnimationType(EntityAnimationType.WALK);
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
			final SequenceAction sequence = createMoveSequence(path);
			getEntityactor().addAction(sequence);
		}
	}

	@Override
	public void moveAttack(List<GridCell> path, Entity target) {
		final SequenceAction sequence = createMoveSequence(path);
		sequence.addAction(new AttackAction(entity, target));
		getEntityactor().addAction(sequence);
	}

	@Override
	public void draw(Batch batch) {
		if (isInBattle()) {
			batch.draw(getFrame(), entity.getCurrentPosition().getTileX(), entity.getCurrentPosition().getTileY() - 0.0f, 1.0f, 1.0f);
		}
	}

	@Override
	public void spawn() {
		setInBattle(true);
	}

}
