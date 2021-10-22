package io.github.aquerr.regionwars.storage;

import io.github.aquerr.regionwars.model.Region;

public interface StorageManager
{
    Region getRegion(String name);

    void saveRegion(Region region);

    void deleteRegion(String name);

    RegionStorage getRegionStorage();

    TeamStorage getTeamStorage();
}
