/**
 *
 * @author Indivisible0
 */
package com.github.Indiv0.util;

import org.bukkit.plugin.Plugin;

public abstract class Util {
    protected static Plugin plugin;
    private static boolean isInitialized = false;

    public static void initialize(Plugin parent) {
        // Checks to make sure Util has not been already initialized.
        if (isInitialized()) return;

        // Sets the parent Plugin for this Util.
        plugin = parent;

        // Sets Util's initialization status to true.
        setInitialized(true);
    }

    public static boolean isInitialized() {
        return isInitialized;
    }

    private static void setInitialized(boolean isInitialized) {
        Util.isInitialized = isInitialized;
    }
}
