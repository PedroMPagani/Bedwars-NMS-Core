package com.sweatsunited.core.command;

import com.sweatsunited.bedwars.Bedwars;
import com.sweatsunited.core.BedwarsCore;
import com.sweatsunited.core.game.Game;
import com.sweatsunited.core.game.GameTeam;
import com.sweatsunited.core.game.enums.GameType;
import com.sweatsunited.core.module.MapHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameSetupCommand extends Command {

    private final Bedwars plugin = Bedwars.getInstance();
    public GameSetupCommand(){
        super("gamesetup");
        this.setDescription("gen command.");
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args){
        if (!(sender instanceof Player)) return false;
        if (!sender.isOp()) return false;
        Player p = (Player) sender;
        switch (args.length){
            case 1:{
                if (args[0].equalsIgnoreCase("testmaps")){
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
                            for (GameTeam team : game.getTeams()){
                                for (UUID user : team.getUsers()){
                                    Player p = Bukkit.getPlayer(user);
                                    p.setMetadata("inGame",new FixedMetadataValue(Bedwars.getInstance(),"tr"));
                                    p.teleport(team.getArenaSpawnLocation());
                                }
                            }
                        }
                    }.runTaskLater(Bedwars.getInstance(),1L);
                }
                break;
            }
            case 2:{
                // /gamesetup duel <NICK>
                if (args[0].equalsIgnoreCase("duel")){
                    Player p2 = Bukkit.getPlayer(args[1]);
                    if (p2 != null){
                        String map = "Lighthouse1v1";
                        Location location = p.getLocation();
                        BedwarsCore bedwarsCore = BedwarsCore.getInstance();
                        ConcurrentHashMap<UUID, Game> games = bedwarsCore.getGameHandler().getGames();
                        MapHandler mapHandler = bedwarsCore.getMapHandler();
                        Game game = new Game(map, GameType.V1, new LinkedList<>());
                        GameTeam gameTeam = new GameTeam(null, null, null);
                        GameTeam gameTeam2 = new GameTeam(null, null, null);
                        gameTeam.getUsers().add(p.getUniqueId());
                        gameTeam2.getUsers().add(p2.getUniqueId());
                        games.put(game.getGameUUID(), game);
                        game.getTeams().add(gameTeam);
                        game.getTeams().add(gameTeam2);
                        mapHandler.genWorld(map, game);
                        new BukkitRunnable(){
                            @Override
                            public void run(){
                                for (GameTeam team : game.getTeams()){
                                    for (UUID user : team.getUsers()){
                                        Player p = Bukkit.getPlayer(user);
                                        p.setMetadata("inGame",new FixedMetadataValue(Bedwars.getInstance(),""));
                                        p.setMetadata("isAlive",new FixedMetadataValue(Bedwars.getInstance(),""));
                                        p.teleport(team.getArenaSpawnLocation());
                                    }
                                }
                            }
                        }.runTaskLater(Bedwars.getInstance(),8L);
                    }
                }
            }
            // /gamesetup <map> <gametype> generator <DIAMOND>
            // /gamesetup <map> <gametype> islandspawn
            // /gamesetup <map> <gametype> islandgenerator
            // /gamesetup <map> <gametype> protectedArea
            // /gamesetup generate <map> <gametype> joina
            // /gamesetup map generate <map>
            // /gamesetup test,a´s
            case 3:{
                if (args[0].equalsIgnoreCase("map")){
                    if (args[1].equalsIgnoreCase("generate")){
                        String map = args[2];
                        new BukkitRunnable(){
                            @Override
                            public void run(){
                                Game game = new Game(map, GameType.V1,new LinkedList<>());
                                BedwarsCore.getInstance().getGameHandler().getGames().put(game.getGameUUID(),game);
                                BedwarsCore.getInstance().getMapHandler().genWorld(map,game);
                                while (Bukkit.getWorld(game.getGameUUID().toString()) == null){

                                }
                                game.load();
                            }
                        }.runTaskAsynchronously(plugin);
                        break;
                    }
                    return true;
                }
                //gamesetup Lighthouse1v1 V1 islandspawn
                //gamesetup Lighthouse1v1 V1 npcshop
                //gamesetup Lighthouse1v1 V1 midlocation
                String map = args[0];
                try {
                    GameType gameType = GameType.valueOf(args[1]);
                    if (args[2].equalsIgnoreCase("islandspawn")){
                        BedwarsCore b = BedwarsCore.getInstance();
                        YamlConfiguration config = b.getGameConfig();
                        List<String> list = config.getStringList("games."+map+".island_locations"+gameType.name());
                        String location = b.getLocationHasher().serialize(p.getLocation());
                        list.add(location);
                        config.set("games."+map+".island_locations"+gameType.name(),list);
                        sender.sendMessage("§aIsland spawn location saved.");
                        config.save(new File(plugin.getDataFolder(),"game.yml"));
                        break;
                    }
                    if (args[2].equalsIgnoreCase("midlocation")){
                        BedwarsCore b = BedwarsCore.getInstance();
                        YamlConfiguration config = b.getGameConfig();
                        String location = b.getLocationHasher().serialize(p.getLocation());
                        config.set("games."+map+".midlocation"+gameType.name(),location);
                        sender.sendMessage("§aMiddle location saved..");
                        config.save(new File(plugin.getDataFolder(),"game.yml"));
                        break;
                    }
                    if (args[2].equalsIgnoreCase("npcshop")){
                        BedwarsCore b = BedwarsCore.getInstance();
                        YamlConfiguration config = b.getGameConfig();
                        List<String> list = config.getStringList("games."+map+".npcshop"+gameType.name());
                        if (list == null) list = new ArrayList<>();
                        String location = b.getLocationHasher().serialize(p.getLocation());
                        list.add(location);
                        config.set("games."+map+".npcshop"+gameType.name(),list);
                        sender.sendMessage("§aIsland ncp shop location saved.");
                        config.save(new File(plugin.getDataFolder(),"game.yml"));
                        break;
                    }
                    //gamesetup Lighthouse1v1 V1 bed
                    if (args[2].equalsIgnoreCase("bed")){
                        BedwarsCore b = BedwarsCore.getInstance();
                        YamlConfiguration config = b.getGameConfig();
                        List<String> list = config.getStringList("games."+map+".bed"+gameType.name());
                        if (list == null) list = new ArrayList<>();
                        String location = b.getLocationHasher().serialize(p.getLocation());
                        list.add(location);
                        config.set("games."+map+".bed"+gameType.name(),list);
                        sender.sendMessage("§aBed loc saved..");
                        config.save(new File(plugin.getDataFolder(),"game.yml"));
                        break;
                    }
                    //gamesetup Lighthouse1v1 V1 islandgenerator
                    if (args[2].equalsIgnoreCase("islandgenerator")){
                        BedwarsCore b = BedwarsCore.getInstance();
                        YamlConfiguration config = b.getGameConfig();
                        List<String> list = config.getStringList("games."+map+".island_generators"+gameType.name());
                        String location = b.getLocationHasher().serialize(p.getLocation());
                        list.add(location);
                        config.set("games."+map+".island_generators"+gameType.name(),list);
                        sender.sendMessage("§aIsland generator location saved.");
                        config.save(new File(plugin.getDataFolder(),"game.yml"));
                        break;
                    }
                } catch (Exception q){
                    p.sendMessage("§cUse one of the types <V1,V2,V3,V4>");
                    return true;
                }
                return true;
            }
            case 4:{
                String map = args[0];
                try {
                    GameType gameType = GameType.valueOf(args[1]);
                    if (args[2].equalsIgnoreCase("generator")){
                        Material material = Material.getMaterial(args[3]);
                        BedwarsCore b = BedwarsCore.getInstance();
                        YamlConfiguration config = b.getGameConfig();
                        List<String> generators = config.getStringList("games."+map+".generators."+gameType.name());
                        String location = b.getLocationHasher().serialize(p.getLocation());
                        generators.add(material.name()+";"+location);
                        config.set("games."+map+".generators."+gameType.name(),generators);
                        sender.sendMessage("§aGenerator location saved.");
                        config.save(new File(plugin.getDataFolder(),"game.yml"));
                    }
                } catch (Exception q){
                    p.sendMessage("§cUse one of the types <V1,V2,V3,V4>");
                    return true;
                }
                return true;
            }
        }
        return false;
    }

}