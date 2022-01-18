package io.github.aquerr.regionwars.model;

import net.md_5.bungee.api.ChatColor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Team
{
    private String name;
    private ChatColor color;
    private Set<UUID> members;

    public Team(String name, ChatColor color)
    {
        this.name = name;
        this.color = color;
        this.members = new HashSet<>();
    }

    public String getName()
    {
        return name;
    }

    public void addMember(final UUID playerUUID)
    {
        this.members.add(playerUUID);
    }

    public Set<UUID> getMembers()
    {
        return members;
    }

    public ChatColor getColor()
    {
        return color;
    }
}
