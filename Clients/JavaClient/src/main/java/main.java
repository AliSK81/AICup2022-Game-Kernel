import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

enum MapType {
    EMPTY,
    AGENT,
    GOLD,
    TREASURY,
    WALL,
    FOG,
    OUT_OF_SIGHT,
    OUT_OF_MAP
}

enum Action {
    STAY,
    MOVE_DOWN,
    MOVE_UP,
    MOVE_RIGHT,
    MOVE_LEFT,
    UPGRADE_DEFENCE,
    UPGRADE_ATTACK,
    LINEAR_ATTACK_DOWN,
    LINEAR_ATTACK_UP,
    LINEAR_ATTACK_RIGHT,
    LINEAR_ATTACK_LEFT,
    RANGED_ATTACK
}

public class main {
    public static Scanner scanner = new Scanner(System.in).useLocale(Locale.US);

    public static void main(String[] args) {
        GameState gameState = new GameState();
        for (int i = 0; i < gameState.rounds; i++) {
            gameState.setInfo();
            System.out.println(new Game(gameState).getAction().ordinal());
        }
        Logger.close();
    }

    static class Game {
        GameState game;
        Grid grid;
        static boolean treasuryFound;

        int myData;
        ArrayList<Point> path = new ArrayList<>();

        Point lastMove;

        ArrayList<MapTile> enemies = new ArrayList<>();

        public Game(GameState gameState) {
            this.game = gameState;

    //        main.Logger.init(game.agentID);

            this.grid = new Grid(game.map);

            Logger.log(game);

            for (int[] row : Grid.D) {
                Logger.log(Arrays.toString(row));
            }

            ArrayList<MapTile> agents = new ArrayList<>();

            for (MapTile tile : game.map.grid) {
                if (tile.type == MapType.TREASURY) {
                    treasuryFound = true;
                }
                // todo change
                if (game.lastAction == -1 && lastMove == tile.coordinates) {
                    Grid.filter.add(tile.coordinates);
                }
                if (tile.coordinates.equals(game.location)) {
                    myData = tile.data;
                }
                if (tile.type == MapType.AGENT) {
                    agents.add(tile);
                }
            }

            for (MapTile agentTile : agents) {
                if (isEnemy(agentTile)) {
                    enemies.add(agentTile);
                }
            }

        }

        public Action getAction() {

            try {
                return getNextAction();
            } catch (Exception e) {
                Logger.error(e);
                Logger.close();
//                throw e;
            }

            return Action.STAY;
        }

        private Action getNextAction() {

            Logger.log("filter: " + Grid.filter);
            Logger.log("enemies: " + enemies);

            Action nextAction = Action.STAY;

            if (enemies.size() > 0 && getAttackAction() != null) {
                Logger.log("result: attack");
                nextAction = getAttackAction();

            } else if (mustSave()) {
                Logger.log("result: save");
                path = bestPath(MapType.TREASURY);
                nextAction = getNextMove(lastMove = path.get(1));

            } else if (canUpgradeDef()) {
                Logger.log("result: upDef");
                nextAction = Action.UPGRADE_DEFENCE;

            } else if (canUpgradeAttack()) {
                Logger.log("result: upAtk");
                nextAction = Action.UPGRADE_ATTACK;

            } else if (canGather()) {
                Logger.log("result: gather");
                path = bestPath(MapType.GOLD);
                nextAction = getNextMove(lastMove = path.get(1));

            } else {
                Logger.log("result: random");
                // todo
            }


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

    //        for (ArrayList<Point> path : allPaths) {
    //            main.Logger.log(path);
    //        }

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

        private int enemyCoinsRadiosAttack() {
            int counter = 0;

            for (MapTile tile : enemies) {
                if (canHitRadius(tile.coordinates)) {
                    int wallet = game.wallets.get(tile.data);
                    counter += wallet;
                }
            }

            return counter;
        }

        private int enemyCoinsLeftLinearAttack() {
            int counter = 0;

            for (MapTile tile : enemies) {
                if (canHitLeftLinear(tile.coordinates)) {
                    int wallet = game.wallets.get(tile.data);
                    counter += wallet;
                }
            }

            return counter;
        }

        private int enemyCoinsRightLinearAttack() {
            int counter = 0;

            for (MapTile tile : enemies) {
                if (canHitRightLinear(tile.coordinates)) {
                    int wallet = game.wallets.get(tile.data);
                    counter += wallet;
                }
            }

            return counter;
        }

        private int enemyCoinsUpLinearAttack() {
            int counter = 0;

            for (MapTile tile : enemies) {
                if (canHitUpLinear(tile.coordinates)) {
                    int wallet = game.wallets.get(tile.data);
                    counter += wallet;
                }
            }

            return counter;
        }

        private int enemyCoinsDownLinearAttack() {
            int counter = 0;

            for (MapTile tile : enemies) {
                if (canHitDownLinear(tile.coordinates)) {
                    int wallet = game.wallets.get(tile.data);
                    counter += wallet;
                }
            }

            return counter;
        }

        private boolean canSee(Point p) {
            if (Math.abs(game.location.x - p.x) + Math.abs(game.location.y - p.y)
                    <= (game.map.sightRange - 1) / 2) {

                return true;
            }

            return false;
        }

        private boolean canHit(Point p) {
            return canHitLinear(p) || canHitRadius(p);
        }

        private boolean canHitRadius(Point p) {
            return Math.abs(game.location.x - p.x) + Math.abs(game.location.y - p.y)
                    <= game.rangedAttackRadius;
        }

        private boolean canHitLinear(Point p) {
            return canHitRightLinear(p) ||
                    canHitLeftLinear(p) ||
                    canHitUpLinear(p) ||
                    canHitDownLinear(p);
        }

        private boolean canHitRightLinear(Point p) {
            if (p.x == game.location.x && p.y > game.location.y) {
                if (Math.abs(game.location.y - p.y) <= game.linearAttackRange) {
                    int s = game.location.y;
                    int end = p.y;
                    for (int i = s; i < end; i++) {
                        if (Grid.D[p.x][i] == MapType.WALL.ordinal())
                            return false;
                    }
                    return true;
                }
            }
            return false;
        }

        private boolean canHitLeftLinear(Point p) {
            if (p.x == game.location.x && p.y < game.location.y) {
                if (Math.abs(game.location.y - p.y) <= game.linearAttackRange) {
                    int s = p.y;
                    int end = game.location.y;
                    for (int i = s; i < end; i++) {
                        if (Grid.D[p.x][i] == MapType.WALL.ordinal())
                            return false;
                    }
                    return true;
                }
            }
            return false;
        }

        private boolean canHitUpLinear(Point p) {
            if (p.y == game.location.y && p.x < game.location.x) {
                if (Math.abs(game.location.x - p.x) <= game.linearAttackRange) {
                    int s = p.x;
                    int end = game.location.x;
                    for (int i = s; i < end; i++) {
                        if (Grid.D[i][p.y] == MapType.WALL.ordinal())
                            return false;
                    }
                    return true;
                }
            }
            return false;
        }

        private boolean canHitDownLinear(Point p) {
            if (p.y == game.location.y && p.x > game.location.x) {
                if (Math.abs(game.location.x - p.x) <= game.linearAttackRange) {
                    int s = game.location.x;
                    int end = p.x;
                    for (int i = s; i < end; i++) {
                        if (Grid.D[i][p.y] == MapType.WALL.ordinal())
                            return false;
                    }
                    return true;
                }
            }
            return false;
        }

        private ArrayList<Point> getVisiblePoints() {

            int x = game.location.x;
            int y = game.location.y;
            int h = Grid.height;
            int w = Grid.width;

            ArrayList<Point> points = new ArrayList<>();
            int r = (game.map.sightRange - 1) / 2;

            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    int dist = Math.abs(x - i) + Math.abs(y - j);
                    if (dist <= r) points.add(new Point(i, j));
                }
            }

            return points;
        }

        private boolean mustSave() {

            if (!treasuryFound) {
                return false;
            }

            if (game.wallet == 0) {
                return false;
            }

            if (game.wallet >= 20) {
                return true;
            }

            ArrayList<Point> path = bestPath(MapType.TREASURY);

            int remRounds = game.rounds - game.currentRound;

            if (!path.isEmpty() && remRounds <= path.size()) {
                return true;
            }

            return false;

        }


        boolean canGather() {
            return bestPath(MapType.GOLD).size() > 1;
        }


        int maxLoss() {
            int enemyAtkLvl = game.currentRound / 20;
            enemyAtkLvl = Math.max(enemyAtkLvl, 1);
            enemyAtkLvl = Math.min(enemyAtkLvl, 4);
            return game.wallet * enemyAtkLvl / (game.defLvl + enemyAtkLvl);
        }


        boolean isNeighborPoint(Point p) {
            if (p == null) return false;
            return getNextMove(p) == Action.STAY;
        }


        boolean isEnemy(MapTile tile) {

            if (tile.type != MapType.AGENT) {
                return false;
            }

            int myTeam = myData <= 1 ? 1 : 2;
            int tileTeam = tile.data <= 1 ? 1 : 2;

            return myTeam != tileTeam;

        }

        boolean canUpgradeDef() {
            return game.rounds / game.currentRound > 5
                    && game.wallet >= game.defUpgradeCost
                    && game.defLvl < 3;
        }

        boolean canUpgradeAttack() {
            return game.rounds / game.currentRound > 4
                    && game.wallet >= game.atkUpgradeCost
                    && game.atkLvl < 3;
        }

        Action getAttackAction() {
            int maxGold = 0;
            Action attackAction = null;
            int dl = enemyCoinsDownLinearAttack();
            int ul = enemyCoinsUpLinearAttack();
            int ll = enemyCoinsLeftLinearAttack();
            int rl = enemyCoinsRightLinearAttack();
            int ra = enemyCoinsRadiosAttack();

            if (dl > maxGold) {
                maxGold = dl;
                attackAction = Action.LINEAR_ATTACK_DOWN;
            }
            if (ul > maxGold) {
                maxGold = ul;
                attackAction = Action.LINEAR_ATTACK_UP;
            }
            if (rl > maxGold) {
                maxGold = rl;
                attackAction = Action.LINEAR_ATTACK_RIGHT;
            }
            if (ll > maxGold) {
                maxGold = ll;
                attackAction = Action.LINEAR_ATTACK_LEFT;
            }
            if (ra > maxGold) {
                maxGold = ra;
                attackAction = Action.RANGED_ATTACK;
            }

            if (game.attackRatio < 1 || game.coolDownRate * maxGold * (game.attackRatio / (1 + game.attackRatio)) < 5) {
                return null;
            }

            return attackAction;
        }


    }

    public static class Grid {

        public static final Integer INF = 9;

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
            return !matches(p, MapType.WALL) && !matches(p, MapType.AGENT) && !filter.contains(p);
        }

        public boolean forbidden(Point p) {
            return !allowed(p);
        }

        public boolean validTile(MapTile tile) {
            return tile.type != MapType.OUT_OF_SIGHT && tile.type != MapType.OUT_OF_MAP;
        }



    }

    public static class Logger {

        private static final String errLoc = "../logs/err.log";
        private static final String logLoc = "../logs/agent.ID.log";
        private static PrintWriter out;
        private static PrintWriter err;

        public static void init(Integer id) {
            if (out != null) return;
            try {
                new File("../logs").mkdir();
                out = new PrintWriter(logLoc.replace("ID", id.toString()));
                err = new PrintWriter(errLoc);
            } catch (IOException e) {
                if (out != null) out.close();
                e.printStackTrace();
            }
        }

        public static <T> void log(T msg) {
            if (out != null) out.println(msg);
        }

        public static <T> void error(Exception e) {
            if (out == null) return;
            err.println(e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(trace -> err.println(trace.toString()));
        }

        public static void close() {
            if (out != null) out.close();
            if (err != null) {
                err.close();
                File err = new File(errLoc);
                if (err.length() == 0) err.delete();
            }
        }
    }
}

class Point {

    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public String toString() {
        return "(x=" + x + ", y=" + y + ')';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (x != point.x) return false;
        return y == point.y;
    }


    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}

class MapTile {
    public MapType type;
    public int data;
    public Point coordinates;


    @Override
    public String toString() {
        return "\n\t\t" +
                coordinates +
                ", " + data +
                ", " + type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapTile mapTile = (MapTile) o;

        return coordinates.equals(mapTile.coordinates);
    }

    @Override
    public int hashCode() {
        return coordinates.hashCode();
    }
}


class Map {
    public int width, height;
    public int goldCount;
    public int sightRange;
    public Vector<MapTile> grid;


    @Override
    public String toString() {

        List<MapTile> grid = this.grid.stream()
                .filter(tile -> tile.type != MapType.OUT_OF_SIGHT && tile.type != MapType.OUT_OF_MAP)
                .sorted(Comparator.comparingInt((MapTile t) -> t.coordinates.x).thenComparing((t -> t.coordinates.y)))
                .collect(Collectors.toList());

        return "\n\tmain.Client.Map{" +
                "width=" + width +
                ", height=" + height +
                ", gold=" + goldCount +
                ", vision=" + sightRange +
                grid +
                "\n\t}\n";
    }
}

class GameState {
    public int rounds;
    public int defUpgradeCost, atkUpgradeCost;
    public float coolDownRate;
    public int linearAttackRange, rangedAttackRadius;
    public Map map;
    public Point location;
    public int agentID;
    public int currentRound;
    public float attackRatio;
    public int defLvl, atkLvl;
    public int wallet, safeWallet;
    public Vector<Integer> wallets;
    public int lastAction;

    public GameState() {
        map = new Map();
        rounds = main.scanner.nextInt();
        defUpgradeCost = main.scanner.nextInt();
        atkUpgradeCost = main.scanner.nextInt();
        coolDownRate = main.scanner.nextFloat();
        linearAttackRange = main.scanner.nextInt();
        rangedAttackRadius = main.scanner.nextInt();
        map.width = main.scanner.nextInt();
        map.height = main.scanner.nextInt();
        map.goldCount = main.scanner.nextInt();
        map.sightRange = main.scanner.nextInt(); // equivalent to (2r+1)
        map.grid = new Vector<>();
    }

    public void setInfo() {
        int x = main.scanner.nextInt();
        int y = main.scanner.nextInt();
        location = new Point(x, y); // (row, column)
        map.grid.clear();
        for (int i = 0; i < map.sightRange * map.sightRange; i++) {
            MapTile tile = new MapTile();
            tile.type = MapType.values()[main.scanner.nextInt()];
            tile.data = main.scanner.nextInt();
            x = main.scanner.nextInt();
            y = main.scanner.nextInt();
            tile.coordinates = new Point(x, y);
            map.grid.add(tile);
        }
        agentID = main.scanner.nextInt(); // player1: 0,1 --- player2: 2,3
        currentRound = main.scanner.nextInt(); // 1 indexed
        attackRatio = main.scanner.nextFloat();
        defLvl = main.scanner.nextInt();
        atkLvl = main.scanner.nextInt();
        wallet = main.scanner.nextInt();
        safeWallet = main.scanner.nextInt();
        wallets = new Vector<>(); // current wallet
        for (int i = 0; i < 4; i++) {
            wallets.add(main.scanner.nextInt());
        }
        lastAction = main.scanner.nextInt(); // -1 if unsuccessful
        main.scanner.nextLine();
    }


    @Override
    public String toString() {
        return "Round " + currentRound + " {" +
                "\n\trounds=" + rounds +
                "\n\tlocation=" + location +
                "\n\tlastAction=" + lastAction +
                "\n\tdefUpgradeCost=" + defUpgradeCost +
                "\n\tatkUpgradeCost=" + atkUpgradeCost +
                "\n\tcoolDownRate=" + coolDownRate +
                "\n\tlinearAttackRange=" + linearAttackRange +
                "\n\trangedAttackRadius=" + rangedAttackRadius +
                "\n\tattackRatio=" + attackRatio +
                "\n\tdefLvl=" + defLvl +
                "\n\tatkLvl=" + atkLvl +
                "\n\twallet=" + wallet +
                "\n\tsafeWallet=" + safeWallet +
                "\n\twallets=" + wallets +
                "\n" + map +
                "}\n";
    }
}
