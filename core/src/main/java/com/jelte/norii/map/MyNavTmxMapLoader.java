package com.jelte.norii.map;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class MyNavTmxMapLoader extends TmxMapLoader {
	private final String navigationLayerName;
	private final String navigationProperty;
	private final String navigationClosedValue;

	public MyNavTmxMapLoader() {
		this("navigation", "walkable", "0");
	}

	public MyNavTmxMapLoader(FileHandleResolver resolver) {
		this(resolver, "navigation", "walkable", "0");
	}

	public MyNavTmxMapLoader(String navigationLayerName, String navigationProperty, String navigationClosedValue) {
		this(new InternalFileHandleResolver(), navigationLayerName, navigationProperty, navigationClosedValue);
	}

	public MyNavTmxMapLoader(FileHandleResolver handler, String navigationLayerName, String navigationProperty,
			String navigationClosedValue) {
		super(handler);
		this.navigationLayerName = navigationLayerName;
		this.navigationProperty = navigationProperty;
		this.navigationClosedValue = navigationClosedValue;
	}

	@Override
	protected void loadTileLayer(TiledMap map, MapLayers maplayers, Element element) {
		final String layerName = element.getAttribute("name", null);
		if (navigationLayerName.equals(layerName)) {
			loadNavigationLayer(map, element, layerName);
		} else {
			super.loadTileLayer(map, maplayers, element);
		}
	}

	private void loadNavigationLayer(TiledMap map, Element element, String layerName) {
		final int width = element.getIntAttribute("width", 0);
		final int height = element.getIntAttribute("height", 0);

		final GridCell[][] nodes = createGridCells(map, element, width, height);
		final MyNavigationTiledMapLayer layer = createNavigationTiledMapLayer(layerName, nodes);
		loadProperties(element, layer);

		map.getLayers().add(layer);
	}

	private GridCell[][] createGridCells(TiledMap map, Element element, int width, int height) {
		final int[] ids = getTileIds(element, width, height);
		final TiledMapTileSets tilesets = map.getTileSets();
		final GridCell[][] nodes = new GridCell[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final int id = ids[(y * width) + x];
				final TiledMapTile tile = tilesets.getTile(id & ~MASK_CLEAR);

				final GridCell cell = new GridCell(x, height - 1 - y, false);
				if (tile != null) {
					final MapProperties tileProp = tile.getProperties();

					final String walkableProp = tileProp.get(navigationProperty, navigationClosedValue, String.class);
					cell.setWalkable(!walkableProp.equals(navigationClosedValue));
				}
				nodes[cell.getX()][cell.getY()] = cell;
			}
		}
		return nodes;
	}

	private MyNavigationTiledMapLayer createNavigationTiledMapLayer(String layerName, GridCell[][] nodes) {
		final MyNavigationTiledMapLayer layer = new MyNavigationTiledMapLayer(nodes);
		layer.setName(layerName);
		layer.setVisible(false);
		return layer;
	}

	private void loadProperties(Element element, MyNavigationTiledMapLayer layer) {
		final Element properties = element.getChildByName("properties");
		if (properties != null) {
			loadProperties(layer.getProperties(), properties);
		}
	}

}
