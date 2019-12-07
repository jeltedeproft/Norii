package com.mygdx.game.UI;

import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.Map;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class PortraitsUI extends HorizontalGroup {
	private static final String TAG = PortraitsUI.class.getSimpleName();

	private ArrayList<PortraitUI> portraits;
	private ArrayList<Stack> stacks;
	private Entity[] entities;

	private static final float PORTRAITS_TOP_PADDING = 3;
	private static final float PORTRAIT_WIDTH = 3.0f;
	private static final float PORTRAIT_HEIGHT = 3.0f;
	
	private float portraitsHeight;
	private float portraitsWidth;


	public PortraitsUI(Entity[] entities){
		initializeVariables(entities);
		updateSizeContainer();
		updateSizePortraits();
	}
	
	private void initializeVariables(Entity[] entities) {
		this.entities = entities;
		portraits = new ArrayList<PortraitUI>();
		stacks = new ArrayList<Stack>();
		this.setTransform(true);
		this.expand(true);
		this.fill();
		createPortraits(entities);
		adjustPosition();
	}
	
	private void createPortraits(Entity[] entities) {
		for(Entity entity : entities) {
			PortraitUI portrait = new PortraitUI(entity);
			portraits.add(portrait);
			
			Stack stack = new Stack();
			stack.addActor(portrait.getHeroPortrait());
			stack.addActor(portrait.getHeroPortraitBorder());
			stacks.add(stack);
			this.addActor(stack);
		}
	}
	
	private void adjustPosition() {
		float currentPortraitsHeight = PORTRAITS_TOP_PADDING * Map.TILE_HEIGHT_PIXEL;
		float xPos = 0;
		float yPos = Gdx.graphics.getHeight() - currentPortraitsHeight;
		this.setPosition(xPos, yPos);
	}

	public void updateSizeContainer() {
		float scaledHeight = (int) (PORTRAIT_HEIGHT * Map.TILE_HEIGHT_PIXEL);
		float scaledWidth = (int) (PORTRAIT_WIDTH * Map.TILE_WIDTH_PIXEL);
		portraitsHeight = scaledHeight;
		portraitsWidth = entities.length * scaledWidth;
		this.setSize(portraitsWidth, portraitsHeight);
		
		updateSizePortraits();
	}
	
	private void updateSizePortraits() {
		float newWidth = portraitsWidth / entities.length;
		for(PortraitUI portrait : portraits) {
			portrait.getHeroPortraitScalable().setMinHeight(portraitsHeight);
			portrait.getHeroPortraitScalable().setMinWidth(newWidth);
			portrait.getHeroPortraitScalableBorder().setMinHeight(portraitsHeight);
			portrait.getHeroPortraitScalableBorder().setMinWidth(newWidth);
		}
		for(Stack stack : stacks) {
			stack.setSize(newWidth, portraitsHeight);
		}
		adjustPosition();
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


