package com.sweatsunited.core.handler;

import com.sweatsunited.core.BedwarsCore;
import com.sweatsunited.core.game.Game;
import com.sweatsunited.core.game.GameTeam;
import com.sweatsunited.core.model.Shop;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class NPCHandler implements Listener {

    private final BedwarsCore bedwarsCore;
    public NPCHandler(BedwarsCore instance){
        this.bedwarsCore = instance;
    }

    @EventHandler
    void onCLick(NPCRightClickEvent e){
        try {
            UUID uuid = UUID.fromString(e.getNPC().getStoredLocation().getWorld().getName());
            Game uuid1 = bedwarsCore.getGameHandler().getGames().get(uuid);
            for (GameTeam team : uuid1.getTeams()){
                if (team.getNpcId1() == e.getNPC().getId()){
                    Shop shop = new Shop();
                    shop.openMenu(e.getClicker());
                    System.out.println("§c SHOP NPC");
                    return;
                }
            }
        } catch (Exception q){

        }

    }
}
