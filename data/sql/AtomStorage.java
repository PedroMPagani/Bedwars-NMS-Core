package com.sweatsunited.data.sql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sweatsunited.core.BedwarsCore;
import com.sweatsunited.data.DataManager;
import com.sweatsunited.data.component.BedwarsUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;

/*public class AtomStorage {

    private static AtomStorage instance;
    private final String tableName;
    private final Gson gson;
    private final MySQL mySQLConnection;
    
    public AtomStorage(final String table){
        instance = this;
        tableName = table;
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.enableComplexMapKeySerialization();
        gson = gsonBuilder.create();
        this.mySQLConnection = BedwarsCore.getMySQLConnection();
    }
    
    public static void loadUser(final Player p){
        instance.selectUser(p);
    }

    public void selectUser(Player p){
        try {
            mySQLConnection.runSQL(new AtomSQLRunnable(mySQLConnection.getConnection(), new AtomSQL("SELECT * FROM `" + tableName + "` WHERE `user` = ?", p.getUniqueId().toString()), (result) -> {
                if (result.next()){
                    BedwarsUser user = gson.fromJson(result.getString("json"), BedwarsUser.class);
                    DataManager.getDataManager().getCache().put(p.getUniqueId(),user);
                } else {
                    BedwarsUser user = new BedwarsUser(p.getUniqueId());
                    DataManager.getDataManager().getCache().put(p.getUniqueId(),user);
                    registerPlayer(p.getUniqueId().toString(), user, true);
                }
            }), true);
        } catch (Exception g){
            Bukkit.getLogger().log(Level.SEVERE,"§cSQL was SETUP INCORRECTLY.");
        }
    }

    public void loadTop(){
        mySQLConnection.runSQL(new AtomSQLRunnable(mySQLConnection.getConnection(), new AtomSQL("SELECT * FROM `" + tableName + "`" + " ORDER BY CAST(`level` AS float) DESC"), (result) -> {
            int position = 1;
            TopHandler.getInstance().clear();
            while (result.next()){
                try {
                    String hash = result.getString("json");
                    User levelingUser = gson.fromJson(hash, User.class);
                    levelingUser.setTopPosition(position);
                    save(levelingUser.getUuid().toString(),levelingUser,false);
                    User user = Main.getInstance().getCache().get(levelingUser.getUuid());
                    if (user != null){
                        user.setTopPosition(position);
                    }
                    TopHandler.getInstance().getUser().add(levelingUser);
                    position++;
                    // adds the user to the top.
                } catch (Exception ignored){
                }
            }
        }), false);
    }

    public static void top(){
        instance.loadTop();
    }

    public void register(String user,BedwarsUser o,boolean async){
        Object[] f = new Object[2];
        instance.mySQLConnection.runUpdate(new AtomSQLUpdateRunnable(mySQLConnection.getConnection(), new AtomSQL("INSERT INTO `"+tableName+"` (`user`,`level`,`json`) VALUES (?,?,?)", user,f, gson.toJson(o))), async);
    }

    public static void registerPlayer(final String user, final BedwarsUser o, final boolean asynchronously) {
        instance.register(user,o,asynchronously);
    }

    public void save(String user,BedwarsUser o,boolean async){
        instance.mySQLConnection.runUpdate(new AtomSQLUpdateRunnable(instance.mySQLConnection.getConnection(), new AtomSQL("UPDATE `"+tableName+"` SET `json` = ?,`level` = ? WHERE `user` = ?;",gson.toJson(o),(long)o.getPlayTime(), user )), async);
    }

    public static void savePlayer(String user,BedwarsUser o, boolean asynchronously) {
        instance.save(user,o,asynchronously);

    }

}*/