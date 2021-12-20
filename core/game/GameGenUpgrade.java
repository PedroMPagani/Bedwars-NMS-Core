package com.sweatsunited.core.game;

import com.sweatsunited.core.types.GeneratorType;
import lombok.Getter;

@Getter
public class GameGenUpgrade {

    private Long happensAt;
    private final GeneratorType generatorType;

    public GameGenUpgrade(GeneratorType generatorType){
        this.generatorType = generatorType;
        switch (generatorType){
            case EMERALD:{
                this.happensAt = System.currentTimeMillis() + 1000L*60L*6;
            }
            case DIAMOND:{
                this.happensAt = System.currentTimeMillis() + 1000L*60L;
            }
        }
    }

    public boolean check(){
        if (System.currentTimeMillis() >= happensAt){
            return true;
        }
        return false;
    }

}