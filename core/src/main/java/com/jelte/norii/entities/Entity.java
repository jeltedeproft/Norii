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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.jelte.norii.audio.AudioCommand;
import com.jelte.norii.audio.AudioManager;
import com.jelte.norii.audio.AudioTypeEvent;
import com.jelte.norii.battle.MessageToBattleScreen;
import com.jelte.norii.entities.EntityAnimation.Direction;
import com.jelte.norii.magic.AbilitiesEnum;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Modifier;
import com.jelte.norii.magic.ModifiersEnum;
import com.jelte.norii.utility.AssetManagerUtility;
import com.jelte.norii.utility.MyPoint;
import com.jelte.norii.utility.TiledMapPosition;

public class Entity extends Actor {
	public static final int MAX_XP = 100;
	protected final EntityData entityData;

	protected int ap;
	protected int hp;
	protected int xp;

	protected int basicAttackCost;

	protected boolean inBattle;
	protected boolean isInAttackPhase;
	protected boolean isDead;
	protected boolean isPlayerUnit;
	protected boolean isActive;
	protected boolean locked;
	protected boolean statsChanged;
	protected int entityID;

	protected TiledMapPosition oldPlayerPosition;
	protected TiledMapPosition currentPlayerPosition;
	protected Direction direction;

	protected EntityAnimation entityAnimation;
	protected EntityAnimation entityTemporaryAnimation;
	protected EntityActor entityactor;
	protected EntityTypes entityType;

	protected Collection<Ability> abilities;
	protected Collection<Modifier> modifiers;

	private Runnable updatePositionAction;
	private Runnable stopWalkAction;
	private Runnable cleanup;

	private UnitOwner owner;

	public Entity(final EntityTypes type, UnitOwner owner) {
		entityData = EntityFileReader.getUnitData().get(type.ordinal());
		entityType = type;
		entityData.setEntity(this);
		entityAnimation = new EntityAnimation(entityData.getEntitySpriteName());
		initEntity(owner);
	}

	public void initEntity(UnitOwner owner) {
		oldPlayerPosition = new TiledMapPosition().setPositionFromScreen(-1000, -1000);
		currentPlayerPosition = new TiledMapPosition().setPositionFromScreen(-1000, -1000);
		hp = entityData.getMaxHP();
		ap = entityData.getMaxAP();
		xp = 0;
		isDead = false;
		inBattle = false;
		isInAttackPhase = false;
		statsChanged = true;
		this.owner = owner;
		this.isPlayerUnit = owner.isPlayer();
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
	}

	public void update(final float delta) {
		entityAnimation.update(delta);
	}

	public EntityData getEntityData() {
		return entityData;
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
		owner.sendMessageToBattleManager(MessageToBattleScreen.UPDATE_POS, this);
	}

	public void setCurrentPositionFromScreen(int x, int y) {
		if ((x != currentPlayerPosition.getTileX()) || (y != currentPlayerPosition.getTileY())) {
			setCurrentPosition(new TiledMapPosition().setPositionFromScreen(x, y));
		}
	}

	public void setActive(final boolean isActive) {
		this.isActive = isActive;
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
			hp = 0;
			removeUnit();
		} else {
			hp = hp - damage;
		}
	}

	public void heal(final int healAmount) {
		hp = hp + healAmount;
		if (hp > entityData.getMaxHP()) {
			hp = entityData.getMaxHP();
		}
	}

	private void removeUnit() {
		getEntityAnimation().setCurrentAnimationType(EntityAnimationType.WALK);
		final SequenceAction sequence = Actions.sequence();
		sequence.addAction(Actions.fadeOut(1));
		sequence.addAction(run(cleanup));
		getEntityactor().addAction(sequence);
		owner.sendMessageToBattleManager(MessageToBattleScreen.REMOVE_HUD_UNIT, this);
	}

	private void cleanUpDeadUnit() {
		hp = 0;
		isDead = true;
		inBattle = false;
		isActive = false;
		getEntityactor().setPosition(-100, -100);
		getEntityactor().remove();
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
			setInBattle(true);
			entityactor.setTouchable(Touchable.disabled);
			owner.sendMessageToBattleManager(MessageToBattleScreen.SET_CHARACTER_HUD, this);
		}
	}

	public void setFocused(final boolean isFocused) {
		if (isFocused) {
			owner.sendMessageToBattleManager(MessageToBattleScreen.SET_CHARACTER_HUD, this);
		} else {
			owner.sendMessageToBattleManager(MessageToBattleScreen.UNSET_CHARACTER_HUD, this);
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

	public UnitOwner getOwner() {
		return owner;
	}

	public int getAp() {
		return ap;
	}

	public void setAp(final int ap) {
		this.ap = ap;
	}

	public int getHp() {
		return hp;
	}

	public int getXp() {
		return xp;
	}

	public void setXp(int xp) {
		this.xp = xp;
	}

	public int getEntityID() {
		return entityID;
	}

	public int getAttackRange() {
		return entityData.getAttackRange();
	}

	public EntityTypes getEntityType() {
		return entityType;
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

	public void addAbility(final AbilitiesEnum abilityEnum, MyPoint location) {
		final Ability ability = new Ability(abilityEnum, location);
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

	public Collection<Modifier> getModifiers() {
		return modifiers;
	}

	public Collection<Ability> getAbilities() {
		return abilities;
	}

	public boolean isStatsChanged() {
		return statsChanged;
	}

	public void setStatsChanged(boolean statsChanged) {
		this.statsChanged = statsChanged;
	}

	public void move(List<GridCell> path) {
		final SequenceAction sequence = createMoveSequence(path);
		getEntityactor().addAction(sequence);
		setAp(getAp() - path.size());
	}

	public void moveAttack(List<GridCell> path, Entity target) {
		final SequenceAction sequence = createMoveSequence(path);
		sequence.addAction(new AttackAction(this, target));

		getEntityactor().addAction(sequence);
		setAp(getAp() - path.size() - getEntityData().getBasicAttackCost());
	}

	public void endTurn() {
		setAp(getEntityData().getMaxAP());
		owner.sendMessageToBattleManager(MessageToBattleScreen.AI_FINISHED_TURN, this);
	}

	private SequenceAction createMoveSequence(List<GridCell> path) {
		getEntityactor().setOrigin(getEntityactor().getWidth() / 2, getEntityactor().getHeight() / 2);
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_PLAY_LOOP, AudioTypeEvent.WALK_LOOP);
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
		setCurrentPosition(new TiledMapPosition().setPositionFromTiles((int) this.getEntityactor().getX(), (int) this.getEntityactor().getY()));
		setDirection(decideDirection(this.getEntityactor().getRotation()));
	}

	private void stopWalkingAction() {
		AudioManager.getInstance().onNotify(AudioCommand.SOUND_STOP, AudioTypeEvent.WALK_LOOP);
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
		if (isPlayerUnit) {
			return "PLAYER: name : " + entityData.getName() + "   ID:" + entityID + "   pos : (" + currentPlayerPosition.getTileX() + "," + currentPlayerPosition.getTileY() + ")";
		} else {
			return "AI: name : " + entityData.getName() + "   ID:" + entityID + "   pos : (" + currentPlayerPosition.getTileX() + "," + currentPlayerPosition.getTileY() + ")";

		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + entityID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Entity other = (Entity) obj;
		if (entityID != other.entityID)
			return false;
		return true;
	}
}
