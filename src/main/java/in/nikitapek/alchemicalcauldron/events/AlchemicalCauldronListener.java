package in.nikitapek.alchemicalcauldron.events;

import java.util.Map.Entry;
import java.util.Set;

import in.nikitapek.alchemicalcauldron.util.AlchemicalCauldronConfigurationContext;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class AlchemicalCauldronListener implements Listener {
    private AlchemicalCauldronConfigurationContext configurationContext;

    public AlchemicalCauldronListener(final AlchemicalCauldronConfigurationContext configurationContext) {
        this.configurationContext = configurationContext;
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        // Gets the block targetted by the player.
        Block targetBlock = event.getPlayer().getTargetBlock(null, 1);

        // Checks to make sure the block is a CAULDRON.
        try {
            if (targetBlock.getType() != Material.CAULDRON)
                return;
        }
        catch (NullPointerException ex) {
            return;
        }

        // If the player does not have permissions to use AlchemicalCauldron, cancels the event.
        if (!event.getPlayer().hasPermission("alchemicalcauldron.use"))
            return;

        // Gets the thrown item and converts it into a usable ItemStack.
        ItemStack thrownItemStack = event.getItemDrop().getItemStack();

        // Checks to make sure the ItemStack contains a valid input material.
        if (!configurationContext.inputMaterials.containsKey(thrownItemStack.getType()))
            return;

        // Gets the probability for that input item.
        double inputProbability = configurationContext.inputMaterials.get(thrownItemStack.getType());

        for (int i = 1; i <= thrownItemStack.getAmount(); i++) {
            // If the conversion fails, delete the ItemStack.
            if (Math.random() > inputProbability) {
                // Sets a timer to despawn the "used up" item.
                setItemDespawnTimer(event.getItemDrop(), 3);
                continue;
            }

            // If the conversion was successful, makes a new ItemStack with a randomized (based on ratio) output item.
            ItemStack newItemStack = new ItemStack(getObjectByProbability(configurationContext.materialMatches.get(thrownItemStack.getType()).entrySet()), 1);

            // Possibly unessessary double-check to make sure the material is not AIR?
            if (newItemStack.getType() == Material.AIR)
                continue;

            // Creates the timer which, when completed, will delete the "input" block and create the "output" block.
            setItemCreationTimer(event.getItemDrop(),
                    new Location(event.getItemDrop().getWorld(), targetBlock.getX() + 0.5, targetBlock.getY() + 1, targetBlock.getZ() + 0.5),
                    newItemStack, 3);
        }
    }

    private void setItemCreationTimer(final Item previousItem, final Location loc, final ItemStack itemStack, final int seconds)
    {
        // Sets a timer to despawn the "used up" item.
        setItemDespawnTimer(previousItem, seconds);

        // Creates an sync task, which when run, creates the new item.
        Bukkit.getScheduler().scheduleSyncDelayedTask(configurationContext.plugin, new Runnable() {
            @Override
            public void run() {
                // Sets the Item and sets its location to the centre of the CAULDRON.
                Item item = previousItem.getWorld().dropItem(loc, itemStack);
                item.setPickupDelay(seconds);

                // Gives the item a slightly randomized vertical velocity.
                Vector zero = new Vector();
                zero.setY(0.3);
                zero.setX(Vector.getRandom().getX() * 0.05);
                zero.setZ(Vector.getRandom().getZ() * 0.05);
                item.setVelocity(zero);
            }
        }, seconds * 20);
    }

    private void setItemDespawnTimer(final Item item, int seconds)
    {
        // Set the item pickup delay to a the requested value + 5s (for safety) so that it can not be picked up in the meantime.
        item.setPickupDelay((seconds + 5) * 20);

        // Creates an sync task, which when run, deletes the item.
        Bukkit.getScheduler().scheduleSyncDelayedTask(configurationContext.plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    item.remove();
                }
                catch (Exception ex) {}
            }
        }, seconds * 20);
    }

    private <K> K getObjectByProbability(Set<Entry<K, Double>> set)
    {
        // Selects a randomized output value based on its probability.
        while (true) {
            double probability = Math.random();

            for (Entry<K, Double> entry : set)
            {
                if (probability < entry.getValue())
                    return entry.getKey();
                else
                    probability -= entry.getValue();
            }
        }
    }
}
