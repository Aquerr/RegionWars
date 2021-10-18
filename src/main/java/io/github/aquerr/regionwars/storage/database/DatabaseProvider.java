package io.github.aquerr.regionwars.storage.database;

import io.github.aquerr.regionwars.RegionWarsPlugin;
import io.github.aquerr.regionwars.storage.StorageType;
import io.github.aquerr.regionwars.storage.sqlite.SqliteDatabase;

public class DatabaseProvider
{
    private static final DatabaseProvider INSTANCE = new DatabaseProvider();

    public static DatabaseProvider getInstance()
    {
        return INSTANCE;
    }

    public Database provide(StorageType storageType)
    {
        Database database;
        switch (storageType)
        {
            case SQLITE -> database = new SqliteDatabase(RegionWarsPlugin.getInstance());
            default -> database = new SqliteDatabase(RegionWarsPlugin.getInstance());
        }
        return database;
    }
}
