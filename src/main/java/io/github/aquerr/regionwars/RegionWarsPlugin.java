package io.github.aquerr.regionwars;

import io.github.aquerr.regionwars.command.RegionWarsCommandManager;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class RegionWarsPlugin extends JavaPlugin
{
    public static final String PLUGIN_PREFIX = ChatColor.GOLD + "[RW] " + ChatColor.RESET;
    public static final String ERROR_PREFIX = ChatColor.DARK_RED + "[RW] ";

    @Override
    public void onEnable()
    {
        // Plugin startup logic

        setupCommandSystem();
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic

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
