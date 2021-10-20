package io.github.aquerr.regionwars;

import io.github.aquerr.regionwars.command.RegionWarsCommandManager;
import io.github.aquerr.regionwars.config.Configuration;
import io.github.aquerr.regionwars.config.ConfigurationImpl;
import io.github.aquerr.regionwars.service.RegionService;
import io.github.aquerr.regionwars.service.RegionServiceImpl;
import io.github.aquerr.regionwars.storage.StorageManager;
import io.github.aquerr.regionwars.storage.StorageManagerImpl;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class RegionWarsPlugin extends JavaPlugin
{
    private static RegionWarsPlugin INSTANCE;

    public static final String PLUGIN_PREFIX = ChatColor.GOLD + "[RW] " + ChatColor.RESET;
    public static final String ERROR_PREFIX = ChatColor.DARK_RED + "[RW] ";

    private Configuration configuration;
    private RegionService regionService;
    private StorageManager storageManager;

    public static RegionWarsPlugin getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void onEnable()
    {
        INSTANCE = this;

        // Plugin startup logic

        setupConfiguration();
        setupStorage();
        setupServices();
        setupCommandSystem();
    }

    private void setupServices()
    {
        this.regionService = new RegionServiceImpl(this.storageManager.getRegionStorage());
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

    public RegionService getRegionService()
    {
        return regionService;
    }

    private void setupStorage()
    {
        this.storageManager = new StorageManagerImpl(this);
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
