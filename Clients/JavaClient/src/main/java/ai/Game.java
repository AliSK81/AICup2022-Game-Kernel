package ai;

import cli.Action;
import cli.GameState;
import cli.MapType;
import cli.Point;

import java.util.ArrayList;
import java.util.Random;

import static ai.Logger.log;

public class Game extends GameState {

    public Action getAction() {
        log(this);

        Grid grid = new Grid();
        grid.initGrid(map);

        ArrayList<Point> path = grid.shortestPath(location, MapType.GOLD);

        if (path.size() <= 1) {
            log("random !");
            Random random = new Random();
            int randInt = random.nextInt(12);
            return Action.values()[randInt];
        }

        Action nextAction = getNextMove(path.get(1));
        log("action: " + nextAction + ", path: " + path);

        return nextAction;
    }

    Action getNextMove(Point point) {
        if (point.x == location.x - 1) return Action.MOVE_UP;
        if (point.x == location.x + 1) return Action.MOVE_DOWN;
        if (point.y == location.y - 1) return Action.MOVE_LEFT;
        if (point.y == location.y + 1) return Action.MOVE_RIGHT;
        return Action.STAY;
    }


}
