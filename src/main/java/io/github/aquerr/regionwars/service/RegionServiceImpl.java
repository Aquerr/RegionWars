package io.github.aquerr.regionwars.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.github.aquerr.regionwars.exception.RegionNotFoundException;
import io.github.aquerr.regionwars.model.Region;
import io.github.aquerr.regionwars.storage.RegionStorage;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RegionServiceImpl implements RegionService
{
    private final RegionStorage regionStorage;
    private final LoadingCache<String, Region> nameRegionCache;

    public RegionServiceImpl(RegionStorage regionStorage)
    {
        this.regionStorage = regionStorage;
        this.nameRegionCache = CacheBuilder.newBuilder()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .build(new CacheLoader<>()
                {
                    @Override
                    public Region load(String key) throws Exception
                    {
                        return Optional.ofNullable(regionStorage.getRegion(key)).orElseThrow(() -> new RegionNotFoundException("No such region in db."));
                    }
                });

        this.regionStorage.getRegions().forEach(region -> this.nameRegionCache.put(region.getName().toLowerCase(), region));
    }

    @Override
    public List<Region> getRegions()
    {
        return List.copyOf(this.nameRegionCache.asMap().values());
    }

    @Override
    public Optional<Region> getRegion(String name)
    {
        Region region = null;
        try
        {
            region = this.nameRegionCache.get(name.toLowerCase());
        }
        catch (ExecutionException e)
        {
            if (!(e.getCause() instanceof RegionNotFoundException))
                e.printStackTrace();
        }
        return Optional.ofNullable(region);
    }

    @Override
    public void saveRegion(Region region)
    {
        this.nameRegionCache.put(region.getName().toLowerCase(), region);
        CompletableFuture.runAsync(() -> this.regionStorage.saveRegion(region));
    }

    @Override
    public void deleteRegion(String name)
    {
        Region region = this.nameRegionCache.getIfPresent(name.toLowerCase());

        if (region != null)
        {
            this.nameRegionCache.invalidate(name.toLowerCase());
        }

        CompletableFuture.runAsync(() -> this.regionStorage.deleteRegion(name));
    }
}
