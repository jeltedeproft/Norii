package Utility;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.game.Map.MyNavTmxMapLoader;

public final class Utility {
	public static final AssetManager _assetManager = new AssetManager();
	private static final String TAG = Utility.class.getSimpleName();
	private static InternalFileHandleResolver filePathResolver =  new InternalFileHandleResolver();
	public static final Random random = new Random();
	
	private static final String STATUSUI_TEXTURE_ATLAS_PATH = "skins/statusui.atlas";
	private static final String STATUSUI_SKIN_PATH = "skins/statusui.json";
	public static final String ON_TILE_HOVER_FILE_PATH = "sprites/gui/selectedTile.png";
	
	private static TextureAtlas statusUITextureAtlas;
	private static Skin statusUISkin;

	private Utility() {
		
	}
	
	public static TextureAtlas getStatusUITextureAtlas() {
		if(statusUITextureAtlas == null) {
			statusUITextureAtlas = new TextureAtlas(STATUSUI_TEXTURE_ATLAS_PATH);
		}
		
		return statusUITextureAtlas;
	}
	
	public static Skin getStatusUISkin() {
		if(statusUISkin == null) {
			statusUISkin = new Skin(Gdx.files.internal(STATUSUI_SKIN_PATH),getStatusUITextureAtlas());
		}
		
		return statusUISkin;
	}
	
	public static void unloadAsset(String assetFilenamePath){
	// once the asset manager is done loading
	if( _assetManager.isLoaded(assetFilenamePath) ){
		_assetManager.unload(assetFilenamePath);
		} else {
			Gdx.app.debug(TAG, "Asset is not loaded; Nothing to unload: " + assetFilenamePath );
		}
	}

	public static float loadCompleted(){
		return _assetManager.getProgress();
	}

	public static int numberAssetsQueued(){
		return _assetManager.getQueuedAssets();
	}

   	public static boolean updateAssetLoading(){
		return _assetManager.update();
	}

	public static boolean isAssetLoaded(String fileName){
	   return _assetManager.isLoaded(fileName);

	}

	public static void loadMapAsset(String mapFilenamePath){
	   if( mapFilenamePath == null || mapFilenamePath.isEmpty() ){
		   return;
	   }

	   //load asset
		if( filePathResolver.resolve(mapFilenamePath).exists() ){
			_assetManager.setLoader(TiledMap.class, new MyNavTmxMapLoader(filePathResolver));
			_assetManager.load(mapFilenamePath, TiledMap.class);
			//Until we add loading screen, just block until we load the map
			_assetManager.finishLoadingAsset(mapFilenamePath);
			Gdx.app.debug(TAG, "Map loaded!: " + mapFilenamePath);
		}
		else{
			Gdx.app.debug(TAG, "Map doesn't exist!: " + mapFilenamePath );
		}
	}


	public static TiledMap getMapAsset(String mapFilenamePath){
		TiledMap map = null;

		// once the asset manager is done loading
		if( _assetManager.isLoaded(mapFilenamePath) ){
			map = _assetManager.get(mapFilenamePath,TiledMap.class);
		} else {
			Gdx.app.debug(TAG, "Map is not loaded: " + mapFilenamePath );
		}

		return map;
	}

	public static void loadTextureAsset(String textureFilenamePath){
		if( textureFilenamePath == null || textureFilenamePath.isEmpty() ){
			return;
		}
		//load asset
		if( filePathResolver.resolve(textureFilenamePath).exists() ){
			_assetManager.setLoader(Texture.class, new TextureLoader(filePathResolver));
			_assetManager.load(textureFilenamePath, Texture.class);
			//Until we add loading screen, just block until we load the map
			_assetManager.finishLoadingAsset(textureFilenamePath);
		}
		else{
			Gdx.app.debug(TAG, "Texture doesn't exist!: " + textureFilenamePath );
		}
	}

	public static Texture getTextureAsset(String textureFilenamePath){
		Texture texture = null;

		// once the asset manager is done loading
		if( _assetManager.isLoaded(textureFilenamePath) ){
			texture = _assetManager.get(textureFilenamePath,Texture.class);
		} else {
			Gdx.app.debug(TAG, "Texture is not loaded: " + textureFilenamePath );
		}

		return texture;
	}
	
	public static void loadParticleAsset(String particleFilenamePath){
		if( particleFilenamePath == null || particleFilenamePath.isEmpty() ){
			return;
		}
		//load asset
		if( filePathResolver.resolve(particleFilenamePath).exists() ){
			_assetManager.setLoader(ParticleEffect.class, new ParticleEffectLoader(filePathResolver));
			_assetManager.load(particleFilenamePath, ParticleEffect.class);
			//block until we load the particle
			_assetManager.finishLoadingAsset(particleFilenamePath);
		}
		else{
			Gdx.app.debug(TAG, "particle doesn't exist!: " + particleFilenamePath );
		}
	}

	public static ParticleEffect  getParticleAsset(String particleFilenamePath){
		ParticleEffect particle = null;

		// once the asset manager is done loading
		if( _assetManager.isLoaded(particleFilenamePath) ){
			particle = _assetManager.get(particleFilenamePath,ParticleEffect.class);
			particle.start();
		} else {
			Gdx.app.debug(TAG, "particle is not loaded: " + particleFilenamePath );
		}
		return particle;
	}

	public static int getRandomIntFrom1to(int to) {
		int result = random.nextInt(to);
		return result + 1;
	}
	
	public static void loadSoundAsset(String soundFilenamePath){
		if( soundFilenamePath == null || soundFilenamePath.isEmpty() ){
			return;
		}

		if( _assetManager.isLoaded(soundFilenamePath) ){
			return;
		}

		//load asset
		if( filePathResolver.resolve(soundFilenamePath).exists() ){
			_assetManager.setLoader(Sound.class, new SoundLoader(filePathResolver));
			_assetManager.load(soundFilenamePath, Sound.class);
			//Until we add loading screen, just block until we load the map
			_assetManager.finishLoadingAsset(soundFilenamePath);
			Gdx.app.debug(TAG, "Sound loaded!: " + soundFilenamePath);
		}
		else{
			Gdx.app.debug(TAG, "Sound doesn't exist!: " + soundFilenamePath );
		}
	}


	public static Sound getSoundAsset(String soundFilenamePath){
		Sound sound = null;

		// once the asset manager is done loading
		if( _assetManager.isLoaded(soundFilenamePath) ){
			sound = _assetManager.get(soundFilenamePath,Sound.class);
		} else {
			Gdx.app.debug(TAG, "Sound is not loaded: " + soundFilenamePath );
		}

		return sound;
	}

	public static void loadMusicAsset(String musicFilenamePath){
		if( musicFilenamePath == null || musicFilenamePath.isEmpty() ){
			return;
		}

		if( _assetManager.isLoaded(musicFilenamePath) ){
			return;
		}

		//load asset
		if( filePathResolver.resolve(musicFilenamePath).exists() ){
			_assetManager.setLoader(Music.class, new MusicLoader(filePathResolver));
			_assetManager.load(musicFilenamePath, Music.class);
			//Until we add loading screen, just block until we load the map
			_assetManager.finishLoadingAsset(musicFilenamePath);
			Gdx.app.debug(TAG, "Music loaded!: " + musicFilenamePath);
		}
		else{
			Gdx.app.debug(TAG, "Music doesn't exist!: " + musicFilenamePath );
		}
	}


	public static Music getMusicAsset(String musicFilenamePath){
		Music music = null;

		// once the asset manager is done loading
		if( _assetManager.isLoaded(musicFilenamePath) ){
			music = _assetManager.get(musicFilenamePath,Music.class);
		} else {
			Gdx.app.debug(TAG, "Music is not loaded: " + musicFilenamePath );
		}

		return music;
	}	
}
