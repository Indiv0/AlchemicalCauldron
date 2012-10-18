package com.github.Indiv0.AlchemicalCauldron;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
    public static AlchemicalCauldron plugin;

    public EntityInteractListener(AlchemicalCauldron instance) {
        plugin = instance;
    }
    
    // Create a method to handle/interact with item throwing events.
    @EventHandler
    public void onEntityInteract(PlayerDropItemEvent event) {
        // Gets the block targetted by the player.
        Block targetBlock = event.getPlayer().getTargetBlock(null, 1);
        
        // Checks to make sure the block is a CAULDRON.
        try {
            if(targetBlock.getType()  != Material.CAULDRON)
                return;
        } catch (NullPointerException ex) {
            return;
        }
        
        // Gets the thrown item and converts it into a usable ItemStack.
        Item thrownItem = event.getItemDrop();
        ItemStack thrownItemStack = thrownItem.getItemStack();
        
        // Checks to make sure the ItemStack contains a valid input material.
        if(!plugin.getInputMaterials().containsKey(thrownItemStack.getType()))
            return;

        // Gets the probability for that input item.
        double inputProbability = plugin.getInputMaterials().get(thrownItemStack.getType());
        
        // If the conversion fails, delete the ItemStack.
        if(Math.random() > inputProbability) {
            // Sets a timer to despawn the "used up" item.
            setItemDespawnTimer(thrownItem, 5);
            return;
        }
        
        // If the conversion was successful, makes a new ItemStack with a randomized (based on ratio) output item.
        ItemStack newItemStack = new ItemStack(getObjectByProbability(plugin.getOutputMaterials().entrySet()), 1);
        
        // Possibly unessessary double-check to make sure the material is not AIR?
        if(newItemStack.getType() == Material.AIR)
            return;
        
        // Sets the Item and sets its location to the centre of the CAULDRON.
        Item item = thrownItem.getWorld().dropItem(new Location(thrownItem.getWorld(), targetBlock.getX() + 0.5, targetBlock.getY() + 1, targetBlock.getZ() + 0.5), newItemStack);
        item.setPickupDelay(20);
        
        // Sets a timer to despawn the "used up" item.
        setItemDespawnTimer(thrownItem, 1);
        
        // Gives the item a slightly randomized vertical velocity.
        Vector zero = new Vector();
        zero.setY(0.3);
        zero.setX(Vector.getRandom().getX() * 0.05);
        zero.setZ(Vector.getRandom().getZ() * 0.05);
        item.setVelocity(zero);
    }
    
    private void setItemDespawnTimer(final Item item, int seconds)
    {
        // Set the item pickup delay to a the requested value + 5s (for safety) so
        // that it can not be picked up in the meantime.
        item.setPickupDelay((seconds + 5) * 20);
        
        // Creates an Async task, which when run, deletes the item.
        plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
            public void run() {
                try { item.remove(); } catch (Exception ex) { }
            }
        }, seconds * 20);
    }
    
    private <K> K getObjectByProbability(Set<Entry<K, Double>> set)
    {
        // Selects a randomized output value based on its probability.
        while(true) {
            double probability = Math.random();
            
            for(Map.Entry<K, Double> entry : set)
            {
                if(probability < entry.getValue()) return entry.getKey();
                else probability -= entry.getValue();
            }
        }
    }
}
