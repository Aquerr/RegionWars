package io.github.aquerr.regionwars.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Team
{
    private String name;
    private Set<UUID> members;

    public Team(String name)
    {
        this.name = name;
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
}
