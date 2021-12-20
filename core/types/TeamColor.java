package com.sweatsunited.core.types;

public enum TeamColor {
    RED("§c","Red"),BLUE("§1","Blue"),GREEN("§2","Green"),YELLOW("§e",
            "Yellow"),AQUA("§b","Aqua"),WHITE("§f","White"),PINK("§5","Pink"),GRAY("§8"
    ,"Gray");

    String name;
    String bukkitColor;

    TeamColor(String q,String n){
        this.bukkitColor = q;
        this.name = n;
    }

    public String getName() {
        return name;
    }

    public String getBukkitColor() {
        return bukkitColor;
    }
}