/**
 *
 * @author Indivisible0
 */
package com.github.Indiv0.AlchemicalCauldron;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.Indiv0.util.Metrics;

public class AlchemicalCauldron extends JavaPlugin {
    private final String CONFIG_VERSION = "1.3.1";

    public final ItemDropListener itemDropListener = new ItemDropListener(this);

    private final HashMap<Material, Double> inputMaterials = new HashMap<Material, Double>();
    private final HashMap<Material, HashMap<Material, Double>> materialMatches = new HashMap<Material, HashMap<Material, Double>>();

    private File mainDataFolder;
    private File configFile;
    private FileConfiguration settings;

    @Override
    public void onLoad() {
        // Gets the main folder for data to be stored in.
        mainDataFolder = getDataFolder();

        // Checks to make sure that the main folder for data stored in exists.
        // If not, creates it.
        checkFolderAndCreate(mainDataFolder);

        // Creates/loads configuration and settings.
        loadConfig(configFile);

        // Enable PluginMetrics.
        enableMetrics();
    }

    @Override
    public void onEnable() {
        // Retrieves an instance of the PluginManager.
        PluginManager pm = getServer().getPluginManager();

        // Registers the blockListener with the PluginManager.
        pm.registerEvents(itemDropListener, this);

        loadInputMaterials("inputs");
        loadOutputMaterials("outputs");
    }

    @Override
    public void onDisable() {
        // Cancels any tasks scheduled by this plugin.
        getServer().getScheduler().cancelTasks(this);
    }

    private void enableMetrics()
    {
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException ex) {
            logException(ex, Level.WARNING, "An error occured while attempting to connect to PluginMetrics.");
        }
    }

    private void loadInputMaterials(String section) {
        Set<String> keyList = getConfigurationSectionKeySet(section);

        for (String materialID : keyList) {
            // Attempts to get the material represented by the key.
            Material material = Material.matchMaterial(materialID);

            // Checks to make sure the material is legitimate, has not been
            // entered twice, and is accepted by
            // the plugin prior to proceeding.
            if (!isAllowedMaterial(getInputMaterials(), material))
                continue;

            // Gets the probability value of the provided material.
            double val = getAndParseConfigDouble(section, materialID);

            // Makes sure that the probability value falls within the expected
            // range.
            if (val < 0 || val > 1) {
                getLogger().log(Level.WARNING, "Config contains an invalid value for key: " + materialID);
                continue;
            }

            // Adds the material/probability key/value set to the material
            // cache.
            getInputMaterials().put(material, val);
        }
    }

    private void loadOutputMaterials(String section) {
        Set<String> keyList = getConfigurationSectionKeySet(section);

        for (String materialID : keyList) {
            // Attempts to get the material represented by the key.
            Material material = Material.matchMaterial(materialID);

            // Checks to make sure the material is legitimate, has not been
            // entered twice, and is accepted by
            // the plugin prior to proceeding.
            if (!isAllowedMaterial(getOutputMaterials(), material))
                continue;

            // Adds the input material and its corresponding HashMap of possible
            // outputs to the cache.
            getOutputMaterials().put(material, new HashMap<Material, Double>());

            // Gets the secondary material list.
            Set<String> outputList = getConfigurationSectionKeySet(section + "." + materialID);

            for (String outputID : outputList) {
                // Attempts to get the material represented by the key.
                Material outputMaterial = Material.matchMaterial(outputID);

                // Checks to make sure the material is legitimate, has not been
                // entered twice, and is accepted by
                // the plugin prior to proceeding.
                if (!isAllowedMaterial(getMaterialMatches(material), outputMaterial))
                    continue;

                // Gets the probability value of the provided material.
                double val = getAndParseConfigDouble(section + "." + materialID, outputID);

                // Makes sure that the probability value falls within the
                // expected range.
                if (val < 0 || val > 1) {
                    getLogger().log(Level.WARNING, "Config contains an invalid value for key: " + materialID);
                    continue;
                }

                // Adds the material/probability key/value set to the material
                // cache.
                getMaterialMatches(material).put(outputMaterial, val);
            }
        }
    }

    private <K> boolean isAllowedMaterial(HashMap<Material, K> materialList, Material material) {
        // If the key is invalid, output as such.
        if (material == null || material == Material.AIR) {
            getLogger().log(Level.WARNING, "Config contains an invalid key.");
            return false;
        }

        // Makes sure an item is not being added twice, then adds the
        // material and its value to the cache.
        if (materialList.containsKey(material)) {
            getLogger().log(Level.WARNING, "Config contains the material " + material.toString() + " twice. It will not be added again.");
            return false;
        }

        return true;
    }

    private double getAndParseConfigDouble(String section, String key) {
        // Tries to lead the ratio value for that key.
        double val = -1;
        try {
            val = Double.parseDouble((String) settings.get(section + "." + key));
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            getLogger().log(Level.WARNING, "Config contains an invalid value for key: " + key);
        }

        // Reduce the precision to 2 decimal places.
        DecimalFormat form = new DecimalFormat();
        form.setMaximumFractionDigits(2);
        form.setMinimumFractionDigits(0);
        val = Double.parseDouble(form.format(val));

        return val;
    }

    private Set<String> getConfigurationSectionKeySet(String section) {
        // Defines the section of the configuration to be searched.
        ConfigurationSection configSection = settings.getConfigurationSection(section);

        // If the configuration section does not exist, outputs a warning.
        if (configSection == null) {
            getLogger().log(Level.WARNING, "No keys/values have been defined for the section \"" + section + "\"");
            return null;
        }

        // Gets all of the keys for the section.
        Set<String> keyList = configSection.getKeys(false);

        return keyList;
    }

    private void loadConfig(File configFile) {
        configFile = new File(mainDataFolder, "config.yml");

        try {
            // Creates the configuration file if it doesn't exist.
            if (!configFile.exists()) {
                getLogger().log(Level.INFO, "No default config file exists, creating one.");
                createDefaultConfigFile(configFile);
            }

            // Initializes the configuration and populates it with settings.
            settings = new YamlConfiguration();
            settings.load(configFile);

            // Makes sure the configuration file is up to date. If not, updates
            // it.
            if (!checkSettingsVersion(CONFIG_VERSION))
                createDefaultConfigFile(configFile);
        } catch (Exception ex) {
            logException(ex, Level.WARNING, "Failed to load configuration.");
        }
    }

    public boolean checkSettingsVersion(String requiredVersion) {
        // Get the version information from the file.
        String configVersion = settings.getString("version", null);

        // Check we got a version from the config file.
        if (configVersion == null) {
            logException(Level.WARNING, "Failed to retrieve the version of the configuration file.");
            return false;
        } else if (!configVersion.equals(requiredVersion)) {
            logException(Level.WARNING, "Configuration is out of date, updating.");
            return false;
        }

        return true;
    }

    public void createDefaultConfigFile(File configFile) {
        BufferedReader bReader = null;
        BufferedWriter bWriter = null;
        String line;

        try {
            // Opens a stream in order to access the config.yml stored in the
            // jar.
            bReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/config.yml")));
            bWriter = new BufferedWriter(new FileWriter(configFile));

            // Writes all of the lines from the built in config.yml to the new
            // one.
            while ((line = bReader.readLine()) != null) {
                bWriter.write(line);
                bWriter.newLine();
            }
        } catch (Exception ex) {
            logException(ex, Level.WARNING, "Failed to create default config.yml");
        } finally {
            try {
                // Confirm the streams are closed.
                if (bReader != null) bReader.close();
                if (bWriter != null) bWriter.close();
            } catch (Exception ex) {
                logException(ex, Level.WARNING, "Failed to close buffers while writing default config.yml");
            }
        }
    }

    public boolean checkFolderAndCreate(File toCheck) {
        // Check to see if the directory exists, creating it if it doesn't.
        if (!toCheck.exists()) try {
            if (toCheck.mkdirs()) return true;
        } catch (Exception ex) {
            logException(ex, Level.WARNING, "Data folder could not be created.");
        }
        return false;
    }

    public void logException(Level level, String message) {
        getLogger().log(level, message);
    }

    public void logException(Exception ex, Level level, String message) {
        ex.printStackTrace(System.out);
        getLogger().log(level, message);
    }

    public HashMap<Material, Double> getInputMaterials() {
        return inputMaterials;
    }

    public HashMap<Material, HashMap<Material, Double>> getOutputMaterials() {
        return materialMatches;
    }

    public HashMap<Material, Double> getMaterialMatches(Material inputMaterial) {
        return materialMatches.get(inputMaterial);
    }
}