package com.jelte.norii.screen;

import com.badlogic.gdx.Screen;
import com.jelte.norii.ai.EnemyType;
import com.jelte.norii.entities.UnitOwner;
import com.jelte.norii.map.MapFactory.MapType;

public enum ScreenEnum {

	MAIN_MENU {
		@Override
		public Screen getScreen(Object... params) {
			return new MainMenuScreen();
		}
	},
	BATTLE {
		@Override
		public Screen getScreen(Object... params) {
			final UnitOwner unitOwner = (UnitOwner) params[ScreenEnum.ScreenParams.UNIT_OWNER.ordinal()];
			final MapType mapType = (MapType) params[ScreenEnum.ScreenParams.MAP.ordinal()];
			return new BattleScreen(unitOwner, mapType);
		}
	},
	SETTINGS {
		@Override
		public Screen getScreen(Object... params) {
			return new SettingsScreen();
		}
	},
	TEAM {
		@Override
		public Screen getScreen(Object... params) {
			return new SetTeamScreen();
		}
	},
	MULTIPLAYER {
		@Override
		public Screen getScreen(Object... params) {
			return new MultiplayerScreen();
		}
	},
	LOGIN {
		@Override
		public Screen getScreen(Object... params) {
			return new LoginScreen();
		}
	};

	public abstract Screen getScreen(Object... params);

	public enum ScreenParams {
		UNIT_OWNER, MAP;
	}
}