package com.sweatsunited.core.module;

import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.nms.CraftSlimeWorld;
import com.grinderwolf.swm.nms.SlimeNMS;
import com.grinderwolf.swm.nms.v1_8_R3.CustomWorldServer;
import com.grinderwolf.swm.plugin.SWMPlugin;
import com.sweatsunited.core.game.Game;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class MapHandler {

    private final SlimeLoader loader;
    private final SWMPlugin swmPlugin;
    private CraftSlimeWorld craftSlimeWorld;
    private final SlimeNMS swmNMS;
    private static final MinecraftServer mcServer = MinecraftServer.getServer();
    private static final CraftServer craftServer = MinecraftServer.getServer().server;
    private final Map<String, World> fields;

    public MapHandler(){
        Map<String, World> fields1 = null;
        this.swmPlugin = SWMPlugin.getInstance();
        this.loader = SWMPlugin.getInstance().getLoader("file");
        this.swmNMS = swmPlugin.getNms();
        Field field;
        try {
            field = CraftServer.class.getDeclaredField("worlds");
            field.setAccessible(true);
            fields1 = (Map<String, World>) field.get(craftServer);
        } catch (NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }
        this.fields = fields1;
    }

    // call it aysnc.
    public void genWorld(String map, Game game){
        if (craftSlimeWorld == null){
            this.craftSlimeWorld = (CraftSlimeWorld) swmPlugin.getNms().getSlimeWorld(Bukkit.getWorld("Lighthouse1v1"));
        }
        // /swm clone <pattern> <name>
        Long clone1 = System.currentTimeMillis();
        SlimeWorld slimeWorld = clone(game.getGameUUID().toString(),this.craftSlimeWorld);
        Long clone2 = System.currentTimeMillis();
        Bukkit.getLogger().log(Level.SEVERE,clone2-clone1 + "ms");
        Long c1 = System.currentTimeMillis();
        addWorldToServerList(this.swmNMS.createNMSWorld(slimeWorld));
        Long c2 = System.currentTimeMillis();
        Bukkit.getLogger().log(Level.SEVERE,c2-c1 + "ms");
        game.load();
    }

    public SlimeWorld clone(String worldName,CraftSlimeWorld slimeWorld){
        CraftSlimeWorld world = new CraftSlimeWorld(this.loader, worldName, new HashMap<>(slimeWorld.getChunks()), slimeWorld.getExtraData().clone(),
                new ArrayList<>(slimeWorld.getWorldMaps()), slimeWorld.getVersion(), slimeWorld.getPropertyMap(), true, slimeWorld.isLocked());
        return world;
    }

    public void addWorldToServerList(Object worldObject){
        long startTime = System.currentTimeMillis();
        CustomWorldServer server = (CustomWorldServer) worldObject;
        String worldName = server.getWorldData().getName();
        server.setReady(true);
        //this.worlds.put(world.getName().toLowerCase(), world);
        World world = server.getWorld();
        fields.put(world.getName(),world);
        mcServer.worlds.add(server);
        Bukkit.getLogger().info("World " + worldName + " loaded in " + (System.currentTimeMillis() - startTime) + "ms.");
    }

}