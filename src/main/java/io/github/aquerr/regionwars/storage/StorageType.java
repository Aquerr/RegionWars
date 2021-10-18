package io.github.aquerr.regionwars.storage;

import java.util.Arrays;
import java.util.Optional;

public enum StorageType
{
    SQLITE("sqlite", true);

    private final String name;
    private final boolean isDatabase;

    StorageType(String name, boolean isDatabase)
    {
        this.name = name;
        this.isDatabase = isDatabase;
    }

    public String getName()
    {
        return this.name;
    }

    public boolean isDatabase()
    {
        return this.isDatabase;
    }

    public static Optional<StorageType> findByName(final String name)
    {
        return Arrays.stream(values())
                .filter(storageType -> storageType.getName().equalsIgnoreCase(name))
                .findFirst();
    }
}
