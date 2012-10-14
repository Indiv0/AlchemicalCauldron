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
        Block targetBlock = event.getPlayer().getTargetBlock(null, 1);
        
        try {
            if(targetBlock.getType()  != Material.CAULDRON)
                return;
        } catch (NullPointerException ex) {
            return;
        }
        
        Item thrownItem = event.getItemDrop();
        ItemStack thrownItemStack = thrownItem.getItemStack();
        
        if(!plugin.getInputMaterials().containsKey(thrownItemStack.getType()))
            return;

        double inputProbability = plugin.getInputMaterials().get(thrownItemStack.getType());
        
        if(Math.random() > inputProbability)
            return;
        
        ItemStack newItem = new ItemStack(getObjectByProbability(plugin.getOutputMaterials().entrySet()), 1);
        
        if(newItem.getType() == Material.AIR)
            return;
        
        thrownItem.setItemStack(newItem);
        thrownItem.teleport(new Location(thrownItem.getWorld(), targetBlock.getX() + 0.5, targetBlock.getY() + 1, targetBlock.getZ() + 0.5));
        
        Vector zero = new Vector();
        zero.setY(0.2);
        zero.setX(Vector.getRandom().getX() * 0.1);
        zero.setZ(Vector.getRandom().getZ() * 0.1);
        thrownItem.setVelocity(zero);
    }
    
    private <K> K getObjectByProbability(Set<Entry<K, Double>> set)
    {
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
