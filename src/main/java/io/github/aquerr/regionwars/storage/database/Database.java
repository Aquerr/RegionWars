package io.github.aquerr.regionwars.storage.database;

import io.github.aquerr.regionwars.storage.StorageType;

import java.sql.Connection;
import java.sql.SQLException;

public interface Database
{
    void init();

    Connection getConnection() throws SQLException;

    StorageType getStorageType();
}
