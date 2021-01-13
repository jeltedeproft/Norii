package com.jelte.norii.audio;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.jelte.norii.utility.AssetManagerUtility;

public class AudioManager {
	private static final String TAG = AudioManager.class.getSimpleName();

	private static AudioManager instance = null;

	private final HashMap<String, Music> queuedMusic;
	private final HashMap<String, Sound> queuedSounds;

	private AudioManager() {
		queuedMusic = new HashMap<>();
		queuedSounds = new HashMap<>();
	}

	public static AudioManager getInstance() {
		if (instance == null) {
			instance = new AudioManager();
		}

		return instance;
	}

	public void onNotify(AudioCommand command, AudioTypeEvent event) {
		switch (command) {
		case MUSIC_LOAD:
			AssetManagerUtility.loadMusicAsset(event.getValue());
			break;
		case MUSIC_PLAY_ONCE:
			playMusic(false, event.getValue());
			break;
		case MUSIC_PLAY_LOOP:
			playMusic(true, event.getValue());
			break;
		case MUSIC_STOP:
			final Music music = queuedMusic.get(event.getValue());
			if (music != null) {
				music.stop();
			}
			break;
		case MUSIC_STOP_ALL:
			for (final Music musicStop : queuedMusic.values()) {
				musicStop.stop();
			}
			break;
		case SOUND_LOAD:
			AssetManagerUtility.loadSoundAsset(event.getValue());
			break;
		case SOUND_PLAY_LOOP:
			playSound(true, event.getValue());
			break;
		case SOUND_PLAY_ONCE:
			playSound(false, event.getValue());
			break;
		case SOUND_STOP:
			final Sound sound = queuedSounds.get(event.getValue());
			if (sound != null) {
				sound.stop();
			}
			break;
		case MUSIC_PAUSE:
			final Music musicToPause = queuedMusic.get(event.getValue());
			if (musicToPause != null) {
				musicToPause.pause();
			}
			break;
		case MUSIC_RESUME:
			final Music musicToResume = queuedMusic.get(event.getValue());
			if (musicToResume != null) {
				musicToResume.play();
			}
			break;
		default:
			break;
		}
	}

	private Music playMusic(boolean isLooping, String fullFilePath) {
		Music music = queuedMusic.get(fullFilePath);
		if (music != null) {
			music.setLooping(isLooping);
			music.play();
		} else if (AssetManagerUtility.isAssetLoaded(fullFilePath)) {
			music = AssetManagerUtility.getMusicAsset(fullFilePath);
			music.setLooping(isLooping);
			music.play();
			queuedMusic.put(fullFilePath, music);
		} else {
			Gdx.app.debug(TAG, "Music not loaded");
			return null;
		}
		return music;
	}

	private Sound playSound(boolean isLooping, String fullFilePath) {
		Sound sound = queuedSounds.get(fullFilePath);
		if (sound != null) {
			final long soundId = sound.play();
			sound.setLooping(soundId, isLooping);
		} else if (AssetManagerUtility.isAssetLoaded(fullFilePath)) {
			sound = AssetManagerUtility.getSoundAsset(fullFilePath);
			final long soundId = sound.play();
			sound.setLooping(soundId, isLooping);
			queuedSounds.put(fullFilePath, sound);
		} else {
			Gdx.app.debug(TAG, "Sound not loaded");
			return null;
		}
		return sound;
	}

	public void dispose() {
		for (final Music music : queuedMusic.values()) {
			music.dispose();
		}

		for (final Sound sound : queuedSounds.values()) {
			sound.dispose();
		}
	}

}
