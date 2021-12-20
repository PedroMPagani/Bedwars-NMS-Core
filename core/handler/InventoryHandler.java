package com.sweatsunited.core.handler;

import com.sweatsunited.core.model.Shop;
import com.sweatsunited.core.types.Category;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryHandler implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory inv = e.getView().getTopInventory();
        if (inv == null) return;
        if (inv.getHolder() == null) return;
        if (!(inv.getHolder() instanceof Shop)) return;
        Shop shop = (Shop) inv.getHolder();
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        switch (e.getRawSlot()){
            case 0:{
                if (shop.getCategory() != Category.PREFERED) {
                    shop.setCategory(Category.PREFERED);
                    shop.openMenu(p);
                    return;
                }
            }
            case 1:{
                if (shop.getCategory() != Category.BLOCKS) {
                    shop.setCategory(Category.BLOCKS);
                    shop.openMenu(p);
                    return;
                }
                break;
            }
            case 2:{
                if (shop.getCategory() != Category.SWORDS) {
                    shop.setCategory(Category.SWORDS);
                    shop.openMenu(p);
                    return;
                }
                break;
            }
            case 3:{
                if (shop.getCategory() != Category.ARMOR) {
                    shop.setCategory(Category.ARMOR);
                    shop.openMenu(p);
                    return;
                }
                break;
            }
            case 4:{
                if (shop.getCategory() != Category.TOOLS) {
                    shop.setCategory(Category.TOOLS);
                    shop.openMenu(p);
                    return;
                }
                break;
            }
            case 5:{
                if (shop.getCategory() != Category.BOW) {
                    shop.setCategory(Category.BOW);
                    shop.openMenu(p);
                    return;
                }
            }
            case 6:{
                if (shop.getCategory() != Category.POTIONS) {
                    shop.setCategory(Category.POTIONS);
                    shop.openMenu(p);
                    return;
                }
            }
            case 7:{
                if (shop.getCategory() != Category.UTILITY) {
                    shop.setCategory(Category.UTILITY);
                    shop.openMenu(p);
                    return;
                }
            }
        }

    }
}
