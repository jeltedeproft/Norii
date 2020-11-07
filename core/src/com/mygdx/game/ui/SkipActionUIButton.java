package com.mygdx.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.entities.EntityObserver.EntityCommand;
import com.mygdx.game.entities.PlayerEntity;

public class SkipActionUIButton extends ActionUIButton {
	public SkipActionUIButton(final ActionsUI ui, final String imageFileName, final PlayerEntity linkedUnit) {
		super(imageFileName);
		active = true;
		infotext = "use this button to \n skip your turn";
		actionName = "Skip";
		initPopUp();

		button.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				linkedUnit.setActive(false);
				linkedUnit.setFocused(false);
				linkedUnit.setLocked(false);
				linkedUnit.setAp(linkedUnit.getEntityData().getMaxAP());
				ui.setVisible(false);
				linkedUnit.notifyEntityObserver(EntityCommand.SKIP);
			}
		});
	}
}
