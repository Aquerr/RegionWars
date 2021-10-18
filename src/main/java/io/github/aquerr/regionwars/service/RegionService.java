package io.github.aquerr.regionwars.service;

import io.github.aquerr.regionwars.model.Region;

import java.util.Optional;

public interface RegionService
{
    Optional<Region> getRegion(String name);

    void saveRegion(Region region);

    void deleteRegion(String name);
}
