package com.jelte.norii.ui;

import java.io.FileNotFoundException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;

public class VideoDrawable extends BaseDrawable implements Disposable {
	public VideoPlayer videoPlayer;

	public VideoDrawable(FileHandle file) {
		videoPlayer = VideoPlayerCreator.createVideoPlayer();
		try {
			videoPlayer.play(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void draw(Batch batch, float x, float y, float width, float height) {
		videoPlayer.update();
		var frame = videoPlayer.getTexture();
		if (frame != null)
			batch.draw(frame, x, y, width, height);
	}

	@Override
	public void dispose() {
		videoPlayer.dispose();
	}
}
