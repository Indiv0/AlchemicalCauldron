package com.github.Indiv0.AlchemicalCauldron;

import java.io.File;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AlchemicalCauldron extends JavaPlugin {
    public final EntityInteractListener entityInteractListener = new EntityInteractListener(this);
    
    private HashMap<Material, Double> inputMaterials = new HashMap<Material, Double>();
    private HashMap<Material, Double> outputMaterials = new HashMap<Material, Double>();

    public void onEnable() {
        // Retrieves an instance of the PluginManager.
        PluginManager pm = getServer().getPluginManager();

        // Registers the blockListener with the PluginManager.
        pm.registerEvents(this.entityInteractListener, this);
        
        loadConfig();
        
        //FileConfiguration probabilityConfig = loadConfig("config.yml");
        loadMaterials(getConfig(), getInputMaterials(), "inputs");
        loadMaterials(getConfig(), outputMaterials, "outputs");
        
        // Prints a message to the server confirming successful initialization of the plugin.
        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().info(pdfFile.getName() + " " + pdfFile.getVersion() + " is enabled.");
    }
    
    private void loadMaterials(FileConfiguration fileConfiguration, HashMap<Material, Double> materials, String section)
    {
        ConfigurationSection configSection = fileConfiguration.getConfigurationSection(section);
        
        if(configSection == null) {
            getLogger().log(Level.WARNING, "No keys/values have been defined for the section \"" + section + "\"");
            return;
        }
            
        Set<String> keyList = configSection.getKeys(false);
        
        for(String materialID : keyList) {
            Material material = Material.matchMaterial(materialID);
            
            if(material == null || material == Material.AIR) {
                getLogger().log(Level.WARNING, "AlCo config contains an invalid key: " + materialID);
            }
            else {
                double val = -1;
                try {
                    val = Double.parseDouble((String) fileConfiguration.get(section + "." + materialID));
                } catch(Exception ex) {
                    getLogger().log(Level.WARNING, "AlCo config contains an invalid value for key: " + materialID);
                }
                if(val < 0 || val > 1)
                    getLogger().log(Level.WARNING, "AlCo config contains an invalid value for key: " + materialID);
                
                materials.put(material, val);
            }
        }        
    }
    
    private void loadConfig()
    {
        if(!(new File("plugins/AlchemicalCauldron/config.yml").exists())) {
            // Create some default configuration values.
            getConfig().addDefault("inputs.2", "0.01");
            getConfig().addDefault("inputs.cobblestone", "0.2");
            getConfig().addDefault("outputs.iron_ingot", "0.6");
            
            getConfig().options().copyDefaults(true);
        }
        
        saveConfig();
    }

    public HashMap<Material, Double> getInputMaterials() {
        return inputMaterials;
    }

    public HashMap<Material, Double> getOutputMaterials() {
        return outputMaterials;
    }
}