package Utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Map.MyNavTmxMapLoader;
import com.mygdx.game.UI.MySkin;

public class AssetManagerUtility {
	private static final String TAG = AssetManagerUtility.class.getSimpleName();

	public static final String SPRITES_ATLAS_PATH = "sprites/Norii.atlas";
	public static final String SKIN_TEXTURE_ATLAS_PATH = "skins/norii.atlas";
	public static final String SKIN_JSON_PATH = "skins/norii.json";
	public static final String ON_TILE_HOVER_FILE_PATH = "sprites/gui/selectedTile.png";

	public static final AssetManager assetManager = new AssetManager();
	private static InternalFileHandleResolver filePathResolver = new InternalFileHandleResolver();
	private static MySkin statusUISkin;

	public static void loadMapAsset(final String mapFilenamePath) {
		loadAsset(mapFilenamePath, TiledMap.class, new MyNavTmxMapLoader(filePathResolver));
	}

	public static TiledMap getMapAsset(final String mapFilenamePath) {
		return (TiledMap) getAsset(mapFilenamePath, TiledMap.class);
	}

	public static void loadTextureAsset(final String textureFilenamePath) {
		loadAsset(textureFilenamePath, Texture.class, new TextureLoader(filePathResolver));
	}

	public static Texture getTextureAsset(final String textureFilenamePath) {
		return (Texture) getAsset(textureFilenamePath, Texture.class);
	}

	public static void loadParticleAsset(final String particleFilenamePath) {
		loadAsset(particleFilenamePath, ParticleEffect.class, new ParticleEffectLoader(filePathResolver));
	}

	public static ParticleEffect getParticleAsset(final String particleFilenamePath) {
		return (ParticleEffect) getAsset(particleFilenamePath, ParticleEffect.class);
	}

	public static void loadSoundAsset(final String soundFilenamePath) {
		loadAsset(soundFilenamePath, Sound.class, new SoundLoader(filePathResolver));
	}

	public static Sound getSoundAsset(final String soundFilenamePath) {
		return (Sound) getAsset(soundFilenamePath, Sound.class);
	}

	public static void loadMusicAsset(final String musicFilenamePath) {
		loadAsset(musicFilenamePath, Music.class, new MusicLoader(filePathResolver));
	}

	public static Music getMusicAsset(final String musicFilenamePath) {
		return (Music) getAsset(musicFilenamePath, Music.class);
	}

	public static void loadTextureAtlas(final String textureAtlasFilenamePath) {
		loadAsset(textureAtlasFilenamePath, TextureAtlas.class, new TextureAtlasLoader(filePathResolver));
	}

	public static TextureAtlas getTextureAtlas(final String textureAtlasFilenamePath) {
		return (TextureAtlas) getAsset(textureAtlasFilenamePath, TextureAtlas.class);
	}

	public static void loadSkin(final String skinFilenamePath) {
		loadAsset(skinFilenamePath, Skin.class, new SkinLoader(filePathResolver));
	}

	public static MySkin getSkin() {
		if (statusUISkin == null) {
			statusUISkin = new MySkin(Gdx.files.internal(SKIN_JSON_PATH), getTextureAtlas(SKIN_TEXTURE_ATLAS_PATH));
		}

		return statusUISkin;
	}

	public static Animation<TextureRegion> getGIFAsset(final String gifFilenamePath) {
		return GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal(gifFilenamePath).read());
	}

	public static void loadFreeTypeFontAsset(final String fontPath, final int size, final int borderWidth, final Color color, final int shadowX, final int shadowY) {
		if (checkValidString(fontPath)) {
			assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(filePathResolver));
			assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(filePathResolver));

			final FreeTypeFontLoaderParameter myFont = createFontParameter(fontPath, size, borderWidth, color, shadowX, shadowY);
			assetManager.load(fontPath, BitmapFont.class, myFont);
		}
	}

	private static FreeTypeFontLoaderParameter createFontParameter(final String fontPath, final int size, final int borderWidth, final Color color, final int shadowX, final int shadowY) {
		final FreeTypeFontLoaderParameter myFont = new FreeTypeFontLoaderParameter();
		myFont.fontFileName = fontPath.substring(3);
		myFont.fontParameters.size = size;
		myFont.fontParameters.borderWidth = borderWidth;
		myFont.fontParameters.color = color;
		myFont.fontParameters.shadowOffsetX = shadowX;
		myFont.fontParameters.shadowOffsetY = shadowY;
		return myFont;
	}

	public static BitmapFont getFreeTypeFontAsset(final String fontPath) {
		return (BitmapFont) getAsset(fontPath, BitmapFont.class);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void loadAsset(final String assetName, final Class className, final AssetLoader loader) {
		if ((!isAssetLoaded(assetName)) && (checkValidString(assetName))) {
			if (filePathResolver.resolve(assetName).exists()) {
				assetManager.setLoader(className, loader);
				assetManager.load(assetName, className);
				assetManager.finishLoadingAsset(assetName);// block
				Gdx.app.debug(TAG, className.getSimpleName() + " loaded: " + assetName);
			} else {
				Gdx.app.debug(TAG, className.getSimpleName() + " doesn't exist: " + assetName);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object getAsset(final String filenamePath, final Class className) {
		Object object = null;

		if (assetManager.isLoaded(filenamePath)) {
			object = assetManager.get(filenamePath, className);
		} else {
			Gdx.app.debug(TAG, className.getSimpleName() + " is not loaded: " + filenamePath);
		}

		return object;
	}

	public static void unloadAsset(final String assetFilenamePath) {
		if (assetManager.isLoaded(assetFilenamePath)) {
			assetManager.unload(assetFilenamePath);
		} else {
			Gdx.app.debug(TAG, "Asset is not loaded; Nothing to unload: " + assetFilenamePath);
		}
	}

	public static Sprite getSprite(String spriteName) {
		return getTextureAtlas(SPRITES_ATLAS_PATH).createSprite(spriteName);
	}

	public static Texture getSpriteSheetTexture(String textureName) {
		return getTextureAtlas(SPRITES_ATLAS_PATH).findRegion(textureName).getTexture();
	}

	public static Animation<TextureRegion> getAnimation(String animationName, float animationSpeed) {
		Array<TextureAtlas.AtlasRegion> regions = getTextureAtlas(SPRITES_ATLAS_PATH).findRegions(animationName);

		if (regions == null) {
			return null;
		} else {
			return new Animation<>(animationSpeed, regions);
		}
	}

	public static float loadCompleted() {
		return assetManager.getProgress();
	}

	public static int numberAssetsQueued() {
		return assetManager.getQueuedAssets();
	}

	public static boolean updateAssetLoading() {
		return assetManager.update();
	}

	public static boolean isAssetLoaded(final String fileName) {
		return assetManager.isLoaded(fileName);
	}

	private static boolean checkValidString(final String string) {
		return (!(string == null || string.isEmpty()));
	}

	private AssetManagerUtility() {

	}

}
