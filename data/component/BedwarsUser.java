package com.sweatsunited.data.component;

import com.sweatsunited.core.game.Game;
import com.sweatsunited.core.game.enums.GameType;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.UUID;

@Getter
public class BedwarsUser {

    private final UUID uuid;
    private int wins;
    private int losses;
    private final LinkedHashMap<GameType,Statistic> data;

    public BedwarsUser(UUID q){
        this.data = new LinkedHashMap<>();
        this.uuid = q;
    }


    public void generateStatistics(Game game){
        GameType type = game.getGameType();
        Statistic statistic = data.get(type);
        if (statistic == null){
            statistic = new Statistic();
        }

        data.replace(type,statistic);
    }


}