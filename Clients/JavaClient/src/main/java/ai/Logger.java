package ai;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {

    private static PrintWriter out;

    public static void init(Integer id) {
        if (out != null) return;
        File file = new File("../logs/agent." + id + ".log");
        try {
            file.getParentFile().mkdirs();
            out = new PrintWriter(file);
        } catch (IOException e) {
            if (out != null) out.close();
            e.printStackTrace();
        }
    }

    public static <T> void log(T msg) {
        if (out != null) out.println(msg);
    }

    public static void close() {
        if (out != null) out.close();
    }
}
