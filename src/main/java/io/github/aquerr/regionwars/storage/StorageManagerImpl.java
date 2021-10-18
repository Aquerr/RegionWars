package io.github.aquerr.regionwars.storage;

import io.github.aquerr.regionwars.RegionWarsPlugin;
import io.github.aquerr.regionwars.model.Region;
import io.github.aquerr.regionwars.storage.database.Database;
import io.github.aquerr.regionwars.storage.database.DatabaseProvider;
import io.github.aquerr.regionwars.storage.sqlite.SqliteRegionStorage;

public class StorageManagerImpl implements StorageManager
{
    private final RegionWarsPlugin plugin;
    private final RegionStorage regionStorage;

    public StorageManagerImpl(RegionWarsPlugin plugin)
    {
        this.plugin = plugin;
        StorageType storageType = plugin.getConfiguration().getSelectedStorageType();
        if (storageType.isDatabase())
        {
            Database database = DatabaseProvider.getInstance().provide(plugin.getConfiguration().getSelectedStorageType());
            database.init();
            this.regionStorage = new SqliteRegionStorage(database);
        }
        else
        {
            //TODO: Logic for file-based storage
            this.regionStorage = null;
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
}
