package com.sweatsunited.core.handler;

import com.sweatsunited.bedwars.Bedwars;
import com.sweatsunited.core.BedwarsCore;
import com.sweatsunited.core.game.Game;
import com.sweatsunited.core.game.GameTeam;
import com.sweatsunited.core.model.ProtectedArea;
import com.sweatsunited.core.types.TeamColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.LinkedList;
import java.util.UUID;

public class GameHandler implements Listener {

    private final String constant = "x";
    private final Bedwars plugin;
    private final BedwarsCore bedwarsCore;

    public GameHandler(BedwarsCore instance){
        this.bedwarsCore = instance;
        this.plugin = Bedwars.getInstance();
    }

    @EventHandler
    public void onDeath(EntityDamageEvent e){
        if (e.getEntity() instanceof ArmorStand){
            ((ArmorStand) e.getEntity()).setMaximumNoDamageTicks(Integer.MAX_VALUE);
            ((ArmorStand) e.getEntity()).setNoDamageTicks(Integer.MAX_VALUE);
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent e){
        Block block = e.getBlock();
        Player p = e.getPlayer();
        if (block.getType() == Material.BED_BLOCK){
            block.getDrops().clear();
            System.out.println("Trying to break bed.");
            try {
                Game game = bedwarsCore.getGameHandler().getGames().get(UUID.fromString(block.getWorld().getName()));
                if (game == null) return;
                int npcId1;
                try {
                    npcId1 = block.getMetadata("bedTeam").get(0).asInt();
                } catch (Exception qfi){
                    return;
                }
                for (GameTeam team : game.getTeams()){
                    if (team.getNpcId1() == npcId1){
                        if (team.getUsers().contains(p.getUniqueId())){
                            e.setCancelled(true);
                            p.playSound(p.getLocation(),Sound.ENDERDRAGON_WINGS,1f,1f);
                            return;
                        }
                        team.setDestroyedBed(true);
                        team.getBedLocation().getBlock().removeMetadata("bedTeam",Bedwars.getInstance());
                        team.getBedLocation2().getBlock().removeMetadata("bedTeam",Bedwars.getInstance());
                        Block b1 = team.getBedLocation().getBlock();
                        Block b2 = team.getBedLocation2().getBlock();
                        b1.getDrops().clear();
                        b2.getDrops().clear();
                        boolean found = game.getTeams().stream().anyMatch(s->s.getUsers().contains(p.getUniqueId()));
                        String text = "";
                        if (found){
                            try {
                                TeamColor qf = game.getTeams().stream().filter(s -> s.getUsers().contains(p.getUniqueId())).findFirst().get().getColor();
                                text = "§f§lBED DESTRUCTION > §r" + team.getColor().getBukkitColor() + team.getColor().getName() + " Bed §r§7 was destroyed by " + qf.getBukkitColor() + p.getName();
                            } catch (Exception q){
                                text = "§f§lBED DESTRUCTION > §r"+team.getColor().getBukkitColor() + team.getColor().getName() + " Bed §r§7 was destroyed by " + p.getName();
                            }
                        } else {
                            text = "§f§lBED DESTRUCTION > §r"+team.getColor().getBukkitColor() + team.getColor().getName() + " Bed §r§7 was destroyed by " + p.getName();
                        }
                        for (Player player : block.getWorld().getPlayers()){
                            player.playSound(player.getLocation(),Sound.ENDERDRAGON_DEATH,0.5f,1f);
                            player.sendMessage("");
                            player.sendMessage(text);
                            player.sendMessage("");
                        }
                        //
                        return;
                    }
                }
            } catch (Exception i){
                i.printStackTrace();
                return;
                // UUID exception or array out of bounds from metadata bedTeam
            }
            return;
        }
        if (block.hasMetadata(constant)){
            if (e.isCancelled()){
                e.setCancelled(false);
            }
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent e){
        Block block = e.getBlockPlaced();
        Location location = block.getLocation();
        World world = location.getWorld();
        UUID teamUUID;
        try {
            teamUUID = UUID.fromString(world.getName());
        } catch (Exception p){
            return;
        }
        Game game = bedwarsCore.getGameHandler().getGames().get(teamUUID);
        if (game == null){
            return;
        }
        try {
            LinkedList<ProtectedArea> protectedAreas = game.getProtectedAreas();
            int size = protectedAreas.size();
            for (int i = 0; i < size; i++){
                ProtectedArea pArea = protectedAreas.get(i);
                if (pArea.isInside(location)){
                    e.setCancelled(true);
                    return;
                }
            }
            block.setMetadata(constant, new FixedMetadataValue(plugin, constant));
        } catch (Exception q){

        }
    }

}