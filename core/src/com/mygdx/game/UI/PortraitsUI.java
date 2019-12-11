package com.mygdx.game.UI;

import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.Map;
import com.mygdx.game.Screen.BattleScreen;

import Utility.Utility;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;

public class PortraitsUI extends Window {
	private static final String TAG = PortraitsUI.class.getSimpleName();

	private ArrayList<PortraitUI> portraits;
	private ArrayList<Stack> stacks;
	private Entity[] entities;
	private HorizontalGroup hgroup;

	private static final float PORTRAITS_TOP_PADDING = 3;
	private static final float PORTRAIT_WIDTH = 3.0f;
	private static final float PORTRAIT_HEIGHT = 3.0f;
	private static final int ALPHA = 30; 
	
	private float portraitsHeight;
	private float portraitsWidth;

	public PortraitsUI(Entity[] entities){
		super("", Utility.getStatusUISkin());
		initializeVariables(entities);
		updateSizeContainer();
		updateSizePortraits();
	}
	
	private void initializeVariables(Entity[] entities) {
		this.entities = entities;
		portraits = new ArrayList<PortraitUI>();
		stacks = new ArrayList<Stack>();
		hgroup = new HorizontalGroup();
		
		this.setTransform(true);
		this.align(Align.bottomLeft);
		this.add(hgroup);
		this.pad(0);
		
        Color newColor = this.getColor();
        newColor.a = ALPHA;
        this.setColor(newColor);
		
		hgroup.setFillParent(true);
		hgroup.align(Align.bottomLeft);
		hgroup.pad(0);

		createPortraits(entities);
		updatePositionContainer();
	}
	
	private void createPortraits(Entity[] entities) {
		for(Entity entity : entities) {
			PortraitUI portrait = new PortraitUI(entity);
			portraits.add(portrait);
			
			Stack stack = new Stack();
			stack.addActor(portrait.getHeroPortrait());
			stack.addActor(portrait.getHeroPortraitBorder());
			stacks.add(stack);
			hgroup.addActor(stack);
		}
	}

	public void updateSizeContainer() {
		portraitsHeight = PORTRAIT_HEIGHT ;
		portraitsWidth = 50;
		this.setSize(portraitsWidth, portraitsHeight);
		
		updateSizePortraits();
	}
	
	private void updateSizePortraits() {
		updatePositionContainer();
		float newWidth = PORTRAIT_WIDTH ;
		for(PortraitUI portrait : portraits) {
			portrait.getHeroPortraitScalable().setMinHeight(portraitsHeight);
			portrait.getHeroPortraitScalable().setMinWidth(newWidth);
			portrait.getHeroPortraitScalableBorder().setMinHeight(portraitsHeight);
			portrait.getHeroPortraitScalableBorder().setMinWidth(newWidth);
		}
		for(Stack stack : stacks) {
			stack.setSize(newWidth, portraitsHeight);
		}
	}
	
	private void updatePositionContainer() {
		float currentPortraitsHeight = PORTRAITS_TOP_PADDING ;
		float xPos = 0;
		float yPos = 50 - currentPortraitsHeight;
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


