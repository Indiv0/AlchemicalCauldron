/**
 *
 * @author Indivisible0
 */
package com.github.Indiv0.util;

import java.util.logging.Level;

public class LogUtil extends Util {
    public static void logException(Level level, String message) {
        plugin.getLogger().log(level, message);
    }

    public static void logException(Exception ex, Level level, String message) {
        ex.printStackTrace(System.out);
        logException(level, message);
    }
}
