package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.Map;

import Utility.Utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class BottomMenu extends Window {
	private static final String TAG = BottomMenu.class.getSimpleName();
	
    private Image _hpBar;
    private Image _mpBar;
    private Image _xpBar;
    
    private Label hp;
    private Label mp;
    private Label xp;
    private Label levelVal;
    private Label iniVal;

    private ImageButton _heroButton;
    private Entity linkedEntity;
    
    private Label heroNameLabel;
    private String heroName = "???";
    private static String title = "Hero Menu";
    
    private final float bottomMenuHeightTiles = 3;
	
	private final float bottomMenuWidth = Gdx.graphics.getWidth();
	private final float bottomMenuHeight = bottomMenuHeightTiles * Map.TILE_HEIGHT_PIXEL;

    //Attributes
    private int _levelVal;
    private int _hpVal;
    private int _mpVal;
    private int _xpVal;
    private int _iniVal;
    
    //groups
    private WidgetGroup group;
    private WidgetGroup group2;
    private WidgetGroup group3;
    
    //bars
    private Image bar;
    private Image bar2;
    private Image bar3;
    
    //labels
    private Label hpLabel;
    private Label mpLabel;
    private Label xpLabel;
    private Label levelLabel;
    private Label iniLabel;
    
    public BottomMenu(){
        super(title, Utility.STATUSUI_SKIN);
 
        initElementsForUI();
        configureElements();
        addElementsToWindow();
    }
    
    public void setHero(Entity entity) {
    	this.linkedEntity = entity;
    	initiateHeroStats();
    	populateElementsForUI(entity);
    }
    
    private void initiateHeroStats() {
        _levelVal = this.linkedEntity.getLevel();
        _hpVal = this.linkedEntity.getHp();
        _mpVal = this.linkedEntity.getMp();
        _xpVal = this.linkedEntity.getXp();
        _iniVal = this.linkedEntity.getIni();
    }
    
    private void initElementsForUI() {
    	//hero name
        heroNameLabel = new Label("", Utility.STATUSUI_SKIN, "inventory-item-count");
        
        //groups
        group = new WidgetGroup();
        group2 = new WidgetGroup();
        group3 = new WidgetGroup();

        //images
        _hpBar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("HP_Bar"));
        bar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("Bar"));
        _mpBar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("MP_Bar"));
        bar2 = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("Bar"));
        _xpBar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("XP_Bar"));
        bar3 = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("Bar"));

        //labels
        hpLabel = new Label(" hp:", Utility.STATUSUI_SKIN);
        hp = new Label("", Utility.STATUSUI_SKIN);
        mpLabel = new Label(" mp:", Utility.STATUSUI_SKIN);
        mp = new Label("", Utility.STATUSUI_SKIN);
        xpLabel = new Label(" xp:", Utility.STATUSUI_SKIN);
        xp = new Label("", Utility.STATUSUI_SKIN);
        levelLabel = new Label(" lv:", Utility.STATUSUI_SKIN);
        levelVal = new Label("", Utility.STATUSUI_SKIN);
        iniLabel = new Label(" ini:", Utility.STATUSUI_SKIN);
        iniVal = new Label("", Utility.STATUSUI_SKIN);

        //buttons
        _heroButton= new ImageButton(Utility.STATUSUI_SKIN, "inventory-button");
        _heroButton.getImageCell().size(32, 32);
    }
    
    private void populateElementsForUI(Entity entity) {
    	heroNameLabel.setText(entity.getName());
    	updateLabels();
    }
    
    private void configureElements() {
        //Align images
        _hpBar.setPosition(3, 6);
        _mpBar.setPosition(3, 6);
        _xpBar.setPosition(3, 6);

        //add to widget groups
        group.addActor(bar);
        group.addActor(_hpBar);
        group2.addActor(bar2);
        group2.addActor(_mpBar);
        group3.addActor(bar3);
        group3.addActor(_xpBar);

        //Add to layout
        defaults().expand().fill();
    }
    
    private void addElementsToWindow() {
        this.add(heroNameLabel);

        //account for the title padding
        this.pad(this.getPadTop() + 10, 10, 10, 10);

        this.add();
        this.add();
        this.add(_heroButton).align(Align.right);
        this.row();

        this.add(group).size(bar.getWidth(), bar.getHeight());
        this.add(hpLabel);
        this.add(hp).align(Align.left);
        this.row();

        this.add(group2).size(bar2.getWidth(), bar2.getHeight());
        this.add(mpLabel);
        this.add(mp).align(Align.left);
        this.row();

        this.add(group3).size(bar3.getWidth(), bar3.getHeight());
        this.add(xpLabel);
        this.add(xp).align(Align.left);
        this.row();

        this.add(levelLabel).align(Align.left);
        this.add(levelVal).align(Align.left);
        this.row();
        
        this.add(iniLabel).align(Align.left);
        this.add(iniVal).align(Align.left);
        this.row();

        //this.debug();
        this.pack();
    }

    public ImageButton getInventoryButton() {
        return _heroButton;
    }
    
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }
    
    public void update() {
    	Gdx.app.debug(TAG, "updating bottom menu UI");
        updateStats();
        updateLabels();
        updateSize();
    }

	private void updateLabels() {
		hp.setText(String.valueOf(_hpVal));
        mp.setText(String.valueOf(_mpVal));
        xp.setText(String.valueOf(_xpVal));
        levelVal.setText(String.valueOf(_levelVal));
        iniVal.setText(String.valueOf(_iniVal));
	}

	private void updateStats() {
		if(linkedEntity != null) {
			_levelVal = linkedEntity.getLevel();
	        _hpVal = linkedEntity.getHp();
	        _mpVal = linkedEntity.getMp();
	        _xpVal = linkedEntity.getXp();
	        
	        if(linkedEntity.getEntityactor().getIsHovering()) {
	        	this.setVisible(true);
	        }
		}
	}
	
	private void updateSize() {
		int scaledWidth = (int) (Gdx.graphics.getWidth());
		int scaledHeight = (int) (bottomMenuHeightTiles * Map.TILE_HEIGHT_PIXEL);
		this.setSize(scaledWidth,scaledHeight);
	}
}

