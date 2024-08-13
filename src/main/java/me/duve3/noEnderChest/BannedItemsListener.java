package me.duve3.noEnderChest;

import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.Objects;

public class BannedItemsListener implements Listener {
    private final NoEnderChest main;

    public BannedItemsListener(NoEnderChest main) {
        this.main = main;
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
                ShulkerBox shulker = (ShulkerBox) ((BlockStateMeta) Objects.requireNonNull(Objects.requireNonNull(e.getCurrentItem()).getItemMeta())).getBlockState();
                for (ItemStack si : shulker.getInventory().getContents()) {
                    if (si == null) {
                        continue;
                    }

                    if (si.getType() == item) {
                        this.main.getLogger().info("Cancelling event because of SHULKER_BOX_CHECK");
                        e.setCancelled(true);
                        player.sendMessage(this.main.CancelledShulker);
                    }
                }

            }
        }
        // WE IGNORE INVENTORY DRAG EVENT <- I tested drag event, it isn't necessary anyway.

        @EventHandler()
        public void onItemMove(InventoryClickEvent e) {
            final Player player = (Player) e.getWhoClicked();

            for (Material item : this.main.bannedItems) {
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
                        this.main.getLogger().info("Cancelling event because of DEFAULT CHECK");
                        e.setCancelled(true);
                        player.sendMessage(this.main.CancelledMessage);
                    }
                    if (e.isCancelled()) {
                        return;
                    }
    
                    if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                        shulkerBoxCheck(e.getCurrentItem().getType(), item, e, player);
                        if (e.getCurrentItem().getType() == item) {
                            this.main.getLogger().info("Cancelling event because of MOVE_TO_OTHER_INVENTORY");
                            e.setCancelled(true);
                            player.sendMessage(this.main.CancelledMessage);
                        }
                    }
                    if (e.isCancelled()) {
                        return;
                    }
    
                    if (e.getAction() == InventoryAction.HOTBAR_SWAP) {
                        shulkerBoxCheck(e.getCurrentItem().getType(), item, e, player);
                        this.main.getLogger().info("Cancelling event because of HOTBAR_SWAP CHECK");
                        e.setCancelled(true);
                        player.sendMessage(this.main.CancelledMessage);
                    }
                    if (e.isCancelled()) {
                        return;
                    }
                }
            }
        }
}
