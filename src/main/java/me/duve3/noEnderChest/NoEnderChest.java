package me.duve3.noEnderChest;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class NoEnderChest extends JavaPlugin {

    String CancelledMessage = ChatColor.RED + "[NoEnderChest] You cannot put this item in an Ender chest!";
    String CancelledShulker = ChatColor.RED + "[NoEnderChest] This shulker box contains a banned item!";
    List<Material> bannedItems = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getLogger().info("Started NoEnderChest!");
        this.getServer().getPluginManager().registerEvents(new BannedItemsListener(this), this);

        bannedItems.add(Material.DRAGON_EGG);
        bannedItems.add(Material.ELYTRA);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
