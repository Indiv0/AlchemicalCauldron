package in.nikitapek.alchemicalcauldron.events;

import com.amshulman.mbapi.MbapiPlugin;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

public final class AlchemicalCauldronListener implements Listener {
    private static final float CAULDRON_HORIZONTAL_OFFSET = 0.5f;
    private static final byte CAULDRON_VERTICAL_OFFSET = 1;
    private static final byte ITEMSTACK_DESPAWN_TIME = 3;

    private static final float ITEM_VERTICAL_VELOCITY = 0.3f;
    private static final float ITEM_HORIZONTAL_VELOCITY_FRACTION = 0.05f;

    private static final byte TICKS_PER_SECOND = 20;

    private static final DecimalFormat decimalFormat = new DecimalFormat();

    private final MbapiPlugin plugin;

    private final Map<Material, Double> inputMaterials;
    private final Map<Material, HashMap<Material, Double>> materialMatches;

    public AlchemicalCauldronListener(final AlchemicalCauldronConfigurationContext configurationContext) {
        this.plugin = configurationContext.plugin;

        this.inputMaterials = configurationContext.inputMaterials;
        this.materialMatches = configurationContext.materialMatches;

        decimalFormat.setMaximumFractionDigits(2);
    }

    @EventHandler
    public void onItemDrop(final PlayerDropItemEvent event) {
        // Get the Item being thrown, as well as the relevant ItemStack.
        final Item item = event.getItemDrop();
        final ItemStack itemStack = item.getItemStack();
        final Material material = itemStack.getType();

        // If the player does not have permissions to use AlchemicalCauldron, cancels the event.
        if (!event.getPlayer().hasPermission("alchemicalcauldron.use")) {
            return;
        }

        // Checks to make sure the ItemStack contains a valid input material.
        if (!inputMaterials.containsKey(material)) {
            return;
        }

        new BukkitRunnable() {
            // Because the Y value and location only get updated at the end of the run() method, setting firstY to location.getY() right away would result in the task only getting called once.
            // Therefore, firstY and previousLocation are set to Integer.MAX_VALUE to ensure that the task doesn't cancel itself on the first run.
            private double firstY = Integer.MAX_VALUE;
            private Location previousLocation = new Location(item.getLocation().getWorld(), Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

            @Override
            public void run() {
                // If the item was deleted or picked up or otherwise removed, there is no need to perform further checks on it.
                if (item == null) {
                    this.cancel();
                }

                final Location location = item.getLocation();

                // If the block has just bounced, then we check the block type to see if it is a cauldron.
                // If it is a cauldron, then the AlchemicalCauldron logic is run and the no longer required task is cancelled.
                if (previousLocation.getY() < location.getY() && firstY > previousLocation.getY() && Material.CAULDRON.equals(previousLocation.getBlock().getType())) {
                    // Gets the probability for that input item.
                    final double inputProbability = inputMaterials.get(material);

                    for (int i = 1; i <= itemStack.getAmount(); i++) {
                        // If the conversion fails, delete the ItemStack.
                        if (Math.random() > inputProbability) {
                            // Sets a timer to despawn the "used up" item.
                            setItemDespawnTimer(event.getItemDrop());
                            continue;
                        }

                        // If the conversion was successful, makes a new ItemStack with a randomized (based on ratio) output item.
                        final ItemStack newItemStack = new ItemStack(getObjectByProbability(materialMatches.get(material).entrySet()), 1);

                        // Possibly unnecessary double-check to make sure the material is not AIR?
                        if (newItemStack.getType() == Material.AIR) {
                            continue;
                        }

                        // Creates the timer which, when completed, will delete the "input" block and create the "output" block.
                        setItemCreationTimer(event.getItemDrop(),
                                new Location(event.getItemDrop().getWorld(), previousLocation.getX() + CAULDRON_HORIZONTAL_OFFSET, previousLocation.getY() + CAULDRON_VERTICAL_OFFSET, previousLocation.getZ() + CAULDRON_HORIZONTAL_OFFSET),
                                newItemStack);
                    }
                    this.cancel();
                } else if (location.getY() == previousLocation.getY()) {
                    // If the item is no longer falling, then we cancel the task.
                    this.cancel();
                }

                firstY = previousLocation.getY();
                previousLocation = location;
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    private void setItemCreationTimer(final Item previousItem, final Location loc, final ItemStack itemStack) {
        // Sets a timer to despawn the "used up" item.
        setItemDespawnTimer(previousItem);

        // Creates an sync task, which when run, creates the new item.
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                // Sets the Item and sets its location to the centre of the CAULDRON.
                final Item item = previousItem.getWorld().dropItem(loc, itemStack);
                item.setPickupDelay(ITEMSTACK_DESPAWN_TIME);

                // Gives the item a slightly randomized vertical velocity.
                final Vector zero = new Vector();
                zero.setY(ITEM_VERTICAL_VELOCITY);
                zero.setX(Vector.getRandom().getX() * ITEM_HORIZONTAL_VELOCITY_FRACTION);
                zero.setZ(Vector.getRandom().getZ() * ITEM_HORIZONTAL_VELOCITY_FRACTION);
                item.setVelocity(zero);
            }
        }, ITEMSTACK_DESPAWN_TIME * TICKS_PER_SECOND);
    }

    private void setItemDespawnTimer(final Item item) {
        // Set the item pickup delay to a the requested value so that it can not be picked up in the meantime.
        item.setPickupDelay((ITEMSTACK_DESPAWN_TIME) * TICKS_PER_SECOND);

        // Creates an sync task, which when run, deletes the item.
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                item.remove();
            }
        }, ITEMSTACK_DESPAWN_TIME * TICKS_PER_SECOND);
    }

    private <K> K getObjectByProbability(final Set<Entry<K, Double>> set) {
        // Selects a randomized output value based on its probability.
        while (true) {
            double probability = Math.random();

            for (final Entry<K, Double> entry : set) {
                if (probability < entry.getValue()) {
                    return entry.getKey();
                } else {
                    probability -= entry.getValue();
                }
            }
        }
    }
}
