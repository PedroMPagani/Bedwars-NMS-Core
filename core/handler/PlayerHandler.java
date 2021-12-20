package com.sweatsunited.core.handler;

import com.sweatsunited.bedwars.Bedwars;
import com.sweatsunited.core.BedwarsCore;
import com.sweatsunited.core.game.Game;
import com.sweatsunited.core.game.GameTeam;
import com.sweatsunited.core.game.enums.GameType;
import com.sweatsunited.core.model.*;
import com.sweatsunited.core.module.MapHandler;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.paperspigot.Title;

import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerHandler implements Listener {

    private final Bedwars plugin;
    private final BedwarsCore bedwarsCore;

    public PlayerHandler(BedwarsCore instance){
        this.bedwarsCore = instance;
        this.plugin = Bedwars.getInstance();
    }

    @EventHandler
    public void onWorld(PlayerChangedWorldEvent e){
        String name = e.getPlayer().getLocation().getWorld().getName();
        Player p = e.getPlayer();
        try {
            UUID x = UUID.fromString(name);
            Game game = bedwarsCore.getGameHandler().getGames().get(x);
            for (Generator generator : game.getGenerators()){
                generator.show(p); // show entities of generator to player.
            }
            World world = p.getWorld();
            game.showPlayer(p);
            for (GameTeam gameTeam : game.getTeams()) {
                if (gameTeam.getUsers().contains(p.getUniqueId())){
                    return;
                }
            }
            // runs only if the player is not on the game
            PacketPlayOutEntityDestroy q = new PacketPlayOutEntityDestroy(p.getEntityId());
            for (GameTeam team : game.getTeams()){
                for (UUID uuid : team.getUsers()){
                    Player pg = Bukkit.getPlayer(uuid);
                    ((CraftPlayer)pg).getHandle().playerConnection.sendPacket(q);
                }
            }
        } catch (Exception i){
            // world uuid.
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent e){
        try {
            Player p = e.getPlayer();
            System.out.println(e.getPlayer().getBedSpawnLocation());
            if (e.getPlayer().getBedSpawnLocation() != null){
                e.setRespawnLocation(e.getPlayer().getBedSpawnLocation());
                p.setVelocity(p.getVelocity().add(new org.bukkit.util.Vector(0,0.25,0)));
                p.setAllowFlight(true);
                p.setFlying(true);
                p.setVelocity(p.getVelocity().add(new org.bukkit.util.Vector(0,0.15,0)));
                p.setAllowFlight(true);
                p.setFlying(true);
            }
            UUID uuid = UUID.fromString(e.getPlayer().getBedSpawnLocation().getWorld().getName());
            Game game = bedwarsCore.getGameHandler().getGames().get(uuid);
            if (game == null) return;
            new BukkitRunnable(){
                @Override
                public void run(){
                    for (Generator generator : game.getGenerators()){
                        generator.show(p); // show entities of generator to player.
                    }
                }
            }.runTaskLaterAsynchronously(Bedwars.getInstance(),2L);
        } catch (Exception QF){
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        UUID uuid;
        Player p = e.getEntity();
        p.getInventory().clear();
        try {
            uuid = UUID.fromString(p.getWorld().getName());
        } catch (Exception i){
            return;
        }
        Game game = bedwarsCore.getGameHandler().getGames().get(uuid);
        if (game == null) return;
        game.hidePlayer(p);
        UUID playerUUID = p.getUniqueId();
        for (GameTeam team : game.getTeams()){
            if (team.getUsers().contains(playerUUID)){
                if (team.isDestroyedBed()){
                    try {
                        p.removeMetadata("isAlive", Bedwars.getInstance());
                    } catch (Exception wfj){

                    }
                    /**
                     * Handle win here.
                     */
                    Player killer = e.getEntity().getKiller();
                    boolean foundAlive = false;
                    if (killer == null){
                        for (GameTeam gameTeam : game.getTeams()){
                            if (gameTeam.getUsers().contains(p.getUniqueId())){
                                for (UUID teamUser : gameTeam.getUsers()){
                                    Player qf = Bukkit.getPlayer(teamUser);
                                    if (qf != null && qf.hasMetadata("isAlive")){
                                        foundAlive = true;
                                        break;
                                    }
                                }
                                if (!foundAlive){
                                    // team eliminated.
                                    Bukkit.broadcastMessage(gameTeam.getColor().getBukkitColor() + "" +gameTeam.getColor().getName() + " team was eliminated.");
                                    GameTeam teamAlive = null;
                                    boolean foundAnotherTeam = false;
                                    for (GameTeam team1 : game.getTeams()){
                                        // if already found at least two teams alive.
                                        if (foundAnotherTeam){
                                            System.out.println("foundAnotherTeam: "+foundAnotherTeam);
                                            break;
                                        }
                                        if (teamAlive != null){
                                            System.out.println("TeamAlive != null");
                                            for (UUID fw : team1.getUsers()){
                                                Player qf = Bukkit.getPlayer(fw);
                                                if (qf != null && qf.hasMetadata("isAlive")){
                                                    foundAnotherTeam = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (team1 != gameTeam){
                                            for (UUID fw : team1.getUsers()){
                                                Player qf = Bukkit.getPlayer(fw);
                                                if (qf != null && qf.hasMetadata("isAlive")){
                                                    teamAlive = team1;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (!foundAnotherTeam){
                                        // game ended here.
                                        Bukkit.broadcastMessage("§cGame ended.");
                                    }
                                }
                            }
                        }
                    } else {
                        GameTeam killerTeam = null;
                        GameTeam eliminatedTeam = null;
                        GameTeam anotherAlive = null;
                        boolean gameEnd = true;
                        for (GameTeam gameTeam : game.getTeams()){
                            if (gameTeam.getUsers().contains(p.getUniqueId())) eliminatedTeam = gameTeam;
                            if (anotherAlive != null){
                                for (UUID user : gameTeam.getUsers()){
                                    Player qf = Bukkit.getPlayer(user);
                                    if (qf.hasMetadata("isAlive")){
                                        gameEnd = false;
                                        break;
                                    }
                                }
                            }
                            if (gameTeam.getUsers().contains(killer.getUniqueId())){
                                killerTeam = gameTeam;
                                for (UUID user : gameTeam.getUsers()){
                                    Player qf = Bukkit.getPlayer(user);
                                    if (qf.hasMetadata("isAlive")){
                                        foundAlive = true;
                                    }
                                }
                            } else {
                                for (UUID user : gameTeam.getUsers()){
                                    Player qf = Bukkit.getPlayer(user);
                                    if (qf.hasMetadata("isAlive")){
                                        anotherAlive = gameTeam;
                                    }
                                }
                            }
                        }
                        if (!foundAlive){
                            // team eliminated.
                            Bukkit.broadcastMessage(eliminatedTeam.getColor().getBukkitColor() + "" +eliminatedTeam.getColor().getName() + " team was eliminated.");
                        }
                        if (gameEnd){
                            Bukkit.broadcastMessage("§cGame ended, winner: " + killerTeam.getColor().getBukkitColor() + " " + killerTeam.getColor().getName());
                        }
                    }
                }
                CraftPlayer player = (CraftPlayer)p;
                EntityPlayer entityPlayer = player.getHandle();
                if (game.getMiddle() == null){
                    Location x = team.getArenaSpawnLocation().add(0,25,0);
                    entityPlayer.setRespawnPosition(new BlockPosition(x.getBlockX(), x.getBlockY(), x.getBlockZ()), true);
                } else {
                    Location x = game.getMiddle();
                    entityPlayer.setRespawnPosition(new BlockPosition(x.getBlockX(), x.getBlockY(), x.getBlockZ()), true);
                }
                entityPlayer.spawnWorld = game.getGameUUID().toString();
                p.spigot().respawn();
                System.out.println(p.getBedSpawnLocation());
                if (team.isDestroyedBed()){
                    p.setVelocity(p.getVelocity().add(new org.bukkit.util.Vector(0,0.25,0)));
                    p.setAllowFlight(true);
                    p.setFlying(true);
                    p.setVelocity(p.getVelocity().add(new org.bukkit.util.Vector(0,0.15,0)));
                    p.setAllowFlight(true);
                    p.setFlying(true);
                    p.teleport(team.getArenaSpawnLocation().add(0,25,0));
                    p.setMetadata("Respawning",new FixedMetadataValue(Bedwars.getInstance(),""));
                    return;
                }
                p.setMetadata("Respawning",new FixedMetadataValue(Bedwars.getInstance(),""));
                new BukkitRunnable(){
                    int timesLeft = 4;
                    @Override
                    public void run(){
                        if (!p.isOnline()){
                            this.cancel();
                            return;
                        }
                        if (timesLeft <= 0){
                            new BukkitRunnable(){
                                @Override
                                public void run(){
                                    p.removeMetadata("Respawning",Bedwars.getInstance());
                                    p.teleport(team.getArenaSpawnLocation());
                                    game.showPlayer(p);
                                    p.setAllowFlight(false);
                                    p.setFlying(false);
                                    p.setMetadata("isAlive",new FixedMetadataValue(Bedwars.getInstance(),""));
                                }
                            }.runTask(Bedwars.getInstance());
                            this.cancel();
                            return;
                        }
                        try {
                            p.sendTitle(new Title("§bRespawning in...","§a"+timesLeft,20,40,20));
                        } catch (Exception qf){
                            qf.printStackTrace();
                            this.cancel();
                        }
                        timesLeft--;
                    }
                }.runTaskTimerAsynchronously(Bedwars.getInstance(),0,20L);
                break;
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        p.setMetadata("inGame",new FixedMetadataValue(Bedwars.getInstance(),"tr"));
        p.setMetadata("isAlive",new FixedMetadataValue(Bedwars.getInstance(),"tr"));
        String map = "Lighthouse1v1";
        BedwarsCore bedwarsCore = BedwarsCore.getInstance();
        ConcurrentHashMap<UUID, Game> games = bedwarsCore.getGameHandler().getGames();
        MapHandler mapHandler = bedwarsCore.getMapHandler();
        Game game = new Game(map, GameType.V1, new LinkedList<>());
        GameTeam gameTeam = new GameTeam(null,null,null);
        gameTeam.getUsers().add(p.getUniqueId());
        GameTeam gameTeam2 = new GameTeam(null,null,null);
        games.put(game.getGameUUID(), game);
        game.getTeams().add(gameTeam);
        game.getTeams().add(gameTeam2);
        mapHandler.genWorld(map, game);
        new BukkitRunnable(){
            @Override
            public void run(){
                p.teleport(gameTeam.getArenaSpawnLocation());
            }
        }.runTaskLater(Bedwars.getInstance(),2L);
    }


}