package com.jelte.norii.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.jelte.norii.battle.MessageToBattleScreen;
import com.jelte.norii.entities.EntityAnimation.Direction;
import com.jelte.norii.magic.AbilitiesEnum;
import com.jelte.norii.magic.Ability;
import com.jelte.norii.magic.Ability.DamageType;
import com.jelte.norii.magic.Modifier;
import com.jelte.norii.magic.ModifiersEnum;
import com.jelte.norii.utility.MyPoint;
import com.jelte.norii.utility.TiledMapPosition;

public class Entity extends Actor {
	public static final int MAX_XP = 100;
	private static final Random random = new Random();

	protected final EntityData entityData;

	protected int hp;
	protected int xp;
	protected int magicalDefense;
	protected int physicalDefense;
	protected boolean statsChanged;

	protected int basicAttackCost;

	protected boolean isPlayerUnit;
	protected boolean isDead;
	protected boolean isInvis;
	protected boolean isReal;
	protected boolean inBattle;

	protected int entityID;

	protected TiledMapPosition oldPlayerPosition;
	protected TiledMapPosition currentPlayerPosition;
	protected Direction direction;

	protected EntityTypes entityType;

	protected Ability ability;
	protected Collection<Modifier> modifiers;
	protected EntityVisualComponentInterface visualComponent;

	private UnitOwner owner;

	public Entity(final EntityTypes type, UnitOwner owner, boolean isReal) {
		entityData = EntityFileReader.getUnitData().get(type.ordinal());
		entityType = type;
		entityData.setEntity(this);
		initEntity(owner, isReal);
	}

	public void initEntity(UnitOwner owner, boolean isReal) {
		oldPlayerPosition = new TiledMapPosition().setPositionFromScreen(-1000, -1000);
		currentPlayerPosition = new TiledMapPosition().setPositionFromScreen(-1000, -1000);
		hp = entityData.getMaxHP();
		isDead = false;
		statsChanged = true;
		isInvis = false;
		direction = Direction.DOWN;
		this.isReal = isReal;
		inBattle = false;
		xp = 0;

		this.owner = owner;
		this.isPlayerUnit = owner.isPlayer();

		modifiers = new ArrayList<>();
		entityID = java.lang.System.identityHashCode(this);
		initAbilities();
		if (isReal) {
			visualComponent = new EntityVisualComponent(this);
		} else {
			visualComponent = new FakeEntityVisualComponent();
		}
	}

	private void initAbilities() {
		setAbility(AbilitiesEnum.valueOf(entityData.getAbility()));
	}

	public void update(final float delta) {
		visualComponent.update(delta);
	}

	public EntityData getEntityData() {
		return entityData;
	}

	public void dispose() {
		visualComponent.dispose();
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
		visualComponent.updateBattleState(pos);
		currentPlayerPosition = pos;
		visualComponent.setVisualPosition(pos);
	}

	public void setOnlyCurrentPosition(final TiledMapPosition pos) {
		currentPlayerPosition = pos;
	}

	public void setCurrentPositionFromScreen(int x, int y) {
		if ((x != currentPlayerPosition.getTileX()) || (y != currentPlayerPosition.getTileY())) {
			setCurrentPosition(new TiledMapPosition().setPositionFromScreen(x, y));
		}
	}

	public void attack(final Entity target, DamageType type) {
		visualComponent.setAnimationType(EntityAnimationType.WALK);
		target.damage(entityData.getAttackPower(), type);
	}

	public boolean canAttack() {
		return isPlayerUnit && (owner.getAp() > basicAttackCost);
	}

	public boolean canCastSpell(Ability ability) {
		return isPlayerUnit && (owner.getAp() >= ability.getSpellData().getApCost());
	}

	public void damage(int damage, DamageType type) {
		owner.sendMessageToBattleManager(MessageToBattleScreen.DAMAGED, this, damage);
		if (hasModifier(ModifiersEnum.LINKED)) {
			damage = (int) (damage * 0.5);
		}
		final int reducedDamage = calculateDamage(damage, type);
		if (reducedDamage >= hp) {
			hp = 0;
			visualComponent.removeUnit();
			inBattle = false;
			setVisible(false);
			isDead = true;
		} else {
			hp = hp - reducedDamage;
		}
	}

	private int calculateDamage(int damage, DamageType type) {
		int factor = 1;
		if (type == DamageType.PHYSICAL) {
			factor = physicalDefense;
		}

		if (type == DamageType.MAGICAL) {
			factor = magicalDefense;
		}
		damage -= (damage / 100) * factor;
		return damage;
	}

	public void heal(final int healAmount) {
		hp = hp + healAmount;
		if (hp > entityData.getMaxHP()) {
			hp = entityData.getMaxHP();
		}
	}

	public boolean canMove() {
		return isPlayerUnit && (owner.getAp() > 0);
	}

	public boolean isDead() {
		return isDead;
	}

	public boolean isStatsChanged() {
		return statsChanged;
	}

	public void setStatsChanged(boolean statsChanged) {
		this.statsChanged = statsChanged;
	}

	public void setFocused(final boolean isFocused) {
		if (isFocused) {
			owner.sendMessageToBattleManager(MessageToBattleScreen.SET_CHARACTER_HUD, this);
		} else {
			owner.sendMessageToBattleManager(MessageToBattleScreen.UNSET_CHARACTER_HUD, this);
		}
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

	public void setVisualComponent(EntityVisualComponentInterface visualComponent) {
		this.visualComponent = visualComponent;
	}

	public EntityVisualComponentInterface getVisualComponent() {
		return visualComponent;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(final Direction direction) {
		this.direction = direction;
		visualComponent.setDirection(direction);
	}

	public int getAp() {
		return owner.getAp();
	}

	public void setAp(final int ap) {
		owner.setAp(ap);
	}

	public int getHp() {
		return hp;
	}

	private void setHp(int hp) {
		this.hp = hp;
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

	public void setEntityID(int entityID) {
		this.entityID = entityID;
	}

	public int getAttackRange() {
		return entityData.getAttackRange();
	}

	public EntityTypes getEntityType() {
		return entityType;
	}

	public void setAbility(final AbilitiesEnum abilityEnum) {
		this.ability = new Ability(abilityEnum);
	}

	public void setAbility(final AbilitiesEnum abilityEnum, MyPoint location) {
		this.ability = new Ability(abilityEnum, location);
	}

	public void addModifier(final ModifiersEnum type, final int turns, final int amount) {
		final Modifier modifier = new Modifier(type, turns, amount);
		modifiers.add(modifier);
	}

	public void addModifier(Modifier mod) {
		modifiers.add(mod);
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

	public Modifier getModifier(final ModifiersEnum type) {
		for (final Modifier modifier : modifiers) {
			if (modifier.getType() == type) {
				return modifier;
			}
		}
		return null;
	}

	public void applyModifiers() {
		modifiers.forEach(this::applymod);
		modifiers.removeIf(mod -> mod.getTurns() == 0);
	}

	public void applymod(Modifier mod) {
		mod.applyModifier(this);

		if (mod.getTurns() == 0) {
			mod.removeModifier(this);
		}
	}

	public Collection<Modifier> getModifiers() {
		return modifiers;
	}

	private void setModifiers(Collection<Modifier> modifiers) {
		this.modifiers = modifiers;
	}

	public Ability getAbility() {
		return ability;
	}

	public void setAbility(Ability ability) {
		this.ability = ability;
	}

	public int getScore() {
		final int score = getModifiersScore();
		if (isPlayerUnit) {
			return score * (-1);
		} else {
			return score;
		}
	}

	private int getModifiersScore() {
		int score = hp;
		for (final Modifier modifier : modifiers) {
			switch (modifier.getType()) {
			case DAMAGE_OVER_TIME_PHYSICAL:
				score -= 2;
				break;
			case DAMAGE_OVER_TIME_MAGICAL:
				score -= 2;
				break;
			case REMOVE_AP:
				score -= 2;
				break;
			case REDUCE_PHYSICAL_DEFENSE:
				score -= 2;
				break;
			case REDUCE_MAGICAL_DEFENSE:
				score -= 2;
				break;
			case REDUCE_DAMAGE:
				score -= 2;
				break;
			case IMPROVE_DAMAGE:
				score += 2;
				break;
			case STUNNED:
				score -= 2;
				break;
			case ROOTED:
				score -= 2;
				break;
			case SILENCED:
				score -= 2;
				break;
			case INVISIBLE:
				score += 2;
				break;
			default:
				return score;
			}
		}
		return score;
	}

	public void move(List<GridCell> path) {
		if (!path.isEmpty()) {
			setAp(getAp() - path.size());
			visualComponent.move(path);
		} else {
			getOwner().sendMessageToBattleManager(MessageToBattleScreen.ACTION_COMPLETED, this);
		}
	}

	public void moveAttack(List<GridCell> path, Entity target) {
		setAp(getAp() - path.size() - getEntityData().getBasicAttackCost());
		visualComponent.moveAttack(path, target);
	}

	public Entity makeCopyWithoutVisual() {
		final Collection<Modifier> copyModifiers = new ArrayList<>();
		for (final Modifier mod : modifiers) {
			copyModifiers.add(new Modifier(mod.getType(), mod.getTurns(), mod.getAmount()));
		}

		final Entity copy = new Entity(entityType, owner, false);
		copy.setVisualComponent(new FakeEntityVisualComponent());
		copy.setCurrentPosition(currentPlayerPosition);
		copy.setEntityID(entityID);
		copy.setInvisible(isInvis);
		copy.setDirection(direction);
		copy.setPlayerUnit(isPlayerUnit);
		copy.setAbility(ability);
		copy.setHp(hp);
		copy.setXp(xp);
		copy.setModifiers(copyModifiers);
		return copy;
	}

	@Override
	public String toString() {
		if (isPlayerUnit) {
			return owner.getType() + ": name : " + entityData.getName() + "   ID:" + entityID + "   pos : (" + currentPlayerPosition.getTileX() + "," + currentPlayerPosition.getTileY() + ")";
		} else {
			return owner.getType() + ": name : " + entityData.getName() + "   ID:" + entityID + "   pos : (" + currentPlayerPosition.getTileX() + "," + currentPlayerPosition.getTileY() + ")";
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

	public void draw(Batch batch) {
		visualComponent.draw(batch);
	}

	public void pushTo(MyPoint casterNewPosition) {
		visualComponent.pushTo(casterNewPosition);
	}

	public void setInvisible(boolean invisible) {
		isInvis = invisible;
	}

	public boolean isInvisible() {
		return isInvis;
	}

	public boolean isInBattle() {
		return inBattle;
	}

	public void setInBattle(final boolean inBattle) {
		this.inBattle = inBattle;
	}

	public boolean isRealUnit() {
		return isReal;
	}

	public void kill() {
		damage(1000000, DamageType.PURE_DAMAGE);
	}
}
