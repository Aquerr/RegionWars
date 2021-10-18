package io.github.aquerr.regionwars.config;

import io.github.aquerr.regionwars.storage.StorageType;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigurationImpl implements Configuration
{
    private final FileConfiguration fileConfiguration;

    private String languageFileName;
    private StorageType selectedStorageType;

    private String databaseUsername;
    private String databasePassword;
    private String databaseName;
    private String databaseUrl;

    public ConfigurationImpl(FileConfiguration fileConfiguration)
    {
        this.fileConfiguration = fileConfiguration;
    }

    @Override
    public void reload()
    {
        this.languageFileName = this.fileConfiguration.getString("language-file");
        this.selectedStorageType = StorageType.findByName(this.fileConfiguration.getString("storage.type"))
                .orElseThrow(() -> new IllegalArgumentException("Selected storage type is invalid!"));

        this.databaseUsername = this.fileConfiguration.getString("storage.username");
        this.databasePassword = this.fileConfiguration.getString("storage.password");
        this.databaseName = this.fileConfiguration.getString("storage.database-name");
        this.databaseUrl = this.fileConfiguration.getString("storage.database-url");
    }


    @Override
    public String getLanguageFileName()
    {
        return this.languageFileName;
    }

    @Override
    public StorageType getSelectedStorageType()
    {
        return this.selectedStorageType;
    }

    @Override
    public String getDatabaseName()
    {
        return this.databaseName;
    }

    @Override
    public String getDatabaseUrl()
    {
        return this.databaseUrl;
    }

    @Override
    public String getDatabaseUsername()
    {
        return databaseUsername;
    }

    @Override
    public String getDatabasePassword()
    {
        return databasePassword;
    }
}
