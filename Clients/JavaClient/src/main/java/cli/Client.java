package cli;


import ai.Game;
import ai.Logger;

import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class Client {
    public static Scanner scanner = new Scanner(System.in).useLocale(Locale.US);

    public static void main(String[] args) {
        try {
            Game game = new Game();
            for (int i = 0; i < game.rounds; i++) {
                game.setInfo();
                System.out.println(game.getAction().ordinal());
            }
        } catch (Exception e) {
            Arrays.stream(e.getStackTrace()).forEach(Logger::error);
        } finally {
            Logger.close();
        }
    }

}