package io.github.aquerr.regionwars.storage;

import io.github.aquerr.regionwars.RegionWarsPlugin;
import io.github.aquerr.regionwars.model.Region;
import io.github.aquerr.regionwars.storage.database.Database;
import io.github.aquerr.regionwars.storage.database.DatabaseProvider;
import io.github.aquerr.regionwars.storage.sqlite.SqliteRegionStorage;
import io.github.aquerr.regionwars.storage.sqlite.SqliteTeamStorage;

public class StorageManagerImpl implements StorageManager
{
    private final RegionWarsPlugin plugin;
    private final RegionStorage regionStorage;
    private final TeamStorage teamStorage;

    public StorageManagerImpl(RegionWarsPlugin plugin)
    {
        this.plugin = plugin;
        StorageType storageType = plugin.getConfiguration().getSelectedStorageType();
        if (storageType.isDatabase())
        {
            Database database = DatabaseProvider.getInstance().provide(plugin.getConfiguration().getSelectedStorageType());
            database.init();
            this.regionStorage = new SqliteRegionStorage(database);
            this.teamStorage = new SqliteTeamStorage(database);
        }
        else
        {
            //TODO: Logic for file-based storage
            this.regionStorage = null;
            this.teamStorage = null;
        }
    }

    @Override
    public Region getRegion(String name)
    {
        return this.regionStorage.getRegion(name);
    }

    @Override
    public void saveRegion(Region region)
    {
        this.regionStorage.saveRegion(region);
    }

    @Override
    public void deleteRegion(String name)
    {
        this.regionStorage.deleteRegion(name);
    }

    @Override
    public RegionStorage getRegionStorage()
    {
        return regionStorage;
    }

    @Override
    public TeamStorage getTeamStorage()
    {
        return this.teamStorage;
    }
}
