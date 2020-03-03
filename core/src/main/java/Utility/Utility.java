
package Utility;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.mygdx.game.Map.MyNavTmxMapLoader;

public final class Utility {
	private static final String TAG = Utility.class.getSimpleName();
	
	private static final String STATUSUI_TEXTURE_ATLAS_PATH = "skins/statusui.atlas";
	private static final String UISKIN_TEXTURE_ATLAS_PATH = "skins/uiskin.atlas";
	private static final String STATUSUI_SKIN_PATH = "skins/statusui.json";
	private static final String SHADOW_WALKER_SKIN_TEXTURE_ATLAS_PATH = "skins/shadowWalker/shadow-walker-ui.atlas";
	private static final String SHADOW_WALKER_SKIN_PATH = "skins/shadowWalker/shadow-walker-ui.json";
	public static final String ON_TILE_HOVER_FILE_PATH = "sprites/gui/selectedTile.png";
	
	public static final AssetManager assetManager = new AssetManager();
	private static InternalFileHandleResolver filePathResolver =  new InternalFileHandleResolver();
	public static final Random random = new Random();

	private static TextureAtlas statusUITextureAtlas;
	private static TextureAtlas uiTextureAtlas;
	private static TextureAtlas shadowWalkerTextureAtlas;
	private static MySkin statusUISkin;
	private static MySkin shadowWalkerSkin;


	public static void loadMapAsset(String mapFilenamePath){
		loadAsset(mapFilenamePath, TiledMap.class, new MyNavTmxMapLoader(filePathResolver));
	}

	public static TiledMap getMapAsset(String mapFilenamePath){
		return (TiledMap) getAsset(mapFilenamePath,TiledMap.class);
	}

	
	public static void loadTextureAsset(String textureFilenamePath){
		loadAsset(textureFilenamePath, Texture.class, new TextureLoader(filePathResolver));
	}

	public static Texture getTextureAsset(String textureFilenamePath){
		return (Texture) getAsset(textureFilenamePath,Texture.class);
	}
	
	public static void loadParticleAsset(String particleFilenamePath){
		loadAsset(particleFilenamePath, ParticleEffect.class, new ParticleEffectLoader(filePathResolver));
	}

	public static ParticleEffect  getParticleAsset(String particleFilenamePath){
		return (ParticleEffect) getAsset(particleFilenamePath,ParticleEffect.class);
	}

	
	public static void loadSoundAsset(String soundFilenamePath){
		loadAsset(soundFilenamePath, Sound.class, new SoundLoader(filePathResolver));
	}


	public static Sound getSoundAsset(String soundFilenamePath){
		return (Sound) getAsset(soundFilenamePath,Sound.class);
	}

	
	public static void loadMusicAsset(String musicFilenamePath){
		loadAsset(musicFilenamePath, Music.class, new MusicLoader(filePathResolver));
	}


	public static Music getMusicAsset(String musicFilenamePath){
		return (Music) getAsset(musicFilenamePath,Music.class);
	}	
	
	public static void loadFreeTypeFontAsset(String fontName, int size){
		if(checkValidString(fontName)){
			assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(filePathResolver));
			assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(filePathResolver));
			
			FreeTypeFontLoaderParameter myFont = new FreeTypeFontLoaderParameter();
			myFont.fontFileName = fontName;
			myFont.fontParameters.size = size;
			myFont.fontParameters.borderWidth = 0.5f;
			myFont.fontParameters.color = Color.LIGHT_GRAY;
			myFont.fontParameters.shadowOffsetX = 2;
			myFont.fontParameters.shadowOffsetY = 2;
			assetManager.load(fontName, BitmapFont.class, myFont);
		}
	}
	
	public static BitmapFont getFreeTypeFontAsset(String fontName){
		return (BitmapFont) getAsset(fontName,BitmapFont.class);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void loadAsset(String assetName,Class className,AssetLoader loader) {
		if(checkValidString(assetName)){
			if( filePathResolver.resolve(assetName).exists() ){
				assetManager.setLoader(className, loader);
				assetManager.load(assetName, className);
				assetManager.finishLoadingAsset(assetName);//block
				Gdx.app.debug(TAG, className.getSimpleName() +  " loaded: " + assetName);
			}
			else{
				Gdx.app.debug(TAG, className.getSimpleName() +  " doesn't exist: " + assetName );
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object getAsset(String filenamePath,Class className){
		Object object = null;

		if( assetManager.isLoaded(filenamePath) ){
			object = assetManager.get(filenamePath,className);
		} else {
			Gdx.app.debug(TAG, className.getSimpleName() +  " is not loaded: " + filenamePath );
		}

		return object;
	}
	
	public static void unloadAsset(String assetFilenamePath){
		if( assetManager.isLoaded(assetFilenamePath) ){
			assetManager.unload(assetFilenamePath);
		} else {
			Gdx.app.debug(TAG, "Asset is not loaded; Nothing to unload: " + assetFilenamePath );
		}
	}

	public static TextureAtlas getStatusUITextureAtlas() {
		if(statusUITextureAtlas == null) {
			statusUITextureAtlas = new TextureAtlas(STATUSUI_TEXTURE_ATLAS_PATH);
		}

		return statusUITextureAtlas;
	}

	public static TextureAtlas getUITextureAtlas() {
		if(uiTextureAtlas == null) {
			uiTextureAtlas = new TextureAtlas(UISKIN_TEXTURE_ATLAS_PATH);
		}

		return uiTextureAtlas;
	}
	
	public static TextureAtlas getShadowWalkerTextureAtlas() {
		if(shadowWalkerTextureAtlas == null) {
			shadowWalkerTextureAtlas = new TextureAtlas(SHADOW_WALKER_SKIN_TEXTURE_ATLAS_PATH);
		}
		
		return shadowWalkerTextureAtlas;
	}

	public static MySkin getStatusUISkin() {
		if(statusUISkin == null) {
			statusUISkin = new MySkin(Gdx.files.internal(STATUSUI_SKIN_PATH),getStatusUITextureAtlas());
		}

		return statusUISkin;
	}
	
	public static MySkin getShadowWalkersUISkin() {
		if(shadowWalkerSkin == null) {
			shadowWalkerSkin = new MySkin(Gdx.files.internal(SHADOW_WALKER_SKIN_PATH),getShadowWalkerTextureAtlas());
		}
		
		return shadowWalkerSkin;
	}

	public static float loadCompleted(){
		return assetManager.getProgress();
	}

	public static int numberAssetsQueued(){
		return assetManager.getQueuedAssets();
	}

	public static boolean updateAssetLoading(){
		return assetManager.update();
	}

	public static boolean isAssetLoaded(String fileName){
		return assetManager.isLoaded(fileName);

	}

	private static boolean checkValidString(String string) {
		return(!( string == null || string.isEmpty()));
	}
	
	public static int getRandomIntFrom1to(int to) {
		int result = random.nextInt(to);
		return result + 1;
	}
	
	private Utility() {

	}
}
