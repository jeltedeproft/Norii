package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jelte.norii.entities.EntityObserver.EntityCommand;
import com.jelte.norii.entities.PlayerEntity;

public class SkipActionUIButton extends ActionUIButton {
	public SkipActionUIButton(final ActionsUi actionsUi, final String imageFileName, final PlayerEntity linkedUnit, int mapWidth, int mapHeight) {
		super(imageFileName);
		active = true;
		actionName = "Skip";
		initPopUp(mapWidth, mapHeight);

		button.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				linkedUnit.setActive(false);
				linkedUnit.setFocused(false);
				linkedUnit.setLocked(false);
				linkedUnit.setAp(linkedUnit.getEntityData().getMaxAP());
				actionsUi.setVisible(false);
				linkedUnit.notifyEntityObserver(EntityCommand.SKIP);
			}
		});
	}
}
