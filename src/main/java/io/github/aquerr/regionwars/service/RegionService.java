package io.github.aquerr.regionwars.service;

import io.github.aquerr.regionwars.model.Region;

import java.util.List;
import java.util.Optional;

public interface RegionService
{
    List<Region> getRegions();

    Optional<Region> getRegion(String name);

    void saveRegion(Region region);

    void deleteRegion(String name);
}
