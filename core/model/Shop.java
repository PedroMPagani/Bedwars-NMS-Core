package com.sweatsunited.core.model;

import com.sweatsunited.core.types.Category;
import com.sweatsunited.core.util.ItemNBT;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

@Getter @Setter
public class Shop implements InventoryHolder {

    private final HashMap<Integer, Consumer<InventoryClickEvent>> actions;
    private Inventory inventory;
    private Category category = Category.BLOCKS;

    public Shop(){
        this.actions = new HashMap<>();
    }

    public void openMenu(Player p){
        int iron = 0;
        int gold = 0;
        int emerald = 0;
        for (ItemStack content : p.getInventory().getContents()){
            if (content != null){
                if (content.getType() == Material.IRON_INGOT){
                    iron+=content.getAmount();
                }
                if (content.getType() == Material.GOLD_INGOT){
                    gold+=content.getAmount();
                }
                if (content.getType() == Material.EMERALD){
                    emerald+=content.getAmount();
                }
            }
        }
        switch (this.category){
            case PREFERED:{
                this.inventory = Bukkit.createInventory(this,54,"Quick Select");
                /**
                 * Make customizable menus for players to setup in /settings, then return their setup settings.
                 */

                break;
            }
            case BLOCKS:{
                this.inventory = Bukkit.createInventory(this,54,"Blocks");

                inventory.setItem(19,craft(Material.WOOL,0
                        ,"§aWool", Arrays.asList("§7Cost: §f4 Iron","",
                                iron>=4 ? "§eClick to purchase!" : "§cYou don't have enough Iron!")));

                inventory.setItem(20,craft(Material.HARD_CLAY,0,
                        "§aHardened Clay",Arrays.asList("§7Cost: §f12 Iron","",
                                iron>=12 ? "§eClick to purchase!" : "§cYou don't have enough Iron!")));

                inventory.setItem(21,craft(Material.GLASS,0,"§aBlast-Proof Glass",
                        Arrays.asList("§7Cost: §f12 Iron", "",
                                iron>=12 ? "§eClick to purchase!" : "§cYou don't have enough Iron!")));

                inventory.setItem(22,craft(Material.ENDER_STONE,0,"§aEnd Stone",
                        Arrays.asList("§7Cost: §f24 Iron","",
                                iron>=24 ? "§eClick to purchase!" : "§cYou don't have enough Iron!")));

                inventory.setItem(23,craft(Material.LADDER,0,"§aLadder",
                        Arrays.asList("§7Cost: §f4 Iron","",
                                iron >= 4 ? "§eClick to purchase!" : "§cYou don't have enough Iron!")));

                inventory.setItem(24,craft(Material.WOOD,0,"§aOak Wood Planks",
                        Arrays.asList("§7Cost: §64 Gold","",
                                gold>=4 ? "§eClick to purchase!" : "§cYou don't have enough Gold!")));

                inventory.setItem(25,craft(Material.OBSIDIAN,0,"§aObsidian",
                        Arrays.asList("§7Cost: §24 Emeralds","",
                                emerald >= 4 ? "§eClick to purchase!" : "§cYou don't have enough Emeralds!")));

                break;
            }
            case ARMOR:{
                this.inventory = Bukkit.createInventory(this,54,"Armor");

                inventory.setItem(19,craft(Material.CHAINMAIL_BOOTS,0,"§aPermanent Chainmail Armor",
                        Arrays.asList("§7Cost: §f40 Iron","",
                                "§7Chainmail leggings and boots","§7which you will always spawn","§7with.","",
                                iron >= 40 ? "§eClick to purchase!" : "§cYou don't have enough Iron!")));

                inventory.setItem(20,craft(Material.IRON_BOOTS,0,"§aPermanent Iron Armor",
                        Arrays.asList("§7Cost: §612 Gold","","§7Iron leggings and boots which","§7you will always spawn with."
                        ,"",gold>=12 ? "§eClick to purchase!" : "§cYou don't have enough Gold!")));

                inventory.setItem(21,craft(Material.DIAMOND_BOOTS,0,"§aPermanent Diamond Armor",
                        Arrays.asList("§7Cost: §26 Emeralds","","§7Diamond leggings and boots which","§7you will always spawn with.",
                                "",emerald>=6 ? "§eClick to purchase!":"§cYou don't have enough Emeralds!")));

                break;
            }
            case TOOLS:{
                this.inventory = Bukkit.createInventory(this,54,"Tools");

                inventory.setItem(19,craft(Material.SHEARS
                ,0,"§aPermanent Shears", Arrays.asList("§7Cost: §f20 Iron","",
                                "§7Great to get rid of wool. You","§7will always spawn with these shears.","",
                                iron >= 20 ? "§eClick to purchase!":"§cYou don't have enough Iron!")));

                boolean foundPickaxe = false;
                for (ItemStack content : p.getInventory().getContents()) {
                    if (content != null && ItemNBT.hasMetadata(content,"PickaxeTier")){
                        int tier = (int) ItemNBT.getMetadata(content,"PickaxeTier");
                        switch (tier){
                            case 1:{
                                inventory.setItem(20, craft(Material.IRON_PICKAXE,0,"§aIron Pickaxe (Efficiency II)"
                                        ,Arrays.asList("§7Cost: §612 Gold", "",
                                                gold>=20 ? "§eClick to purchase!":"§cYou don't have enough Gold!"),
                                        true,ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_ENCHANTS));
                                break;
                            }
                            case 2:{
                                inventory.setItem(20, craft(Material.DIAMOND_AXE,0,"§aDiamond Pickaxe (Efficiency III)"
                                        ,Arrays.asList("§7Cost: §25 Emeralds", "",
                                                emerald>=5 ? "§eClick to purchase!":"§cYou don't have enough Emerald!"),
                                        true,ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_ENCHANTS));
                                break;
                            }
                            default:{
                                inventory.setItem(20, craft(Material.DIAMOND_AXE,0,"§aDiamond Pickaxe (Efficiency III)"
                                        ,Arrays.asList("§7Cost: §25 Emeralds", "",
                                                "§eYou already have this."),
                                        true,ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_ENCHANTS));
                                break;
                            }
                        }

                        foundPickaxe=true;
                        break;
                    }
                }

                if (!foundPickaxe){
                    inventory.setItem(20, craft(Material.WOOD_PICKAXE,0,"§aWood Pickaxe (Efficiency I)"
                            ,Arrays.asList("§7Cost: §f20 Iron", "",
                                    iron>=20 ? "§eClick to purchase!":"§cYou don't have enough Iron!"),
                            true,ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_ENCHANTS));
                }

                boolean foundAxe = false;
                for (ItemStack content : p.getInventory().getContents()) {
                    if (content != null && ItemNBT.hasMetadata(content,"AxeTier")){
                        int tier = (int) ItemNBT.getMetadata(content,"AxeTier");


                        switch (tier){
                            case 1:{
                                inventory.setItem(21, craft(Material.IRON_AXE,0,"§aIron Axe (Efficiency II)"
                                        ,Arrays.asList("§7Cost: §612 Gold", "",
                                                gold>=20 ? "§eClick to purchase!":"§cYou don't have enough Gold!"),
                                        true,ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_ENCHANTS));
                                break;
                            }
                            case 2:{
                                inventory.setItem(21, craft(Material.DIAMOND_AXE,0,"§aDiamond Axe (Efficiency III)"
                                        ,Arrays.asList("§7Cost: §25 Emeralds", "",
                                                emerald>=5 ? "§eClick to purchase!":"§cYou don't have enough Emerald!"),
                                        true,ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_ENCHANTS));
                                break;
                            }
                            default:{
                                inventory.setItem(21, craft(Material.DIAMOND_AXE,0,"§aDiamond Axe (Efficiency III)"
                                        ,Arrays.asList("§7Cost: §25 Emeralds", "",
                                                "§eYou already have this."),
                                        true,ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_ENCHANTS));
                                break;
                            }
                        }

                        foundAxe=true;
                        break;
                    }
                }

                if (!foundAxe){
                    inventory.setItem(21, craft(Material.WOOD_AXE,0,"§aWood Axe (Efficiency I)"
                            ,Arrays.asList("§7Cost: §f20 Iron", "",
                                    iron>=20 ? "§eClick to purchase!":"§cYou don't have enough Iron!"),
                            true,ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_ENCHANTS));
                }

                break;
            }
            case SWORDS:{
                this.inventory = Bukkit.createInventory(this,54,"Weapons");

                inventory.setItem(19,craft(Material.STONE_SWORD,0,"§aStone Sword",
                        Arrays.asList("§7Cost: §f10 Iron", "",
                                iron >= 10 ? "§eClick to purchase!" : "§cYou don't have enough Iron!")));

                inventory.setItem(20,craft(Material.IRON_SWORD,0,"§aIron Sword",
                        Arrays.asList("§7Iron: §67 Gold","",
                                gold >= 7 ? "§eClick to purchase!" : "§cYou don't have enough Gold!")));

                inventory.setItem(21,craft(Material.DIAMOND_SWORD,0,"§aDiamond Sword",
                        Arrays.asList("§7Cost: §24 Emeralds","",
                                emerald>=4 ? "§eClick to purchase!" : "§cYou don't have enough Emeralds!")));

                inventory.setItem(22,craft(Material.STICK, 0, "§aStick (Knockback I)",
                        Arrays.asList("§7Cost: §65 Gold", "",
                                gold>=5 ? "§eClick to purchase!" : "§cYou don't have enough Gold!"),
                        true, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS));

                break;
            }
            case BOW:{
                this.inventory = Bukkit.createInventory(this,54,"Ranged");

                inventory.setItem(19, craft(Material.ARROW, 0, "§aArrows",
                        Arrays.asList("§7Cost: §62 Gold", "",
                                gold>=2 ? "§eClick to purchase!" : "§cYou don't have enough Gold!"),
                        false, 8));

                inventory.setItem(20, craft(Material.BOW, 0, "§aBow",
                        Arrays.asList("§7Cost: §612 Gold", "",
                                gold>=12 ? "§eClick to purchase!" : "§cYou don't have enough Gold!")));

                inventory.setItem(21, craft(Material.BOW, 0, "§aBow (Power I)",
                        Arrays.asList("§7Cost: §624 Gold", "",
                                gold>=24 ? "§eClick to purchase!" : "§cYou don't have enough Gold!"),
                        true, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS));

                inventory.setItem(22, craft(Material.BOW, 0, "§aBow (Punch I, Power I)",
                        Arrays.asList("§7Cost: §26 Emeralds", "",
                                emerald>=6 ? "§eClick to purchase!" : "§cYou don't have enough Emeralds!"),
                        true, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS));
                break;
            }
            case POTIONS:{
                this.inventory = Bukkit.createInventory(this,54,"Potions");

                inventory.setItem(19, craft(Material.POTION, 8226, "§aSpeed II",
                        Arrays.asList("§7Cost: §21 Emerald", "", "§9Duration: 45 seconds.", "",
                                emerald>=1 ? "§eClick to purchase!" : "§cYou don't have enough Emeralds!"),
                        true, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS));

                inventory.setItem(20, craft(Material.POTION, 8267, "§aJump Boost V",
                        Arrays.asList("§7Cost: §21 Emerald", "", "§9Duration: 45 seconds.", "",
                                emerald>=1 ? "§eClick to purchase!" : "§cYou don't have enough Emeralds!"),
                        true, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS));

                inventory.setItem(21, craft(Material.POTION, 8270, "§aInvisibility I",
                        Arrays.asList("§7Cost: §22 Emeralds", "", "§9Duration: 30 seconds.", "",
                                emerald>=2 ? "§eClick to purchase!" : "§cYou don't have enough Emeralds!"),
                        true, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS));
                break;
            }
            case UTILITY:{
                this.inventory = Bukkit.createInventory(this,54,"Utilities");

                inventory.setItem(19, craft(Material.GOLDEN_APPLE, 0, "§aGolden Apple",
                        Arrays.asList("§7Cost: §63 Gold", "", "§7Well-rounded healing item.", "",
                                gold>=3 ? "§eClick to purchase!" : "§cYou don't have enough Gold!")));

                inventory.setItem(20, craft(Material.SNOW_BALL, 0, "§aBed Bug",
                        Arrays.asList("§7Cost: §f40 Iron", "", "§7Spawns silverfish where the", "§7snowball lands to distract your",
                                "§7enemies. Lasts 15 seconds.", "",
                                iron>=40 ? "§eClick to purchase!" : "§cYou don't have enough Iron!")));

                inventory.setItem(21, craft(Material.MONSTER_EGG, 0, "§aDream Defender",
                        Arrays.asList("§7Cost: §f120 Iron", "", "§7Iron Golem to help defend your", "§7base. Lasts 4 minutes.",
                                "", iron>=120 ? "§eClick to purchase!" : "§cYou don't have enough Iron!")));

                inventory.setItem(22, craft(Material.FIREBALL, 0, "§aFireball",
                        Arrays.asList("§7Cost: §f40 Iron", "", "§7Right Click to launch!", "§7Great to",
                                "§7knock back enemies walking", "§7on small thin bridges.", "",
                                iron>=40 ? "§eClick to purchase!" : "§cYou don't have enough Iron!")));

                inventory.setItem(23, craft(Material.TNT, 0, "§aTNT",
                        Arrays.asList("§7Cost: §64 Gold", "", "§7Instantly ignites, appropriate", "§7to explode things!",
                                "", gold>=4 ? "§eClick to purchase!" : "§cYou don't have enough Gold!")));

                inventory.setItem(24, craft(Material.ENDER_PEARL, 0, "§aEnder Pearl",
                        Arrays.asList("§7Cost: §24 Emeralds", "", "§7The quickest way to invade", "§7enemy bases.",
                                "", emerald>=4 ? "§eClick to purchase!" : "§cYou don't have enough Emeralds!")));

                inventory.setItem(25, craft(Material.WATER_BUCKET, 0, "§aWater Bucket",
                        Arrays.asList("§7Cost: §63 Gold", "", "§7Great to slow down approaching", "§7enemies. Can also pretect",
                                "§7against TNT.", "",
                                gold>=3 ? "§eClick to purchase!" : "§cYou don't have enough Gold!")));

                inventory.setItem(28, craft(Material.EGG, 0, "§aBridge Egg",
                        Arrays.asList("§7Cost: §22 Emeralds", "", "§7This egg creates a bridge in its", "§7trial after being thrown.",
                                "", emerald>=2 ? "§eClick to purchase!" : "§cYou don't have enough Emeralds!")));

                inventory.setItem(29, craft(Material.MILK_BUCKET, 0, "§aMagic Milk",
                        Arrays.asList("§7Cost: §64 Gold", "", "§7Avoid triggering traps for", "§730 seconds after consuming.",
                                "", gold>=4 ? "§eClick to purchase!" : "§cYou don't have enough Gold!")));

                inventory.setItem(30, craft(Material.SPONGE, 0, "§aSponges",
                        Arrays.asList("§7Cost: §63 Gold", "", "§7Great for soaking up water.",
                                "", gold>=3 ? "§eClick to purchase!" : "§cYou don't have enough Gold!"),
                        false, 4));

                inventory.setItem(31, craft(Material.CHEST, 0, "§aCompact Pop-up Tower",
                        Arrays.asList("§7Cost: §f24 Iron", "", "§7Place a pop-up defence tower!",
                                "", iron>=24 ? "§eClick to purchase!" : "§cYou don't have enough Iron!")));
                break;
            }
        }

        inventory.setItem(0,craft(Material.BLAZE_POWDER, 0, "§aQuick Select",Arrays.asList("§7» §fYour quick select."), ItemFlag.HIDE_ATTRIBUTES));

        inventory.setItem(1,craft(Material.HARD_CLAY,0,"§aBlocks",Arrays.asList("§7» §fBlocks to buy."), ItemFlag.HIDE_ATTRIBUTES));
        inventory.setItem(2,craft(Material.GOLD_SWORD,0,"§aWeapons", Arrays.asList("§7» §fWeapons to buy."), ItemFlag.HIDE_ATTRIBUTES));
        inventory.setItem(3,craft(Material.CHAINMAIL_BOOTS,0,"§aArmor",Arrays.asList("§7» §fArmors to buy."), ItemFlag.HIDE_ATTRIBUTES));
        inventory.setItem(4,craft(Material.STONE_PICKAXE,0,"§aTools",Arrays.asList("§7» §fTools to buy."), ItemFlag.HIDE_ATTRIBUTES));
        inventory.setItem(5,craft(Material.BOW,0,"§aRanged",Arrays.asList("§7» §fRanged weapons to buy."), ItemFlag.HIDE_ATTRIBUTES));
        inventory.setItem(6,craft(Material.BREWING_STAND_ITEM,0,"§aPotions",Arrays.asList("§7» §fPotions to buy."), ItemFlag.HIDE_ATTRIBUTES));
        inventory.setItem(7,craft(Material.TNT,0,"§aUtilities",Arrays.asList("§7» §fUtilities to buy."), ItemFlag.HIDE_ATTRIBUTES));

        for (int i = 9; i <= 17; i++) {
            inventory.setItem(i,craft(Material.STAINED_GLASS_PANE, 15, "§0"));
        }

        p.openInventory(inventory);
    }

    public ItemStack craft(Material material, int data,String name, List<String> lore, ItemFlag... flags){
        ItemStack itemStack = new ItemStack(material);
        itemStack.setDurability((short) data);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(lore);
        meta.setDisplayName(name);
        for (ItemFlag flag : flags) {
            meta.addItemFlags(flag);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }


    public ItemStack craft(Material material, int data, String name, List<String> lore, boolean glow, ItemFlag... flags){
        ItemStack itemStack = new ItemStack(material);
        itemStack.setDurability((short) data);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(lore);
        meta.setDisplayName(name);
        for (ItemFlag flag : flags) {
            meta.addItemFlags(flag);
        }
        itemStack.setItemMeta(meta);
        if (glow){
            itemStack.addUnsafeEnchantment(Enchantment.DURABILITY,1);
        }

        return itemStack;
    }

    public ItemStack craft(Material material, int data, String name){
        ItemStack itemStack = new ItemStack(material);
        itemStack.setDurability((short) data);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack craft(Material material, int data, String name, List<String> lore, boolean glow, int amount, ItemFlag... flags){
        ItemStack itemStack = new ItemStack(material);
        itemStack.setDurability((short) data);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(lore);
        meta.setDisplayName(name);
        itemStack.setAmount(amount);
        for (ItemFlag flag : flags) {
            meta.addItemFlags(flag);
        }
        itemStack.setItemMeta(meta);
        if (glow){
            itemStack.addUnsafeEnchantment(Enchantment.DURABILITY,1);
        }

        return itemStack;
    }

    @Override
    public Inventory getInventory(){
        return null;
    }

}