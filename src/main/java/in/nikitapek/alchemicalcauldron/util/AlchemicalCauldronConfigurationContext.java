package in.nikitapek.alchemicalcauldron.util;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import com.amshulman.mbapi.MbapiPlugin;
import com.amshulman.mbapi.util.ConfigurationContext;

public class AlchemicalCauldronConfigurationContext extends ConfigurationContext {
    public final HashMap<Material, Double> inputMaterials = new HashMap<Material, Double>();
    public final HashMap<Material, HashMap<Material, Double>> materialMatches = new HashMap<Material, HashMap<Material, Double>>();

    public AlchemicalCauldronConfigurationContext(MbapiPlugin plugin) {
        super(plugin);

        plugin.saveDefaultConfig();

        loadInputMaterials("inputs");
        loadOutputMaterials("outputs");
    }

    private void loadInputMaterials(String section) {
        Set<String> keyList = plugin.getConfig().getConfigurationSection(section).getKeys(false);

        for (String materialID : keyList) {
            // Attempts to get the material represented by the key.
            Material material = Material.matchMaterial(materialID);

            // Checks to make sure the material is legitimate, has not been entered twice, and is accepted by the plugin prior to proceeding.
            if (!isAllowedMaterial(inputMaterials, material))
                continue;

            // Gets the probability value of the provided material.
            double val = getAndParseConfigDouble(section, materialID);

            // Makes sure that the probability value falls within the expected range.
            if (val < 0 || val > 1) {
                Bukkit.getLogger().log(Level.WARNING, "Config contains an invalid value for key: " + materialID);
                continue;
            }

            // Adds the material/probability key/value set to the material cache.
            inputMaterials.put(material, val);
        }
    }

    private void loadOutputMaterials(String section) {
        Set<String> keyList = plugin.getConfig().getConfigurationSection(section).getKeys(false);

        for (String materialID : keyList) {
            // Attempts to get the material represented by the key.
            Material material = Material.matchMaterial(materialID);

            // Checks to make sure the material is legitimate, has not been entered twice, and is accepted by the plugin prior to proceeding.
            if (!isAllowedMaterial(materialMatches, material))
                continue;

            // Adds the input material and its corresponding HashMap of possible outputs to the cache.
            materialMatches.put(material, new HashMap<Material, Double>());

            // Gets the secondary material list.
            Set<String> outputList = plugin.getConfig().getConfigurationSection(section + "." + materialID).getKeys(false);

            for (String outputID : outputList) {
                // Attempts to get the material represented by the key.
                Material outputMaterial = Material.matchMaterial(outputID);

                // Checks to make sure the material is legitimate, has not been entered twice, and is accepted by the plugin prior to proceeding.
                if (!isAllowedMaterial(materialMatches.get(material), outputMaterial))
                    continue;

                // Gets the probability value of the provided material.
                double val = getAndParseConfigDouble(section + "." + materialID, outputID);

                // Makes sure that the probability value falls within the expected range.
                if (val < 0 || val > 1) {
                    Bukkit.getLogger().log(Level.WARNING, "Config contains an invalid value for key: " + materialID);
                    continue;
                }

                // Adds the material/probability key/value set to the material cache.
                materialMatches.get(material).put(outputMaterial, val);
            }
        }
    }

    public double getAndParseConfigDouble(String section, String key) {
        // Tries to lead the ratio value for that key.
        double val = -1;
        try {
            String valString = plugin.getConfig().getString(section + "." + key);
            val = Double.parseDouble(valString);
        }
        catch (Exception ex) {
            Bukkit.getLogger().log(Level.WARNING, "Config contains an invalid value for key: " + key);
        }

        // Reduce the precision to 2 decimal places.
        DecimalFormat form = new DecimalFormat();
        form.setMaximumFractionDigits(2);
        form.setMinimumFractionDigits(0);
        String formuniversal = form.format(val);
        try {
            val = Double.parseDouble(formuniversal);
        }
        catch (NumberFormatException e) {
            formuniversal = formuniversal.replace(',', '.');
            try {
                val = Double.parseDouble(formuniversal);
            }
            catch (NumberFormatException ex) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to parse config value for key: " + key);
            }
        }

        return val;
    }

    private <K> boolean isAllowedMaterial(HashMap<Material, K> materialList, Material material) {
        // If the key is invalid, output as such.
        if (material == null || material == Material.AIR) {
            Bukkit.getLogger().log(Level.WARNING, "Config contains an invalid key.");
            return false;
        }

        // Makes sure an item is not being added twice, then adds the
        // material and its value to the cache.
        if (materialList.containsKey(material)) {
            Bukkit.getLogger().log(Level.WARNING, "Config contains the material " + material.toString() + " twice. It will not be added again.");
            return false;
        }

        return true;
    }
}
