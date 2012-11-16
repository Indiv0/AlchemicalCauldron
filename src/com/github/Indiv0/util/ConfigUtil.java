/**
 *
 * @author Indivisible0
 */
package com.github.Indiv0.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigUtil extends Util {
    private static File mainDataFolder;
    private static File configFile;
    private static FileConfiguration settings;

    public static void initialize(Plugin parent) {
        Util.initialize(parent);

        // Gets the main folder for data to be stored in.
        mainDataFolder = plugin.getDataFolder();
        // Gets the File representation of config.yml
        configFile = new File(mainDataFolder, "config.yml");

        // Checks to make sure that the main folder for data stored in exists.
        // If not, creates it.
        if (!mainDataFolder.exists())
            createFolder(mainDataFolder);

        // Creates the configuration file if it doesn't exist.
        if (!configFile.exists())
            writeDefaultConfigFile(configFile);

        // Creates/loads configuration and settings.
        loadConfig(configFile);
    }

    private static void loadConfig(File configFile) {
        try {
            // Initializes the configuration and populates it with settings.
            settings = new YamlConfiguration();
            settings.load(configFile);
        } catch (Exception ex) {
            LogUtil.logException(ex, Level.WARNING, "Failed to load configuration.");
        }
    }

    private static void writeDefaultConfigFile(File configFile) {
        plugin.getLogger().log(Level.INFO, "No default config file exists, creating one.");

        BufferedReader bReader = null;
        BufferedWriter bWriter = null;
        String line;

        try {
            // Opens a stream in order to access the config.yml stored in the
            // jar.
            bReader = new BufferedReader(new InputStreamReader(plugin.getClass().getResourceAsStream("/config.yml")));
            bWriter = new BufferedWriter(new FileWriter(configFile));

            // Writes all of the lines from the built in config.yml to the new
            // one.
            while ((line = bReader.readLine()) != null) {
                bWriter.write(line);
                bWriter.newLine();
            }
        } catch (Exception ex) {
            LogUtil.logException(ex, Level.WARNING, "Failed to create default config.yml");
        } finally {
            try {
                // Confirm the streams are closed.
                if (bReader != null) bReader.close();
                if (bWriter != null) bWriter.close();
            } catch (Exception ex) {
                LogUtil.logException(ex, Level.WARNING, "Failed to close buffers while writing default config.yml");
            }
        }
    }

    private static boolean createFolder(File folder) {
        // Attempts to create the folder directory.
        try {
            if (folder.mkdirs()) return true;
        } catch (Exception ex) {
            LogUtil.logException(ex, Level.WARNING, "Data folder could not be created.");
        }
        return false;
    }

    public static boolean getSetting(String setting)
    {
        return settings.getBoolean(setting);
    }
}
