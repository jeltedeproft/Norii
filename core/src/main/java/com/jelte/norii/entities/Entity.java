package com.jelte.norii.entities;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;
import com.jelte.norii.audio.AudioManager;
import com.jelte.norii.audio.AudioObserver;
import com.jelte.norii.audio.AudioSubject;
import com.jelte.norii.entities.EntityAnimation.Direction;
import com.jelte.norii.entities.EntityObserver.EntityCommand;
import com.jelte.norii.magic.AbilitiesEnum;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Modifier;
import com.jelte.norii.magic.ModifiersEnum;
import com.jelte.norii.testUI.PortraitAndStats;
import com.jelte.norii.ui.StatusUI;
import com.jelte.norii.utility.AssetManagerUtility;
import com.jelte.norii.utility.TiledMapPosition;

public class Entity extends Actor implements EntitySubject, AudioSubject {
	protected final EntityData entityData;

	protected int ap;
	protected int hp;

	protected int basicAttackCost;

	protected boolean inBattle;
	protected boolean isInAttackPhase;
	protected boolean isDead;
	protected boolean isPlayerUnit;
	protected boolean isActive;
	protected boolean locked;
	protected int entityID;

	protected TiledMapPosition oldPlayerPosition;
	protected TiledMapPosition currentPlayerPosition;
	protected Direction direction;

	private StatusUI statusui;
	private PortraitAndStats characterHUD;

	protected EntityAnimation entityAnimation;
	protected EntityAnimation entityTemporaryAnimation;
	protected EntityActor entityactor;

	protected Array<EntityObserver> entityObservers;
	protected Array<AudioObserver> audioObservers;
	protected Collection<Ability> abilities;
	protected Collection<Modifier> modifiers;

	private Runnable updatePositionAction;
	private Runnable aiFinishTurn;
	private Runnable stopWalkAction;
	private Runnable cleanup;

	public Entity(final EntityTypes type) {
		entityData = EntityFileReader.getUnitData().get(type.ordinal());
		entityData.setEntity(this);
		entityAnimation = new EntityAnimation(entityData.getEntitySpriteName());
		initEntity();
	}

	public void initEntity() {
		entityObservers = new Array<>();
		audioObservers = new Array<>();
		this.addAudioObserver(AudioManager.getInstance());
		oldPlayerPosition = new TiledMapPosition().setPositionFromScreen(-1000, -1000);
		currentPlayerPosition = new TiledMapPosition().setPositionFromScreen(-1000, -1000);
		hp = entityData.getMaxHP();
		ap = entityData.getMaxAP();
		isDead = false;
		inBattle = false;
		isInAttackPhase = false;
		isPlayerUnit = true;
		locked = false;
		abilities = new ArrayList<>();
		modifiers = new ArrayList<>();
		entityID = java.lang.System.identityHashCode(this);
		initAbilities();
		initActions();
	}

	private void initAbilities() {
		for (final String abilityString : entityData.getAbilties()) {
			addAbility(AbilitiesEnum.valueOf(abilityString));
		}
	}

	private void initActions() {
		updatePositionAction = this::updatePositionFromActor;
		stopWalkAction = this::stopWalkingAction;
		cleanup = this::cleanUpDeadUnit;
		aiFinishTurn = () -> notifyEntityObserver(EntityCommand.AI_FINISHED_TURN);
	}

	public void update(final float delta) {
		entityAnimation.update(delta);
	}

	public EntityData getEntityData() {
		return entityData;
	}

	public StatusUI getStatusui() {
		return statusui;
	}

	public void setStatusui(final StatusUI statusui) {
		this.statusui = statusui;
	}

	public void setbottomMenu(final PortraitAndStats portraitAndStats) {
		this.characterHUD = portraitAndStats;
	}

	public void dispose() {
		AssetManagerUtility.unloadAsset(entityAnimation.getSpriteName());
	}

	public TiledMapPosition getCurrentPosition() {
		return currentPlayerPosition;
	}

	public TiledMapPosition getOldPlayerPosition() {
		return oldPlayerPosition;
	}

	public void setOldPlayerPosition(final TiledMapPosition oldPlayerPosition) {
		this.oldPlayerPosition = oldPlayerPosition;
	}

	public void setCurrentPosition(final TiledMapPosition pos) {
		currentPlayerPosition = pos;
		entityactor.setPos();

		updateUI();
	}

	public void updateUI() {
		statusui.update();
		characterHUD.update();
	}

	public EntityActor getEntityactor() {
		return entityactor;
	}

	public void setEntityactor(final EntityActor entityactor) {
		this.entityactor = entityactor;
	}

	public boolean isDead() {
		return isDead;
	}

	public boolean isActive() {
		return isActive;
	}

	public boolean isInAttackPhase() {
		return isInAttackPhase;
	}

	public void attack(final Entity target) {
		getEntityAnimation().setCurrentAnimationType(EntityAnimationType.WALK);
		target.damage(entityData.getAttackPower());
	}

	public boolean canAttack() {
		return ap > basicAttackCost;
	}

	public void damage(final int damage) {
		if (damage >= hp) {
			removeUnit();
		} else {
			hp = hp - damage;
		}

		updateUI();
	}

	private void removeUnit() {
		this.getEntityAnimation().setCurrentAnimationType(EntityAnimationType.WALK);
		final SequenceAction sequence = Actions.sequence();
		sequence.addAction(Actions.fadeOut(1));
		sequence.addAction(run(cleanup));
		this.getEntityactor().addAction(sequence);
	}

	private void cleanUpDeadUnit() {
		hp = 0;
		isDead = true;
		inBattle = false;
		isActive = false;
		getEntityactor().setPosition(-100, -100);
		setVisible(false);
	}

	public boolean canMove() {
		return ap > 0;
	}

	public boolean isInBattle() {
		return inBattle;
	}

	public void setInBattle(final boolean inBattle) {
		this.inBattle = inBattle;
	}

	public void setInDeploymentPhase(final boolean isInDeploymentPhase) {
		if (isInDeploymentPhase) {
			characterHUD.setHero(this);
			notifyEntityObserver(EntityCommand.UNIT_ACTIVE);
		}
	}

	public void setFocused(final boolean isFocused) {
		if (isFocused) {
			characterHUD.setHero(this);
		} else {
			characterHUD.setHero(null);
		}
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isPlayerUnit() {
		return isPlayerUnit;
	}

	public void setPlayerUnit(boolean isPlayerUnit) {
		this.isPlayerUnit = isPlayerUnit;
	}

	public int getAp() {
		return ap;
	}

	public void setAp(final int ap) {
		this.ap = ap;
		updateUI();
	}

	public int getHp() {
		return hp;
	}

	public void setHp(final int hp) {
		this.hp = hp;
		updateUI();
	}

	public int getEntityID() {
		return entityID;
	}

	public TextureRegion getFrame() {
		return entityAnimation.getFrame();
	}

	public void setDirection(final Direction direction) {
		entityAnimation.setDirection(direction);
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

	public void addAbility(final AbilitiesEnum abilityEnum) {
		final Ability ability = new Ability(abilityEnum);
		abilities.add(ability);
	}

	public void removeAbility(final AbilitiesEnum abilityEnum) {
		abilities.removeIf(ability -> (ability.getId() == abilityEnum.ordinal()));
	}

	public void addModifier(final ModifiersEnum type, final int turns, final int amount) {
		final Modifier modifier = new Modifier(type, turns, amount);
		modifiers.add(modifier);
	}

	public boolean hasModifier(final ModifiersEnum type) {
		boolean result = false;
		for (final Modifier modifier : modifiers) {
			if (modifier.getType() == type) {
				result = true;
			}
		}
		return result;
	}

	public void applyModifiers() {
		modifiers.forEach(this::applymod);
		modifiers.removeIf(mod -> mod.getTurns() == 0);
	}

	private void applymod(Modifier mod) {
		mod.applyModifier(this);

		if (mod.getTurns() == 0) {
			mod.removeModifier(this);
		}
	}

	public Collection<Ability> getAbilities() {
		return abilities;
	}

	@Override
	public void addEntityObserver(final EntityObserver entityObserver) {
		entityObservers.add(entityObserver);
	}

	@Override
	public void removeObserver(final EntityObserver entityObserver) {
		entityObservers.removeValue(entityObserver, true);
	}

	@Override
	public void removeAllObservers() {
		entityObservers.removeAll(entityObservers, true);
	}

	@Override
	public void notifyEntityObserver(final EntityObserver.EntityCommand command) {
		for (int i = 0; i < entityObservers.size; i++) {
			entityObservers.get(i).onEntityNotify(command, this);
		}
	}

	public void notifyEntityObserver(final EntityObserver.EntityCommand command, final Ability ability) {
		for (int i = 0; i < entityObservers.size; i++) {
			entityObservers.get(i).onEntityNotify(command, this, ability);
		}
	}

	public void move(List<GridCell> path) {
		final SequenceAction sequence = createMoveSequence(path);
		sequence.addAction(run(aiFinishTurn));
		getEntityactor().addAction(sequence);
		setAp(getAp() - path.size());
	}

	public void moveAttack(List<GridCell> path, Entity target) {
		final SequenceAction sequence = createMoveSequence(path);
		sequence.addAction(new AttackAction(this, target));
		sequence.addAction(run(aiFinishTurn));

		getEntityactor().addAction(sequence);
		setAp(getAp() - path.size() - getEntityData().getBasicAttackCost());
	}

	private SequenceAction createMoveSequence(List<GridCell> path) {
		getEntityactor().setOrigin(getEntityactor().getWidth() / 2, getEntityactor().getHeight() / 2);
		notifyAudio(AudioObserver.AudioCommand.SOUND_PLAY_LOOP, AudioObserver.AudioTypeEvent.WALK_LOOP);
		getEntityAnimation().setCurrentAnimationType(EntityAnimationType.WALK);
		GridCell oldCell = new GridCell(getCurrentPosition().getTileX(), getCurrentPosition().getTileY());
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
		this.setCurrentPosition(new TiledMapPosition().setPositionFromTiles((int) this.getEntityactor().getX(), (int) this.getEntityactor().getY()));
		this.setDirection(decideDirection(this.getEntityactor().getRotation()));
	}

	private void stopWalkingAction() {
		notifyAudio(AudioObserver.AudioCommand.SOUND_STOP, AudioObserver.AudioTypeEvent.WALK_LOOP);
		this.getEntityAnimation().setCurrentAnimationType(EntityAnimationType.WALK);
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
	public String toString() {
		return "name : " + entityData.getName() + "   ID:" + entityID;
	}

	@Override
	public void addAudioObserver(AudioObserver audioObserver) {
		audioObservers.add(audioObserver);
	}

	@Override
	public void removeAudioObserver(AudioObserver audioObserver) {
		audioObservers.removeValue(audioObserver, true);
	}

	@Override
	public void removeAllAudioObservers() {
		audioObservers.removeAll(audioObservers, true);
	}

	@Override
	public void notifyAudio(AudioObserver.AudioCommand command, AudioObserver.AudioTypeEvent event) {
		for (final AudioObserver observer : audioObservers) {
			observer.onNotify(command, event);
		}
	}
}
