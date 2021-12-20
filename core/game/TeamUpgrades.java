package com.sweatsunited.core.game;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class TeamUpgrades {

    private int sharpenedWords;
    private int reinforcedArmor;
    private int maniacMiner;
    private int ironForge;
    private int healPool;
    private int dragonBuff;

    public TeamUpgrades(){
        this.sharpenedWords = 0;
        this.reinforcedArmor = 0;
        this.maniacMiner = 0;
        this.ironForge = 0;
        this.healPool = 0;
        this.dragonBuff = 0;
    }

}