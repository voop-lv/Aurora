package com.zenya.aurora.storage;

import com.cryptomorin.xseries.XBiome;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ParticleFileCache {
    public static ParticleFileCache INSTANCE = new ParticleFileCache();
    private HashMap<XBiome, List<ParticleFile>> particleCacheMap = new HashMap<>();

    public ParticleFileCache() {
        for(String filename : ParticleFileManager.INSTANCE.getFiles()) {
            ParticleFile particleFile = ParticleFileManager.INSTANCE.getClass(filename);
            if(particleFile.getSpawning() == null || particleFile.getSpawning().getBiomes() == null || particleFile.getSpawning().getBiomes().length == 0) continue;

            for(String biome : particleFile.getSpawning().getBiomes()) {
                try {
                    registerClass(XBiome.valueOf(biome.toUpperCase()), particleFile);
                } catch(Exception exc) {
                    Logger.logError("Error loading biome %s from particle %s", biome.toUpperCase(), particleFile.getName());
                }
            }
        }
    }

    public List<ParticleFile> getClass(XBiome biome) {
        return particleCacheMap.getOrDefault(biome, new ArrayList<>());
    }

    public Set<XBiome> getBiomes() {
        return particleCacheMap.keySet();
    }

    public void registerClass(XBiome biome, ParticleFile particleFile) {
        particleCacheMap.computeIfAbsent(biome, k -> new ArrayList<>()).add(particleFile);
    }

    public void unregisterFile(String name) {
        particleCacheMap.remove(name);
    }

    public static void reload() {
        ParticleFileManager.INSTANCE.reload();
        INSTANCE = new ParticleFileCache();
    }
}
