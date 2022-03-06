package com.jelte.norii.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGridGraph;
import org.xguzm.pathfinding.grid.NavigationGridGraphNode;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.jelte.norii.map.Map;
import com.jelte.norii.map.MapFactory.MapType;
import com.jelte.norii.map.MapManager;

public class CreateMapInfo {
	static MapManager mapMgr;
	private static Map linkedMap;
	private static AStarGridFinder<GridCell> aStarGridFinder;
	private static NavigationGridGraph<GridCell> navGrid;
	private static GridFinderOptions gridFinderOptions;
	private static HashMap<MyPoint, HashMap<MyPoint, List<GridCell>>> precalculatedPaths;
	private static HashMap<MyPoint, HashMap<MyPoint, Boolean>> precalculatedLineOfSightsWithNonBlockableUnits;

	public CreateMapInfo(MapType mapType, MapManager mapMgr) {
		precalculatedPaths = new HashMap<>();
		precalculatedLineOfSightsWithNonBlockableUnits = new HashMap<>();
		initDataStructures();
		gridFinderOptions = new GridFinderOptions();
		gridFinderOptions.allowDiagonal = false;
		aStarGridFinder = new AStarGridFinder<>(GridCell.class, gridFinderOptions);
		CreateMapInfo.mapMgr = mapMgr;
		CreateMapInfo.mapMgr.loadMap(mapType);
		linkedMap = mapMgr.getCurrentMap();
		preprocessMap();
		FileHandle file = Gdx.files.local("maps/" + mapType.name() + "_mapInfo.txt");
		writeHashMapsToFile(file, mapType);
	}

	private void initDataStructures() {
		int startX = 0;
		int startY = 0;
		while (startX < linkedMap.getMapWidth()) {
			while (startY < linkedMap.getMapHeight()) {
				final MyPoint start = new MyPoint(startX, startY);
				HashMap<MyPoint, List<GridCell>> paths = new HashMap<>();
				HashMap<MyPoint, Boolean> visions = new HashMap<>();
				int endX = 0;
				int endY = 0;
				while (endX < linkedMap.getMapWidth()) {
					while (endY < linkedMap.getMapHeight()) {
						final MyPoint end = new MyPoint(endX, endY);
						if (start.equals(end)) {
							List<GridCell> path = new ArrayList<>();
							path.add(new GridCell(start.x, start.y));
							paths.put(end, path);

							visions.put(end, true);
						}
						visions.put(end, false);
						paths.put(end, null);
					}
				}
				precalculatedPaths.put(start, paths);
				precalculatedLineOfSightsWithNonBlockableUnits.put(start, visions);
				startY++;
			}
			startY = 0;
			startX++;
		}
	}

	private static void preprocessMap() {
		System.out.println("starting preprocess");
		navGrid = mapMgr.getCurrentMap().getNavLayer().navGrid;
		List<MyPoint> walkablePoints = new ArrayList<>();
		int preprocessI = 0;
		int preprocessJ = 0;
		while (preprocessI < linkedMap.getMapWidth()) {
			System.out.println("a");
			while (preprocessJ < linkedMap.getMapHeight()) {
				System.out.println("b");
				final MyPoint start = new MyPoint(preprocessI, preprocessJ);
				if (navGrid.getCell(preprocessI, preprocessJ).isWalkable()) {
					walkablePoints.add(start);
				}
				preprocessJ++;
			}
			preprocessJ = 0;
			preprocessI++;
		}

		for (MyPoint start : walkablePoints) {
			calculatePathsToEveryOtherCell(start);
			calculateLineOfSightWithoutBlocksToEveryOtherCell(start);
		}
		System.out.println("preprocess ended");
	}

	private static void calculatePathsToEveryOtherCell(MyPoint start) {
		for (int i = 0; i < linkedMap.getMapWidth(); i++) {
			for (int j = 0; j < linkedMap.getMapHeight(); j++) {
				final MyPoint end = new MyPoint(i, j);
				final MyPoint closestStartNeighbour = getClosestStartNeighbour(start, end);
				final MyPoint closestEndNeighbour = getClosestEndNeighbour(start, end);
				final MyPoint closestStartNeighbour2 = getClosestStartNeighbour2(start, end);
				final MyPoint closestEndNeighbour2 = getClosestEndNeighbour2(start, end);

				if (precalculatedPaths.get(start).get(end) == null) {
					// check neighbours --> more efficient
					if (existsPath(closestStartNeighbour, end)) {
						List<GridCell> pathStartNeighbour = precalculatedPaths.get(closestStartNeighbour).get(end);
						pathStartNeighbour.add(0, new GridCell(start.x, start.y));
						precalculatedPaths.get(start).put(end, pathStartNeighbour);
						precalculatedPaths.get(end).put(start, reversePath(pathStartNeighbour));
					} else if (existsPath(start, closestEndNeighbour)) {
						List<GridCell> pathEndNeighbour = precalculatedPaths.get(start).get(closestEndNeighbour);
						pathEndNeighbour.add(new GridCell(end.x, end.y));
						precalculatedPaths.get(start).put(end, pathEndNeighbour);
						precalculatedPaths.get(end).put(start, reversePath(pathEndNeighbour));
					} else if (existsPath(closestStartNeighbour2, end)) {
						List<GridCell> pathStartNeighbour = precalculatedPaths.get(closestStartNeighbour2).get(end);
						pathStartNeighbour.add(0, new GridCell(start.x, start.y));
						precalculatedPaths.get(start).put(end, pathStartNeighbour);
						precalculatedPaths.get(end).put(start, reversePath(pathStartNeighbour));
					} else if (existsPath(start, closestEndNeighbour2)) {
						List<GridCell> pathEndNeighbour = precalculatedPaths.get(start).get(closestEndNeighbour2);
						pathEndNeighbour.add(new GridCell(end.x, end.y));
						precalculatedPaths.get(start).put(end, pathEndNeighbour);
						precalculatedPaths.get(end).put(start, reversePath(pathEndNeighbour));
					} else {
						final List<GridCell> path = aStarGridFinder.findPath(start.x, start.y, i, j, navGrid);
						if (path != null) {
							final List<GridCell> copy = new ArrayList<>(path);
							precalculatedPaths.get(start).put(end, copy);
							precalculatedPaths.get(end).put(start, reversePath(copy));
							addSubPaths(path);
						}
					}
				}
			}
		}
	}

	private static void addSubPaths(List<GridCell> path) {
		for (int i = 0; i < path.size(); i++) {
			for (int j = 0; j < path.size(); j++) {
				MyPoint fakeStart = new MyPoint(path.get(i).x, path.get(i).y);
				MyPoint fakeEnd = new MyPoint(path.get(j).x, path.get(j).y);
				if (precalculatedPaths.get(fakeStart).get(fakeEnd) == null) {
					List<GridCell> miniPath = path.subList(i, j);
					precalculatedPaths.get(fakeStart).put(fakeEnd, miniPath);
					precalculatedPaths.get(fakeEnd).put(fakeStart, reversePath(miniPath));
				}
			}
		}
	}

	private static boolean existsPath(MyPoint start, MyPoint end) {
		HashMap<MyPoint, List<GridCell>> path = precalculatedPaths.get(start);
		return ((path != null) && (path.get(end) != null));
	}

	private static List<GridCell> reversePath(List<GridCell> list) {
		Collections.reverse(list);
		return list;
	}

	private static MyPoint getClosestEndNeighbour(MyPoint start, MyPoint end) {
		if ((start.x < end.x) && (start.y < end.y)) {
			return new MyPoint(end.x - 1, end.y);
		}
		if ((start.x > end.x) && (start.y < end.y)) {
			return new MyPoint(end.x + 1, end.y);
		}
		if ((start.x < end.x) && (start.y > end.y)) {
			return new MyPoint(end.x - 1, end.y);
		}
		return new MyPoint(end.x + 1, end.y);
	}

	private static MyPoint getClosestStartNeighbour(MyPoint start, MyPoint end) {
		if ((start.x < end.x) && (start.y < end.y)) {
			return new MyPoint(start.x + 1, start.y);
		}
		if ((start.x > end.x) && (start.y < end.y)) {
			return new MyPoint(start.x - 1, start.y);
		}
		if ((start.x < end.x) && (start.y > end.y)) {
			return new MyPoint(start.x + 1, start.y);
		}
		return new MyPoint(start.x - 1, start.y);
	}

	private static MyPoint getClosestEndNeighbour2(MyPoint start, MyPoint end) {
		if (((start.x < end.x) && (start.y < end.y)) || ((start.x > end.x) && (start.y < end.y))) {
			return new MyPoint(end.x, end.y - 1);
		}
		if ((start.x < end.x) && (start.y > end.y)) {
		}
		return new MyPoint(end.x, end.y + 1);
	}

	private static MyPoint getClosestStartNeighbour2(MyPoint start, MyPoint end) {
		if (((start.x < end.x) && (start.y < end.y)) || ((start.x > end.x) && (start.y < end.y))) {
			return new MyPoint(start.x, start.y + 1);
		}
		if ((start.x < end.x) && (start.y > end.y)) {
		}
		return new MyPoint(start.x, start.y - 1);
	}

	private static void calculateLineOfSightWithoutBlocksToEveryOtherCell(MyPoint point) {
		for (int i = 0; i < linkedMap.getMapWidth(); i++) {
			for (int j = 0; j < linkedMap.getMapHeight(); j++) {
				final MyPoint end = new MyPoint(i, j);
				if (navGrid.getCell(i, j).isWalkable() && navGrid.getCell(point.x, point.y).isWalkable() && !precalculatedLineOfSightsWithNonBlockableUnits.get(point).get(end)) {
					final GridCell unitCell = navGrid.getCell(point.x, point.y);
					final GridCell targetCell = navGrid.getCell(end.x, end.y);
					final NavigationGridGraphNode node = unitCell;
					final NavigationGridGraphNode neigh = targetCell;
					if (calculateLineOfSight(node, neigh)) {
						precalculatedLineOfSightsWithNonBlockableUnits.get(point).put(end, true);
						precalculatedLineOfSightsWithNonBlockableUnits.get(end).put(point, true);
					}
				}

			}
		}
	}

	private static boolean calculateLineOfSight(final NavigationGridGraphNode node, final NavigationGridGraphNode neigh) {
		int x1 = node.getX();
		int y1 = node.getY();
		final int x2 = neigh.getX();
		final int y2 = neigh.getY();
		final int dx = Math.abs(x1 - x2);
		final int dy = Math.abs(y1 - y2);
		final int xinc = (x1 < x2) ? 1 : -1;
		final int yinc = (y1 < y2) ? 1 : -1;

		int error = dx - dy;

		for (int n = dx + dy; n > 0; n--) {
			final int e2 = 2 * error;
			if (e2 > -dy) {
				error -= dy;
				x1 += xinc;
			}
			if (e2 < dx) {
				error += dx;
				y1 += yinc;
			}
			if (!navGrid.isWalkable(x1, y1)) {
				return false;
			}

		}
		return true;
	}

	private static void writeHashMapsToFile(FileHandle file, MapType mapType) {
		file.writeString(mapType.name() + "\n", true);
		file.writeString("==============\n", true);
		file.writeString("precalculatedPaths\n", true);
		file.writeString("==================\n", true);
		System.out.println("starting preporcesstoFile");
		for (Entry<MyPoint, HashMap<MyPoint, List<GridCell>>> entry : precalculatedPaths.entrySet()) {
			System.out.println("1");
			if (entry.getValue() == null) {
				System.out.println("2");
				file.writeString(entry.getKey().x + "," + entry.getKey().y + " --> null\n", true);
			} else {
				System.out.println("3");
				for (Entry<MyPoint, List<GridCell>> subEntry : entry.getValue().entrySet()) {
					System.out.println("4");
					if (subEntry.getValue() == null) {
						System.out.println("5");
						file.writeString(subEntry.getKey().x + "," + subEntry.getKey().y + " --> null\n", true);
					} else {
						System.out.println("6");
						file.writeString(entry.getKey().x + "," + entry.getKey().y + " --> ", true);
						file.writeString(subEntry.getKey().x + "," + subEntry.getKey().y + ":\n", true);
						for (GridCell cell : subEntry.getValue()) {
							System.out.println("7");
							file.writeString(cell.x + "," + cell.y + " / ", true);
						}
					}
				}
			}
		}
		file.writeString("precalculatedLineOfSights\n", true);
		file.writeString("=========================\n", true);
		System.out.println("starting line of sight");
		for (Entry<MyPoint, HashMap<MyPoint, Boolean>> entry : precalculatedLineOfSightsWithNonBlockableUnits.entrySet()) {
			System.out.println("8");
			if (entry.getValue() == null) {
				System.out.println("5");
				file.writeString(entry.getKey().x + "," + entry.getKey().y + " --> null\n", true);
			} else {
				for (Entry<MyPoint, Boolean> subEntry : entry.getValue().entrySet()) {
					System.out.println("9");
					file.writeString(entry.getKey().x + "," + entry.getKey().y + " --> ", true);
					file.writeString(subEntry.getKey().x + "," + subEntry.getKey().y + " : subEntry.getValue()\n", true);
				}
			}
		}
		System.out.println("to file ended");
	}

}
