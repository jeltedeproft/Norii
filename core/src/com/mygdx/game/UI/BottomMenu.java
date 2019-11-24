package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.Map;

import Utility.Utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class BottomMenu extends HorizontalGroup {
	private static final String TAG = BottomMenu.class.getSimpleName();
	
    private String unknownHeroImageLocation = "sprites/gui/portraits/unknown.png";
    
    //identifier labels
    private Label hpLabel;
    private Label mpLabel;
    private Label xpLabel;
    private Label levelLabel;
    private Label iniLabel;
    
    //value labels
    private Label hp;
    private Label mp;
    private Label xp;
    private Label levelVal;
    private Label iniVal;
    private Label heroNameLabel;
    
    //Attributes
    private int heroLevel;
    private int heroHP;
    private int heroMP;
    private int heroXP;
    private int heroINI;

    private Image heroImage;
    private Entity linkedEntity;
    
    private static final float BOTTOMMENUHEIGHTTILES = 6;
    private static final float HERONAMESCALE = 3.0f;
    private static final Color NAMECOLOR = Color.DARK_GRAY;
    
    
    private HorizontalGroup bottomMenuTable;
    private Table statsGroup;
    private Table smallMenu;
    private Container<Table> statsGroupContainer;
    private Container<Table> smallMenuContainer;
    
    private TextButton testButton;
    private TextButton test2Button;
    private TextButton test3Button;
    
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
    	initActionsMenu();
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
		trd.setMinHeight((BOTTOMMENUHEIGHTTILES * Map.TILE_HEIGHT_PIXEL) - 60);
		trd.setMinWidth(Gdx.graphics.getWidth() / 3.0f);
		if(heroImage != null) {
			heroImage.setDrawable(trd);
		}else {
			heroImage = new Image(trd);
			heroImage.setAlign(Align.center);
		}
	}
	
	private void initStatsMenu() {
		statsGroup = new Table();
    	statsGroup.center();
    	
        heroNameLabel = new Label("", Utility.STATUSUI_SKIN, "inventory-item-count");
        heroNameLabel.setFontScale(HERONAMESCALE);
        heroNameLabel.setColor(NAMECOLOR);
    	
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
	}

	private void initActionsMenu() {
		smallMenu = new Table();
    	smallMenu.setTransform(true);
    	smallMenu.center();
    	
    	testButton = new TextButton("test", Utility.STATUSUI_SKIN);
    	testButton.setTransform(true);
    	
    	test2Button = new TextButton("test2", Utility.STATUSUI_SKIN);
    	test2Button.setTransform(true);
    	
    	test3Button = new TextButton("test3", Utility.STATUSUI_SKIN);
    	test3Button.setTransform(true);
	}
    
    private void addElementsToWindow() {
    	this.addActor(bottomMenuTable);
    	
    	populateHeroImage();   	
    	populateStatsGroup();   	
    	populateActionsMenu();

    }

	private void populateHeroImage() {
		bottomMenuTable.addActor(heroImage);
	}
	
	private void populateStatsGroup() {
		statsGroup.setHeight(BOTTOMMENUHEIGHTTILES * Map.TILE_HEIGHT_PIXEL);
    	statsGroup.setWidth(Gdx.graphics.getWidth() / 3.0f);
		
    	statsGroup.add(heroNameLabel).expandX().align(Align.left).padLeft(15.0f);
    	statsGroup.row();
    	
    	statsGroup.add(hpLabel).expandX().align(Align.left);
    	statsGroup.add(hp).expandX().align(Align.left);
    	statsGroup.row();
    	
    	statsGroup.add(mpLabel).expandX().align(Align.left);
    	statsGroup.add(mp).expandX().align(Align.left);
    	statsGroup.row();
    	
    	statsGroup.add(xpLabel).align(Align.left);
    	statsGroup.add(xp).align(Align.left);
    	statsGroup.row();

    	statsGroup.add(levelLabel).align(Align.left);
    	statsGroup.add(levelVal).align(Align.left);
    	statsGroup.row();

    	statsGroup.add(iniLabel).align(Align.left);
    	statsGroup.add(iniVal).align(Align.left);
    	statsGroup.row();
    	
    	statsGroupContainer = new Container<Table>(statsGroup);
    	bottomMenuTable.addActor(statsGroupContainer.fill().prefSize(Gdx.graphics.getWidth() / 3.0f,BOTTOMMENUHEIGHTTILES * Map.TILE_HEIGHT_PIXEL));
    	statsGroup.setFillParent(true);
	}

	private void populateActionsMenu() {
		smallMenu.add(testButton).expandX().align(Align.center);
    	smallMenu.row();
    	smallMenu.add(test2Button).expandX().align(Align.center);
    	smallMenu.row();
    	smallMenu.add(test3Button).expandX().align(Align.center);
    	smallMenu.row();
    	smallMenuContainer = new Container<Table>(smallMenu);
    	
    	bottomMenuTable.addActor(smallMenuContainer.fill().prefSize(Gdx.graphics.getWidth() / 3.0f,BOTTOMMENUHEIGHTTILES * Map.TILE_HEIGHT_PIXEL));
	}
    
	//change hero methods
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
        heroINI = this.linkedEntity.getIni();
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
    	Gdx.app.debug(TAG, "updating bottom menu UI");
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
    	updateActionsMenu();
    	updateContainers();
	}

	private void updateMainTable() {
		int scaledWidth = Gdx.graphics.getWidth();
		int scaledHeight = (int) (BOTTOMMENUHEIGHTTILES * Map.TILE_HEIGHT_PIXEL);
		this.setSize(scaledWidth,scaledHeight);
		bottomMenuTable.setSize(scaledWidth,scaledHeight);
	}

	private void updateHeroImage() {
		heroImage.getDrawable().setMinHeight((BOTTOMMENUHEIGHTTILES * Map.TILE_HEIGHT_PIXEL) - 45);
		heroImage.getDrawable().setMinWidth((Gdx.graphics.getWidth() / 3.0f));
	}

	private void updateStatsMenu() {
		statsGroup.setHeight(BOTTOMMENUHEIGHTTILES * Map.TILE_HEIGHT_PIXEL);
    	statsGroup.setWidth(Gdx.graphics.getWidth() / 3.0f);
    	for(Actor actor : statsGroup.getChildren()) {
        	Label label = (Label) actor;
        	label.setFontScale(Gdx.graphics.getWidth() * 0.0018f, Gdx.graphics.getHeight() * 0.0018f);
    	}
    	statsGroup.setPosition(Gdx.graphics.getWidth() / 3.0f, Gdx.graphics.getHeight() * 0.0008f);
	}

	private void updateActionsMenu() {
		smallMenu.setHeight(BOTTOMMENUHEIGHTTILES * Map.TILE_HEIGHT_PIXEL);
    	smallMenu.setWidth(Gdx.graphics.getWidth() / 3.0f);
    	smallMenu.getCell(testButton).minHeight(Map.TILE_HEIGHT_PIXEL);
    	smallMenu.getCell(testButton).minWidth(Map.TILE_WIDTH_PIXEL);
    	smallMenu.getCell(test2Button).minHeight(Map.TILE_HEIGHT_PIXEL);
    	smallMenu.getCell(test2Button).minWidth(Map.TILE_WIDTH_PIXEL);
    	smallMenu.getCell(test3Button).minHeight(Map.TILE_HEIGHT_PIXEL);
    	smallMenu.getCell(test3Button).minWidth(Map.TILE_WIDTH_PIXEL);
    	smallMenu.padTop(Map.TILE_HEIGHT_PIXEL);
    	smallMenu.padBottom(Map.TILE_HEIGHT_PIXEL);
	}

	private void updateContainers() {
		statsGroupContainer.fill().prefSize(Gdx.graphics.getWidth() / 3.0f,BOTTOMMENUHEIGHTTILES * Map.TILE_HEIGHT_PIXEL);
    	smallMenuContainer.fill().prefSize(Gdx.graphics.getWidth() / 3.0f,BOTTOMMENUHEIGHTTILES * Map.TILE_HEIGHT_PIXEL);
	}
}

