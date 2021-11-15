package io.github.aquerr.regionwars.storage.sqlite;

import io.github.aquerr.regionwars.model.Team;
import io.github.aquerr.regionwars.storage.TeamStorage;
import io.github.aquerr.regionwars.storage.database.Database;
import net.md_5.bungee.api.ChatColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SqliteTeamStorage implements TeamStorage
{
    private static final String SELECT_ALL_TEAMS = "SELECT * FROM team";
    private static final String SELECT_TEAM_WHERE_NAME = "SELECT * FROM team WHERE name = ?";
    private static final String SELECT_TEAM_MEMBERS_WHERE_TEAM_NAME = "SELECT player_uuid FROM team_member WHERE team_name = ?";
    private static final String INSERT_TEAM = "INSERT INTO team (name, color) VALUES (?, ?)";
    private static final String UPDATE_TEAM_WHERE_NAME = "UPDATE team SET name=?, color=? WHERE name=?";
    private static final String DELETE_TEAM_WHERE_NAME = "DELETE FROM team WHERE name = ?";
    private static final String DELETE_TEAM_MEMBER_WHERE_PLAYER_UUID = "DELETE FROM team_member WHERE player_uuid = ?";
    private static final String INSERT_TEAM_MEMBER = "INSERT INTO team_member (team_name, player_uuid) VALUES (?, ?)";

    private final Database database;

    public SqliteTeamStorage(final Database database)
    {
        this.database = database;
    }

    @Override
    public List<Team> getTeams()
    {
        final List<Team> teams = new ArrayList<>();
        try(Connection connection = this.database.getConnection())
        {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_TEAMS);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                String teamName = resultSet.getString("name");
                String teamColor = resultSet.getString("color");
                Team team = new Team(teamName, ChatColor.of(teamColor));
                getTeamMembers(connection, teamName).forEach(team::addMember);
                teams.add(team);
            }
            preparedStatement.close();
            resultSet.close();
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }
        return teams;
    }

    @Override
    public Team getTeam(String name)
    {
        try(Connection connection = this.database.getConnection())
        {
            String teamName = null;
            ChatColor color = ChatColor.GREEN;
            try(PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TEAM_WHERE_NAME))
            {
                preparedStatement.setString(1, name);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next())
                {
                    name = resultSet.getString("name");
                    color = ChatColor.of(resultSet.getString("color"));
                    teamName = name;
                }
            }
            if (teamName == null)
                return null;

            Team team = new Team(teamName, color);
            getTeamMembers(connection, teamName).forEach(team::addMember);
            return team;
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveTeam(Team team)
    {
        try(Connection connection = this.database.getConnection())
        {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TEAM_WHERE_NAME);
            preparedStatement.setString(1, team.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean isUpdate = false;
            if (resultSet.next())
            {
                isUpdate = true;
            }
            resultSet.close();
            preparedStatement.close();

            String query = isUpdate ? UPDATE_TEAM_WHERE_NAME : INSERT_TEAM;
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, team.getName());
            preparedStatement.setString(2, team.getColor().getName());

            if (isUpdate)
                preparedStatement.setString(3, team.getName());

            preparedStatement.executeUpdate();
            preparedStatement.close();

            saveTeamMembers(connection, team);
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }
    }

    @Override
    public void deleteTeam(String name)
    {
        try(Connection connection = this.database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TEAM_WHERE_NAME))
        {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }
    }

    private Set<UUID> getTeamMembers(Connection connection, String teamName)
    {
        final Set<UUID> members = new HashSet<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TEAM_MEMBERS_WHERE_TEAM_NAME))
        {
            preparedStatement.setString(1, teamName);
            try(ResultSet resultSet = preparedStatement.executeQuery())
            {
                while (resultSet.next())
                {
                    UUID memberUUID = UUID.fromString(resultSet.getString(1));
                    members.add(memberUUID);
                }
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }
        return members;
    }

    private void saveTeamMembers(final Connection connection, final Team team) throws SQLException
    {
        final Set<UUID> existingTeamMembers = getTeamMembers(connection, team.getName());
        final List<UUID> teamMembersToRemove = existingTeamMembers.stream().filter(member -> !team.getMembers().contains(member)).toList();
        final List<UUID> teamMembersToAdd = team.getMembers().stream().filter(member -> !existingTeamMembers.contains(member)).toList();

        if(!teamMembersToRemove.isEmpty())
        {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TEAM_MEMBER_WHERE_PLAYER_UUID);
            for (final UUID teamMemberToRemove : teamMembersToRemove)
            {
                preparedStatement.setString(1, teamMemberToRemove.toString());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            preparedStatement.close();
        }

        if (!teamMembersToAdd.isEmpty())
        {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TEAM_MEMBER);
            for (final UUID teamMemberToAdd : teamMembersToAdd)
            {
                preparedStatement.setString(1, team.getName());
                preparedStatement.setString(2, teamMemberToAdd.toString());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            preparedStatement.close();
        }
    }
}
