package com.sweatsunited.core.game;

import com.sweatsunited.core.model.Generator;
import com.sweatsunited.core.types.TeamColor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Getter @Setter
public class GameTeam {

    private final List<UUID> users;
    private TeamColor color;
    private Location arenaSpawnLocation;
    private Location bedLocation;
    private Location bedLocation2;
    private Location shopLocation;
    private boolean destroyedBed;
    private final TeamUpgrades teamUpgrades;
    private final int npcId1;
    private Generator generator;

    public GameTeam(Location island,Location b,Location b2){
        this.users=  new LinkedList<>();
        this.arenaSpawnLocation = island;
        this.bedLocation = b;
        this.bedLocation2 = b2;
        this.teamUpgrades = new TeamUpgrades();
        this.teamUpgrades.setIronForge(4);
        this.destroyedBed = false;
        this.npcId1 = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
    }





}