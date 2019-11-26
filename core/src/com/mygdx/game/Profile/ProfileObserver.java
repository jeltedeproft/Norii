package com.mygdx.game.Profile;

public interface ProfileObserver {
    public enum ProfileEvent{
        PROFILE_LOADED,
        SAVING_PROFILE
    }

    void onNotify(final ProfileManager profileManager, ProfileEvent event);
}