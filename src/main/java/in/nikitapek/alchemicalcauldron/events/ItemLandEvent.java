package in.nikitapek.alchemicalcauldron.events;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class ItemLandEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /**
     * The Item that landed.
     */
    private final Item item;
    /**
     * The Location that the Item landed at.
     */
    private final Location location;
    /**
     * The Player who dropped the Item (if the item was dropped by a Player).
     */
    private final Player player;

    /**
     *
     * @param item The Item that landed.
     * @param player The Player who threw the Item.
     */
    public ItemLandEvent(final Item item, final Player player) {
        this.item = item;
        this.location = item.getLocation();
        this.player = player;
    }

    /**
     *
     * @return the Item that landed.
     */
    public Item getItem() {
        return item;
    }

    /**
     *
     * @return the Location that the Item landed at.
     */
    public Location getLocation() {
        return location;
    }

    /**
     *
     * @return the Player who threw the Item.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     *
     * @return the Block that the Item landed on.
     */
    public Block getBlock() {
        return location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY() - 1, location.getBlockZ());
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
