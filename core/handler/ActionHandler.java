package com.sweatsunited.core.handler;

import com.sweatsunited.core.model.Shop;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ActionHandler implements Listener {


    @EventHandler
    void handle(PlayerDeathEvent e){
        System.out.println(e.getEntity().getName());
    }

    @EventHandler
    void handle2(InventoryClickEvent e){
        if (e.getView() == null) return;
        Inventory inventory = e.getView().getTopInventory();
        if (inventory == null) return;
        InventoryHolder inventoryHolder = inventory.getHolder();
        if (inventoryHolder == null )return;
        if (inventoryHolder instanceof Shop){
            e.setCancelled(true);
        }
    }

    @EventHandler
    void handle3(PlayerJoinEvent e){

    }

    @EventHandler
    void handle4(){

    }

}