package com.jelte.norii.audio;

public enum AudioTypeEvent {
	MUSIC_BATTLE("audio/battleTheme3.wav"),
	MUSIC_TITLE("audio/main2.wav"),
	MUSIC_TITLE2("audio/mainMenu2.ogg"),
	ATTACK_SOUND("audio/attack.wav"),
	SPELL_SOUND("audio/spell.wav"),
	FIREBALL_SOUND("audio/fireball.mp3"),
	SWAP_SOUND("audio/swap.mp3"),
	STONE_SOUND("audio/stone.mp3"),
	HAMMER_SOUND("audio/hammer.mp3"),
	WALK_LOOP("audio/walkloop.wav"),
	NONE("");

	private final String audioFullFilePath;

	AudioTypeEvent(final String audioFullFilePath) {
		this.audioFullFilePath = audioFullFilePath;
	}

	public String getValue() {
		return audioFullFilePath;
	}
}
