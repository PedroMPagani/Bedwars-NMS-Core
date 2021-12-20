package com.sweatsunited.core.model;

import com.sweatsunited.core.game.enums.GeneratorSource;
import com.sweatsunited.core.types.GeneratorType;
import com.sweatsunited.core.types.UpdatableStand;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.UUID;

@Getter @Setter
public class Generator {

    private final UUID generatorUUID;
    private Location location;
    private final GeneratorType generatorType;
    private Long firstGeneration;
    private Long nextGeneration;
    private int tier;
    private final LinkedList<GeneratorStands> stands;
    private LinkedList<EntityItem> items;
    private GeneratorStands head;
    private GeneratorSource generatorSource;

    public Generator(Location loc, UUID generatorUUID,GeneratorType generatorType){
        this.location = loc;
        this.items = new LinkedList<>();
        this.generatorUUID = generatorUUID;
        this.generatorType = generatorType;
        this.stands = new LinkedList<>();
        this.firstGeneration = System.currentTimeMillis();
        this.nextGeneration = System.currentTimeMillis() + 20L;
        this.tier = 1;
    }

    public void show(Player p){
        CraftPlayer craftPlayer = (CraftPlayer) p;
        PlayerConnection pc = craftPlayer.getHandle().playerConnection;
        for (GeneratorStands generatorStand : getStands()){
            ArmorStand a = (ArmorStand) generatorStand.getLivingEntity().getBukkitEntity();
            a.setVisible(false);
            a.setCustomNameVisible(true);
            PacketPlayOutSpawnEntityLiving eg = new PacketPlayOutSpawnEntityLiving(generatorStand.getLivingEntity());
            PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(a.getEntityId(), generatorStand.getLivingEntity().getDataWatcher(), true);
            pc.sendPacket(eg);
            pc.sendPacket(packetPlayOutEntityMetadata);
        }
        if (head == null) return;
        ArmorStand a = (ArmorStand) head.getLivingEntity().getBukkitEntity();
        a.setVisible(false);
        PacketPlayOutSpawnEntityLiving eg = new PacketPlayOutSpawnEntityLiving(head.getLivingEntity());
        PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(a.getEntityId(), head.getLivingEntity().getDataWatcher(), true);
        PacketPlayOutEntityEquipment e = new PacketPlayOutEntityEquipment(a.getEntityId(),4, CraftItemStack.asNMSCopy(a.getHelmet()));
        pc.sendPacket(eg);
        pc.sendPacket(e);
        pc.sendPacket(packetPlayOutEntityMetadata);
    }

    public void init(){
        World world = location.getWorld();
        this.location = new Location(world,location.getBlockX(),location.getBlockY(),location.getBlockZ());
        CraftWorld craftWorld = (CraftWorld)world;
        EntityArmorStand armorStand1 = new EntityArmorStand(craftWorld.getHandle());
        EntityArmorStand armorStand2 = new EntityArmorStand(craftWorld.getHandle());
        EntityArmorStand armorStand3 = new EntityArmorStand(craftWorld.getHandle());
        location.add(0.5,2,0.5);
        armorStand1.setLocation(location.getX(),location.getY(),location.getZ(),0,0);
        armorStand1.maxNoDamageTicks = Integer.MAX_VALUE;
        armorStand1.noDamageTicks = Integer.MAX_VALUE;
        location.add(0,0.5,0);
        armorStand2.setLocation(location.getX(),location.getY(),location.getZ(),0,0);
        armorStand2.maxNoDamageTicks = Integer.MAX_VALUE;
        armorStand2.noDamageTicks = Integer.MAX_VALUE;
        location.add(0,0.5,0);
        armorStand3.setLocation(location.getX(),location.getY(),location.getZ(),0,0);
        armorStand3.maxNoDamageTicks = Integer.MAX_VALUE;
        armorStand3.noDamageTicks = Integer.MAX_VALUE;
        location.subtract(0,3,0);
        startArmor(armorStand1);
        startArmor(armorStand2);
        startArmor(armorStand3);
        try {
            GeneratorStands gen = new GeneratorStands(armorStand1, UpdatableStand.Y);
            GeneratorStands gen2 = new GeneratorStands(armorStand2, UpdatableStand.N);
            GeneratorStands gen3 = new GeneratorStands(armorStand3, UpdatableStand.N);
            stands.add(gen);
            stands.add(gen2);
            stands.add(gen3);
            armorStand1.setCustomName("§e Spawns in §c15 §eseconds");
            armorStand2.setCustomName(name(generatorType));
            armorStand3.setCustomName("§e"+tier());
        } catch (Exception q){
            System.out.println("Exception happened.");
        }
        EntityArmorStand as = new EntityArmorStand(craftWorld.getHandle());
        location.add(0,0.9,0);
        startArmor(as);
        as.setLocation(location.getX(),location.getY(),location.getZ(),0,0);
        location.subtract(0,0.9,0); // styas the same as the begginnig.
        head = new GeneratorStands(as,UpdatableStand.Y);
        ArmorStand q = (ArmorStand) as.getBukkitEntity();
        switch (generatorType){
            case IRON:{
                q.setHelmet(new ItemStack(Material.IRON_BLOCK));
                break;
            }
            case DIAMOND:{
                q.setHelmet(new ItemStack(Material.DIAMOND_BLOCK));
                break;
            }
            case EMERALD:{
                q.setHelmet(new ItemStack(Material.EMERALD_BLOCK));
                break;
            }
        }
        q.setSmall(false);
        q.setCustomNameVisible(false);
    }

    public String name(GeneratorType material){
        String r = "NONE";
        switch (material){
            case IRON:{
                r = "§f§lIRON";
                break;
            }
            case DIAMOND:{
                r = "§b§lDIAMOND";
                break;
            }
            case EMERALD:{
                r = "§a§lEMERALD";
                break;
            }
        }
        return r;
    }

    public String tier(){
        switch (tier){
            case 1:{
                return "Tier I";
            }
            case 2:{
                return "Tier II";
            }
            case 3:{
                return "Tier III";
            }
            case 4:{
                return "Tier IV";
            }
            case 5:{
                return "Tier V";
            }
            case 6:{
                return "Tier VI";
            }
            case 7:{
                return "Tier VII";
            }
            case 8:{
                return "Tier VIII";
            }
            case 9:{
                return "Tier IX";
            }
            case 10:{
                return "X";
            }
        }
        return "";
    }


    public void startArmor(EntityArmorStand a){
        a.setBasePlate(false);
        a.setArms(false);
        a.setSmall(true);
        a.setGravity(false);
        a.setCustomNameVisible(true);
    }

}