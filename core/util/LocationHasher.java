package com.sweatsunited.core.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationHasher {

    public String serialize(Location location){
        World world = location.getWorld();
        double y = location.getY();
        double x = location.getX();
        double z=  location.getZ();
        float pitch = location.getPitch();
        float yaw = location.getYaw();
        return world.getName() + ";"+x+";"+y+";"+z+";"+yaw+";"+pitch;
    }

    public Location back(String serialized){
        String[] data = serialized.split(";");

        String worldName = data[0];
        try {
            double x = Double.parseDouble(data[1]);
            double y = Double.parseDouble(data[2]);
            double z = Double.parseDouble(data[3]);
            float yaw = Float.parseFloat(data[4]);
            float pitch = Float.parseFloat(data[5]);
            World world = Bukkit.getWorld(worldName);

            if (world == null) return null;
            return new Location(world,x,y,z,yaw,pitch);
        } catch (Exception q) {
            return null;
        }
    }

    public Location back(String world,String serialized){
        String[] data = serialized.split(";");
        try {
            double x = Double.parseDouble(data[1]);
            double y = Double.parseDouble(data[2]);
            double z = Double.parseDouble(data[3]);
            float yaw = Float.parseFloat(data[4]);
            float pitch = Float.parseFloat(data[5]);
            World w = Bukkit.getWorld(world);
            if (w == null) return null;
            return new Location(w,x,y,z,yaw,pitch);
        } catch (Exception q) {
            return null;
        }
    }

}
