package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.jelte.norii.battle.ApFileReader;
import com.jelte.norii.utility.AssetManagerUtility;

public class ApBar {
	private int oldAp;
	private final HorizontalGroup allAP;
	private final Table table;

	private final float tilePixelWidth;
	private final float tilePixelHeight;

	public ApBar(int mapWidth, int mapHeight) {
		tilePixelWidth = Hud.UI_VIEWPORT_WIDTH / mapWidth;
		tilePixelHeight = Hud.UI_VIEWPORT_HEIGHT / mapHeight;

		allAP = new HorizontalGroup();
		table = new Table();
		table.setBackground(AssetManagerUtility.getSkin().getDrawable("window-noborder"));
		table.setTransform(false);
		table.pad(0);
		table.setPosition(0, tilePixelHeight * (mapHeight - 2));
		final Skin statusUISkin = AssetManagerUtility.getSkin();

		for (int i = 0; i < ApFileReader.maxAp; i++) {
			final Button button = new Button(statusUISkin, "ap");
			allAP.addActor(button);
		}
		table.add(allAP).width(500).height(100);
		table.validate();
		table.invalidateHierarchy();
		table.pack();
	}

	public Table getTable() {
		return table;
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
