package com.jelte.norii.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;

public class MySkin extends Skin {

	public MySkin(final FileHandle skinFile) {
		super(skinFile);
	}

	public MySkin(final FileHandle skinFile, final TextureAtlas textureAtlas) {
		super(skinFile, textureAtlas);
	}

	@Override
	protected Json getJsonLoader(final FileHandle skinFile) {
		final Json json = super.getJsonLoader(skinFile);

		final Skin skin = this;

		json.setSerializer(BitmapFont.class, new Json.ReadOnlySerializer<BitmapFont>() {
			@Override
			public BitmapFont read(final Json json, final JsonValue jsonData, @SuppressWarnings("rawtypes") final Class type) {
				final String path = json.readValue("file", String.class, jsonData);
				final int scaledSize = json.readValue("scaledSize", int.class, -1, jsonData);
				final Boolean flip = json.readValue("flip", Boolean.class, false, jsonData);
				final Boolean markupEnabled = json.readValue("markupEnabled", Boolean.class, false, jsonData);

				FileHandle fontFile = skinFile.parent().child(path);
				if (!fontFile.exists()) {
					fontFile = Gdx.files.internal(path);
				}
				if (!fontFile.exists()) {
					throw new SerializationException("Font file not found: " + fontFile);
				}

				if (path.endsWith(".ttf")) {
					final int dpSize = json.readValue("dpSize", int.class, 16, jsonData);
					final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
					final FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
					parameter.size = (int) (dpSize * Gdx.graphics.getDensity());
					parameter.borderWidth = 0.5f;
					parameter.color = Color.LIGHT_GRAY;
					parameter.shadowOffsetX = 2;
					parameter.shadowOffsetY = 2;
					final BitmapFont font = generator.generateFont(parameter);
					generator.dispose();
					return font;
				}
				// Use a region with the same name as the font, else use a PNG file in the same
				// directory as the FNT file.
				final String regionName = fontFile.nameWithoutExtension();
				try {
					BitmapFont font;
					final Array<TextureRegion> regions = skin.getRegions(regionName);
					if (regions != null) {
						font = new BitmapFont(new BitmapFont.BitmapFontData(fontFile, flip), regions, true);
					} else {
						final TextureRegion region = skin.optional(regionName, TextureRegion.class);
						if (region != null) {
							font = new BitmapFont(fontFile, region, flip);
						} else {
							final FileHandle imageFile = fontFile.parent().child(regionName + ".png");
							if (imageFile.exists()) {
								font = new BitmapFont(fontFile, imageFile, flip);
							} else {
								font = new BitmapFont(fontFile, flip);
							}
						}
					}
					font.getData().markupEnabled = markupEnabled;
					// Scaled size is the desired cap height to scale the font to.
					if (scaledSize != -1) {
						font.getData().setScale(scaledSize / font.getCapHeight());
					}
					return font;
				} catch (final RuntimeException ex) {
					throw new SerializationException("Error loading bitmap font: " + fontFile, ex);
				}
			}
		});

		return json;
	}
}
