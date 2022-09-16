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
