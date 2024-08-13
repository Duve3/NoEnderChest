package me.duve3.noEnderChest;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class NoEnderChest extends JavaPlugin implements Listener {

    String CancelledMessage = ChatColor.RED + "[NoEnderChest] You cannot put this item in an Ender chest!";
    String CancelledShulker = ChatColor.RED + "[NoEnderChest] This shulker box contains a banned item!";
    List<Material> bannedItems = new ArrayList<>();

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

    private boolean isShulkerBox(Material m) {
        return switch (m) {
            case LIGHT_GRAY_SHULKER_BOX, BLACK_SHULKER_BOX, BLUE_SHULKER_BOX, BROWN_SHULKER_BOX, CYAN_SHULKER_BOX,
                 GRAY_SHULKER_BOX, GREEN_SHULKER_BOX, LIGHT_BLUE_SHULKER_BOX, LIME_SHULKER_BOX, MAGENTA_SHULKER_BOX,
                 ORANGE_SHULKER_BOX, PINK_SHULKER_BOX, PURPLE_SHULKER_BOX, RED_SHULKER_BOX, WHITE_SHULKER_BOX,
                 YELLOW_SHULKER_BOX, SHULKER_BOX -> true;
            default -> false;
        };
    }

    private void shulkerBoxCheck(Material itemMoved, Material item, InventoryClickEvent e, Player player) {
        if (isShulkerBox(itemMoved)) {
            ShulkerBox shulker = (ShulkerBox) ((BlockStateMeta) e.getCurrentItem().getItemMeta()).getBlockState();
            for (ItemStack si : shulker.getInventory().getContents()) {
                if (si == null) {
                    continue;
                }

                if (si.getType() == item) {
                    this.getLogger().info("Cancelling event because of SHULKER_BOX_CHECK");
                    e.setCancelled(true);
                    player.sendMessage(CancelledShulker);
                }
            }

        }
    }
    // WE IGNORE INVENTORY DRAG EVENT <- I tested drag event, it isn't necessary anyway.

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
