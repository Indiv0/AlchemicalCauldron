package in.nikitapek.alchemicalcauldron;

import in.nikitapek.alchemicalcauldron.events.AlchemicalCauldronListener;
import in.nikitapek.alchemicalcauldron.util.AlchemicalCauldronConfigurationContext;

import org.bukkit.Bukkit;

import com.amshulman.mbapi.MbapiPlugin;

public final class AlchemicalCauldronPlugin extends MbapiPlugin {
    @Override
    public void onEnable() {
        registerEventHandler(new AlchemicalCauldronListener(new AlchemicalCauldronConfigurationContext(this)));

        super.onEnable();
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }
}
