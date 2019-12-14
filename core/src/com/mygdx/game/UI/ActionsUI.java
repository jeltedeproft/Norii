package com.mygdx.game.UI;

import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Screen.BattleScreen;

import Utility.Utility;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class ActionsUI extends Window {
    private float tileWidthPixel;
    private float tileHeightPixel;
    
    private static final float WIDTH_IN_TILES = 2.5f;
    private static final float HEIGHT_IN_TILES = 3.3f;
    private static final int BUTTON_WIDTH = 1;
    private static final int BUTTON_HEIGHT = 1;
    private static final int TILE_TO_PIXEL_RATIO = 20;
    private static final int ALPHA = 40;
	
	private static final String MOVE_BUTTON_SPRITEPATH = "sprites/gui/move.png";
	private static final String ATTACK_BUTTON_SPRITEPATH = "sprites/gui/attack.png";
	private static final String SKIP_BUTTON_SPRITEPATH = "sprites/gui/skip.png";
	
    
    private MoveActionUIButton moveActionUIButton;
    private AttackActionUIButton attackActionUIButton;
    private SkipActionUIButton skipActionUIButton;
    
    private Label moveLabel;
    private Label attackLabel;
    private Label skipLabel;
    
    private Boolean right;
    private Boolean up;
    
    private ArrayList<ActionUIButton> buttons;
    private Entity linkedEntity;

    public ActionsUI(Entity entity){
        super("",Utility.getStatusUISkin());
        initVariables(entity);
        
        createButtons();
        createLabels();
        storeButtons();
        addButtons();
               
        calculateSize();
    }

	private void initVariables(Entity entity) {
		buttons = new ArrayList<ActionUIButton>();
        this.linkedEntity = entity;
        entity.setActionsui(this);
        this.setVisible(false);
        this.pad(0);
        this.setKeepWithinStage(false);
        setFadeBackgroundEffect();
        
    	tileWidthPixel = Gdx.graphics.getWidth() / (float) TILE_TO_PIXEL_RATIO;
    	tileHeightPixel = Gdx.graphics.getHeight() / (float) TILE_TO_PIXEL_RATIO;
	}
	
	private void setFadeBackgroundEffect() {
		Color newColor = this.getColor();
        newColor.a = ALPHA;
        this.setColor(newColor);
	}

	private void createButtons() {
		moveActionUIButton = new MoveActionUIButton(MOVE_BUTTON_SPRITEPATH,linkedEntity);
        attackActionUIButton = new AttackActionUIButton(ATTACK_BUTTON_SPRITEPATH,linkedEntity);
        skipActionUIButton = new SkipActionUIButton(SKIP_BUTTON_SPRITEPATH,linkedEntity);
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

	private void addButtons() {  
		float buttonWidth = BUTTON_WIDTH * tileWidthPixel;
		float buttonHeight = BUTTON_HEIGHT * tileHeightPixel;
        this.add(moveActionUIButton.getButton()).height(buttonHeight).width(buttonWidth);
		this.add(moveLabel);
        this.row();
        
        this.add(attackActionUIButton.getButton()).height(buttonHeight).width(buttonWidth);
        this.add(attackLabel);
        this.row();
        
        this.add(skipActionUIButton.getButton()).height(buttonHeight).width(buttonWidth);
        this.add(skipLabel);
        this.add();
	}

	private void calculateSize() {
        float actionsMenuWidth = tileWidthPixel * WIDTH_IN_TILES;
        float actionsMenuHeight = tileHeightPixel * HEIGHT_IN_TILES;
        this.setSize(actionsMenuWidth, actionsMenuHeight);
	}

    public void update() {
    	this.setVisible(linkedEntity.isActive());
    	
    	tileWidthPixel = Gdx.graphics.getWidth() / (float) TILE_TO_PIXEL_RATIO;
    	tileHeightPixel = Gdx.graphics.getHeight() / (float) TILE_TO_PIXEL_RATIO;
    	this.setSize(WIDTH_IN_TILES * tileWidthPixel, HEIGHT_IN_TILES * tileHeightPixel);
        this.setPosition((linkedEntity.getCurrentPosition().getCameraX()), (linkedEntity.getCurrentPosition().getCameraY())); 
        adjustPosition();
    }
    
    private void adjustPosition() {
    	float x = linkedEntity.getCurrentPosition().getCameraX();
    	float y = linkedEntity.getCurrentPosition().getCameraY();
    	float offsetX = Gdx.graphics.getWidth() / (float) BattleScreen.VISIBLE_WIDTH;
    	float offsetY = Gdx.graphics.getHeight() / (float) BattleScreen.VISIBLE_HEIGHT;
    	right = x > (Gdx.graphics.getWidth() / 2);
    	up = y > (Gdx.graphics.getHeight() / 2);
    	
    	if(right) {
    		this.setX(x - (offsetX * 2));
    	}else {
    		this.setX(x + offsetX);
    	}
    	
    	if(up) {
    		this.setY(y - (offsetY * 3));
    	}else {
    		this.setY(y + offsetY);
    	}
    }
}

