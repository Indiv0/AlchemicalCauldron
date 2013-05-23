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
    private final HashMap<Material, Double> inputMaterials = new HashMap<Material, Double>();
    private final HashMap<Material, HashMap<Material, Double>> materialMatches = new HashMap<Material, HashMap<Material, Double>>();

    public AlchemicalCauldronConfigurationContext(final MbapiPlugin plugin) {
        super(plugin);

        plugin.saveDefaultConfig();

        loadInputMaterials("inputs");
        loadOutputMaterials("outputs");
    }

    private void loadInputMaterials(final String section) {
        final Set<String> keyList = plugin.getConfig().getConfigurationSection(section).getKeys(false);

        for (final String materialID : keyList) {
            // Attempts to get the material represented by the key.
            final Material material = Material.matchMaterial(materialID);

            // Checks to make sure the material is legitimate, has not been entered twice, and is accepted by the plugin prior to proceeding.
            if (!isAllowedMaterial(getInputMaterials(), material)) {
                continue;
            }

            // Gets the probability value of the provided material.
            final double val = getAndParseConfigDouble(section, materialID);

            // Makes sure that the probability value falls within the expected range.
            if (val < 0 || val > 1) {
                Bukkit.getLogger().log(Level.WARNING, "Config contains an invalid value for key: " + materialID);
                continue;
            }

            // Adds the material/probability key/value set to the material cache.
            getInputMaterials().put(material, val);
        }
    }

    private void loadOutputMaterials(final String section) {
        final Set<String> keyList = plugin.getConfig().getConfigurationSection(section).getKeys(false);

        for (final String materialID : keyList) {
            // Attempts to get the material represented by the key.
            final Material material = Material.matchMaterial(materialID);

            // Checks to make sure the material is legitimate, has not been entered twice, and is accepted by the plugin prior to proceeding.
            if (!isAllowedMaterial(getMaterialMatches(), material)) {
                continue;
            }

            // Adds the input material and its corresponding HashMap of possible outputs to the cache.
            getMaterialMatches().put(material, new HashMap<Material, Double>());

            // Gets the secondary material list.
            final Set<String> outputList = plugin.getConfig().getConfigurationSection(section + "." + materialID).getKeys(false);

            for (final String outputID : outputList) {
                // Attempts to get the material represented by the key.
                final Material outputMaterial = Material.matchMaterial(outputID);

                // Checks to make sure the material is legitimate, has not been entered twice, and is accepted by the plugin prior to proceeding.
                if (!isAllowedMaterial(getMaterialMatches().get(material), outputMaterial)) {
                    continue;
                }

                // Gets the probability value of the provided material.
                final double val = getAndParseConfigDouble(section + "." + materialID, outputID);

                // Makes sure that the probability value falls within the expected range.
                if (val < 0 || val > 1) {
                    Bukkit.getLogger().log(Level.WARNING, "Config contains an invalid value for key: " + materialID);
                    continue;
                }

                // Adds the material/probability key/value set to the material cache.
                getMaterialMatches().get(material).put(outputMaterial, val);
            }
        }
    }

    public final double getAndParseConfigDouble(final String section, final String key) {
        // Tries to lead the ratio value for that key.
        double val = -1;
        try {
            final String valString = plugin.getConfig().getString(section + "." + key);
            val = Double.parseDouble(valString);
        } catch (final NumberFormatException ex) {
            Bukkit.getLogger().log(Level.WARNING, "Config contains an invalid value for key: " + key);
        }

        // Reduce the precision to 2 decimal places.
        final DecimalFormat form = new DecimalFormat();
        form.setMaximumFractionDigits(2);
        form.setMinimumFractionDigits(0);
        String formuniversal = form.format(val);
        try {
            val = Double.parseDouble(formuniversal);
        } catch (final NumberFormatException e) {
            formuniversal = formuniversal.replace(',', '.');
            try {
                val = Double.parseDouble(formuniversal);
            } catch (final NumberFormatException ex) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to parse config value for key: " + key);
            }
        }

        return val;
    }

    private <K> boolean isAllowedMaterial(final HashMap<Material, K> materialList, final Material material) {
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

    /**
     * @return the inputMaterials
     */
    public final HashMap<Material, Double> getInputMaterials() {
        return inputMaterials;
    }

    /**
     * @return the materialMatches
     */
    public final HashMap<Material, HashMap<Material, Double>> getMaterialMatches() {
        return materialMatches;
    }
}
