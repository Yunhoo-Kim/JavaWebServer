package helper;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.core.BasicConfigurationFactory;
import org.apache.logging.log4j.simple.SimpleLogger;

/**
 * Created by semaj on 17. 10. 20.
 */

public class DebugUtil {
    public static Logger log;

    static
    {
        log = Logger.getLogger(SimpleLogger.class);
        BasicConfigurator.configure();
    }

}
