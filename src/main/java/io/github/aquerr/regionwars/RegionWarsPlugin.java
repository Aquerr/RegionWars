package io.github.aquerr.regionwars;

import io.github.aquerr.regionwars.command.RegionWarsCommandManager;
import io.github.aquerr.regionwars.config.Configuration;
import io.github.aquerr.regionwars.config.ConfigurationImpl;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class RegionWarsPlugin extends JavaPlugin
{
    public static final String PLUGIN_PREFIX = ChatColor.GOLD + "[RW] " + ChatColor.RESET;
    public static final String ERROR_PREFIX = ChatColor.DARK_RED + "[RW] ";

    private Configuration configuration;

    @Override
    public void onEnable()
    {
        // Plugin startup logic

        setupConfiguration();
        setupCommandSystem();
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic

    }

    public Configuration getConfiguration()
    {
        return configuration;
    }

    private void setupConfiguration()
    {
        saveDefaultConfig();
        this.configuration = new ConfigurationImpl(getConfig());
        this.configuration.reload();
    }

    private void setupCommandSystem()
    {
        RegionWarsCommandManager regionWarsCommandManager = new RegionWarsCommandManager(this);
        regionWarsCommandManager.init();
        PluginCommand regionWarsCommand = getCommand("RegionWars");
        regionWarsCommand.setTabCompleter(regionWarsCommandManager);
        regionWarsCommand.setExecutor(regionWarsCommandManager);
    }
}
