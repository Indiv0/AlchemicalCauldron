package in.nikitapek.alchemicalcauldron;

import in.nikitapek.alchemicalcauldron.events.AlchemicalCauldronListener;
import in.nikitapek.alchemicalcauldron.util.AlchemicalCauldronConfigurationContext;

import com.amshulman.mbapi.MbapiPlugin;

public final class AlchemicalCauldronPlugin extends MbapiPlugin {
    @Override
    public void onEnable() {
        registerEventHandler(new AlchemicalCauldronListener(new AlchemicalCauldronConfigurationContext(this)));
        super.onEnable();
    }
}
