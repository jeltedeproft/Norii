package com.mygdx.game.UI;

import com.mygdx.game.Entities.Entity;
import Utility.Utility;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;

public class PortraitsUI extends Window {
	private ArrayList<PortraitUI> portraits;
	private ArrayList<Stack> stacks;
	private HorizontalGroup hgroup;

	private static final float PORTRAITS_TOP_PADDING = 3;
	private static final int PORTRAIT_WIDTH = 3;
	private static final int PORTRAIT_HEIGHT = 3;
	private static final int ALPHA = 80; 
	
    private static final int TILE_TO_PIXEL_RATIO = 30;
	
    private float tileWidthPixel;
    private float tileHeightPixel;
	private float portraitsHeight;
	private float portraitsWidth;

	public PortraitsUI(Entity[] entities){
		super("", Utility.getStatusUISkin());
		initializeVariables(entities);
		updateSizeContainer();
		updateSizePortraits();
	}
	
	private void initializeVariables(Entity[] entities) {
		portraits = new ArrayList<PortraitUI>();
		stacks = new ArrayList<Stack>();
		hgroup = new HorizontalGroup();
    	tileWidthPixel = Gdx.graphics.getWidth() / (float) TILE_TO_PIXEL_RATIO;
    	tileHeightPixel = Gdx.graphics.getHeight() / (float) TILE_TO_PIXEL_RATIO;
		
		configureWindow();
        setFadeEffectBackground();
		configureHorizontalGroup();
		createPortraits(entities);
		updatePositionContainer();
	}

	private void configureWindow() {
		this.setTransform(true);
		this.align(Align.bottomLeft);
		this.add(hgroup);
		this.pad(0);
	}

	private void setFadeEffectBackground() {
		Color newColor = this.getColor();
        newColor.a = ALPHA;
        this.setColor(newColor);
	}

	private void configureHorizontalGroup() {
		hgroup.setFillParent(true);
		hgroup.align(Align.bottomLeft);
		hgroup.pad(0);
	}
	
	private void createPortraits(Entity[] entities) {
		for(Entity entity : entities) {
			PortraitUI portrait = addPortrait(entity);		
			addStackPortraitBorder(portrait);
		}
	}

	private PortraitUI addPortrait(Entity entity) {
		PortraitUI portrait = new PortraitUI(entity);
		portraits.add(portrait);
		return portrait;
	}

	private void addStackPortraitBorder(PortraitUI portrait) {
		Stack stack = new Stack();
		stack.addActor(portrait.getHeroPortrait());
		stack.addActor(portrait.getHeroPortraitBorder());
		stacks.add(stack);
		
		float x = portrait.getHeroPortrait().getX();
		float y = portrait.getHeroPortrait().getY();
		stack.setBounds(x, y, PORTRAIT_WIDTH * tileWidthPixel, PORTRAIT_HEIGHT * tileHeightPixel);
		stack.addListener(new PortraitClickListener(portrait.getLinkedEntity()));
		hgroup.addActor(stack);
	}

	public void updateSizeContainer() {
    	tileWidthPixel = Gdx.graphics.getWidth() / (float) TILE_TO_PIXEL_RATIO;
    	tileHeightPixel = Gdx.graphics.getHeight() / (float) TILE_TO_PIXEL_RATIO;
		portraitsHeight = PORTRAIT_HEIGHT * tileHeightPixel;
		portraitsWidth = Gdx.graphics.getWidth();
		this.setSize(portraitsWidth, portraitsHeight);
		
		updateSizePortraits();
	}
	
	private void updateSizePortraits() {
		updatePositionContainer();		
		updatePortraits();		
		updateStacks();
	}
	
	private void updatePortraits() {
		for(PortraitUI portrait : portraits) {
			portrait.getHeroPortraitScalable().setMinHeight(PORTRAIT_HEIGHT * tileHeightPixel);
			portrait.getHeroPortraitScalable().setMinWidth(PORTRAIT_WIDTH * tileWidthPixel);
			portrait.getHeroPortraitScalableBorder().setMinHeight(PORTRAIT_HEIGHT * tileHeightPixel);
			portrait.getHeroPortraitScalableBorder().setMinWidth(PORTRAIT_WIDTH * tileWidthPixel);
		}
	}

	private void updateStacks() {
		for(Stack stack : stacks) {
			stack.setSize(PORTRAIT_WIDTH * tileWidthPixel, portraitsHeight);
		}
	}
	
	private void updatePositionContainer() {
		float currentPortraitsHeight = PORTRAITS_TOP_PADDING * tileHeightPixel;
		float xPos = 0;
		float yPos = Gdx.graphics.getHeight() - currentPortraitsHeight;
		this.setPosition(xPos, yPos);
	}

	public void updateBorders(Entity unit) {
		for(PortraitUI portrait : portraits) {
			portrait.getHeroPortraitBorder().setVisible(false);
			if(portrait.getLinkedEntity().getName().equalsIgnoreCase(unit.getName())) {
				portrait.getHeroPortraitBorder().setVisible(true);
			}
		}
	}
}


