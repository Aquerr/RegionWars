package io.github.aquerr.regionwars.storage.sqlite;

import io.github.aquerr.regionwars.model.Region;
import io.github.aquerr.regionwars.model.Vector3i;
import io.github.aquerr.regionwars.storage.RegionStorage;
import io.github.aquerr.regionwars.storage.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SqliteRegionStorage implements RegionStorage
{
    private static final String SELECT_ALL_REGIONS = "SELECT * FROM region";
    private static final String SELECT_REGION_WHERE_NAME = "SELECT * FROM region WHERE name = ?";
    private static final String INSERT_REGION = "INSERT INTO region (name, first_position, second_position, active) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_REGION_WHERE_NAME = "UPDATE region SET name=?, first_position=?, second_position=?, active=? WHERE name=?";
    private static final String DELETE_REGION_WHERE_NAME = "DELETE FROM region WHERE name = ?";

    private final Database database;

    public SqliteRegionStorage(Database database)
    {
        this.database = database;
    }

    @Override
    public List<Region> getRegions()
    {
        final List<Region> regions = new ArrayList<>();
        try(Connection connection = this.database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_REGIONS))
        {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                String name = resultSet.getString("name");
                UUID worldUUID = UUID.fromString(resultSet.getString("world_uuid"));
                String firstPosition = resultSet.getString("first_position");
                String secondPosition = resultSet.getString("second_position");
                boolean active = resultSet.getBoolean("active");
                Region region = new Region(name, worldUUID, Vector3i.from(firstPosition), Vector3i.from(secondPosition), active);
                regions.add(region);
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }
        return regions;
    }

    @Override
    public Region getRegion(String name)
    {
        try(Connection connection = this.database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_REGION_WHERE_NAME))
        {
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                name = resultSet.getString("name");
                if (name == null)
                    return null;

                UUID worldUUID = UUID.fromString(resultSet.getString("world_uuid"));
                String firstPosition = resultSet.getString("first_position");
                String secondPosition = resultSet.getString("second_position");
                boolean active = resultSet.getBoolean("active");
                return new Region(name, worldUUID, Vector3i.from(firstPosition), Vector3i.from(secondPosition), active);
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveRegion(Region region)
    {
        try(Connection connection = this.database.getConnection())
        {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_REGION_WHERE_NAME);
            preparedStatement.setString(1, region.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean isUpdate = false;
            if (resultSet.next())
            {
                isUpdate = true;
            }
            resultSet.close();
            preparedStatement.close();

            String query = isUpdate ? UPDATE_REGION_WHERE_NAME : INSERT_REGION;
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, region.getName());
            preparedStatement.setString(2, region.getFirstPosition().toString());
            preparedStatement.setString(3, region.getSecondPosition().toString());
            preparedStatement.setBoolean(4, region.isActive());

            if (isUpdate)
                preparedStatement.setString(5, region.getName());

            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }
    }

    @Override
    public void deleteRegion(String name)
    {
        try(Connection connection = this.database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_REGION_WHERE_NAME))
        {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }
    }
}
