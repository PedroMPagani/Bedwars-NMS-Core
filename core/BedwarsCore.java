package com.sweatsunited.core;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.io.ByteStreams;
import com.sweatsunited.bedwars.Bedwars;
import com.sweatsunited.core.assemble.Assemble;
import com.sweatsunited.core.assemble.AssembleStyle;
import com.sweatsunited.core.game.enums.GameType;
import com.sweatsunited.core.handler.*;
import com.sweatsunited.core.module.GameManager;
import com.sweatsunited.core.module.GeneratorExecutor;
import com.sweatsunited.core.module.MapHandler;
import com.sweatsunited.core.module.ShopManager;
import com.sweatsunited.core.scoreboard.BedwarsScoreboard;
import com.sweatsunited.core.util.GameQueue;
import com.sweatsunited.core.util.LocationHasher;
import com.sweatsunited.data.handler.ConnectionHandler;
import com.sweatsunited.data.sql.MySQL;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Getter
public class BedwarsCore {

    private static BedwarsCore instance;
    private final GameManager gameHandler;
    private final GameQueue gameQueue = new GameQueue();
    private YamlConfiguration gameConfig;
    private final LocationHasher locationHasher;
    private final MapHandler mapHandler;
    private final ProtocolManager protocolManager;
    private final ShopManager shopManager;
    private static MySQL MySQLConnection;

    public BedwarsCore(){
        CompletableFuture.runAsync(this::setupSQL);
        this.shopManager = new ShopManager();
        this.locationHasher = new LocationHasher();
        instance = this;
        Bukkit.getLogger().log(Level.FINE, "§aStarting Bedwars Game Core..");
        gameHandler = new GameManager();
        gameHandler.startHandling();
        registerHandlers();
        generateGameFile();
        this.mapHandler = new MapHandler();
        //PacketType.Play.Server.COLLECT
        protocolManager = ProtocolLibrary.getProtocolManager();
        new GeneratorExecutor();
        Assemble assemble = new Assemble(Bedwars.getInstance(), new BedwarsScoreboard());
        assemble.setTicks(2);
        assemble.setAssembleStyle(AssembleStyle.CUSTOM);
    }

    public void setupSQL(){
        String q = "";
        for (GameType value : GameType.values()){
            q = "`"+value.name()+"` DOUBLE NOT NULL, ";
        }
        System.out.println(q);
        MySQLConnection = new MySQL(Bedwars.getInstance(), Bedwars.getInstance().getConfig().getString("mysql.host"), Bedwars.getInstance().getConfig().getString("mysql.user"), Bedwars.getInstance().getConfig().getString("mysql.password"), Bedwars.getInstance().getConfig().getString("mysql.db"), Bedwars.getInstance().getConfig().getInt("mysql.port"));
        final String statement = "CREATE TABLE IF NOT EXISTS `" +
                Bedwars.getInstance().getConfig().getString("mysql.tablename") + "` ( `id` INT NOT NULL AUTO_INCREMENT , `user` VARCHAR(50) NOT NULL , " + q + "`json` MEDIUMTEXT NOT NULL , PRIMARY KEY (`id`));";
        MySQLConnection.addTableStatement(statement);
        MySQLConnection.openConnection();
        //new AtomStorage(Bedwars.getInstance().getConfig().getString("mysql.tablename"));
        for (final Player onlinePlayer : Bukkit.getOnlinePlayers()){
        //    AtomStorage.loadUser(onlinePlayer);
        }
    }

    public void registerHandlers(){
        PluginManager pluginManager = Bukkit.getPluginManager();
        Bedwars bedwars = Bedwars.getInstance();
        pluginManager.registerEvents(new ActionHandler(),bedwars);
        pluginManager.registerEvents(new PlayerHandler(this),bedwars);
        pluginManager.registerEvents(new GameHandler(this),bedwars);
        pluginManager.registerEvents(new NPCHandler(this),bedwars);
        pluginManager.registerEvents(new InventoryHandler(),bedwars);
        pluginManager.registerEvents(new ConnectionHandler(),bedwars);
    }

    public void generateGameFile(){
        Bedwars bed = Bedwars.getInstance();
        File file = new File(bed.getDataFolder(),"game.yml");
        if (!file.exists()){
            try {
                boolean c = file.createNewFile();
                if (!c) return;
                byte[] array = ByteStreams.toByteArray(bed.getResource("game.yml"));
                FileOutputStream fi = new FileOutputStream(file);
                fi.write(array);
                fi.flush();
                fi.close();
                gameConfig = YamlConfiguration.loadConfiguration(file);
            } catch (IOException e){
                e.printStackTrace();
            }
            return;
        }
        gameConfig = YamlConfiguration.loadConfiguration(file);
    }

    public static MySQL getMySQLConnection(){
        return MySQLConnection;
    }

    public YamlConfiguration getGameConfig(){
        return this.gameConfig;
    }

    public static BedwarsCore getInstance(){
        return instance;
    }

}