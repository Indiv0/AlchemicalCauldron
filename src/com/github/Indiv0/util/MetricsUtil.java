/**
 *
 * @author Indivisible0
 */
package com.github.Indiv0.util;

import java.io.IOException;

import org.bukkit.plugin.Plugin;

public class MetricsUtil extends Util {
    public static void initialize(Plugin parent) {
        Util.initialize(parent);

        // Enable PluginMetrics.
        MetricsUtil.enableMetrics();
    }

    private static void enableMetrics()
    {
        try {
            Metrics metrics = new Metrics(plugin);
            metrics.start();
        } catch (IOException ex) {
            System.out.println("An error occured while attempting to connect to PluginMetrics.");
        }
    }
}
