package com.mygdx.game.UI;

import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Screen.BattleScreen;

import Utility.Utility;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ActionsUI extends Table {
    private float tileWidthPixel;
    private float tileHeightPixel;
    private static final int BAR_LENGTH = 3;
    private static final int NUMBER_OF_BARS = 1;
	
	private static final String MOVE_BUTTON_SPRITEPATH = "sprites/gui/move.png";
	private static final String ATTACK_BUTTON_SPRITEPATH = "sprites/gui/attack.png";
	private static final String SKIP_BUTTON_SPRITEPATH = "sprites/gui/skip.png";
	
    
    private MoveActionUIButton moveActionUIButton;
    private AttackActionUIButton attackActionUIButton;
    private SkipActionUIButton skipActionUIButton;
    
    private ArrayList<ActionUIButton> buttons;
    private Entity linkedEntity;

    public ActionsUI(Entity entity){
        super(Utility.getStatusUISkin());
        initVariables(entity);
        
        createButtons();
        storeButtons();
        addButtons();
               
        calculateSize();
    }

	private void initVariables(Entity entity) {
		buttons = new ArrayList<ActionUIButton>();
        this.linkedEntity = entity;
        entity.setActionsui(this);
        this.setVisible(false);
        
    	tileWidthPixel = (Gdx.graphics.getWidth() / (float) BattleScreen.VISIBLE_WIDTH);
    	tileHeightPixel = (Gdx.graphics.getHeight() / (float) BattleScreen.VISIBLE_HEIGHT);
	}

	private void createButtons() {
		moveActionUIButton = new MoveActionUIButton(MOVE_BUTTON_SPRITEPATH,linkedEntity);
        attackActionUIButton = new AttackActionUIButton(ATTACK_BUTTON_SPRITEPATH,linkedEntity);
        skipActionUIButton = new SkipActionUIButton(SKIP_BUTTON_SPRITEPATH,linkedEntity);
	}

	private void storeButtons() {
		buttons.add(moveActionUIButton);
        buttons.add(attackActionUIButton);
        buttons.add(skipActionUIButton);
	}

	private void addButtons() {      
        this.add(moveActionUIButton.getButton()).expand().fill();
        this.add(attackActionUIButton.getButton()).expand().fill();
        this.add(skipActionUIButton.getButton()).expand().fill();
        this.add();
	}

	private void calculateSize() {
        float actionsMenuWidth = tileWidthPixel * BAR_LENGTH;
        float actionsMenuHeight = tileHeightPixel * NUMBER_OF_BARS;
        this.setSize(actionsMenuWidth, actionsMenuHeight);
	}

    public void update() {
    	this.setVisible(linkedEntity.isActive());
    	
    	tileWidthPixel = (Gdx.graphics.getWidth() / (float) BattleScreen.VISIBLE_WIDTH);
    	tileHeightPixel = (Gdx.graphics.getHeight() / (float) BattleScreen.VISIBLE_HEIGHT);
    	this.setSize(BAR_LENGTH * tileWidthPixel, NUMBER_OF_BARS * tileHeightPixel);
        //we offset the position a little bit to make it look better
        this.setPosition((linkedEntity.getCurrentPosition().getCameraX()) + tileWidthPixel, (linkedEntity.getCurrentPosition().getCameraY()) + tileHeightPixel); 
    }
}

