package io.github.aquerr.regionwars.storage.sqlite;

import io.github.aquerr.regionwars.RegionWarsPlugin;
import io.github.aquerr.regionwars.storage.StorageType;
import io.github.aquerr.regionwars.storage.database.AbstractDatabase;
import io.github.aquerr.regionwars.storage.database.Database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteDatabase extends AbstractDatabase implements Database
{
    private final String databasePath;

    public SqliteDatabase(RegionWarsPlugin plugin)
    {
        super(plugin);
        String databaseName = plugin.getConfiguration().getDatabaseName();
        Path databaseDir = plugin.getDataFolder().toPath().resolve("storage").resolve("sqlite");
        try
        {
            Files.createDirectories(databaseDir);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        this.databasePath = databaseDir.resolve(databaseName).toAbsolutePath() + ".db";
    }

    @Override
    protected int getDatabaseVersion() throws SQLException
    {
        try(final Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM sqlite_master WHERE type='table' AND name='version'"))
        {
            final ResultSet resultSet = preparedStatement.executeQuery();
            boolean versionTableExists = false;
            while(resultSet.next())
            {
                versionTableExists = true;
            }

            if(versionTableExists)
            {
                try(Statement statement = connection.createStatement();
                    ResultSet resultSet1 = statement.executeQuery("SELECT MAX(version) FROM version"))
                {
                    if(resultSet1.next())
                    {
                        return resultSet1.getInt(1);
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        //TODO: Add connection pool
        Connection connection = DriverManager.getConnection("jdbc:sqlite://" + this.databasePath);
        try(Statement statement = connection.createStatement())
        {
            statement.execute("PRAGMA foreign_keys = ON;");
        }
        return connection;
    }

    @Override
    public StorageType getStorageType()
    {
        return StorageType.SQLITE;
    }
}
