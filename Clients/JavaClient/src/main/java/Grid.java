import java.util.*;

public class Grid {

    public static final Integer INF = 9999;

    public static Set<Point> filter = new HashSet<>();

    public static int[][] D;
    public static int width;
    public static int height;

    public Grid(Map map) {

        if (D == null) {
            height = map.height;
            width = map.width;
            D = new int[height][width];
            for (int i = 0; i < height; i++) {
                Arrays.fill(D[i], INF);
            }
        }

        for (MapTile tile : map.grid) {
            if (!validTile(tile)) continue;
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

    ArrayList<ArrayList<Point>> shortestPaths(Point start, MapType targetType) {

        ArrayList<ArrayList<Point>> allPaths = new ArrayList<>();

        Queue<ArrayList<Point>> queue = new LinkedList<>();
        queue.add(new ArrayList<>(List.of(start)));
        HashSet<Point> seen = new HashSet<>();
        seen.add(start);

        while (!queue.isEmpty()) {
            ArrayList<Point> path = queue.poll();

            Point p = path.get(path.size() - 1);

            if (matches(p, targetType) && allowed(p)) {
                allPaths.add(new ArrayList<>(path));
            }

            for (Point dir : dirs(p)) {
                if (allowed(dir) && !seen.contains(dir)) {
                    ArrayList<Point> newPath = new ArrayList<>(path);
                    newPath.add(dir);
                    queue.add(newPath);
                    seen.add(dir);
                }
            }
        }

        return allPaths;
    }

    public boolean matches(Point p, MapType type) {
        return D[p.x][p.y] == type.ordinal();
    }

    public boolean allowed(Point p) {
        return !matches(p, MapType.WALL) && !filter.contains(p);
    }

    public boolean forbidden(Point p) {
        return !allowed(p);
    }

    public boolean validTile(MapTile tile) {
        return tile.type != MapType.OUT_OF_SIGHT && tile.type != MapType.OUT_OF_MAP;
    }



}
