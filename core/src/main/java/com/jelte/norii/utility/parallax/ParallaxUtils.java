package com.jelte.norii.utility.parallax;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * utility class for common calculations
 *
 * @author Rahul Verma
 *
 */
public class ParallaxUtils {

	public enum WH {
		WIDTH, HEIGHT
	}

	/**
	 * calculate new width/height maintaining aspect ratio
	 *
	 * @param wh       what oneDimen represents
	 * @param oneDimen either width or height
	 * @param region   the texture region
	 * @return if oneDimen is width then height else width
	 */
	public static float calculateOtherDimension(WH wh, float oneDimen, TextureRegion region) {
		return wh == WH.WIDTH	? region.getRegionHeight() * (oneDimen / region.getRegionWidth())
								: region.getRegionWidth() * (oneDimen / region.getRegionHeight());
	}

	/**
	 * calculate new width/height maintaining aspect ratio
	 *
	 * @param wh             what oneDimen represents
	 * @param oneDimen       either width or height
	 * @param originalWidth  the original width
	 * @param originalHeight the original height
	 * @return if oneDimen is width then height else width
	 */
	public static float calculateOtherDimension(WH wh, float oneDimen, float originalWidth, float originalHeight) {
		return wh == WH.WIDTH	? originalHeight * (oneDimen / originalWidth)
								: originalWidth * (oneDimen / originalHeight);
	}

}