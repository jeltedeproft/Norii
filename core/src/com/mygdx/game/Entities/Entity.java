package com.mygdx.game.Entities;

import java.util.UUID;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Entities.EntityAnimation.Direction;
import com.mygdx.game.Entities.EntityAnimation.State;
import com.mygdx.game.Entities.EntityObserver.EntityCommand;
import com.mygdx.game.UI.ActionsUI;
import com.mygdx.game.UI.BottomMenu;
import com.mygdx.game.UI.StatusUI;

import Utility.TiledMapPosition;
import Utility.Utility;

public class Entity extends Actor implements EntitySubject{
	private static final String TAG = Entity.class.getSimpleName();
	
	private String _entityID;
	private String name;
	private String portraitSpritePath;
	private int mp;
	private int maxMP;
	private int hp;
	private int level;
	private int xp;
	private int ini;
	private int attackRange;
	private int attackPower;
	private boolean inBattle;
	private boolean isActive;
	private boolean isInMovementPhase;
	private boolean isInAttackPhase;
	private boolean isInActionPhase;
	private boolean isInDeploymentPhase;
	private boolean isDead;
	

	protected TiledMapPosition nextPlayerPosition;
	protected TiledMapPosition currentPlayerPosition;
	protected State state = State.IDLE;
	
	private StatusUI statusui;
	private ActionsUI actionsui;
	private BottomMenu bottomMenu;
	
	private EntityAnimation entityAnimation;
	protected EntityActor entityactor;
	
	private Array<EntityObserver> observers;
	
	public Entity(String name,EntityFilePath entityfilepath,EntityPortaitFilePath portraitFilePath){
		this.entityAnimation = new EntityAnimation(entityfilepath.getValue());
		this.portraitSpritePath = portraitFilePath.getValue();
		initEntity();
		this.name = name;
	}
	
	public void initEntity(){
		this.observers = new Array<EntityObserver>();
		this._entityID = UUID.randomUUID().toString();
		this.nextPlayerPosition = new TiledMapPosition();
		this.currentPlayerPosition = new TiledMapPosition().setPositionFromScreen(-100, -100);
		this.hp = 10;
		this.mp = 3;
		this.maxMP = 3;
		this.ini = Utility.getRandomIntFrom1to(100);
		this.attackRange = 2;
		this.attackPower = Utility.getRandomIntFrom1to(5);
		this.isDead = false;
		this.inBattle = false;
		this.isInMovementPhase = false;
		this.isInAttackPhase = false;
		this.isInDeploymentPhase = false;
	}
	
	public void update(float delta){
		this.entityAnimation.update(delta);
	}

	public StatusUI getStatusui() {
		return statusui;
	}

	public void setStatusui(StatusUI statusui) {
		this.statusui = statusui;
	}
	
	public ActionsUI getActionsui() {
		return actionsui;
	}

	public void setActionsui(ActionsUI actionsui) {
		this.actionsui = actionsui;
	}

	public BottomMenu getbottomMenu() {
		return bottomMenu;
	}

	public void setbottomMenu(BottomMenu bottomMenu) {
		this.bottomMenu = bottomMenu;
	}
	
	public void dispose(){
		Utility.unloadAsset(this.entityAnimation.getSpritePath());
	}
	
	public void setState(State state){
		this.state = state;
	}
	
	public TiledMapPosition getCurrentPosition(){
		return currentPlayerPosition;
	}
	
	public void setCurrentPosition(TiledMapPosition pos){
		entityAnimation.setFramePos(pos);
		this.currentPlayerPosition = pos;
		this.entityactor.setPos();
		
		updateUI();
	}
	
	public void updateUI() {
		statusui.update();
		bottomMenu.update();
	}

	@Override
	public String getName() {
		return name;
	}
	
	public String getPortraitPath() {
		return portraitSpritePath;
	}

	public EntityActor getEntityactor() {
		return entityactor;
	}

	public void setEntityactor(EntityActor entityactor) {
		this.entityactor = entityactor;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
		actionsui.update();
	}

	public boolean isInMovementPhase() {
		return isInMovementPhase;
	}

	public void setInMovementPhase(boolean isInMovementPhase) {
		this.isInMovementPhase = isInMovementPhase;
		if(isInMovementPhase) {
			actionsui.setVisible(false);			
			this.notify(EntityCommand.IN_MOVEMENT);
		}
	}
	
	public boolean isInAttackPhase() {
		return isInAttackPhase;
	}

	public void setInAttackPhase(boolean isInAttackPhase) {
		this.isInAttackPhase = isInAttackPhase;
		if(isInAttackPhase) {
			actionsui.setVisible(false);			
			this.notify(EntityCommand.IN_ATTACK_PHASE);
		}
	}
	
	public void attack(Entity target) {
		target.damage(attackPower);
	}
	
	private void damage(int damage) {
		if(damage > this.hp) {
			this.hp = 0;
			this.isDead = true;
		}else {
			this.hp = this.hp - damage;
		}
		
		updateUI();
	}
	
	public boolean canMove() {
		return (this.mp > 0);
	}

	public boolean isInBattle() {
		return inBattle;
	}

	public void setInBattle(boolean inBattle) {
		this.inBattle = inBattle;
	}
	
	public boolean isInActionPhase() {
		return isInActionPhase;
	}

	public void setInActionPhase(boolean isInActionPhase) {
		this.isInActionPhase = isInActionPhase;
		if(isInActionPhase) {
			actionsui.update();
			actionsui.setVisible(true);			
		}
		this.notify(EntityCommand.IN_ACTION_PHASE);
	}
	
	public boolean isInDeploymentPhase() {
		return isInDeploymentPhase;
	}

	public void setInDeploymentPhase(boolean isInDeploymentPhase) {
		this.isInDeploymentPhase = isInDeploymentPhase;
		if (isInDeploymentPhase){
			bottomMenu.setHero(this);
		}
	}
	
	public void setFocused(boolean isFocused) {
		if (isFocused){
			bottomMenu.setHero(this);
		}else {
			bottomMenu.setHero(null);
		}
	}

	public int getMp() {
		return mp;
	}

	public void setMp(int mp) {
		this.mp = mp;
		updateUI();
	}
	
	public int getMaxMp() {
		return maxMP;
	}
	
	public void setMaxMp(int maxMP) {
		this.maxMP = maxMP;
		updateUI();
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
		updateUI();
	}

	public int getIni() {
		return ini;
	}

	public void setIni(int ini) {
		this.ini = ini;
		updateUI();
	}
	
	public int getAttackRange() {
		return attackRange;
	}
	
	public void setAttackRange(int attackRange) {
		this.attackRange = attackRange;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
		updateUI();
	}

	public int getXp() {
		return xp;
	}

	public void setXp(int xp) {
		this.xp = xp;
		updateUI();
	}
	
	public TextureRegion getFrame() {
		return entityAnimation.getFrame();
	}
	
	public void setDirection(Direction direction) {
		entityAnimation.setDirection(direction);
	}
	
    @Override
    public void addObserver(EntityObserver entityObserver) {
        observers.add(entityObserver);
    }

    @Override
    public void removeObserver(EntityObserver entityObserver) {
        observers.removeValue(entityObserver, true);
    }

    @Override
    public void removeAllObservers() {
        observers.removeAll(observers, true);
    }

    @Override
    public void notify(EntityObserver.EntityCommand command) {
        for(EntityObserver observer: observers){
            observer.onNotify(command,this);
        }
    }

}
