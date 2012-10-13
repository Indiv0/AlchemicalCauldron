package com.github.Indiv0.AlchemicalCauldron;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class EntityInteractListener implements Listener {
    // Create a method to handle/interact with item throwing events.
    @EventHandler
    public void onEntityInteract(PlayerDropItemEvent event) {
        Block targetBlock = event.getPlayer().getTargetBlock(null, 1);
        
        try {
            if(targetBlock.getType()  != Material.CAULDRON)
                return;
        } catch (NullPointerException ex) {
            return;
        }
        
        Item thrownItem = event.getItemDrop();
        ItemStack newItem = new ItemStack(Material.IRON_INGOT, 1);
        
        thrownItem.setItemStack(newItem);
        thrownItem.teleport(new Location(thrownItem.getWorld(), targetBlock.getX() + 0.5, targetBlock.getY() + 1, targetBlock.getZ() + 0.5));
        
        Vector zero = new Vector();
        zero.setY(0.2);
        zero.setX(zero.getRandom().getX() * 0.1);
        zero.setZ(zero.getRandom().getZ() * 0.1);
        thrownItem.setVelocity(zero);
    }
}
