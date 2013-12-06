package in.nikitapek.alchemicalcauldron;

import com.amshulman.mbapi.MbapiPlugin;
import in.nikitapek.alchemicalcauldron.events.AlchemicalCauldronListener;
import in.nikitapek.alchemicalcauldron.util.AlchemicalCauldronConfigurationContext;

public final class AlchemicalCauldronPlugin extends MbapiPlugin {
    @Override
    public void onEnable() {
        registerEventHandler(new AlchemicalCauldronListener(new AlchemicalCauldronConfigurationContext(this)));
        super.onEnable();
    }
}
