package com.github.Indiv0.AlchemicalCauldron;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AlchemicalCauldron extends JavaPlugin {
    public final EntityInteractListener entityInteractListener = new EntityInteractListener();

    public void onEnable() {
        // Retrieves an instance of the PluginManager.
        PluginManager pm = getServer().getPluginManager();

        // Registers the blockListener with the PluginManager.
        pm.registerEvents(this.entityInteractListener, this);

        // Prints a message to the server confirming successful initialization of the plugin.
        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().info(pdfFile.getName() + " " + pdfFile.getVersion() + " is enabled.");
    }
}