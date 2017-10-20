package helper;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by semaj on 17. 10. 20.
 */

public class DebugUtil {
    public static Logger log;

    static
    {
        log = Logger.getLogger("");
        log.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.ALL);
        log.addHandler(handler);
    }

    public static void info(String text){
        log.info(text);
    }

    public static void warn(String text){
        log.warning(text);
    }
}
