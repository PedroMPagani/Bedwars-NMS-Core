package com.sweatsunited.core.module;

import com.sweatsunited.bedwars.Bedwars;
import com.sweatsunited.core.BedwarsCore;
import com.sweatsunited.core.game.Game;
import com.sweatsunited.core.game.enums.GameStatus;
import com.sweatsunited.core.game.GameTeam;
import com.sweatsunited.core.model.Generator;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.PacketPlayOutCollect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.util.NumberConversions;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class GeneratorExecutor implements Runnable {

    private final ScheduledExecutorService service;
    private final ConcurrentHashMap<UUID, Game> map;
    private static GeneratorExecutor instance;

    public GeneratorExecutor(){
        instance = this;
        this.service = Executors.newSingleThreadScheduledExecutor();
        this.service.scheduleWithFixedDelay(this,250,250, TimeUnit.MILLISECONDS);
        this.map = BedwarsCore.getInstance().getGameHandler().getGames();
    }

    @Override
    public void run() {
        Collection<Game> games = map.values();
        for (Game game : games) {
            // always get player by uuid. -> faster than name.
            for (GameTeam team : game.getTeams()) {
                List<UUID> users = team.getUsers();
                int size = users.size();
                for (int i = 0; i < size; i++) {
                    UUID uuid = users.get(i);
                    Player p = Bukkit.getPlayer(uuid);
                    if (p == null) continue;
                    Location loc = p.getLocation();
                    if (loc.getY() < 45){
                        CraftPlayer cp = (CraftPlayer) p;
                        if (!game.getGameStatus().equals(GameStatus.ENDED)){
                            if (p.hasMetadata("isAlive")) {
                                try {
                                    cp.damage(cp.getMaxHealth());
                                    PlayerDeathEvent playerDeathEvent = new PlayerDeathEvent(p, new ArrayList<>(), 0, "");
                                    Bukkit.getScheduler().runTask(Bedwars.getInstance(), () -> Bukkit.getPluginManager().callEvent(playerDeathEvent));
                                } catch (Exception ifg) {
                                    continue;
                                }
                            }
                        }
                        continue;
                    }
                    LinkedList<Generator> gen = game.getGenerators();
                    int length = gen.size();
                    for (int j = 0; j < length; j++){
                        Generator q = gen.get(j);
                        if (loc.getWorld() != q.getLocation().getWorld()) continue;
                        if (distance(q.getLocation(), loc) >= 1.5) continue;
                        Iterator<EntityItem> iterator = q.getItems().iterator();
                        while (iterator.hasNext()) {
                            EntityItem item = iterator.next();
                            if (System.currentTimeMillis() > item.activatedTick + 350L){
                                PacketPlayOutCollect pa = new PacketPlayOutCollect(item.getId(), p.getEntityId());
                                List<Player> players = item.getWorld().getWorld().getPlayers();
                                synchronized (players){
                                    for (Player player : players){
                                        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(pa);
                                    }
                                }
                                iterator.remove();
                                try {
                                    ItemStack qf = item.getItemStack();
                                    org.bukkit.inventory.ItemStack qfg = CraftItemStack.asBukkitCopy(qf);
                                    qfg.setAmount(1);
                                    p.getInventory().addItem(qfg);
                                    p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1f, 1f);
                                } catch (Exception qf) {
                                }
                            }
                        }
                    }


                }
            }
        }
    }

    public double distance(Location o,Location q){
        return Math.sqrt(NumberConversions.square(o.getX() - q.getX()) + NumberConversions.square(o.getY() - q.getY()) + NumberConversions.square(o.getZ() - q.getZ()));
    }

    public static GeneratorExecutor getInstance(){
        return instance;
    }

}