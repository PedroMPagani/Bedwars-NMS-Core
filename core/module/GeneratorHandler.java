package com.sweatsunited.core.module;

import com.sweatsunited.bedwars.Bedwars;
import com.sweatsunited.core.game.Game;
import com.sweatsunited.core.game.GameTeam;
import com.sweatsunited.core.game.enums.GeneratorSource;
import com.sweatsunited.core.model.*;
import com.sweatsunited.core.types.UpdatableStand;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GeneratorHandler implements Runnable {

    private final GameManager gameHandler;

    public GeneratorHandler(GameManager instance){
        this.gameHandler = instance;
    }

    public void start(){
        Bukkit.getScheduler().runTaskTimerAsynchronously(Bedwars.getInstance(),this, 0L,1);
    }

    @Override
    public void run(){
        ConcurrentHashMap<UUID, Game> games = gameHandler.getGames();
        Long current = System.currentTimeMillis();
        for (Map.Entry<UUID, Game> entry : games.entrySet()){
            Game game = entry.getValue();
            World world = Bukkit.getWorld(game.getGameUUID().toString());
            if (world == null) continue;
            game.rotateArmor(); // rotates the armorstand.
            boolean upgrade = game.getGeneratorUpgrades().check();
            LinkedList<Generator> generators = game.getGenerators();
            if (upgrade){
                for (Generator generator : generators){
                    if (generator.getGeneratorType() == game.getGeneratorUpgrades().getGeneratorType()){
                    generator.setTier(generator.getTier() + 1);
                    }
                }
                game.nextUpgrade();
            }
            int size = generators.size();
            for (int i = 0; i < size; i++){
                Generator gen = generators.get(i);
                // ISLAND PRIVATE GENERATOR.
                if(gen.getGeneratorSource() == GeneratorSource.PRIVATE){
                    if (current >= gen.getNextGeneration()){
                        for (GameTeam team : game.getTeams()){
                            if (team.getGenerator() == gen){
                                gen.setNextGeneration(System.currentTimeMillis() + getAgain(team));
                                break;
                            }
                        }
                        dropItemStack(gen, Material.DIAMOND);
                        dropItemStack(gen, Material.IRON_INGOT);
                        dropItemStack(gen, Material.EMERALD);
                    }
                    continue;
                }
                for (GeneratorStands stands : gen.getStands()){
                    if (stands.getUpdatableStand() == UpdatableStand.Y){
                        stands.getLivingEntity().setCustomName("§eSpawns in §c" + (gen.getNextGeneration()-current)/1000 + "§e seconds");
                        PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(stands.getLivingEntity().getId(),stands.getLivingEntity().getDataWatcher(), true);
                        for (Player player : world.getPlayers()) {
                            if (player == null) continue;
                            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetPlayOutEntityMetadata);
                        }
                    }
                }
                if (current < gen.getNextGeneration()) continue;
                if (current >= gen.getNextGeneration()){
                    gen.setNextGeneration(System.currentTimeMillis() + getPublicAgain(gen));
                    // can we spawn items async? Packets.. -> YES WE CAN BITCHES
                    switch (gen.getGeneratorType()){
                        case EMERALD:{
                            dropItemStack(gen,Material.EMERALD);
                            break;
                        }
                        case DIAMOND:{
                            dropItemStack(gen,Material.DIAMOND);
                            break;
                        }
                        case IRON:{
                            dropItemStack(gen,Material.IRON_INGOT);
                            break;
                        }
                    }
                }
            }
        }
    }

    public Long getPublicAgain(Generator q){
        switch (q.getTier()){
            case 1:{
                return 30000L;
            }
            case 2:{
                return 25000L;
            }
            case 3:{
                return 20000L;
            }
            case 4:{
                return 15000L;
            }
            case 5:{
                return 10000L;
            }
        }
        return 10000L;
    }

    private Long getAgain(GameTeam gameTeam){
        if (gameTeam.getTeamUpgrades().getIronForge() == 0){
            return 1500L;
        }
        switch (gameTeam.getTeamUpgrades().getIronForge()){
            case 1:{
                return 1250L;
            }
            case 2:{
                return 1000L;
            }
            case 3:{
                return 900L;
            }
            case 4:{
                return 850L;
            }
            case 5:{
                return 750L;
            }
        }
        return 500L;
    }

    public void dropItemStack(Generator gen,Material material){
        Location loc = gen.getLocation();
        CraftWorld craftServer = (CraftWorld)loc.getWorld();
        ItemStack item = new ItemStack(material);
        EntityItem entityItem = new EntityItem(craftServer.getHandle(), loc.getX(), loc.getY(), loc.getZ(), CraftItemStack.asNMSCopy(item));
        gen.getItems().add(entityItem);
        entityItem.activatedTick = System.currentTimeMillis();
        entityItem.getBukkitEntity().setVelocity(new Vector(0,0.1,0));
        PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(entityItem.getId(), entityItem.getDataWatcher(), true);
        PacketPlayOutSpawnEntity eg = new PacketPlayOutSpawnEntity(entityItem, 2);
        PacketPlayOutEntityVelocity q = new PacketPlayOutEntityVelocity(entityItem);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()){
            CraftPlayer craftPlayer = (CraftPlayer)onlinePlayer;
            if (craftPlayer.getWorld().getName().equals(entityItem.getWorld().getWorld().getName())){
                PlayerConnection playerConnection = craftPlayer.getHandle().playerConnection;
                playerConnection.sendPacket(eg);
                playerConnection.sendPacket(packetPlayOutEntityMetadata);
                playerConnection.sendPacket(q);
            }
        }
    }

}