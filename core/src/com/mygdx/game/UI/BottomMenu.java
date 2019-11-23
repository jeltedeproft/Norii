package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.Map;

import Utility.Utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

public class BottomMenu extends HorizontalGroup {
	private static final String TAG = BottomMenu.class.getSimpleName();
	
    private static String title = "";
    private String unknownHeroImageLocation = "sprites/gui/portraits/unknown.png";
    
    private Image heroImage;
    
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

    private Entity linkedEntity;
    
    private static final float BOTTOMMENUHEIGHTTILES = 6;
    private static final float BOTTOMMENUWIDGETS = 3;
	

    //Attributes
    private int heroLevel;
    private int heroHP;
    private int heroMP;
    private int heroXP;
    private int heroINI;
    
    
    private HorizontalGroup bottomMenuTable;
    private Table statsGroup;
    private VerticalGroup smallMenu;
    private Container statsGroupContainer;
    private Container smallMenuContainer;
    
    private TextButton testButton;
    
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
    	//hero name
        heroNameLabel = new Label("", Utility.STATUSUI_SKIN, "inventory-item-count");
        heroNameLabel.setFontScale(2.0f);;
        
		changeHeroImage(unknownHeroImageLocation);
        
        //groups
		bottomMenuTable = new HorizontalGroup();
		bottomMenuTable.setFillParent(true);
		this.setTransform(true);
		this.setPosition(0, 0);
		this.debug();
		
    	statsGroup = new Table();
    	statsGroup.padBottom(15.0f);
    	statsGroup.center();
    	
    	smallMenu = new VerticalGroup();
    	smallMenu.setTransform(true);
    	smallMenu.padBottom(15.0f);
    	smallMenu.center();
    	
    	testButton = new TextButton("test", Utility.STATUSUI_SKIN);
    	testButton.setTransform(true);

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
			heroImage.debug();
			heroImage.setAlign(Align.center);
		}
	}

    
    private void addElementsToWindow() {
    	this.addActor(bottomMenuTable);
    	
    	bottomMenuTable.addActor(heroImage);
    	
    	statsGroup.setHeight(BOTTOMMENUHEIGHTTILES * Map.TILE_HEIGHT_PIXEL);
    	statsGroup.setWidth(Gdx.graphics.getWidth() / 3.0f);
		
    	statsGroup.add(heroNameLabel).expandX().expandY().align(Align.left);
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
    	
    	statsGroupContainer = new Container(statsGroup);
    	bottomMenuTable.addActor(statsGroupContainer.fill().prefSize(Gdx.graphics.getWidth() / 3.0f,BOTTOMMENUHEIGHTTILES * Map.TILE_HEIGHT_PIXEL));
    	statsGroup.setFillParent(true);
    	
    	smallMenu.addActor(testButton);
    	smallMenuContainer = new Container(smallMenu);
//    	testButton.setHeight(BOTTOMMENUHEIGHTTILES * Map.TILE_HEIGHT_PIXEL);
//    	testButton.setWidth(Gdx.graphics.getWidth() / 3);
    	
    	bottomMenuTable.addActor(smallMenuContainer.fill().prefSize(Gdx.graphics.getWidth() / 3.0f,BOTTOMMENUHEIGHTTILES * Map.TILE_HEIGHT_PIXEL));

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
		int scaledWidth = Gdx.graphics.getWidth();
		int scaledHeight = (int) (BOTTOMMENUHEIGHTTILES * Map.TILE_HEIGHT_PIXEL);
		this.setSize(scaledWidth,scaledHeight);
		bottomMenuTable.setSize(scaledWidth,scaledHeight);
		
		heroImage.getDrawable().setMinHeight((BOTTOMMENUHEIGHTTILES * Map.TILE_HEIGHT_PIXEL) - 45);
		heroImage.getDrawable().setMinWidth((Gdx.graphics.getWidth() / 3.0f));
		
    	statsGroup.setHeight(BOTTOMMENUHEIGHTTILES * Map.TILE_HEIGHT_PIXEL);
    	statsGroup.setWidth(Gdx.graphics.getWidth() / 3.0f);
    	
    	testButton.setHeight(BOTTOMMENUHEIGHTTILES * Map.TILE_HEIGHT_PIXEL);
    	testButton.setWidth(Gdx.graphics.getWidth() / 3.0f);

    	statsGroupContainer.fill().prefSize(Gdx.graphics.getWidth() / 3.0f,BOTTOMMENUHEIGHTTILES * Map.TILE_HEIGHT_PIXEL);
    	smallMenuContainer.fill().prefSize(Gdx.graphics.getWidth() / 3.0f,BOTTOMMENUHEIGHTTILES * Map.TILE_HEIGHT_PIXEL);
    	
    	//smallMenu.setTransform(true);
    	//smallMenu.setScale(3.0f);
	}
}

