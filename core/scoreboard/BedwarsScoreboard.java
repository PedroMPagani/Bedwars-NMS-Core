package com.sweatsunited.core.scoreboard;

import com.sweatsunited.core.BedwarsCore;
import com.sweatsunited.core.assemble.AssembleAdapter;
import com.sweatsunited.core.game.Game;
import com.sweatsunited.core.game.GameTeam;
import com.sweatsunited.core.model.Generator;
import com.sweatsunited.core.types.GeneratorType;
import com.sweatsunited.core.types.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class BedwarsScoreboard implements AssembleAdapter {

    private final LinkedHashMap<UUID,List<String>> lines = new LinkedHashMap<>();
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final SimpleDateFormat upgradeFormat = new SimpleDateFormat("mm:ss");
    private final SimpleDateFormat yearFormat = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    public String getTitle(Player player){
        int q = random.nextInt(5);
        switch (q){
            case 0:{
                return "§e§l   B§bE§dD W§5A§eR§aS   ";
            }
            case 1:{
                return "§e§l   B§cE§dD W§4A§eR§aS   ";
            }
            case 2:{
                return "§e§l   B§dE§dD W§3A§fR§bS   ";
            }
            case 3:{
                return "§e§l   B§5E§dD W§2A§bR§fS   ";
            }
            case 4:{
                return "§e§l   B§4E§dD W§6A§fR§aS   ";
            }
            case 5:{
                return "§e§l   B§3E§dD W§4A§aR§aS   ";
            }
        }
        return "§e§l   BED WARS   ";
    }

    @Override
    public List<String> getLines(Player player){
        List<String> a = lines.get(player.getUniqueId());
        a.clear();
        if (player.hasMetadata("inGame")){
         String wName = player.getWorld().getName();
         try {
             UUID uuid = UUID.fromString(wName);
             Game game = BedwarsCore.getInstance().getGameHandler().getGames().get(uuid);
             if (game == null) return a;
             a.add("§7"+yearFormat.format(System.currentTimeMillis()));
             a.add("");
             a.add(nextUpgrade(game) + " in §a" + upgradeFormat.format(game.getGeneratorUpgrades().getHappensAt()-System.currentTimeMillis()));
             a.add("");
             for (GameTeam team : game.getTeams()){
                 a.add(getBegining(team,team.getUsers().contains(player.getUniqueId())));
             }
             a.add("");
             a.add("§esweatsunited.com");
             return a;
         } catch (Exception i){
             a.add("TST");
             a.add("§esweatsunited.com");
             return a;
         }
        } else {
            a.add("TST");
            a.add("§esweatsunited.com");
        }
        return a;
    }

    public String nextUpgrade(Game game){
        switch (game.getGeneratorUpgrades().getGeneratorType()){
            case EMERALD:{
                for (Generator generator : game.getGenerators()) {
                    if (generator.getGeneratorType() == GeneratorType.EMERALD){
                        String a = "Emerald ";
                        switch (generator.getTier()){
                            case 1:{
                                a+= "II";
                                break;
                            }
                            case 2:{
                                a+= "III";
                                break;
                            }
                            case 3:{
                                a+= "IV";
                                break;
                            }
                            case 4:{
                                a+= "V";
                                break;
                            }
                            case 5:{
                                a+= "VI";
                                break;
                            }
                        }
                        return a;
                    }
                }
                return "";
            }
            case DIAMOND:{
                for (Generator generator : game.getGenerators()){
                    if (generator.getGeneratorType() == GeneratorType.DIAMOND){
                        String a = "Diamond ";
                        switch (generator.getTier()){
                            case 1:{
                                a+= "II";
                                break;
                            }
                            case 2:{
                                a+= "III";
                                break;
                            }
                            case 3:{
                                a+= "IV";
                                break;
                            }
                            case 4:{
                                a+= "V";
                                break;
                            }
                            case 5:{
                                a+= "VI";
                                break;
                            }
                        }
                        return a;
                    }
                }
                return "";
            }
        }
        return "";
    }



    public String getBegining(GameTeam gameTeam,boolean you){
        TeamColor teamColor = gameTeam.getColor();
        int manyLeft = 0;
        for (UUID gameTeamUser : gameTeam.getUsers()){
            if (Bukkit.getPlayer(gameTeamUser).hasMetadata("isAlive")){
                manyLeft++;
            }
        }
        return teamColor.getBukkitColor() + teamColor.getName().charAt(0) + " §f"+teamColor.getName()+":" +
                (gameTeam.isDestroyedBed() ? manyLeft > 0 ? "§a "+ manyLeft : " §c§l✗" : " §a§l✓") + (you ? "§7 YOU" : " ");
    }


    @Override
    public LinkedHashMap<UUID, List<String>> getArrayCache(){
        return lines;
    }

}