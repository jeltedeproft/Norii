package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MoveActionUIButton extends ActionUIButton {
	Hud hud;
	int entityID;

	public MoveActionUIButton(final String imageFileName, final int entityID, int mapWidth, int mapHeight, Hud hud) {
		super(imageFileName);
		active = true;
		actionName = "Move";
		initPopUp(mapWidth, mapHeight);
		this.hud = hud;
		this.entityID = entityID;

		button.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				hud.getEntityIdWithActionUi().get(entityID).setVisible(false);
				hud.sendMessage(MessageToBattleScreen.CLICKED_ON_MOVE, entityID, null);
			}
		});
	}

	@Override
	public void show() {
		hud.sendMessage(MessageToBattleScreen.HOVERED_ON_MOVE, entityID, null);
		super.show();
	}

	@Override
	public void hide() {
		hud.sendMessage(MessageToBattleScreen.STOP_HOVERED_ON_MOVE, entityID, null);
		super.hide();
	}
}
