package com.mygdx.game.UI;

import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.Map;

import Utility.Utility;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ActionsUI extends Table {
	private static final String TAG = ActionsUI.class.getSimpleName();
	
	//padding
    private float actionsMenuWidth;
    private float actionsMenuHeight;
    private float iconWidth = Gdx.graphics.getWidth() / 22.0f;
    private float iconHeight = Gdx.graphics.getHeight() / 26.0f;
    private static final int BAR_LENGTH = 6;
    private static final int NUMBER_OF_BARS = 2;
	
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
    private ActionUIButton attackActionUIButton;
    private ActionUIButton spellActionUIButton;
    private ActionUIButton skipActionUIButton;
    private ActionUIButton upgradeActionUIButton;
    private ActionUIButton spell1ActionUIButton;
    private ActionUIButton spell2ActionUIButton;
    private ActionUIButton spell3ActionUIButton;
    
    private ArrayList<ActionUIButton> buttons;
	

    private Image heroPortrait; //just an idea
    private Entity linkedEntity;

    public ActionsUI(Entity entity){
        super(Utility.STATUSUI_SKIN);
        this.debug();
        buttons = new ArrayList<ActionUIButton>();

        this.linkedEntity = entity;
        entity.setActionsui(this);
        
        this.setVisible(false); //have to set this false and true when active
        
        moveActionUIButton = new MoveActionUIButton(MOVE_BUTTON_SPRITEPATH,Action.MOVE,linkedEntity);
        attackActionUIButton = new ActionUIButton(ATTACK_BUTTON_SPRITEPATH,Action.ATTACK);
        spellActionUIButton = new ActionUIButton(SPELL_BUTTON_SPRITEPATH,Action.SPELLBOOK);
        skipActionUIButton = new ActionUIButton(SKIP_BUTTON_SPRITEPATH,Action.SKIP);
        upgradeActionUIButton = new ActionUIButton(UPGRADE_BUTTON_SPRITEPATH,Action.UPGRADE);
        spell1ActionUIButton = new ActionUIButton(SPELL1_BUTTON_SPRITEPATH,Action.SPELL);
        spell2ActionUIButton = new ActionUIButton(SPELL2_BUTTON_SPRITEPATH,Action.SPELL);
        spell3ActionUIButton = new ActionUIButton(SPELL3_BUTTON_SPRITEPATH,Action.SPELL);
        
        buttons.add(moveActionUIButton);
        buttons.add(attackActionUIButton);
        buttons.add(spellActionUIButton);
        buttons.add(skipActionUIButton);
        buttons.add(upgradeActionUIButton);
        buttons.add(spell1ActionUIButton);
        buttons.add(spell2ActionUIButton);
        buttons.add(spell3ActionUIButton);

        this.add(spell1ActionUIButton.getButton()).expand().fill();
        this.add(spell2ActionUIButton.getButton()).expand().fill();
        this.add(spell3ActionUIButton.getButton()).expand().fill();
        this.add();
        this.add();
        this.add();
        this.row();
        
        this.add(moveActionUIButton.getButton()).expand().fill();
        this.add(attackActionUIButton.getButton()).expand().fill();
        this.add(spellActionUIButton.getButton()).expand().fill();
        this.add(skipActionUIButton.getButton()).expand().fill();
        this.add(upgradeActionUIButton.getButton()).expand().fill();
        this.add();
               
        //size
        actionsMenuWidth = iconWidth * BAR_LENGTH;
        actionsMenuHeight = NUMBER_OF_BARS * iconHeight;
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

