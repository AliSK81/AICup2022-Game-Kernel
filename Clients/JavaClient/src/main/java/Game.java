import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

class Game {
    GameState game;
    Grid grid;
    boolean treasuryFound;

    public Game(GameState gameState) {
        this.game = gameState;
        Logger.init(game.agentID);
        this.grid = new Grid(game.map);
        Logger.log(game);
    }

    public Action getAction() {

        Logger.log(getVisiblePoints());

        for (int[] row : Grid.D) {
            Logger.log(Arrays.toString(row));
        }

        try {
            return getNextAction();
        } catch (Exception e) {
            Logger.error(e);
            Logger.close();
            throw e;
        }

//        return Action.STAY;
    }

    private Action getNextAction() {
        this.grid = new Grid(game.map);

        int myData = -1;
        ArrayList<MapTile> agents = new ArrayList<>();

        for (MapTile tile : game.map.grid) {
            if (tile.type == MapType.TREASURY) {
                treasuryFound = true;
            }
            if (tile.type == MapType.WALL || tile.type == MapType.FOG && game.lastAction == -1) {
                Grid.filter.add(tile.coordinates);
            }
            if (tile.coordinates.equals(game.location)) {
                myData = tile.data;
            }
            if (tile.type == MapType.AGENT) {
                agents.add(tile);
            }
        }

        if (game.rounds - game.currentRound <= 7 || game.wallet >= 20 && treasuryFound) {

            int randInt = new Random().nextInt(10);

            if (randInt <= 2 && game.defUpgradeCost <= game.wallet && game.defLvl < game.rounds / 15) {
                return Action.UPGRADE_DEFENCE;
            }

            ArrayList<Point> path = bestPath(MapType.TREASURY);
            if (path.size() > 1) {
                return getNextMove(path.get(1));
            }
        }

        ArrayList<Point> path = bestPath(MapType.GOLD);

        Logger.log(path);

//        if (path.size() <= 1) {
//            Logger.log("random !");
//
//            if (visibleAgents > 0) {
//                return Action.RANGED_ATTACK;
//            } else {
//                Random random = new Random();
//                int randInt = random.nextInt(4) + 1;
//                return Action.values()[randInt];
//            }
//        }

        Action nextAction = getNextMove(path.get(1));
        Logger.log("action: " + nextAction + ", path: " + path + "\n");

        return nextAction;
    }

    private Action getNextMove(Point point) {
        if (point.x == game.location.x - 1) return Action.MOVE_UP;
        if (point.x == game.location.x + 1) return Action.MOVE_DOWN;
        if (point.y == game.location.y - 1) return Action.MOVE_LEFT;
        if (point.y == game.location.y + 1) return Action.MOVE_RIGHT;
        return Action.STAY;
    }

    private ArrayList<Point> bestPath(MapType targetType) {
        ArrayList<ArrayList<Point>> allPaths = grid.shortestPaths(game.location, targetType);

        for (ArrayList<Point> path : allPaths) {
            Logger.log(path);
        }

        int minSize = Integer.MAX_VALUE;
        ArrayList<Point> shortestPath = new ArrayList<>();
        for (ArrayList<Point> p : allPaths) {
            if (p.size() > 1) {
                if (p.size() < minSize) {
                    minSize = p.size();
                    shortestPath = p;
                }
            }
        }
        return shortestPath;
    }

    private boolean canSee(Point p) {
        return false;
    }

    private boolean canHit(Point p) {
        return false;
    }

    private ArrayList<Point> getVisiblePoints() {

        int x = game.location.x;
        int y = game.location.y;
        int h = Grid.height;
        int w = Grid.width;

        ArrayList<Point> points = new ArrayList<>();
        int r = (game.map.sightRange - 1) / 2;

        int i1 = Math.max(0, x - r);
        int i2 = Math.min(h, x + r);
        int j1 = Math.max(0, y - r);
        int j2 = Math.min(w, y + r);

        for (int i = i1; i < i2; i++) {
            for (int j = j1; j < j2; j++) {
                points.add(new Point(i, j));
            }
        }

        return points;
    }


}
