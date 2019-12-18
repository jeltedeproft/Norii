package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Entities.Entity;
import Utility.Utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class BottomMenu extends UIWindow {
    private String unknownHeroImageLocation = "sprites/gui/portraits/unknown.png";
    
    private static final int BOTTOM_MENU_HEIGHT_TILES = 3;
    private static final float HERO_PORTRAIT_WIDTH_TILES = 3;
    
    private static final float STATS_MENU_WIDTH_TILES = 17;
    private static final int STATS_MENU_ELEMENT_PADDING = 20;
    private static final int STATS_MENU_TOP_PADDING = 0;
    
    private static final int HP_LABEL_WIDTH = 50;
    
    private Label hpLabel;
    private Label apLabel;
    private Label xpLabel;
    private Label levelLabel;
    private Label iniLabel;
    
    private Label hp;
    private Label ap;
    private Label xp;
    private Label levelVal;
    private Label iniVal;
    private Label heroNameLabel;
    
    private int heroLevel;
    private int heroHP;
    private int heroAP;
    private int heroXP;
    private int heroINI;
    private int heroMaxHP;
    private int heroMaxMP;
    private int heroMaxXP;

    private Image heroImage;
    private Entity linkedEntity;  
    
    private HorizontalGroup bottomMenuTable;
    private Window statsGroup;
    private Container<Table> statsGroupContainer;  

    public BottomMenu(Entity[] entities){
        super("",HERO_PORTRAIT_WIDTH_TILES + STATS_MENU_WIDTH_TILES,BOTTOM_MENU_HEIGHT_TILES);
        this.debugAll();
        linkUnitsToMenu(entities); 
        configureMainWindow();
        createWidgets();
        addWidgets();
    }
    
	private void linkUnitsToMenu(Entity[] entities) {
		for(Entity entity : entities) {
        	entity.setbottomMenu(this);
        }
	}
    
    protected void configureMainWindow() {
		this.pad(0);
		this.setTransform(true);
		this.setPosition(0, 0);
    }
    
	@Override
	protected void createWidgets() {
		initElementsForUI();
	}

	@Override
	protected void addWidgets() {
		addElementsToWindow();	
	}
    
    private void initElementsForUI() {
		initBottomMenuTable();
		initHeroImage(unknownHeroImageLocation);
    	initStatsMenu();  	
    }
	
	private void initBottomMenuTable() {
		bottomMenuTable = new HorizontalGroup();
		bottomMenuTable.setFillParent(true);
		bottomMenuTable.pad(0);
        Color tableColor = bottomMenuTable.getColor();
        tableColor.a = ALPHA;
	}

	private void initHeroImage(String heroImageLink) {
		Utility.loadTextureAsset(heroImageLink);
		TextureRegion tr = new TextureRegion(Utility.getTextureAsset(heroImageLink));
		TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		trd.setMinHeight(BOTTOM_MENU_HEIGHT_TILES * tileHeightPixel);
		trd.setMinWidth(HERO_PORTRAIT_WIDTH_TILES * tileWidthPixel);
		if(heroImage != null) {
			heroImage.setDrawable(trd);
		}else {
			heroImage = new Image(trd);
			heroImage.setAlign(Align.center);
			heroImage.debug();
		}
	}
	
	private void initStatsMenu() {
    	Skin statusUISkin = Utility.getStatusUISkin();
    	
		statsGroup = new Window("", statusUISkin);
    	
        heroNameLabel = new Label("", statusUISkin);
        hpLabel = new Label(" hp:", statusUISkin);
        hp = new Label("", statusUISkin);
        apLabel = new Label(" ap:", statusUISkin);
        ap = new Label("", statusUISkin);
        xpLabel = new Label(" xp:", statusUISkin);
        xp = new Label("", statusUISkin);
        levelLabel = new Label(" lv:", statusUISkin);
        levelVal = new Label("", statusUISkin);
        iniLabel = new Label(" ini:", statusUISkin);
        iniVal = new Label("", statusUISkin);
        
        Color newStatsGroupColor = statsGroup.getColor();
        newStatsGroupColor.a = ALPHA;
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
		float statsWidth = STATS_MENU_WIDTH_TILES * tileWidthPixel;
		float statsHeight = BOTTOM_MENU_HEIGHT_TILES;
		
		statsGroup.setHeight(statsHeight);
    	statsGroup.setWidth(statsWidth);
    	statsGroup.align(Align.left);
    	statsGroup.debug();
    	addLabelsToStatsGroup();

    	statsGroupContainer = new Container<Table>(statsGroup);
    	bottomMenuTable.addActor(statsGroupContainer.prefSize(statsWidth,statsHeight));
    	statsGroup.setFillParent(true);
	}

	private void addLabelsToStatsGroup() {
		statsGroup.add(heroNameLabel).padTop(STATS_MENU_TOP_PADDING).align(Align.topLeft).colspan(3);
    	statsGroup.row();
    	
    	statsGroup.add(hpLabel).align(Align.topLeft).expandX().width(HP_LABEL_WIDTH);
    	statsGroup.add(hp).align(Align.topLeft).padRight(STATS_MENU_ELEMENT_PADDING).expandX();

    	statsGroup.add(levelLabel).align(Align.left).expandX();
    	statsGroup.add(levelVal).align(Align.left).padRight(STATS_MENU_ELEMENT_PADDING).expandX();

    	
    	statsGroup.add(apLabel).align(Align.left).expandX();
    	statsGroup.add(ap).align(Align.left).padRight(STATS_MENU_ELEMENT_PADDING).expandX();

    	statsGroup.add(iniLabel).align(Align.left).expandX();
    	statsGroup.add(iniVal).align(Align.left).padRight(STATS_MENU_ELEMENT_PADDING).expandX();

    	
    	statsGroup.add(xpLabel).align(Align.left).expandX();
    	statsGroup.add(xp).align(Align.left).padRight(STATS_MENU_ELEMENT_PADDING).expandX();
	}
    
    public void setHero(Entity entity) {
    	if(entity != null) {
	    	if(!entity.getName().equalsIgnoreCase(heroNameLabel.getText().toString())) {
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
        heroAP = this.linkedEntity.getAp();
        heroXP = this.linkedEntity.getXp();
        heroINI = this.linkedEntity.getBaseInitiative();
        heroMaxHP = this.linkedEntity.getMaxHp();
        heroMaxMP = this.linkedEntity.getMaxMp();
        heroMaxXP = this.linkedEntity.getMaxXP();
    }
    
    private void populateElementsForUI(Entity entity) {
    	heroNameLabel.setText(entity.getName());
    	initHeroImage(entity.getPortraitPath());
    	updateLabels();
    }
    
    private void resetStats() {
    	heroNameLabel.setText("");
    	hp.setText("");
    	ap.setText("");
    	xp.setText("");
    	levelVal.setText("");
    	iniVal.setText("");
    	initHeroImage(unknownHeroImageLocation);
    }
    
    @Override
    public void update() {
    	super.update();
        updateStats();
        updateLabels();
        updateSizeBottomMenu();
    }

	private void updateLabels() {
		hp.setText(String.valueOf(heroHP));
        ap.setText(String.valueOf(heroAP));
        xp.setText(String.valueOf(heroXP));
        levelVal.setText(String.valueOf(heroLevel));
        iniVal.setText(String.valueOf(heroINI));
        
        if(heroAP == 0) {
        	ap.setColor(Color.RED);
        }else {
        	ap.setColor(Color.WHITE);
        }
	}

	private void updateStats() {
		if(linkedEntity != null) {
			heroLevel = linkedEntity.getLevel();
	        heroHP = linkedEntity.getHp();
	        heroAP = linkedEntity.getAp();
	        heroXP = linkedEntity.getXp();
	        
	        if(linkedEntity.getEntityactor().getIsHovering()) {
	        	this.setVisible(true);
	        }
		}
	}
	
	protected void updateSizeBottomMenu() {
		updateMainTable();		
		updateHeroImage();		
    	updateStatsMenu();
    	updateContainers();
	}

	private void updateMainTable() {
		float scaledWidth = Gdx.graphics.getWidth();
		float scaledHeight = BOTTOM_MENU_HEIGHT_TILES * tileHeightPixel;
		bottomMenuTable.setSize(scaledWidth,scaledHeight);
	}

	private void updateHeroImage() {
		heroImage.getDrawable().setMinHeight(BOTTOM_MENU_HEIGHT_TILES * tileHeightPixel);
		heroImage.getDrawable().setMinWidth(HERO_PORTRAIT_WIDTH_TILES * tileWidthPixel );
	}

	private void updateStatsMenu() {
		float statsWidth = Gdx.graphics.getWidth() - (HERO_PORTRAIT_WIDTH_TILES * tileWidthPixel);
		float statsHeight = BOTTOM_MENU_HEIGHT_TILES * tileHeightPixel;
		
		statsGroup.setHeight(statsHeight);
    	statsGroup.setWidth(statsWidth);
    	for(Actor actor : statsGroup.getChildren()) {
    		if(actor.getClass() == Label.class) {
            	Label label = (Label) actor;
            	label.setFontScale(Gdx.graphics.getWidth() * 0.0014f, Gdx.graphics.getHeight() * 0.0014f);
    		}
    	}
    	statsGroup.setPosition(HERO_PORTRAIT_WIDTH_TILES * tileWidthPixel , 0);
	}

	private void updateContainers() {
		this.setSize(Gdx.graphics.getWidth() ,BOTTOM_MENU_HEIGHT_TILES * tileHeightPixel);
		statsGroupContainer.setPosition(HERO_PORTRAIT_WIDTH_TILES * tileWidthPixel, 0);
		statsGroupContainer.setSize(Gdx.graphics.getWidth() - (HERO_PORTRAIT_WIDTH_TILES * tileWidthPixel) ,BOTTOM_MENU_HEIGHT_TILES * tileHeightPixel);
		statsGroupContainer.fill().prefSize(Gdx.graphics.getWidth() - (HERO_PORTRAIT_WIDTH_TILES * tileWidthPixel),BOTTOM_MENU_HEIGHT_TILES * tileHeightPixel);
	}
	
	@Override
	protected void updatePos() {
		// todo : refactor to more organized methods , update size and update pos	
	}
}

