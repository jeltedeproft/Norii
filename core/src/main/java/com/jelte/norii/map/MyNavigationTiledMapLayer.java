package com.jelte.norii.map;

import java.util.List;

import org.xguzm.pathfinding.NavigationNode;
import org.xguzm.pathfinding.PathFinderOptions;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.NavigationGridGraph;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.utils.Array;

public class MyNavigationTiledMapLayer extends MapLayer implements NavigationGridGraph<GridCell> {

	public NavigationGrid<GridCell> navGrid;

	public MyNavigationTiledMapLayer() {
		this(null);
	}

	@SuppressWarnings("deprecation")
	public MyNavigationTiledMapLayer(final GridCell[][] nodes) {
		navGrid = new NavigationGrid<>(nodes);
	}

	@Override
	public void setCell(final int x, final int y, final GridCell node) {
		navGrid.setCell(x, y, node);
	}

	@Override
	public List<GridCell> getNeighbors(final GridCell node) {
		return navGrid.getNeighbors(node);
	}

	@Override
	public List<GridCell> getNeighbors(final GridCell node, final PathFinderOptions opt) {
		return navGrid.getNeighbors(node, opt);
	}

	@Override
	public float getMovementCost(final GridCell node1, final GridCell node2, final PathFinderOptions opt) {
		return navGrid.getMovementCost(node1, node2, opt);
	}

	@Override
	public boolean isWalkable(final GridCell node) {
		return navGrid.isWalkable(node);
	}

	@Override
	public GridCell getCell(final int x, final int y) {
		return navGrid.getCell(x, y);
	}

	@Override
	public boolean contains(final int x, final int y) {
		return navGrid.contains(x, y);
	}

	@Override
	public void setWalkable(final int x, final int y, final boolean walkable) {
		if (navGrid.contains(x, y)) {
			navGrid.setWalkable(x, y, walkable);
		}
	}

	@Override
	public boolean isWalkable(final int x, final int y) {
		return navGrid.isWalkable(x, y);
	}

	@Override
	public GridCell[][] getNodes() {
		return navGrid.getNodes();
	}

	@Override
	public void setNodes(final GridCell[][] nodes) {
		navGrid.setNodes(nodes);
	}

	@Override
	public int getWidth() {
		return navGrid.getWidth();
	}

	@Override
	public void setWidth(final int width) {
		navGrid.setWidth(width);
	}

	@Override
	public int getHeight() {
		return navGrid.getHeight();
	}

	@Override
	public void setHeight(final int height) {
		navGrid.setHeight(height);
	}

	@Override
	public boolean lineOfSight(final NavigationNode from, final NavigationNode to) {
		return navGrid.lineOfSight(from, to);
	}

	public Array<GridCell> getUnwalkableNodes() {
		Array<GridCell> nodes = new Array<>();
		for (GridCell[] row : navGrid.getNodes()) {
			for (GridCell cell : row) {
				if (!cell.isWalkable()) {
					nodes.add(cell);
				}
			}
		}
		return nodes;
	}

}
