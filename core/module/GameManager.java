package com.sweatsunited.core.module;

import com.sweatsunited.core.game.Game;
import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class GameManager {

    private final ConcurrentHashMap<UUID, Game> games = new ConcurrentHashMap<>();
    private final GeneratorHandler generatorHandler;

    public GameManager(){
        this.generatorHandler = new GeneratorHandler(this);
    }

    public void startHandling(){
        generatorHandler.start();
    }

}