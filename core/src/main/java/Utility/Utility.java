
package Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.TeamLeader;
import com.mygdx.game.Map.MyNavTmxMapLoader;
import com.mygdx.game.UI.MySkin;

public final class Utility {
	private static final String TAG = Utility.class.getSimpleName();

	private static final String STATUSUI_TEXTURE_ATLAS_PATH = "skins/statusui.atlas";
	private static final String UISKIN_TEXTURE_ATLAS_PATH = "skins/uiskin.atlas";
	private static final String STATUSUI_SKIN_PATH = "skins/statusui.json";
	private static final String SHADOW_WALKER_SKIN_TEXTURE_ATLAS_PATH = "skins/shadowWalker/shadow-walker-ui.atlas";
	private static final String SHADOW_WALKER_SKIN_PATH = "skins/shadowWalker/shadow-walker-ui.json";
	public static final String ON_TILE_HOVER_FILE_PATH = "sprites/gui/selectedTile.png";

	public static final AssetManager assetManager = new AssetManager();
	private static InternalFileHandleResolver filePathResolver = new InternalFileHandleResolver();
	public static final Random random = new Random();

	private static TextureAtlas statusUITextureAtlas;
	private static TextureAtlas uiTextureAtlas;
	private static TextureAtlas shadowWalkerTextureAtlas;
	private static MySkin statusUISkin;
	private static MySkin shadowWalkerSkin;

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

	public static void loadFreeTypeFontAsset(final String fontName, final int size) {
		if (checkValidString(fontName)) {
			assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(filePathResolver));
			assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(filePathResolver));

			final FreeTypeFontLoaderParameter myFont = new FreeTypeFontLoaderParameter();
			myFont.fontFileName = fontName;
			myFont.fontParameters.size = size;
			myFont.fontParameters.borderWidth = 0.5f;
			myFont.fontParameters.color = Color.LIGHT_GRAY;
			myFont.fontParameters.shadowOffsetX = 2;
			myFont.fontParameters.shadowOffsetY = 2;
			assetManager.load(fontName, BitmapFont.class, myFont);
		}
	}

	public static LabelStyle createLabelStyle(final String fontPath, final int size, final int borderWidth, final Color color, final int shadowX, final int shadowY) {
		final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
		final FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = size;
		parameter.borderWidth = borderWidth;
		parameter.color = color;
		parameter.shadowOffsetX = shadowX;
		parameter.shadowOffsetY = shadowY;
		final BitmapFont font = generator.generateFont(parameter);
		final LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = font;
		return labelStyle;
	}

	public static BitmapFont getFreeTypeFontAsset(final String fontName) {
		return (BitmapFont) getAsset(fontName, BitmapFont.class);
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

	public static TextureAtlas getStatusUITextureAtlas() {
		if (statusUITextureAtlas == null) {
			statusUITextureAtlas = new TextureAtlas(STATUSUI_TEXTURE_ATLAS_PATH);
		}

		return statusUITextureAtlas;
	}

	public static TextureAtlas getUITextureAtlas() {
		if (uiTextureAtlas == null) {
			uiTextureAtlas = new TextureAtlas(UISKIN_TEXTURE_ATLAS_PATH);
		}

		return uiTextureAtlas;
	}

	public static TextureAtlas getShadowWalkerTextureAtlas() {
		if (shadowWalkerTextureAtlas == null) {
			shadowWalkerTextureAtlas = new TextureAtlas(SHADOW_WALKER_SKIN_TEXTURE_ATLAS_PATH);
		}

		return shadowWalkerTextureAtlas;
	}

	public static MySkin getStatusUISkin() {
		if (statusUISkin == null) {
			statusUISkin = new MySkin(Gdx.files.internal(STATUSUI_SKIN_PATH), getStatusUITextureAtlas());
		}

		return statusUISkin;
	}

	public static MySkin getShadowWalkersUISkin() {
		if (shadowWalkerSkin == null) {
			shadowWalkerSkin = new MySkin(Gdx.files.internal(SHADOW_WALKER_SKIN_PATH), getShadowWalkerTextureAtlas());
		}

		return shadowWalkerSkin;
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

	public static int getRandomIntFrom1to(final int to) {
		final int result = random.nextInt(to);
		return result + 1;
	}

	public static Entity[] sortUnits(final Entity[] sortedUnits) {
		Arrays.sort(sortedUnits, new Comparator<Entity>() {
			@Override
			public int compare(final Entity e1, final Entity e2) {
				if (e1.getCurrentInitiative() > e2.getCurrentInitiative()) {
					return 1;
				} else if (e1.getCurrentInitiative() < e2.getCurrentInitiative()) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		return sortedUnits;
	}

	public static List<TiledMapPosition> collectPositionsUnits(Entity[] units) {
		final ArrayList<TiledMapPosition> positions = new ArrayList<TiledMapPosition>();

		for (final Entity unit : units) {
			positions.add(unit.getCurrentPosition());
		}

		return positions;
	}

	public static ArrayList<TiledMapPosition> collectPositionsEnemeyUnits(ArrayList<Entity> units, boolean isPlayer) {
		final ArrayList<TiledMapPosition> enemyPositions = new ArrayList<TiledMapPosition>();

		for (final Entity unit : units) {
			if (unit.isPlayerUnit() != isPlayer) {
				enemyPositions.add(unit.getCurrentPosition());
			}
		}

		return enemyPositions;
	}

	public static ArrayList<TiledMapPosition> collectPositionsUnits(List<TeamLeader> players) {
		final ArrayList<TiledMapPosition> positions = new ArrayList<TiledMapPosition>();
		for (final TeamLeader owner : players) {
			for (final Entity character : owner.getTeam()) {
				positions.add(character.getCurrentPosition());
			}
		}
		return positions;
	}

	public static boolean checkIfUnitsWithinDistance(Entity unit1, TiledMapPosition targetPos, int distance) {
		final TiledMapPosition pos1 = unit1.getCurrentPosition();
		return checkIfWithinDistance(pos1, targetPos, distance);
	}

	public static boolean checkIfUnitsWithinDistance(Entity unit1, Entity unit2, int distance) {
		final TiledMapPosition pos1 = unit1.getCurrentPosition();
		final TiledMapPosition pos2 = unit2.getCurrentPosition();
		return checkIfWithinDistance(pos1, pos2, distance);
	}

	private static boolean checkIfWithinDistance(TiledMapPosition pos1, TiledMapPosition pos2, int distance) {
		return (Math.abs(pos1.getTileX() - pos2.getTileX()) + Math.abs(pos1.getTileY() - pos2.getTileY())) <= distance;
	}

	public static int getDistanceBetweenUnits(Entity unit1, Entity unit2) {
		final TiledMapPosition pos1 = unit1.getCurrentPosition();
		final TiledMapPosition pos2 = unit2.getCurrentPosition();
		return (Math.abs(pos1.getTileX() - pos2.getTileX()) + Math.abs(pos1.getTileY() - pos2.getTileY()));
	}

	public static int getDistance(TiledMapPosition pos1, Entity unit2) {
		final TiledMapPosition pos2 = unit2.getCurrentPosition();
		return (Math.abs(pos1.getTileX() - pos2.getTileX()) + Math.abs(pos1.getTileY() - pos2.getTileY()));
	}

	public static float clamp(final float var, final float max, final float min) {
		if (var > min) {
			if (var < max) {
				return var;
			} else {
				return max;
			}
		} else {
			return min;
		}
	}

	private Utility() {

	}
}
