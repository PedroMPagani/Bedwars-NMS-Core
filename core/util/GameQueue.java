package com.sweatsunited.core.util;

import com.sweatsunited.core.game.enums.GameType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GameQueue {

    private final HashMap<GameType, List<UUID>> queue;

    public GameQueue(){
        this.queue =  new HashMap<>();
    }

    public void init(){
        for (GameType type : GameType.values()) {
            List<UUID> list = new ArrayList<>();
            queue.put(type,list);
        }
    }

}
