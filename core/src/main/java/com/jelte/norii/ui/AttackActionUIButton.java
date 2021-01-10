package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class AttackActionUIButton extends ActionUIButton {
	public AttackActionUIButton(final String imageFileName, final int entityID, int mapWidth, int mapHeight, Hud hud) {
		super(imageFileName);
		active = true;
		actionName = "Attack";
		initPopUp(mapWidth, mapHeight);

		button.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				hud.getEntityIdWithActionUi().get(entityID).setVisible(false);
				hud.sendMessage(MessageToBattleScreen.CLICKED_ON_ATTACK, entityID, null);
			}
		});
	}

}
