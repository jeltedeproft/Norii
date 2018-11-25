package com.mygdx.game.Screen;

import com.badlogic.gdx.Screen;

public enum ScreenEnum {
	 
    MAIN_MENU {
        public Screen getScreen(Object... params) {
            return new MainMenuScreen(params);
        }
    },
    BATTLE {
        public Screen getScreen(Object... params) {
            return new BattleScreen(params);
        }
    };
 
    public abstract Screen getScreen(Object... params);
}