package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.Map;

import Utility.Utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class StatusUI extends Window {
	private static final String TAG = StatusUI.class.getSimpleName();
	
    private int levelVal;
    private int hpVal;
    private int mpVal;
    private int xpVal;
    private int iniVal;
	
    private Image hpBar;
    private Image xpBar;
    private Image bar;
    private Image bar3;
    private WidgetGroup group;
    private WidgetGroup group2;
    private WidgetGroup group3;
    
    private Image loadingBarBackground;
    private Image loadingBar;
    
    private Label heroName;
    private Label hp;
    private Label mp;
    private Label xp;
    private Label levelValLabel;
    private Label iniValLabel;
    
    private Label hpLabel;
    private LabelStyle labelStyle;
    private Label mpLabel;
    private Label xpLabel;
    private Label levelLabel;
    private Label iniLabel;

    private Entity linkedEntity;
    
	private int statsUIOffsetX = 32;
	private int statsUIOffsetY = 32;
	
	private static final int WIDTH_TILES = 7;
	private static final int HEIGHT_TILES = 7; 

    
    public StatusUI(Entity entity){
        super("", Utility.getStatusUISkin());
        this.setVisible(false);
        this.linkedEntity = entity;
        this.setResizable(true);
        entity.setStatusui(this);
        
        initiateHeroStats();
        createElementsForUI();
        configureElements();
        addElementsToWindow();
    }
    
    private void initiateHeroStats() {
        levelVal = this.linkedEntity.getLevel();
        hpVal = this.linkedEntity.getHp();
        mpVal = this.linkedEntity.getMp();
        xpVal = this.linkedEntity.getXp();
        iniVal = this.linkedEntity.getBaseInitiative();
    }
    
    private void createElementsForUI() {
    	TextureAtlas statusUITextureAtlas = Utility.getStatusUITextureAtlas();
    	BitmapFont font = Utility.getFreeTypeFontAsset("fonts/BLKCHCRY.ttf");
    	labelStyle = new LabelStyle();
    	labelStyle.font = font;
        
        group = new WidgetGroup();
        group2 = new WidgetGroup();
        group3 = new WidgetGroup();

        hpBar = new Image(statusUITextureAtlas.findRegion("HP_Bar"));
        bar = new Image(statusUITextureAtlas.findRegion("Bar"));
        xpBar = new Image(statusUITextureAtlas.findRegion("XP_Bar"));
        bar3 = new Image(statusUITextureAtlas.findRegion("Bar"));
        
        heroName = new Label(linkedEntity.getName(),labelStyle);

        hpLabel = new Label(" hp:", labelStyle);
        hp = new Label(String.valueOf(hpVal), labelStyle);
        mpLabel = new Label(" mp:", labelStyle);
        mp = new Label(String.valueOf(mpVal), labelStyle);
        xpLabel = new Label(" xp:", labelStyle);
        xp = new Label(String.valueOf(xpVal), labelStyle);
        levelLabel = new Label(" lv:", labelStyle);
        levelValLabel = new Label(String.valueOf(levelVal), labelStyle);
        iniLabel = new Label(" ini:", labelStyle);
        iniValLabel = new Label(String.valueOf(iniVal), labelStyle);
        
        //dynamic hp bar
        TextureAtlas skinAtlas = Utility.getUITextureAtlas();
        NinePatch loadingBarBackgroundPatch = new NinePatch(skinAtlas.findRegion("default-round"), 5, 5, 4, 4);
        NinePatch loadingBarPatch = new NinePatch(skinAtlas.findRegion("default-round-down"), 5, 5, 4, 4);
        loadingBar = new Image(loadingBarPatch);
        loadingBarBackground = new Image(loadingBarBackgroundPatch);
    }
    
    private void configureElements() {
        hpBar.setPosition(3, 6);
        xpBar.setPosition(3, 6);
        loadingBar.setPosition(3, 6);
        loadingBarBackground.setPosition(3, 6);
        loadingBarBackground.setWidth(bar.getWidth());

        group.addActor(bar);
        group.addActor(hpBar);
        group2.addActor(loadingBarBackground);
        group2.addActor(loadingBar);
        group2.addActor(bar);
        group3.addActor(bar3);
        group3.addActor(xpBar);

        defaults().expand().fill();
    }
    
    private void addElementsToWindow() {
        //account for the title padding
        this.pad(0, 10, 10, 10);
        
        this.add(heroName);
        this.row();

        this.add(hpLabel);
        this.add(hp);
        this.add(group2).size(bar.getWidth(), bar.getHeight());
        this.row();

        this.add(mpLabel);
        this.add(mp).align(Align.left);
        this.row();

        this.add(levelLabel).align(Align.left);
        this.add(levelValLabel).align(Align.left);
        this.row();
        
        this.add(iniLabel).align(Align.left);
        this.add(iniValLabel).align(Align.left);
        this.row();
        
        this.add(xpLabel);
        this.add(xp);
        this.add(group3).size(bar3.getWidth(), bar3.getHeight());
        this.row();

        this.pack();
    }
    
    public void update() {
        updateStats();
        updateLabels();
        updateSize();
        
        if(linkedEntity.getEntityactor().getIsHovering()) {
        	this.setVisible(true);
        }
        
        //we offset the position a little bit to make it look better
        this.setPosition((linkedEntity.getCurrentPosition().getRealScreenX()) + statsUIOffsetX, (linkedEntity.getCurrentPosition().getRealScreenY()) + statsUIOffsetY);

    }

	private void updateLabels() {
		hp.setText(String.valueOf(hpVal));
        mp.setText(String.valueOf(mpVal));
        xp.setText(String.valueOf(xpVal));
        levelValLabel.setText(String.valueOf(levelVal));
        iniValLabel.setText(String.valueOf(iniVal));
	}

	private void updateStats() {
		levelVal = linkedEntity.getLevel();
        hpVal = linkedEntity.getHp();
        mpVal = linkedEntity.getMp();
        xpVal = linkedEntity.getXp();
	}
	
	private void updateSize() {
		this.setSize(WIDTH_TILES * Map.TILE_WIDTH_PIXEL, HEIGHT_TILES * Map.TILE_HEIGHT_PIXEL);
		loadingBar.setWidth(((float)linkedEntity.getHp() / (float)linkedEntity.getMaxHp()) * bar.getWidth());
		loadingBarBackground.setWidth(bar.getWidth());
	}
}

