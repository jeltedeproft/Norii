package com.mygdx.game.Map;

import java.util.ArrayList;
import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;

import com.badlogic.gdx.Gdx;

public class MyPathFinder {
	private static final String TAG = MyPathFinder.class.getSimpleName();
	
	private GridCell[][] cells;
	private int length;
	private int height;
	private NavigationGrid<GridCell> navGrid;
	private GridFinderOptions opt;
	private AStarGridFinder<GridCell> finder;
	
	public MyPathFinder(int length, int height) {
		this.length = length;
		this.height = height;
		cells = new GridCell[length][height];
		
		for(int y=0; y<height;y++)
			for(int x=0; x<length; x++)
				cells[x][y] = new GridCell(x,y);
		
		//create a navigation grid with the cells you just created
		navGrid = new NavigationGrid(cells);
		
		//or create your own pathfinder options:
		opt = new GridFinderOptions();
		opt.allowDiagonal = false;
			
		finder = new AStarGridFinder(GridCell.class, opt);
	}

	public AStarGridFinder<GridCell> getFinder() {
		return finder;
	}

	public void dispose() {
		// dispose stuff
		finder = null;
		opt = null;
		navGrid = null;
		cells = null;
		
	}
	
	public List<GridCell> getCellsWithin(int x, int y, int range) {
		List<GridCell> cells = new ArrayList<GridCell>();
		GridCell start = navGrid.getCell(x, y);
		//go over all cells, if distance between start and cell is 3 or less, add it
		for(GridCell[] gridcells : navGrid.getNodes()) {
			for(GridCell gridcell : gridcells) {
				if((Math.abs(start.x - gridcell.x) + (Math.abs(start.y - gridcell.y))) <= range){
					if((finder.findPath(start.x, start.y, gridcell.x, gridcell.y, navGrid).size() < range)) {
						cells.add(gridcell);
					}
				}
			}
			
		}
		return cells;
	}
}
