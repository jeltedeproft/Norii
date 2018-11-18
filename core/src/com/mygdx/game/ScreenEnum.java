package com.mygdx.game;

import com.badlogic.gdx.Screen;

public enum ScreenEnum {
	 
    MAIN_MENU {
        public Screen getScreen(Object... params) {
            return new MainMenuScreen();
        }
    },
    BATTLE {
        public Screen getScreen(Object... params) {
            return new BattleScreen();
        }
    };
 
    public abstract Screen getScreen(Object... params);
}