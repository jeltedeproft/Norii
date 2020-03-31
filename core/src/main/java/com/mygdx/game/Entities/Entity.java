package com.mygdx.game.Entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Entities.EntityAnimation.Direction;
import com.mygdx.game.Entities.EntityAnimation.State;
import com.mygdx.game.Entities.EntityObserver.EntityCommand;
import com.mygdx.game.Magic.AbilitiesEnum;
import com.mygdx.game.Magic.Ability;
import com.mygdx.game.UI.ActionsUI;
import com.mygdx.game.UI.BottomMenu;
import com.mygdx.game.UI.StatusUI;

import Utility.TiledMapPosition;
import Utility.Utility;

public class Entity extends Actor implements EntitySubject {
	private final EntityData entityData;

	private int ap;
	private int hp;

	private int basicAttackCost;
	private int currentInitiative;

	private boolean inBattle;
	private boolean isActive;
	private boolean isInMovementPhase;
	private boolean isInAttackPhase;
	private boolean isInActionPhase;
	private boolean isInDeploymentPhase;
	private boolean isInSpellPhase;
	private boolean isDead;

	protected TiledMapPosition oldPlayerPosition;
	protected TiledMapPosition nextPlayerPosition;
	protected TiledMapPosition currentPlayerPosition;
	protected Direction direction;
	protected State state = State.IDLE;

	private StatusUI statusui;
	private ActionsUI actionsui;
	private BottomMenu bottomMenu;

	private final EntityAnimation entityAnimation;
	protected EntityActor entityactor;

	private Array<EntityObserver> observers;
	private Collection<Ability> abilities;

	public Entity(final int id) {
		entityData = EntityFileReader.getUnitData().get(id);
		entityAnimation = new EntityAnimation(entityData.getEntitySpriteFilePath());
		initEntity();
	}

	public void initEntity() {
		observers = new Array<EntityObserver>();
		nextPlayerPosition = new TiledMapPosition();
		oldPlayerPosition = new TiledMapPosition().setPositionFromScreen(-100, -100);
		currentPlayerPosition = new TiledMapPosition().setPositionFromScreen(-100, -100);
		hp = entityData.getMaxHP();
		ap = entityData.getMaxAP();
		currentInitiative = entityData.getIni();
		isDead = false;
		inBattle = false;
		isInMovementPhase = false;
		isInAttackPhase = false;
		isInDeploymentPhase = false;
		abilities = new ArrayList<Ability>();
		initAbilities();
	}

	private void initAbilities() {
		for (final String abilityString : entityData.getAbilties()) {
			addAbility(AbilitiesEnum.valueOf(abilityString));
		}
	}

	public void update(final float delta) {
		entityAnimation.update(delta);
	}

	public StatusUI getStatusui() {
		return statusui;
	}

	public void setStatusui(final StatusUI statusui) {
		this.statusui = statusui;
	}

	public ActionsUI getActionsui() {
		return actionsui;
	}

	public void setActionsui(final ActionsUI actionsui) {
		this.actionsui = actionsui;
	}

	public BottomMenu getbottomMenu() {
		return bottomMenu;
	}

	public void setbottomMenu(final BottomMenu bottomMenu) {
		this.bottomMenu = bottomMenu;
	}

	public void dispose() {
		Utility.unloadAsset(entityAnimation.getSpritePath());
	}

	public void setState(final State state) {
		this.state = state;
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
		bottomMenu.update();
	}

	@Override
	public String getName() {
		return entityData.getName();
	}

	public String getPortraitPath() {
		return entityData.getPortraitSpritePath();
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

	public void setActive(final boolean isActive) {
		this.isActive = isActive;
		actionsui.update();
	}

	public boolean isInMovementPhase() {
		return isInMovementPhase;
	}

	public void setInMovementPhase(final boolean isInMovementPhase) {
		this.isInMovementPhase = isInMovementPhase;
		if (isInMovementPhase) {
			actionsui.setVisible(false);
			notifyEntityObserver(EntityCommand.IN_MOVEMENT);
		}
	}

	public boolean isInAttackPhase() {
		return isInAttackPhase;
	}

	public void setInAttackPhase(final boolean isInAttackPhase) {
		if (canAttack()) {
			this.isInAttackPhase = isInAttackPhase;
			if (isInAttackPhase) {
				actionsui.setVisible(false);
				notifyEntityObserver(EntityCommand.IN_ATTACK_PHASE);
			}
		}
	}

	public boolean isInSpellPhase() {
		return isInSpellPhase;
	}

	public void setInSpellPhase(final boolean isInSpellPhase, final Ability ability) {
		this.isInSpellPhase = isInSpellPhase;
		if (isInSpellPhase) {
			actionsui.setVisible(false);
			notifyEntityObserver(EntityCommand.IN_SPELL_PHASE, ability);
		}
	}

	public void attack(final Entity target) {
		target.damage(getAttackPower());
	}

	public boolean canAttack() {
		return ap > basicAttackCost;
	}

	public void damage(final int damage) {
		if (damage > hp) {
			hp = 0;
			isDead = true;
		} else {
			hp = hp - damage;
		}

		updateUI();
	}

	public boolean canMove() {
		return (ap > 0);
	}

	public boolean isInBattle() {
		return inBattle;
	}

	public void setInBattle(final boolean inBattle) {
		this.inBattle = inBattle;
	}

	public boolean isInActionPhase() {
		return isInActionPhase;
	}

	public void setInActionPhase(final boolean isInActionPhase) {
		this.isInActionPhase = isInActionPhase;
		if (isInActionPhase) {
			actionsui.update();
			actionsui.setVisible(true);
		}
		notifyEntityObserver(EntityCommand.UNIT_ACTIVE);
	}

	public boolean isInDeploymentPhase() {
		return isInDeploymentPhase;
	}

	public void setInDeploymentPhase(final boolean isInDeploymentPhase) {
		this.isInDeploymentPhase = isInDeploymentPhase;
		if (isInDeploymentPhase) {
			bottomMenu.setHero(this);
			notifyEntityObserver(EntityCommand.UNIT_ACTIVE);
		}
	}

	public void setFocused(final boolean isFocused) {
		if (isFocused) {
			bottomMenu.setHero(this);
		} else {
			bottomMenu.setHero(null);
		}
	}

	public int getAp() {
		return ap;
	}

	public void setAp(final int ap) {
		this.ap = ap;
		updateUI();
	}

	public int getMaxAp() {
		return entityData.getMaxAP();
	}

	public void setMaxAp(final int maxAP) {
		entityData.setMaxAP(maxAP);
		updateUI();
	}

	public int getHp() {
		return hp;
	}

	public void setHp(final int hp) {
		this.hp = hp;
		updateUI();
	}

	public int getMaxXP() {
		return entityData.getMaxXP();
	}

	public int getMaxHp() {
		return entityData.getMaxHP();
	}

	public void setMaxHp(final int maxHP) {
		entityData.setMaxHP(maxHP);
		updateUI();
	}

	public int getBaseInitiative() {
		return entityData.getIni();
	}

	public void setBaseIniative(final int ini) {
		entityData.setIni(ini);
		updateUI();
	}

	public int getAttackRange() {
		return entityData.getAttackrange();
	}

	public void setAttackRange(final int attackRange) {
		entityData.setAttackrange(attackRange);
	}

	public int getAttackPower() {
		return entityData.getAttackPower();
	}

	public void setAttackPower(final int attackPower) {
		entityData.setAttackPower(attackPower);
	}

	public int getAbasicAttackCost() {
		return entityData.getBasicAttackCost();
	}

	public void setbasicAttackCost(final int basicAttackCost) {
		entityData.setBasicAttackCost(basicAttackCost);
	}

	public int getLevel() {
		return entityData.getLevel();
	}

	public void setLevel(final int level) {
		entityData.setLevel(level);
		updateUI();
	}

	public int getXp() {
		return entityData.getXp();
	}

	public void setXp(final int xp) {
		entityData.setXp(xp);
		updateUI();
	}

	public int getCurrentInitiative() {
		return currentInitiative;
	}

	public void setCurrentInitiative(final int currentInitiative) {
		this.currentInitiative = currentInitiative;
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

	public void addAbility(final AbilitiesEnum abilityEnum) {
		final Ability ability = new Ability(abilityEnum);
		abilities.add(ability);
	}

	public void removeAbility(final AbilitiesEnum abilityEnum) {
		abilities.removeIf(new Predicate<Ability>() {
			@Override
			public boolean test(final Ability ability) {
				return ability.getId() == abilityEnum.ordinal();
			}
		});
	}

	public Collection<Ability> getAbilities() {
		return abilities;
	}

	@Override
	public void addEntityObserver(final EntityObserver entityObserver) {
		observers.add(entityObserver);
	}

	@Override
	public void removeObserver(final EntityObserver entityObserver) {
		observers.removeValue(entityObserver, true);
	}

	@Override
	public void removeAllObservers() {
		observers.removeAll(observers, true);
	}

	@Override
	public void notifyEntityObserver(final EntityObserver.EntityCommand command) {
		for (int i = 0; i < observers.size; i++) {
			observers.get(i).onEntityNotify(command, this);
		}
	}

	public void notifyEntityObserver(final EntityObserver.EntityCommand command, final Ability ability) {
		for (int i = 0; i < observers.size; i++) {
			observers.get(i).onEntityNotify(command, this, ability);
		}
	}
}
