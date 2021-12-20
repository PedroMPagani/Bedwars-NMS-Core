package com.sweatsunited.data;

import com.sweatsunited.data.component.BedwarsUser;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

/*
THIS CLASS IS AN SINGLETON.
 */
@Getter
public class DataManager {

    private final HashMap<UUID, BedwarsUser> cache = new HashMap<>();
    private static final DataManager dataManager = new DataManager();

    private DataManager(){}

    /**
     * SINGLETON
     * @return
     */
    public static DataManager getDataManager(){







        return dataManager;
    }

    public static void main(String[] args){
        String[] aqf = new String[3];
        aqf[0] = "EWFA";
        aqf[1] = "3141FA";
        aqf[2] = "EZXWAF";
        af(aqf);
    }

    public static void af(String... a){
        System.out.println(Arrays.toString(a));
    }

}