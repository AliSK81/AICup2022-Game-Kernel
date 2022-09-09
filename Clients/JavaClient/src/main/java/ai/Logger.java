package ai;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Logger {

    private static PrintWriter out;

    public static void init(Integer id) {
        if (out != null) return;
        try {
            out = new PrintWriter(String.format("Clients/logs/Agent-%d.log", id));
        } catch (FileNotFoundException e) {
            out.close();
            e.printStackTrace();
        }
    }

    public static <T> void log(T msg) {
        if (out == null) return;
        out.println(msg);
    }

    public static void close() {
        out.close();
    }
}
