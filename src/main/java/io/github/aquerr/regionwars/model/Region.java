package io.github.aquerr.regionwars.model;

import java.util.Objects;
import java.util.UUID;

public class Region
{
    private String name;
    private UUID worldUUID;
    private Vector3i firstPosition;
    private Vector3i secondPosition;
    private boolean active;

    public Region(String name, UUID worldUUID, Vector3i firstPosition, Vector3i secondPosition, boolean active)
    {
        this.name = name;
        this.worldUUID = worldUUID;
        this.firstPosition = firstPosition;
        this.secondPosition = secondPosition;
        this.active = active;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public UUID getWorldUUID()
    {
        return worldUUID;
    }

    public void setWorldUUID(UUID worldUUID)
    {
        this.worldUUID = worldUUID;
    }

    public Vector3i getFirstPosition()
    {
        return firstPosition;
    }

    public void setFirstPosition(Vector3i firstPosition)
    {
        this.firstPosition = firstPosition;
    }

    public Vector3i getSecondPosition()
    {
        return secondPosition;
    }

    public void setSecondPosition(Vector3i secondPosition)
    {
        this.secondPosition = secondPosition;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    @Override
    public String toString()
    {
        return "Region{" +
                "name='" + name + '\'' +
                ", worldUUID=" + worldUUID +
                ", firstPosition=" + firstPosition +
                ", secondPosition=" + secondPosition +
                ", active=" + active +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Region region = (Region) o;
        return active == region.active && Objects.equals(name, region.name) && Objects.equals(worldUUID, region.worldUUID) && Objects.equals(firstPosition, region.firstPosition) && Objects.equals(secondPosition, region.secondPosition);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, worldUUID, firstPosition, secondPosition, active);
    }
}
