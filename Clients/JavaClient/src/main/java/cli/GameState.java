package cli;

import ai.Logger;

import java.util.Vector;

public class GameState {
    public int rounds;
    public int defUpgradeCost, atkUpgradeCost;
    public float coolDownRate;
    public int linearAttackRange, rangedAttackRadius;
    public Map map;
    public Point location;
    public int agentID;
    public int currentRound;
    public float attackRatio;
    public int deflvl, atklvl;
    public int wallet, safeWallet;
    public Vector<Integer> wallets;
    public int lastAction;

    public GameState() {
        map = new Map();
        rounds = Client.scanner.nextInt();
        defUpgradeCost = Client.scanner.nextInt();
        atkUpgradeCost = Client.scanner.nextInt();
        coolDownRate = Client.scanner.nextFloat();
        linearAttackRange = Client.scanner.nextInt();
        rangedAttackRadius = Client.scanner.nextInt();
        map.width = Client.scanner.nextInt();
        map.height = Client.scanner.nextInt();
        map.goldCount = Client.scanner.nextInt();
        map.sightRange = Client.scanner.nextInt(); // equivalent to (2r+1)
        map.grid = new Vector<>();
    }

    public void setInfo() {
        int x = Client.scanner.nextInt();
        int y = Client.scanner.nextInt();
        location = new Point(x, y); // (row, column)
        map.grid.clear();
        for (int i = 0; i < map.sightRange * map.sightRange; i++) {
            MapTile tile = new MapTile();
            tile.type = MapType.values()[Client.scanner.nextInt()];
            tile.data = Client.scanner.nextInt();
            x = Client.scanner.nextInt();
            y = Client.scanner.nextInt();
            tile.coordinates = new Point(x, y);
            map.grid.add(tile);
        }
        agentID = Client.scanner.nextInt(); // player1: 0,1 --- player2: 2,3
        currentRound = Client.scanner.nextInt(); // 1 indexed
        attackRatio = Client.scanner.nextFloat();
        deflvl = Client.scanner.nextInt();
        atklvl = Client.scanner.nextInt();
        wallet = Client.scanner.nextInt();
        safeWallet = Client.scanner.nextInt();
        wallets = new Vector<>(); // current wallet
        for (int i = 0; i < 4; i++) {
            wallets.add(Client.scanner.nextInt());
        }
        lastAction = Client.scanner.nextInt(); // -1 if unsuccessful
        Client.scanner.nextLine();

        Logger.init(agentID);
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
                "\n\tdefLvl=" + deflvl +
                "\n\tatkLvl=" + atklvl +
                "\n\twallet=" + wallet +
                "\n\tsafeWallet=" + safeWallet +
                "\n\twallets=" + wallets +
                "\n" + map +
                "}\n";
    }
}
