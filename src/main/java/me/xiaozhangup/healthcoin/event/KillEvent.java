package me.xiaozhangup.healthcoin.event;

import me.xiaozhangup.healthcoin.Config;
import me.xiaozhangup.healthcoin.HealthCoin;
import me.xiaozhangup.healthcoin.tinylib.ItemNameConsumer;
import me.xiaozhangup.healthcoin.tinylib.Skull;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class KillEvent implements Listener {

    public static Map<UUID, Double> dailyCoin = new HashMap<>();


    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private final ItemStack ball = Skull.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTA5ODcwNTk0OWZjNGM0YjI4ZWI4MzQ3NDVjNTc2YTFjMzVkOGQ3MDIyMGM5YTBiNTQyZGVmOGY0NzA5Nzc4YyJ9fX0=");
    private final String COIN_NAME = "Coin|";

    @EventHandler
    public void onPlayerKill(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        EntityType entityType = e.getEntityType();
        if (entityType != EntityType.PLAYER && entity.getKiller() != null) {
            double point = entity.getMaxHealth() / 10 - 1;
            double coinValue;
            if (point < 0.00) coinValue = getRandom(0, entity.getMaxHealth() / 10);
            else coinValue = getRandom(point, entity.getMaxHealth() / 10);

            AtomicBoolean flag = new AtomicBoolean(false);
            for(Item item : entity.getWorld().getNearbyEntitiesByType(Item.class, entity.getLocation(), 3)) {
                ItemStack itemStack = item.getItemStack();
                ItemNameConsumer.check(itemStack, (meta, name) -> {
                    if(name.startsWith(COIN_NAME)) {
                        String newCoin = decimalFormat.format(Double.parseDouble(name.replace(COIN_NAME, "")) + coinValue);
                        meta.setDisplayName(COIN_NAME + newCoin);
                        itemStack.setItemMeta(meta);
                        item.setCustomName(Skull.addColor(Config.COIN_HOLOGRAM.replace("{coin}", newCoin)));
                        flag.set(true);
                    }
                });

                if(flag.get()) return;
            }

            String coin = decimalFormat.format(coinValue);
            Item item = (Item) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.DROPPED_ITEM);
            ItemStack theBall = ball.clone();
            ItemMeta itemMeta = theBall.getItemMeta();
            itemMeta.setDisplayName(COIN_NAME + coin);
            theBall.setItemMeta(itemMeta);

            item.setItemStack(theBall);
            item.setCanMobPickup(false);
            item.setCustomNameVisible(true);
            item.setPickupDelay(8);
            item.setThrower(entity.getUniqueId());
            item.setOwner(entity.getKiller().getUniqueId());
            item.setCustomName(Skull.addColor(Config.COIN_HOLOGRAM.replace("{coin}", coin)));

        }
    }

    @EventHandler
    public void onPlayerPickup(PlayerAttemptPickupItemEvent e) {
        ItemStack itemStack = e.getItem().getItemStack();
        if(itemStack.getType() != Material.PLAYER_HEAD) return;

        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta.hasDisplayName()) {
                String itemName = itemMeta.getDisplayName();
                if (!itemName.startsWith(COIN_NAME)) {
                    return;
                }
                e.setCancelled(true);

                Player player = e.getPlayer();
                UUID uuid = player.getUniqueId();

                double playerDailyCoin = dailyCoin.getOrDefault(uuid, 0.0);
                if (playerDailyCoin <= Config.DAILY_MAX) {
                    String coin = itemName.replace(COIN_NAME, "");
                    player.sendActionBar(Skull.addColor(Config.COIN_ACTION.replace("{coin}", coin)));
                    e.getItem().remove();
                    HealthCoin.getEconomy().depositPlayer(player, Double.parseDouble(coin));
                    dailyCoin.put(uuid , playerDailyCoin + Double.parseDouble(coin));
                } else {
                    player.sendActionBar(Skull.addColor(Config.COIN_FULL));
                    e.getItem().remove();
                }
            }
        }
    }

    @EventHandler
    public void onHopperWork(InventoryPickupItemEvent e) {
        if(e.getInventory().getType() != InventoryType.HOPPER) return;

        ItemStack itemStack = e.getItem().getItemStack();
        ItemNameConsumer.check(itemStack, (meta, name) -> {
            if (itemStack.getType() != Material.PLAYER_HEAD) {
                return;
            }
            if (!name.startsWith(COIN_NAME)) {
                return;
            }
            e.setCancelled(true);
        });
    }

    public static int getRandom(int start, int end) {
        return (int) (Math.random() * (end - start + 1) + start);
    }

    public static double getRandom(final double start, final double end) {
        return (Math.random() * (end - start + 1) + start);
    }

}
