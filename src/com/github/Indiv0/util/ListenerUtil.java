/**
 *
 * @author Indivisible0
 */
package com.github.Indiv0.util;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class ListenerUtil extends Util {
    private static PluginManager pluginManager;

    public static void initialize(Plugin parent) {
        Util.initialize(parent);

        // Retrieves an instance of the PluginManager.
        pluginManager = plugin.getServer().getPluginManager();
    }

    public static void registerListener(Listener listener) {
        // Registers the Listener with the PluginManager.
        pluginManager.registerEvents(listener, plugin);
    }
}
