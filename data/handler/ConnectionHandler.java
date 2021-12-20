package com.sweatsunited.data.handler;

import com.sweatsunited.data.DataManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionHandler implements Listener {

    public ConnectionHandler(){}

    @EventHandler
    void onJoin(PlayerJoinEvent e){
        //AtomStorage.loadUser(e.getPlayer());
    }

    @EventHandler
    void onQuit(PlayerQuitEvent e){
        DataManager.getDataManager().getCache().remove(e.getPlayer().getUniqueId());
    }

}