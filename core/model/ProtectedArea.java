package com.sweatsunited.core.model;

import lombok.Getter;
import org.bukkit.Location;

@Getter
public class ProtectedArea {

    private final Location minLocation;
    private final Location maxLocation;

    public ProtectedArea(Location minLocation, Location maxLocation){
        this.minLocation = minLocation;
        this.maxLocation = maxLocation;
    }

    public boolean isInside(Location location){
        return false;
    }

}