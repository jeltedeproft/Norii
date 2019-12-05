package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.Map;

import Utility.Utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class StatusUI extends Window {
	private static final String TAG = StatusUI.class.getSimpleName();
	
    private Image hpBar;
    private Image mpBar;
    private Image xpBar;
    
    private Label hp;
    private Label mp;
    private Label xp;
    private Label levelValLabel;
    private Label iniValLabel;

    private ImageButton heroButton;
    private Entity linkedEntity;
    
    private Label heroNameLabel;
    private String heroName = "???";
    private static String title = "Hero stats";
    
	private int statsUIOffsetX = 32;
	private int statsUIOffsetY = 32;
	
	private static final float BASE_WIDTH = 200;
	private static final float BASE_HEIGHT = 200f;
	
	private static final float STATUS_UI_WIDTH = 0.1f;
	private static final float STATUS_UI_HEIGHT = 0.1f;

    private int levelVal;
    private int hpVal;
    private int mpVal;
    private int xpVal;
    private int iniVal;
    
    private WidgetGroup group;
    private WidgetGroup group2;
    private WidgetGroup group3;
    
    private Image bar;
    private Image bar2;
    private Image bar3;
    
    private Label hpLabel;
    private Label mpLabel;
    private Label xpLabel;
    private Label levelLabel;
    private Label iniLabel;
    
    public StatusUI(Entity entity){
        super(title, Utility.getStatusUISkin());
        this.setVisible(false); //have to set this false and true on hover
        this.linkedEntity = entity;
        this.setResizable(true);
        entity.setStatusui(this);
        
        initiateHeroStats();
        createElementsForUI(entity);
        configureElements();
        addElementsToWindow();
    }
    
    private void initiateHeroStats() {
        levelVal = this.linkedEntity.getLevel();
        hpVal = this.linkedEntity.getHp();
        mpVal = this.linkedEntity.getMp();
        xpVal = this.linkedEntity.getXp();
        iniVal = this.linkedEntity.getIni();
    }
    
    private void createElementsForUI(Entity entity) {
    	TextureAtlas statusUITextureAtlas = Utility.getStatusUITextureAtlas();
    	Skin statusUISkin = Utility.getStatusUISkin();
    	
        heroNameLabel = new Label(entity.getName(), statusUISkin, "inventory-item-count");
        
        group = new WidgetGroup();
        group2 = new WidgetGroup();
        group3 = new WidgetGroup();

        hpBar = new Image(statusUITextureAtlas.findRegion("HP_Bar"));
        bar = new Image(statusUITextureAtlas.findRegion("Bar"));
        mpBar = new Image(statusUITextureAtlas.findRegion("MP_Bar"));
        bar2 = new Image(statusUITextureAtlas.findRegion("Bar"));
        xpBar = new Image(statusUITextureAtlas.findRegion("XP_Bar"));
        bar3 = new Image(statusUITextureAtlas.findRegion("Bar"));

        hpLabel = new Label(" hp:", statusUISkin);
        hp = new Label(String.valueOf(hpVal), statusUISkin);
        mpLabel = new Label(" mp:", statusUISkin);
        mp = new Label(String.valueOf(mpVal), statusUISkin);
        xpLabel = new Label(" xp:", statusUISkin);
        xp = new Label(String.valueOf(xpVal), statusUISkin);
        levelLabel = new Label(" lv:", statusUISkin);
        levelValLabel = new Label(String.valueOf(levelVal), statusUISkin);
        iniLabel = new Label(" ini:", statusUISkin);
        iniValLabel = new Label(String.valueOf(iniVal), statusUISkin);

        heroButton= new ImageButton(statusUISkin, "inventory-button");
        heroButton.getImageCell().size(32, 32);
    }
    
    private void configureElements() {
        hpBar.setPosition(3, 6);
        mpBar.setPosition(3, 6);
        xpBar.setPosition(3, 6);

        group.addActor(bar);
        group.addActor(hpBar);
        group2.addActor(bar2);
        group2.addActor(mpBar);
        group3.addActor(bar3);
        group3.addActor(xpBar);

        defaults().expand().fill();
    }
    
    private void addElementsToWindow() {
        this.add(heroNameLabel);

        //account for the title padding
        this.pad(this.getPadTop() + 10, 10, 10, 10);

        this.add();
        this.add();
        this.add(heroButton).align(Align.right);
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
        this.add(levelValLabel).align(Align.left);
        this.row();
        
        this.add(iniLabel).align(Align.left);
        this.add(iniValLabel).align(Align.left);
        this.row();

        this.pack();
    }

    public ImageButton getInventoryButton() {
        return heroButton;
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
		statsUIOffsetX = (int) Map.TILE_WIDTH_PIXEL;
		statsUIOffsetY = (int) Map.TILE_HEIGHT_PIXEL;
		int scaledHeight = (int) (STATUS_UI_HEIGHT * Gdx.graphics.getHeight());
		int scaledWidth = (int) (STATUS_UI_WIDTH * Gdx.graphics.getWidth());
		this.setSize(BASE_WIDTH + scaledWidth, BASE_HEIGHT + scaledHeight);
	}
}

