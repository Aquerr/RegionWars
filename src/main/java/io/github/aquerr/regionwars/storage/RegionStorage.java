package io.github.aquerr.regionwars.storage;

import io.github.aquerr.regionwars.model.Region;

import java.util.List;

public interface RegionStorage
{
    List<Region> getRegions();

    Region getRegion(String name);

    void saveRegion(Region region);

    void deleteRegion(String name);
}
