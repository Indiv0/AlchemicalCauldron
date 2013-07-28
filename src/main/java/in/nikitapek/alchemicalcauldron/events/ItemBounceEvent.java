package in.nikitapek.alchemicalcauldron.events;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class ItemBounceEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /**
     * The Item that bounced.
     */
    private final Item item;
    /**
     * The Location that the nadir of the bounce occured at.
     */
    private final Location location;
    /**
     * The Player who dropped the Item (if the Item was dropped by a Player).
     */
    private final Player player;

    /**
     *
     * @param item the Item that bounced.
     * @param location the Location that the nadir of the bounce occured at.
     * @param player the Player who threw the Item.
     */
    public ItemBounceEvent(final Item item, final Location location, final Player player) {
        this.item = item;
        this.location = location;
        this.player = player;
    }

    /**
     *
     * @return the Item that bounced.
     */
    public Item getItem() {
        return item;
    }

    /**
     *
     * @return the Location that the nadir of the bounce occured at.
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

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
