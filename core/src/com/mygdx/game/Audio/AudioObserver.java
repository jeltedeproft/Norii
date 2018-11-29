package com.mygdx.game.Audio;

public interface AudioObserver {
    public static enum AudioTypeEvent{
        MUSIC_BATTLE("audio/battleTheme2.mp3"),
        MUSIC_TITLE("audio/mainMenu.mp3"),
        NONE("");

        private String _audioFullFilePath;

        AudioTypeEvent(String audioFullFilePath){
            this._audioFullFilePath = audioFullFilePath;
        }

        public String getValue(){
            return _audioFullFilePath;
        }
    }

    public static enum AudioCommand {
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
