package me.xiaozhangup.healthcoin;

import me.xiaozhangup.healthcoin.event.KillEvent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class HealthCoin extends JavaPlugin {

    public static Plugin plugin;
    private static Economy econ = null;

    @Override
    public void onEnable() {
        plugin = this;
        Config.loadConfig();
        setupEconomy();
        Bukkit.getPluginManager().registerEvents(new KillEvent() , this);
        getLogger().info("HealthCoin Loaded!");
    }

    public static Economy getEconomy() {
        return econ;
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            econ = economyProvider.getProvider();
        }
        return (econ != null);
    }
}
