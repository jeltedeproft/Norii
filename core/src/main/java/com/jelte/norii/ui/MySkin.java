package com.jelte.norii.ui;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.Hinting;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

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

		json.setSerializer(FreeTypeFontGenerator.class, new Json.ReadOnlySerializer<FreeTypeFontGenerator>() {
			@Override
			public FreeTypeFontGenerator read(Json json, JsonValue jsonData, Class type) {
				final String path = json.readValue("font", String.class, jsonData);
				jsonData.remove("font");

				final Hinting hinting = Hinting.valueOf(json.readValue("hinting", String.class, "AutoMedium", jsonData));
				jsonData.remove("hinting");

				final TextureFilter minFilter = TextureFilter.valueOf(json.readValue("minFilter", String.class, "Nearest", jsonData));
				jsonData.remove("minFilter");

				final TextureFilter magFilter = TextureFilter.valueOf(json.readValue("magFilter", String.class, "Nearest", jsonData));
				jsonData.remove("magFilter");

				final FreeTypeFontParameter parameter = json.readValue(FreeTypeFontParameter.class, jsonData);
				parameter.hinting = hinting;
				parameter.minFilter = minFilter;
				parameter.magFilter = magFilter;
				final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(skinFile.parent().child(path));
				final BitmapFont font = generator.generateFont(parameter);
				skin.add(jsonData.name, font);
				if (parameter.incremental) {
					generator.dispose();
					return null;
				} else {
					return generator;
				}
			}
		});

		return json;
	}
}
