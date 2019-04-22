package com.mygdx.game.UI;

import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.Map;

import Utility.Utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ActionsUI extends Table {
	private static final String TAG = ActionsUI.class.getSimpleName();
	
	//padding
    private int width;
    private int height;
    private final int iconWidth = 32;
    private final int iconHeight = 32;
    private final int topBarIcons = 6;
    private final int botBarIcons = 6;
    private final int amountOfBars = 2;
	
	//images
	private static final String _moveButtonSpritePath = "sprites/gui/move.png";
	private static final String _attackButtonSpritePath = "sprites/gui/attack.png";
	private static final String _spellButtonSpritePath = "sprites/gui/spell.png";
	private static final String _skipButtonSpritePath = "sprites/gui/skip.png";
	private static final String _upgradeButtonSpritePath = "sprites/gui/upgrade.png";
	private static final String _spell1ButtonSpritePath = "sprites/gui/spell1.jpg";
	private static final String _spell2ButtonSpritePath = "sprites/gui/spell2.jpg";
	private static final String _spell3ButtonSpritePath = "sprites/gui/spell3.png";
	
    
    //ActionUIButtons
    private MoveActionUIButton _moveActionUIButton;
    private ActionUIButton _attackActionUIButton;
    private ActionUIButton _spellActionUIButton;
    private ActionUIButton _skipActionUIButton;
    private ActionUIButton _upgradeActionUIButton;
    private ActionUIButton _spell1ActionUIButton;
    private ActionUIButton _spell2ActionUIButton;
    private ActionUIButton _spell3ActionUIButton;
	

    private Image _heroPortrait; //just an idea
    private Entity linkedEntity;
	private int actionsUIOffsetX = 32;
	private int actionsUIOffsetY = 32;


    public ActionsUI(Entity entity){
        super(Utility.STATUSUI_SKIN);

        this.linkedEntity = entity;
        entity.setActionsui(this);
        
        this.setVisible(false); //have to set this false and true when active
        
        _moveActionUIButton = new MoveActionUIButton(_moveButtonSpritePath,Action.MOVE,linkedEntity);
        _attackActionUIButton = new ActionUIButton(_attackButtonSpritePath,Action.ATTACK);
        _spellActionUIButton = new ActionUIButton(_spellButtonSpritePath,Action.SPELLBOOK);
        _skipActionUIButton = new ActionUIButton(_skipButtonSpritePath,Action.SKIP);
        _upgradeActionUIButton = new ActionUIButton(_upgradeButtonSpritePath,Action.UPGRADE);
        _spell1ActionUIButton = new ActionUIButton(_spell1ButtonSpritePath,Action.SPELL);
        _spell2ActionUIButton = new ActionUIButton(_spell2ButtonSpritePath,Action.SPELL);
        _spell3ActionUIButton = new ActionUIButton(_spell3ButtonSpritePath,Action.SPELL);

        this.add(_spell1ActionUIButton.getButton());
        this.add(_spell2ActionUIButton.getButton());
        this.add(_spell3ActionUIButton.getButton());
        this.add();
        this.add();
        this.add();
        this.row();
        
        this.add(_moveActionUIButton.getButton());
        this.add(_attackActionUIButton.getButton());
        this.add(_spellActionUIButton.getButton());
        this.add(_skipActionUIButton.getButton());
        this.add(_upgradeActionUIButton.getButton());
        this.add();
        
        //size
        width = iconWidth * topBarIcons;
        height = amountOfBars * iconHeight;
        this.setSize(width, height);
    }


    
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }
    
    public void update() {
    	Gdx.app.debug(TAG, "updating actionsUI");
        //if unit is active, set visible
        if(linkedEntity.isActive()) {
        	this.setVisible(true);
        }
        
        //we offset the position a little bit to make it look better
        this.setPosition((linkedEntity.getCurrentPosition().x / Map.UNIT_SCALE) + actionsUIOffsetX - ( (1 / Map.UNIT_SCALE) / 2), (linkedEntity.getCurrentPosition().y / Map.UNIT_SCALE) + actionsUIOffsetY);
    }
}

