package ai;

import cli.Map;
import cli.MapTile;
import cli.MapType;
import cli.Point;

import java.util.*;

public class Grid {

    private static final Integer INF = 9999;

    public static ArrayList<Point> filter = new ArrayList<>();
    public int[][] D;
    private int width;
    private int height;

    public void initGrid(Map map) {
        height = map.height;
        width = map.width;

        D = new int[height][width];

        for (int i = 0; i < height; i++) {
            Arrays.fill(D[i], INF);
        }

        for (MapTile tile : map.grid) {
            int i = tile.coordinates.x;
            int j = tile.coordinates.y;
            D[i][j] = tile.type.ordinal();
        }
    }

    public ArrayList<Point> dirs(Point point) {
        int i = point.x, j = point.y;
        ArrayList<Point> dirs = new ArrayList<>();
        if (j > 0 && D[i][j - 1] != INF) dirs.add(new Point(i, j - 1));
        if (j < width - 1 && D[i][j + 1] != INF) dirs.add(new Point(i, j + 1));
        if (i > 0 && D[i - 1][j] != INF) dirs.add(new Point(i - 1, j));
        if (i < height - 1 && D[i + 1][j] != INF) dirs.add(new Point(i + 1, j));
        return dirs;
    }

    ArrayList<Point> shortestPath(Point start, MapType targetType) {

        ArrayList<ArrayList<Point>> allPaths = new ArrayList<>();

        Queue<ArrayList<Point>> queue = new LinkedList<>();
        queue.add(new ArrayList<>(List.of(start)));
        HashSet<Point> seen = new HashSet<>();
        seen.add(start);

        while (!queue.isEmpty()) {

            ArrayList<Point> path = queue.poll();

            Point p = path.get(path.size() - 1);

            if (D[p.x][p.y] == targetType.ordinal()) {
                allPaths.add(new ArrayList<>(path));
            }

            for (Point dir : dirs(p)) {
                if (allowed(dir) && !seen.contains(dir)) {
                    seen.add(dir);
                    ArrayList<Point> newPath = new ArrayList<>(path);
                    newPath.add(dir);
                    queue.add(newPath);
                }
            }
        }

        for (ArrayList<Point> path : allPaths) {
            Logger.log(path);
        }

        return allPaths.stream().min(Comparator.comparing(ArrayList::size)).orElse(new ArrayList<>());
    }

    public boolean allowed(Point p) {
        return D[p.x][p.y] != MapType.WALL.ordinal()
                && !filter.contains(p);
    }

}