package com.sweatsunited.core.util;

import com.sweatsunited.core.BedwarsCore;
import com.sweatsunited.core.game.enums.GameType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class GameData {

    private final LinkedHashMap<String,GeneratorsData> genData;

    public GameData(){
        this.genData = new LinkedHashMap<>();
    }

    public void readData(){

        YamlConfiguration config = BedwarsCore.getInstance().getGameConfig();

        ConfigurationSection sec = config.getConfigurationSection("games");
        if (sec == null) return;
        for (String map : sec.getKeys(false)) {
            String name = sec.getString(map+".name");
            ConfigurationSection configurationSection = sec.getConfigurationSection(map+".generators");
            Set<String> keys = configurationSection.getKeys(false);
            for (String s : keys){
                try {
                    GameType gameType = GameType.valueOf(s);
                    List<String> generatorHash = configurationSection.getStringList(s);




                } catch (Exception g){

                }
            }




        }





    }






    public static class GeneratorsData {

        private final LinkedHashMap<GameType, List<String>> locations;

        public GeneratorsData(LinkedHashMap<GameType, List<String>> locations) {
            this.locations = locations;
        }
    }


}
