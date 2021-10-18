package io.github.aquerr.regionwars.config;

import io.github.aquerr.regionwars.storage.StorageType;

public interface Configuration
{
    void reload();

    String getLanguageFileName();

    StorageType getSelectedStorageType();

    String getDatabaseName();

    String getDatabaseUrl();

    String getDatabaseUsername();

    String getDatabasePassword();
}
