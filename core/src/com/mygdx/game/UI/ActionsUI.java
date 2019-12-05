package com.mygdx.game.UI;

import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.Map;

import Utility.Utility;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ActionsUI extends Table {
	private static final String TAG = ActionsUI.class.getSimpleName();
	
	//padding
    private float iconWidth = Gdx.graphics.getWidth() / 22.0f;
    private float iconHeight = Gdx.graphics.getHeight() / 26.0f;
    private static final int BAR_LENGTH = 3;
    private static final int NUMBER_OF_BARS = 1;
	
	//images
	private static final String MOVE_BUTTON_SPRITEPATH = "sprites/gui/move.png";
	private static final String ATTACK_BUTTON_SPRITEPATH = "sprites/gui/attack.png";
	private static final String SPELL_BUTTON_SPRITEPATH = "sprites/gui/spell.png";
	private static final String SKIP_BUTTON_SPRITEPATH = "sprites/gui/skip.png";
	private static final String UPGRADE_BUTTON_SPRITEPATH = "sprites/gui/upgrade.png";
	private static final String SPELL1_BUTTON_SPRITEPATH = "sprites/gui/spell1.jpg";
	private static final String SPELL2_BUTTON_SPRITEPATH = "sprites/gui/spell2.jpg";
	private static final String SPELL3_BUTTON_SPRITEPATH = "sprites/gui/spell3.png";
	
    
    //ActionUIButtons
    private MoveActionUIButton moveActionUIButton;
    private AttackActionUIButton attackActionUIButton;
    private ActionUIButton spellActionUIButton;
    private SkipActionUIButton skipActionUIButton;
    private ActionUIButton upgradeActionUIButton;
    private ActionUIButton spell1ActionUIButton;
    private ActionUIButton spell2ActionUIButton;
    private ActionUIButton spell3ActionUIButton;
    
    private ArrayList<ActionUIButton> buttons;
	

    private Image heroPortrait; //just an idea
    private Entity linkedEntity;

    public ActionsUI(Entity entity){
        super(Utility.getStatusUISkin());
        this.debug();
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
        float actionsMenuWidth = iconWidth * BAR_LENGTH;
        float actionsMenuHeight = NUMBER_OF_BARS * iconHeight;
        this.setSize(actionsMenuWidth, actionsMenuHeight);
	}

    public void update() {
    	this.setVisible(linkedEntity.isActive());
    	
    	iconWidth = Map.TILE_WIDTH_PIXEL;
    	iconHeight = Map.TILE_HEIGHT_PIXEL;
    	this.setSize(BAR_LENGTH * iconWidth, NUMBER_OF_BARS * iconHeight);
        //we offset the position a little bit to make it look better
        this.setPosition((linkedEntity.getCurrentPosition().getRealScreenX()) + iconWidth, (linkedEntity.getCurrentPosition().getRealScreenY()) + iconHeight); 
    }
}

