import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class Logger {

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
