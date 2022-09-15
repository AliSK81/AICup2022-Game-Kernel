package ai;

import java.util.Random;

import static ai.Client.Action;
import static ai.Client.GameState;
import static ai.Logger.log;

public class Game extends GameState {

    public Action getAction() {
        log(this);

        Random random = new Random();
        int randInt = random.nextInt(12);
        return Action.values()[randInt];
    }


}
