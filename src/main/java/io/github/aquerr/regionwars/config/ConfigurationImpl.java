package io.github.aquerr.regionwars.config;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigurationImpl implements Configuration
{
    private final FileConfiguration fileConfiguration;

    private String languageFileName;

    public ConfigurationImpl(FileConfiguration fileConfiguration)
    {
        this.fileConfiguration = fileConfiguration;
    }

    @Override
    public void reload()
    {
        this.languageFileName = this.fileConfiguration.getString("language-file");
    }

    @Override
    public String getLanguageFileName()
    {
        return this.languageFileName;
    }
}
