package com.sweatsunited.core.game;

import com.sweatsunited.bedwars.Bedwars;
import com.sweatsunited.core.BedwarsCore;
import com.sweatsunited.core.game.enums.GameStatus;
import com.sweatsunited.core.game.enums.GameType;
import com.sweatsunited.core.game.enums.GeneratorSource;
import com.sweatsunited.core.model.*;
import com.sweatsunited.core.types.GeneratorType;
import com.sweatsunited.core.types.TeamColor;
import com.sweatsunited.core.types.UpdatableStand;
import com.sweatsunited.core.util.LocationHasher;
import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.HologramTrait;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Getter @Setter
public class Game {

    private final UUID gameUUID;
    private final List<GameTeam> teams;
    private final String arenaIdentifier;
    private final LinkedList<Generator> generators;
    private final GameType gameType;
    private final LinkedList<ProtectedArea> protectedAreas;
    private Location min,max,middle;
    private GameStatus gameStatus = GameStatus.WAITING;
    private GameGenUpgrade generatorUpgrades;
    private int waitTime = 0;

    public Game(String id, GameType g, LinkedList<ProtectedArea> protectedAreas){
      this.gameUUID = UUID.randomUUID();
      this.arenaIdentifier = id;
      this.protectedAreas = protectedAreas;
      this.teams = new LinkedList<>();
      this.generators = new LinkedList<>();
      this.gameType = g;
      this.generatorUpgrades = new GameGenUpgrade(GeneratorType.DIAMOND);
    }

    public void rotateArmor(){
        try {
            for (Generator generator : generators){
                GeneratorStands g = generator.getHead();
                if (g == null) continue;
                ArmorStand armorStand = (ArmorStand) g.getLivingEntity().getBukkitEntity();
                armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0.17, 0));
                Location q = armorStand.getLocation();
                // check if distance is
                if (waitTime == 0){
                    if (g.getUpdatableStand() == UpdatableStand.Y){
                        q.setY(q.getY() - 0.05);
                    } else {
                        q.setY(q.getY() + 0.05);
                    }
                }
                if (q.getY()-generator.getLocation().getY() > 1.1){
                    g.setUpdatableStand(UpdatableStand.Y);
                    waitTime++;
                    if (waitTime == 10){
                        waitTime = 0;
                    }
                }
                if (q.getY()-generator.getLocation().getY() < 0.4){
                    g.setUpdatableStand(UpdatableStand.N);
                    waitTime++;
                    if (waitTime == 10){
                        waitTime = 0;
                    }
                }
                q.setYaw(q.getYaw() + 18);
                g.getLivingEntity().setLocation(q.getX(),q.getY(),q.getZ(),q.getYaw(),0);
                PacketPlayOutEntityTeleport pac = new PacketPlayOutEntityTeleport(g.getLivingEntity());
                World world = Bukkit.getWorld(gameUUID.toString());
                for (Player player : world.getPlayers()){
                    ((CraftPlayer)player).getHandle().playerConnection.sendPacket(pac);
                }
            }
        } catch (Exception qi){
            qi.printStackTrace();
        }
    }

    public void load(){
        TeamColor[] colors = TeamColor.values();
        int[] collected = new int[teams.size()];

        Random random = ThreadLocalRandom.current();
        for (int i = 0; i < teams.size(); i++){
            collected[i] = random.nextInt(colors.length-1);
        }

        for (GameTeam team : teams){
            for (int i : collected){
                boolean fq = false;
                for (GameTeam gameTeam : teams) {
                    if (gameTeam.getColor() != null && gameTeam.getColor() == colors[i]){
                        fq = true;
                        continue;
                    }
                }
                if (fq) continue;
                if (team.getColor() == null){
                    team.setColor(colors[i]);
                    break; // breaks i loop.
                }
            }
        }

        BedwarsCore bedwarsCore = BedwarsCore.getInstance();
        LocationHasher locationHasher = bedwarsCore.getLocationHasher();
        YamlConfiguration config = BedwarsCore.getInstance().getGameConfig();

        String qfm = config.getString("games."+arenaIdentifier+".midlocation"+gameType.name());
        if (qfm != null){
            this.middle = locationHasher.back(gameUUID.toString(),qfm);
        }

        List<String> generators = config.getStringList("games."+arenaIdentifier+".generators."+gameType.name());
        for (String generator : generators){
            String[] data = generator.split(";");
            String material = data[0];
            Material m = Material.getMaterial(material);
            if (m == null) continue;
            String location = generator.replace(material+";","");
            Location loc = locationHasher.back(gameUUID.toString(),location);
            if (loc == null){
                continue;
            }
            Generator generator1 = new Generator(loc,UUID.randomUUID(), GeneratorType.valueOf(m.name()));
            generator1.setGeneratorSource(GeneratorSource.PUBLIC);
            generator1.init();
            this.generators.add(generator1);
        }

        List<String> islandLocations = config.getStringList("games."+arenaIdentifier+".island_locations"+gameType.name());
        for (String s : islandLocations){
            Location loc = locationHasher.back(gameUUID.toString(),s);
            if (loc == null) continue;
            for (GameTeam team : teams){
                if (team.getArenaSpawnLocation() == null) {
                    team.setArenaSpawnLocation(loc);
                    break;
                }
            }
        }

        List<String> npcshopLocations = config.getStringList("games."+arenaIdentifier+".npcshop"+gameType.name());
        for (String s : npcshopLocations){
            Location loc = locationHasher.back(gameUUID.toString(),s);
            if (loc == null) continue;
            for (GameTeam team : teams){
                if(team.getShopLocation() == null){
                    team.setShopLocation(loc);
                }
            }
        }

        List<String> genLocation = config.getStringList("games."+arenaIdentifier+".island_generators"+gameType.name());
        for (String s : genLocation){
            Location loc = locationHasher.back(gameUUID.toString(),s);
            if (loc == null) continue;
            Generator generator1 = new Generator(loc,UUID.randomUUID(), GeneratorType.valueOf(Material.DIAMOND.name()));
            generator1.setGeneratorSource(GeneratorSource.PRIVATE);
            for (GameTeam team : teams){
                if (team.getArenaSpawnLocation().distance(loc) <= 10){// distance between arena spawn and distance is less than 10 blocks, this is the team generator.
                    team.setGenerator(generator1);
                    break;
                }
            }
            this.generators.add(generator1);
        }

        List<String> bedLocations = config.getStringList("games."+arenaIdentifier+".bed"+gameType.name());
        for (String s : bedLocations){
            Location loc = locationHasher.back(gameUUID.toString(),s);
            if (loc == null) continue;
            for (GameTeam team : teams){
                if(team.getBedLocation() == null){
                    team.setBedLocation(loc);
                    break;
                }
            }
        }
        new BukkitRunnable(){
            @Override
            public void run(){
                for (GameTeam team : getTeams()){
                    Block block = team.getBedLocation().getBlock();
                    if (block.getType() == Material.BED_BLOCK){
                        // the first player joins the game, per each team.
                        block.setMetadata("bedTeam", new FixedMetadataValue(Bedwars.getInstance(), team.getNpcId1()));
                        for (int x1 = -2; x1 < 2; x1++){
                            for (int y = -2; y < 2; y++){
                                for (int z = -2; z < 2; z++){
                                    Block b = block.getRelative(x1, y, z);
                                    if (b.getType() == Material.BED_BLOCK){
                                        team.setBedLocation2(b.getLocation());
                                        b.setMetadata("bedTeam", new FixedMetadataValue(Bedwars.getInstance(), team.getNpcId1()));
                                    }
                                }
                            }
                        }
                    } else {
                        block = team.getBedLocation().getBlock();
                        for (int x1 = -2; x1 < 2; x1++){
                            for (int y = -2; y < 2; y++){
                                for (int z = -2; z < 2; z++){
                                    Block b = block.getRelative(x1, y, z);
                                    if (b.getType() == Material.BED_BLOCK){
                                        if (team.getBedLocation().getBlock().getType() != Material.BED_BLOCK){
                                            team.setBedLocation(b.getLocation());
                                        } else {
                                            team.setBedLocation2(b.getLocation());
                                        }
                                        b.setMetadata("bedTeam", new FixedMetadataValue(Bedwars.getInstance(), team.getNpcId1()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTask(Bedwars.getInstance());
        spawnNPC();
    }

    public void spawnNPC(){
        for (GameTeam team : teams){
            if (team.getShopLocation() != null){
                NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER,UUID.randomUUID(),team.getNpcId1(),"§e§lRIGHT CLICK");
                try {
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            npc.spawn(team.getShopLocation());
                            if (npc.getTrait(HologramTrait.class) == null){
                                npc.addTrait(HologramTrait.class);
                            }
                            HologramTrait hologramTrait = npc.getTrait(HologramTrait.class);
                            hologramTrait.addLine("§bITEM SHOP");
                            for (ArmorStand entity : hologramTrait.getHologramEntities()){
                                entity.setSmall(false);
                                entity.setMaximumNoDamageTicks(Integer.MAX_VALUE);
                                entity.setNoDamageTicks(Integer.MAX_VALUE);
                            }
                        }
                    }.runTask(Bedwars.getInstance());
                } catch (Exception q){
                    q.printStackTrace();
                }
            }
        }
    }

    /**
     * The player is gonna be shown for all users, whicih means hes joining or respawning, while he respawn we
     * should hide those who have viewstatus as false.
     * @param p Player whose gonna be shown
     */
    public void showPlayer(Player p){
        Bukkit.getScoreboardManager().getNewScoreboard();
        World world = Bukkit.getWorld(gameUUID.toString());
        p.removeMetadata("ViewStatus",Bedwars.getInstance());
        p.setMetadata("ViewStatus",new FixedMetadataValue(Bedwars.getInstance(),true));
        //PacketPlayOutNamedEntitySpawn PCS = new PacketPlayOutNamedEntitySpawn(((CraftPlayer)p).getHandle());
        for (Player worldPlayer : world.getPlayers()){
            if (worldPlayer == p) continue;
            CraftPlayer craftPlayer = (CraftPlayer) worldPlayer;
            if (craftPlayer.hasMetadata("ViewStatus")){
                if (!craftPlayer.getMetadata("ViewStatus").get(0).asBoolean()){
                    PacketPlayOutEntityDestroy PQ = new PacketPlayOutEntityDestroy(craftPlayer.getEntityId());
                    CraftPlayer q = (CraftPlayer) p;
                    q.getHandle().playerConnection.sendPacket(PQ);
                }
            }
            PlayerConnection playerConnection = craftPlayer.getHandle().playerConnection;
            craftPlayer.showPlayer(p);
            //playerConnection.sendPacket(PCS);
        }
    }

    public void hidePlayer(Player p){
        World world = Bukkit.getWorld(gameUUID.toString());
        //PacketPlayOutEntityDestroy PCS = new PacketPlayOutEntityDestroy(p.getEntityId());
        p.removeMetadata("ViewStatus",Bedwars.getInstance());
        p.setMetadata("ViewStatus",new FixedMetadataValue(Bedwars.getInstance(),false));
        for (Player worldPlayer : world.getPlayers()){
            if (worldPlayer == p) continue;
            CraftPlayer craftPlayer = (CraftPlayer) worldPlayer;
            PlayerConnection playerConnection = craftPlayer.getHandle().playerConnection;
            if (craftPlayer.hasMetadata("ViewStatus")) {
                if (!craftPlayer.getMetadata("ViewStatus").get(0).asBoolean()) {
                    // do not hide the player for those who are alreaday hidden.
                    continue;
                }
            }
            craftPlayer.hidePlayer(p);
            //playerConnection.sendPacket(PCS);
        }
    }

    public void nextUpgrade(){
        for (Generator generator : generators){
            try {
                GeneratorStands a = generator.getStands().get(2);
                a.getLivingEntity().setCustomName("§e"+generator.tier());
                World world = Bukkit.getWorld(gameUUID.toString());
                for (Player player : world.getPlayers()){
                    generator.show(player);
                }
            } catch (Exception qf){

            }
        }

        switch (this.generatorUpgrades.getGeneratorType()) {
            case DIAMOND:{
                this.generatorUpgrades = new GameGenUpgrade(GeneratorType.EMERALD);
                break;
            }
            case EMERALD:{
                this.generatorUpgrades = new GameGenUpgrade(GeneratorType.DIAMOND);
                break;
            }
        }
    }

    /**
     * Checks if inside of location
     * @param loc
     * @return
     */
    public boolean isInside(Location loc){
        if (!(loc.getZ() >= min.getZ() && loc.getX() >= min.getX())){
            return false;
        }
        if (!(loc.getZ() <= max.getZ() && loc.getX() <= max.getX())){
            return false;
        }
        if (!(loc.getY() >= min.getY() && loc.getY() <= max.getY())){
            return false;
        }
        return true;
    }

}