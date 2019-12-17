package com.mygdx.game.UI;

import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Screen.BattleScreen;

import Utility.Utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class StatusUI extends UIWindow {
    private int levelVal;
    private int hpVal;
    private int maxHpVal;
    private int mpVal;
    private int maxMpVal;
    private int xpVal;
    private int maxXpVal;
    private int iniVal;
	
    private WidgetGroup group;
    private WidgetGroup group2;
    private Image hpBarBackground;
    private Image hpBar;
    private Image xpBarBackground;
    private Image xpBar;
    
    private Label heroName;
    private Label hp;
    private Label mp;
    private Label xp;
    private Label levelValLabel;
    private Label iniValLabel;
    
    private LabelStyle labelStyle;
    private Label hpLabel;
    private Label mpLabel;
    private Label xpLabel;
    private Label levelLabel;
    private Label iniLabel;

    private Entity linkedEntity;
    
	private float statsUIOffsetX;
	private float statsUIOffsetY;
	
	private static final float WIDTH_TILES = 6;
	private static final float HEIGHT_TILES = 8; 
	private static final float BAR_WIDTH = 1.5f; 
	private static final float BAR_HEIGHT = 1;  

    
    public StatusUI(Entity entity){
        super("",WIDTH_TILES,HEIGHT_TILES);            
    	initVariables(entity); 
        configureMainWindow();
		createWidgets();
        addWidgets();
    }

	protected void configureMainWindow() {
		this.setVisible(false);
        this.setResizable(true);
	}

	private void initVariables(Entity entity) {
        this.linkedEntity = entity;
        entity.setStatusui(this);
		initiateHeroStats();
		statsUIOffsetX = Gdx.graphics.getWidth() / (float) BattleScreen.VISIBLE_WIDTH;
    	statsUIOffsetY = Gdx.graphics.getHeight() / (float) BattleScreen.VISIBLE_HEIGHT;
	}
    
    private void initiateHeroStats() {
        levelVal = this.linkedEntity.getLevel();
        hpVal = this.linkedEntity.getHp();
        maxHpVal = this.linkedEntity.getMaxHp();
        mpVal = this.linkedEntity.getMp();
        maxMpVal = this.linkedEntity.getMaxMp();
        xpVal = this.linkedEntity.getXp();
        maxXpVal = this.linkedEntity.getMaxXP();
        iniVal = this.linkedEntity.getBaseInitiative();
    }
    
    protected void createWidgets() {
    	createFont();    
        createLabels();
        createDynamicHpBar();
        createDynamicXpBar();
    	createGroups();
    }
    
	private void createFont() {
		BitmapFont font = Utility.getFreeTypeFontAsset("fonts/BLKCHCRY.ttf");
    	labelStyle = new LabelStyle();
    	labelStyle.font = font;
	}
	
	
	private void createLabels() {
		heroName = new Label(linkedEntity.getName(),labelStyle);
        hpLabel = new Label(" hp:", labelStyle);
        hp = new Label(String.valueOf(hpVal) + "/" + maxHpVal, labelStyle);
        mpLabel = new Label(" mp:", labelStyle);
        mp = new Label(String.valueOf(mpVal) + "/" + maxMpVal, labelStyle);
        xpLabel = new Label(" xp:", labelStyle);
        xp = new Label(String.valueOf(xpVal) + "/" + maxXpVal, labelStyle);
        levelLabel = new Label(" lv:", labelStyle);
        levelValLabel = new Label(String.valueOf(levelVal), labelStyle);
        iniLabel = new Label(" ini:", labelStyle);
        iniValLabel = new Label(String.valueOf(iniVal), labelStyle);
	}

	private void createDynamicHpBar() {
        TextureAtlas skinAtlas = Utility.getUITextureAtlas();
        NinePatch hpBarBackgroundPatch = new NinePatch(skinAtlas.findRegion("default-round"), 5, 5, 4, 4);
        NinePatch hpBarPatch = new NinePatch(skinAtlas.findRegion("default-round-down"), 5, 5, 4, 4);
        hpBar = new Image(hpBarPatch);
        hpBarBackground = new Image(hpBarBackgroundPatch);
        
        hpBar.setWidth(BAR_WIDTH * tileWidthPixel);
        hpBarBackground.setWidth(BAR_WIDTH * tileWidthPixel);
	}
	
	private void createDynamicXpBar() {
		TextureAtlas skinAtlas = Utility.getUITextureAtlas();
		NinePatch xpBarBackgroundPatch = new NinePatch(skinAtlas.findRegion("default-round"), 5, 5, 4, 4);
		NinePatch xpBarPatch = new NinePatch(skinAtlas.findRegion("default-round-down"), 5, 5, 4, 4);
		xpBarPatch.setColor(Color.BLACK);
		xpBar = new Image(xpBarPatch);
		xpBarBackground = new Image(xpBarBackgroundPatch);
	}
    
    private void createGroups() {
		group = new WidgetGroup();
        group2 = new WidgetGroup();
        group.addActor(hpBarBackground);
        group.addActor(hpBar);
        group.setFillParent(true);
        group2.addActor(xpBar);
        group2.addActor(xpBarBackground);
        group2.setFillParent(true);

        defaults().expand().fill();
    }
    
    protected void addWidgets() {
        this.add(heroName).colspan(3);
        this.row();

        this.add(hpLabel).align(Align.left).colspan(1).expandX();
        this.add(hp).align(Align.left).colspan(1).expandX();
        this.add(group).colspan(3).expandX();
        this.row();

        this.add(mpLabel).align(Align.left).colspan(1).expandX();
        this.add(mp).align(Align.left).colspan(1).expandX();
        this.row();

        this.add(levelLabel).align(Align.left).colspan(1).expandX();
        this.add(levelValLabel).align(Align.left).colspan(1).expandX();
        this.row();
        
        this.add(iniLabel).align(Align.left).colspan(1).expandX();
        this.add(iniValLabel).align(Align.left).colspan(1).expandX();
        this.row();
        
        this.add(xpLabel).align(Align.left).colspan(1).expandX();
        this.add(xp).align(Align.left).colspan(1).expandX();
        this.add(group2).colspan(3).expandX();
        this.row();

        this.pack();
    }
    
    @Override
    public void update() {
    	super.update();
		statsUIOffsetX = Gdx.graphics.getWidth() / (float) BattleScreen.VISIBLE_WIDTH;
    	statsUIOffsetY = Gdx.graphics.getHeight() / (float) BattleScreen.VISIBLE_HEIGHT;
    	
        updateStats();
        updateLabels();
        updateSizeElements();
        
        if(linkedEntity.getEntityactor().getIsHovering()) {
        	this.setVisible(true);
        }
        
        updatePos();
    }
    
    protected void updatePos() {
        //we offset the position a little bit to make it look better
        this.setPosition((linkedEntity.getCurrentPosition().getCameraX()) + statsUIOffsetX, (linkedEntity.getCurrentPosition().getCameraY()) + statsUIOffsetY);
    }
    
	private void updateStats() {
		levelVal = linkedEntity.getLevel();
		
        hpVal = linkedEntity.getHp();
        maxHpVal = linkedEntity.getMaxHp();
        mpVal = linkedEntity.getMp();
        maxMpVal = linkedEntity.getMaxMp();
        xpVal = linkedEntity.getXp();
        maxXpVal = linkedEntity.getMaxXP();
	}
	
	private void updateLabels() {
		hp.setText(String.valueOf(hpVal) + "/" + maxHpVal);
        mp.setText(String.valueOf(mpVal) + "/" + maxMpVal);
        xp.setText(String.valueOf(xpVal) + "/" + maxXpVal);
        levelValLabel.setText(String.valueOf(levelVal));
        iniValLabel.setText(String.valueOf(iniVal));
	}

	
	private void updateSizeElements() {
		this.setSize(WIDTH_TILES * tileWidthPixel, HEIGHT_TILES * tileHeightPixel);
		float barWidth = BAR_WIDTH * tileWidthPixel;
		float barHeight = BAR_HEIGHT * tileHeightPixel;
		
		hpBar.setWidth(((float)linkedEntity.getHp() / (float)linkedEntity.getMaxHp()) * barWidth);
		hpBarBackground.setWidth(barWidth);
		
		hpBar.setHeight(barHeight);
		hpBarBackground.setHeight(barHeight);
		
		xpBar.setWidth(((float)linkedEntity.getXp() / (float)linkedEntity.getMaxXP()) * barWidth);
		xpBarBackground.setWidth(barWidth);
		
		xpBar.setHeight(barHeight);
		xpBarBackground.setHeight(barHeight);
		
		this.invalidate();
	}
}

