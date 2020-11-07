package com.mygdx.game.profile;

import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class ProfileManager extends ProfileSubject {
	private static final String TAG = ProfileManager.class.getSimpleName();
    private Json json;
    private static ProfileManager profileManager;
    private HashMap<String,FileHandle> profilesWithFile = null;
    private ObjectMap<String, Object> profileProperties = new ObjectMap<String, Object>();
    private String profileName;

    private static final String SAVEGAME_SUFFIX = ".sav";
    public static final String DEFAULT_PROFILE = "default";

    private ProfileManager(){
        json = new Json();
        profilesWithFile = new HashMap<String,FileHandle>();
        profilesWithFile.clear();
        profileName = DEFAULT_PROFILE;
        storeAllProfiles();
    }

    public static final ProfileManager getInstance(){
        if( profileManager == null){
            profileManager = new ProfileManager();
        }
        return profileManager;
    }

    public Array<String> getProfileList(){
        Array<String> profiles = new Array<String>();
        for (Iterator<String> e = profilesWithFile.keySet().iterator(); e.hasNext();){
            profiles.add(e.next());
        }
        return profiles;
    }

    public FileHandle getProfileFile(String profile){
        if( !doesProfileExist(profile) ){
            return null;
        }
        return profilesWithFile.get(profile);
    }

    public void storeAllProfiles(){
        if( Gdx.files.isLocalStorageAvailable() ){
            FileHandle[] files = Gdx.files.local(".").list(SAVEGAME_SUFFIX);

            for(FileHandle file: files) {
                profilesWithFile.put(file.nameWithoutExtension(), file);
            }
        }else{
            //try external directory
        }
    }

    public boolean doesProfileExist(String profName){
        return profilesWithFile.containsKey(profName);
    }

    public void writeProfileToStorage(String profName, String fileData, boolean overwrite){
        String fullFilename = profName+SAVEGAME_SUFFIX;

        boolean localFileExists = Gdx.files.internal(fullFilename).exists();

        //If we cannot overwrite and the file exists, exit
        if( localFileExists && !overwrite ){
            return;
        }

        FileHandle file =  null;

        if( Gdx.files.isLocalStorageAvailable() ) {
            file = Gdx.files.local(fullFilename);
            file.writeString(fileData, !overwrite);
            
            profilesWithFile.put(profName, file);
        }
    }

    public void setProperty(String key, Object object){
        profileProperties.put(key, object);
    }

    public <T extends Object> T getProperty(String key){
        T property = null;
        if( !profileProperties.containsKey(key) ){
            return property;
        }
        property = (T)profileProperties.get(key);
        return property;
    }

    public void saveProfile(){
        notify(this, ProfileObserver.ProfileEvent.SAVING_PROFILE);
        String text = json.prettyPrint(json.toJson(profileProperties));
        writeProfileToStorage(profileName, text, true);
    }

    public void loadProfile(){
        String fullProfileFileName = profileName+SAVEGAME_SUFFIX;
        boolean doesProfileFileExist = Gdx.files.internal(fullProfileFileName).exists();

        if( !doesProfileFileExist ){
        	Gdx.app.debug(TAG, "File doesn't exist!");
            return;
        }

        profileProperties = json.fromJson(ObjectMap.class, profilesWithFile.get(profileName));
        notify(this, ProfileObserver.ProfileEvent.PROFILE_LOADED);
    }

    public void setCurrentProfile(String profName){
        if( doesProfileExist(profName) ){
            profileName = profName;
        }else{
            profileName = DEFAULT_PROFILE;
        }
    }

}
