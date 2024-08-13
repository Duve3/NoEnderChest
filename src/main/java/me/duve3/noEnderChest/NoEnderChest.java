package me.duve3.noEnderChest;

import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

// TODO: can bypass by using number keys, and using shulkerboxes, also prevents you from moving it in yr inv (might force dropping)

public final class NoEnderChest extends JavaPlugin implements Listener {

    String CancelledMessage = "[NoEnderChest] You cannot put this item in an Ender chest!";
    String CancelledShulker = "[NoEnderChest] This shulkerbox contains a banned item!";
    List<Material> bannedItems = new ArrayList<Material>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getLogger().info("Started NoEnderChest!");
        this.getServer().getPluginManager().registerEvents(this, this);

        bannedItems.add(Material.DRAGON_EGG);
        bannedItems.add(Material.ELYTRA);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void shulkerBoxCheck(Material itemMoved, Material item, InventoryClickEvent e, Player player) {
        if (itemMoved == Material.SHULKER_BOX) {
            ShulkerBox shulker = (ShulkerBox) itemMoved.createBlockData();
            for (ItemStack si : shulker.getInventory().getContents()) {
                if (si.getType() == item) {
                    this.getLogger().info("Cancelling event because of SHULKER_BOX_CHECK");
                    e.setCancelled(true);
                    player.sendMessage(CancelledShulker);
                }
            }

        }
    }
    // WE IGNORE INVENTORY DRAG EVENT BECAUSE WE ARE CALCULATING FOR NONSTACKABLES

    @EventHandler()
    public void onItemMove(InventoryClickEvent e) {
        final Player player = (Player) e.getWhoClicked();

        for (Material item : bannedItems) {
            if (e.getInventory().equals(player.getEnderChest())) {
                if (e.getCurrentItem() == null) {
                    return;
                }

                Material itemMoved = e.getCurrentItem().getType();
                shulkerBoxCheck(itemMoved, item, e, player);
                if (e.isCancelled()) {
                    return;
                }

                if (itemMoved == item) {
                    this.getLogger().info("Cancelling event because of DEFAULT CHECK");
                    e.setCancelled(true);
                    player.sendMessage(CancelledMessage);
                }
                if (e.isCancelled()) {
                    return;
                }

                if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    shulkerBoxCheck(e.getCurrentItem().getType(), item, e, player);
                    if (e.getCurrentItem().getType() == item) {
                        this.getLogger().info("Cancelling event because of MOVE_TO_OTHER_INVENTORY");
                        e.setCancelled(true);
                        player.sendMessage(CancelledMessage);
                    }
                }
                if (e.isCancelled()) {
                    return;
                }

                if (e.getAction() == InventoryAction.HOTBAR_SWAP) {
                    shulkerBoxCheck(e.getCurrentItem().getType(), item, e, player);
                    this.getLogger().info("Cancelling event because of HOTBAR_SWAP CHECK");
                    e.setCancelled(true);
                    player.sendMessage(CancelledMessage);
                }
                if (e.isCancelled()) {
                    return;
                }
            }
        }
    }


}
