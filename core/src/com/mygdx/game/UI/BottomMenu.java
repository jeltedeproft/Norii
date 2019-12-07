package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.Map;

import Utility.Utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class BottomMenu extends HorizontalGroup {
	private static final String TAG = BottomMenu.class.getSimpleName();
	
    private String unknownHeroImageLocation = "sprites/gui/portraits/unknown.png";
    
    private Label hpLabel;
    private Label mpLabel;
    private Label xpLabel;
    private Label levelLabel;
    private Label iniLabel;
    
    private Label hp;
    private Label mp;
    private Label xp;
    private Label levelVal;
    private Label iniVal;
    private Label heroNameLabel;
    
    private int heroLevel;
    private int heroHP;
    private int heroMP;
    private int heroXP;
    private int heroINI;

    private Image heroImage;
    private Entity linkedEntity;
    
    private static final float BOTTOM_MENU_HEIGHT_TILES = 3.0f;
    private static final float HERO_PORTRAIT_WIDTH_TILES = 3.0f;
    private static final float STATS_MENU_WIDTH_TILES = 17.0f;
    private static final int HERONAMESCALE = 10;
    
    
    private HorizontalGroup bottomMenuTable;
    private Table statsGroup;
    private Container<Table> statsGroupContainer;

    public BottomMenu(Entity[] entities){
        super();
        linkUnitsToMenu(entities);
        initElementsForUI();
        addElementsToWindow();
    }

	private void linkUnitsToMenu(Entity[] entities) {
		for(Entity entity : entities) {
        	entity.setbottomMenu(this);
        }
	}
    
    private void initElementsForUI() {
		initMainContainer();
		changeHeroImage(unknownHeroImageLocation);
    	initStatsMenu();  	
    }
    
	private void initMainContainer() {
		bottomMenuTable = new HorizontalGroup();
		bottomMenuTable.setFillParent(true);
		this.setTransform(true);
		this.setPosition(0, 0);
	}
	
	private void changeHeroImage(String heroImageLink) {
		Utility.loadTextureAsset(heroImageLink);
		TextureRegion tr = new TextureRegion(Utility.getTextureAsset(heroImageLink));
		TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		trd.setMinHeight(BOTTOM_MENU_HEIGHT_TILES * Map.TILE_HEIGHT_PIXEL);
		trd.setMinWidth(HERO_PORTRAIT_WIDTH_TILES * Map.TILE_WIDTH_PIXEL);
		if(heroImage != null) {
			heroImage.setDrawable(trd);
		}else {
			heroImage = new Image(trd);
			heroImage.setAlign(Align.center);
		}
	}
	
	private void initStatsMenu() {
    	Skin statusUISkin = Utility.getStatusUISkin();
    	
		statsGroup = new Table();
    	
        heroNameLabel = new Label("", statusUISkin);
        hpLabel = new Label(" hp:", statusUISkin);
        hp = new Label("", statusUISkin);
        mpLabel = new Label(" mp:", statusUISkin);
        mp = new Label("", statusUISkin);
        xpLabel = new Label(" xp:", statusUISkin);
        xp = new Label("", statusUISkin);
        levelLabel = new Label(" lv:", statusUISkin);
        levelVal = new Label("", statusUISkin);
        iniLabel = new Label(" ini:", statusUISkin);
        iniVal = new Label("", statusUISkin);
	}
    
    private void addElementsToWindow() {
    	this.addActor(bottomMenuTable);
    	
    	populateHeroImage();   	
    	populateStatsGroup();   	
    }

	private void populateHeroImage() {
		bottomMenuTable.addActor(heroImage);
	}
	
	private void populateStatsGroup() {
		float statsWidth = STATS_MENU_WIDTH_TILES * Map.TILE_WIDTH_PIXEL;
		float statsHeight = BOTTOM_MENU_HEIGHT_TILES * Map.TILE_HEIGHT_PIXEL;
		
		statsGroup.setHeight(statsHeight);
    	statsGroup.setWidth(statsWidth);
    	statsGroup.align(Align.left);
    	
    	addLabelsToStatsGroup();

    	statsGroupContainer = new Container<Table>(statsGroup);
    	bottomMenuTable.addActor(statsGroupContainer.prefSize(statsWidth,statsHeight));
    	statsGroup.setFillParent(true);
	}

	private void addLabelsToStatsGroup() {
		statsGroup.add(heroNameLabel).align(Align.topLeft).colspan(3);
    	statsGroup.row();
    	
    	statsGroup.add(hpLabel).align(Align.topLeft).expandX().width(50);
    	statsGroup.add(hp).align(Align.topLeft).padRight(20).expandX();

    	statsGroup.add(levelLabel).align(Align.left).expandX();
    	statsGroup.add(levelVal).align(Align.left).padRight(20).expandX();

    	
    	statsGroup.add(mpLabel).align(Align.left).expandX();
    	statsGroup.add(mp).align(Align.left).padRight(20).expandX();

    	statsGroup.add(iniLabel).align(Align.left).expandX();
    	statsGroup.add(iniVal).align(Align.left).padRight(20).expandX();

    	
    	statsGroup.add(xpLabel).align(Align.left).expandX();
    	statsGroup.add(xp).align(Align.left).padRight(20).expandX();
	}
    
    public void setHero(Entity entity) {
    	if(entity != null) {
	    	if(entity.getName() != heroNameLabel.getText().toString()) {
	        	this.linkedEntity = entity;
	        	initiateHeroStats();
	        	populateElementsForUI(entity);
	    	}
    	}else {
    		resetStats();
    	}
    }
    
    private void initiateHeroStats() {
        heroLevel = this.linkedEntity.getLevel();
        heroHP = this.linkedEntity.getHp();
        heroMP = this.linkedEntity.getMp();
        heroXP = this.linkedEntity.getXp();
        heroINI = this.linkedEntity.getBaseInitiative();
    }
    
    private void populateElementsForUI(Entity entity) {
    	heroNameLabel.setText(entity.getName());
    	changeHeroImage(entity.getPortraitPath());
    	updateLabels();
    }
    
    private void resetStats() {
    	heroNameLabel.setText("");
    	hp.setText("");
    	mp.setText("");
    	xp.setText("");
    	levelVal.setText("");
    	iniVal.setText("");
    	changeHeroImage(unknownHeroImageLocation);
    }
    
    public void update() {
        updateStats();
        updateLabels();
        updateSize();
    }

	private void updateLabels() {
		hp.setText(String.valueOf(heroHP));
        mp.setText(String.valueOf(heroMP));
        xp.setText(String.valueOf(heroXP));
        levelVal.setText(String.valueOf(heroLevel));
        iniVal.setText(String.valueOf(heroINI));
	}

	private void updateStats() {
		if(linkedEntity != null) {
			heroLevel = linkedEntity.getLevel();
	        heroHP = linkedEntity.getHp();
	        heroMP = linkedEntity.getMp();
	        heroXP = linkedEntity.getXp();
	        
	        if(linkedEntity.getEntityactor().getIsHovering()) {
	        	this.setVisible(true);
	        }
		}
	}
	
	private void updateSize() {
		updateMainTable();		
		updateHeroImage();		
    	updateStatsMenu();
    	updateContainers();
	}

	private void updateMainTable() {
		int scaledWidth = Gdx.graphics.getWidth();
		int scaledHeight = (int) (BOTTOM_MENU_HEIGHT_TILES * Map.TILE_HEIGHT_PIXEL);
		this.setSize(scaledWidth,scaledHeight);
		bottomMenuTable.setSize(scaledWidth,scaledHeight);
	}

	private void updateHeroImage() {
		heroImage.getDrawable().setMinHeight(BOTTOM_MENU_HEIGHT_TILES * Map.TILE_HEIGHT_PIXEL);
		heroImage.getDrawable().setMinWidth(HERO_PORTRAIT_WIDTH_TILES * Map.TILE_WIDTH_PIXEL);
	}

	private void updateStatsMenu() {
		float statsWidth = STATS_MENU_WIDTH_TILES * Map.TILE_WIDTH_PIXEL;
		float statsHeight = BOTTOM_MENU_HEIGHT_TILES * Map.TILE_HEIGHT_PIXEL;
		
		statsGroup.setHeight(statsHeight);
    	statsGroup.setWidth(statsWidth);
    	for(Actor actor : statsGroup.getChildren()) {
        	Label label = (Label) actor;
        	label.setFontScale(Gdx.graphics.getWidth() * 0.0018f, Gdx.graphics.getHeight() * 0.0018f);
    	}
    	statsGroup.setPosition(HERO_PORTRAIT_WIDTH_TILES * Map.TILE_WIDTH_PIXEL, 0);
	}

	private void updateContainers() {
		statsGroupContainer.fill().prefSize(STATS_MENU_WIDTH_TILES * Map.TILE_WIDTH_PIXEL ,BOTTOM_MENU_HEIGHT_TILES * Map.TILE_HEIGHT_PIXEL);
	}
}

