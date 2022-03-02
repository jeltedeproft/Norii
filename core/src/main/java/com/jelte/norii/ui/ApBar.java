package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.jelte.norii.battle.ApFileReader;
import com.jelte.norii.utility.AssetManagerUtility;

public class ApBar extends Table {
	private int oldAp;
	private final HorizontalGroup allAP;

	public ApBar() {
		allAP = new HorizontalGroup();
		setBackground(AssetManagerUtility.getSkin().getDrawable("window-noborder"));
		setTransform(false);
		pad(0);
		final Skin statusUISkin = AssetManagerUtility.getSkin();

		for (int i = 0; i < ApFileReader.maxAp; i++) {
			final Button button = new Button(statusUISkin, "ap");
			allAP.addActor(button);
		}
		add(allAP).width(Hud.UI_VIEWPORT_WIDTH).height(Hud.UI_VIEWPORT_HEIGHT * 0.1f);
		validate();
		invalidateHierarchy();
		pack();
	}

	public void update(int currentAp) {
		if (oldAp != currentAp) {
			for (final Actor actor : allAP.getChildren()) {
				final Button button = (Button) actor;
				button.setChecked(false);
			}
			if (currentAp != 0) {
				for (int i = 0; i < currentAp; i++) {
					final Button button = (Button) allAP.getChild(i);
					button.setChecked(true);
				}
			}
			oldAp = currentAp;
		}
	}
}
