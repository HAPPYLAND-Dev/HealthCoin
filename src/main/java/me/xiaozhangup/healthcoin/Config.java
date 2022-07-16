package me.xiaozhangup.healthcoin;

import java.util.List;

import static me.xiaozhangup.healthcoin.HealthCoin.plugin;

public class Config {

    public static String COIN_ACTION;
    public static String COIN_HOLOGRAM;
    public static String COIN_FULL;
    public static Double DAILY_MAX;

    public static void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        COIN_ACTION = plugin.getConfig().getString("CoinActionBar");
        COIN_HOLOGRAM = plugin.getConfig().getString("CoinHologram");
        COIN_FULL = plugin.getConfig().getString("CoinFull");
        DAILY_MAX = plugin.getConfig().getDouble("DailyMax");
    }

}
