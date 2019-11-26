package com.mygdx.game.Audio;

public interface AudioObserver {
    public enum AudioTypeEvent{
        MUSIC_BATTLE("audio/battleTheme3.wav"),
        MUSIC_TITLE("audio/main2.wav"),
        NONE("");

        private String audioFullFilePath;

        AudioTypeEvent(String audioFullFilePath){
            this.audioFullFilePath = audioFullFilePath;
        }

        public String getValue(){
            return audioFullFilePath;
        }
    }

    public enum AudioCommand {
        MUSIC_LOAD,
        MUSIC_PLAY_ONCE,
        MUSIC_PLAY_LOOP,
        MUSIC_STOP,
        MUSIC_STOP_ALL,
        SOUND_LOAD,
        SOUND_PLAY_ONCE,
        SOUND_PLAY_LOOP,
        SOUND_STOP
    }

    void onNotify(AudioCommand command, AudioTypeEvent event);
}
