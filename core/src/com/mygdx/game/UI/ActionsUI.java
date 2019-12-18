package com.mygdx.game.UI;

import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Screen.BattleScreen;

import Utility.Utility;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class ActionsUI extends UIWindow {  
    private static final float WINDOW_WIDTH = 2.5f;
    private static final float WINDOW_HEIGHT = 3.3f;
    private static final int BUTTON_WIDTH = 1;
    private static final int BUTTON_HEIGHT = 1;
    private static final String MOVE_BUTTON_SPRITEPATH = "sprites/gui/move.png";
	private static final String ATTACK_BUTTON_SPRITEPATH = "sprites/gui/attack.png";
	private static final String SKIP_BUTTON_SPRITEPATH = "sprites/gui/skip.png";
	
    
    private MoveActionUIButton moveActionUIButton;
    private AttackActionUIButton attackActionUIButton;
    private SkipActionUIButton skipActionUIButton;
    
    private Label moveLabel;
    private Label attackLabel;
    private Label skipLabel;
    
    private ArrayList<ActionUIButton> buttons;
    private Entity linkedEntity;

    public ActionsUI(Entity entity){
        super("",WINDOW_WIDTH,WINDOW_HEIGHT);
        configureMainWindow();
        initVariables(entity);
        createWidgets();
        addWidgets();
    }
    
    protected void configureMainWindow() {
        this.setVisible(false);
        this.pad(0);
        this.setKeepWithinStage(false);
    }

	private void initVariables(Entity entity) {
		buttons = new ArrayList<ActionUIButton>();
        linkedEntity = entity;
        entity.setActionsui(this);
	}
	
	protected void createWidgets() {
        createButtons();
        createLabels();
        storeButtons();
	}

	private void createButtons() {
		moveActionUIButton = new MoveActionUIButton(MOVE_BUTTON_SPRITEPATH,linkedEntity);
        attackActionUIButton = new AttackActionUIButton(ATTACK_BUTTON_SPRITEPATH,linkedEntity);
        skipActionUIButton = new SkipActionUIButton(this,SKIP_BUTTON_SPRITEPATH,linkedEntity);
	}
	
	private void createLabels() {
	    moveLabel = new Label("move",Utility.getStatusUISkin());
	    attackLabel = new Label("attack",Utility.getStatusUISkin());
	    skipLabel = new Label("skip",Utility.getStatusUISkin());
	}

	private void storeButtons() {
		buttons.add(moveActionUIButton);
        buttons.add(attackActionUIButton);
        buttons.add(skipActionUIButton);
	}
	
	protected void addWidgets() {
		addButtons();
	}

	private void addButtons() {  
		float buttonWidth = BUTTON_WIDTH * tileWidthPixel;
		float buttonHeight = BUTTON_HEIGHT * tileHeightPixel;
        this.add(moveActionUIButton.getButton()).size(buttonWidth,buttonHeight);
		this.add(moveLabel).expand().fill();;
        this.row();
        
        this.add(attackActionUIButton.getButton()).size(buttonWidth,buttonHeight);
        this.add(attackLabel).expand().fill();
        this.row();
        
        this.add(skipActionUIButton.getButton()).size(buttonWidth,buttonHeight);
        this.add(skipLabel).expand().fill();
        this.add();
	}

    public void updatePos() {
        this.setPosition((linkedEntity.getCurrentPosition().getCameraX()), (linkedEntity.getCurrentPosition().getCameraY())); 
        adjustPosition();
    }
    
    private void adjustPosition() {
    	float x = linkedEntity.getCurrentPosition().getCameraX();
    	float y = linkedEntity.getCurrentPosition().getCameraY();
    	float offsetX = Gdx.graphics.getWidth() / (float) BattleScreen.VISIBLE_WIDTH;
    	float offsetY = WINDOW_HEIGHT * tileHeightPixel;
    	Boolean right = x > (Gdx.graphics.getWidth() / 3);
    	Boolean up = y > (Gdx.graphics.getHeight() / 3);
    	
    	if(right) {
    		this.setX(x - (offsetX * 2));
    	}else {
    		this.setX(x + offsetX);
    	}
    	
    	if(up) {
    		this.setY(y - (offsetY));
    	}else {
    		this.setY(y + (offsetY / WINDOW_HEIGHT));
    	}
    }
}

