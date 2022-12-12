package com.zenya.aurora.storage;

import com.github.ipecter.rtu.biomelib.RTUBiomeLib;
import com.zenya.aurora.Aurora;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.util.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ParticleFileCache {

    private final Aurora plugin;
    private final ParticleFileManager particleFileManager;
    private HashMap<String, List<ParticleFile>> particleCacheMap = new HashMap<>();

    public ParticleFileCache(Aurora plugin) {
        this.plugin = plugin;
        particleFileManager = plugin.getParticleFileManager();
        for (ParticleFile particleFile : particleFileManager.getParticles()) {
            if (particleFile.getSpawning() == null || particleFile.getSpawning().getBiomes() == null || particleFile.getSpawning().getBiomes().length == 0) {
                continue;
            }
            String[] biomes = particleFile.getSpawning().getBiomes();
            if (biomes.length == 1 && biomes[0].equals("ALL")) {
                for (String biome : RTUBiomeLib.getInterface().getBiomesAsString()) {
                    registerBiome(biome.toUpperCase(), particleFile);
                }
                return;
            }
            for (String biome : particleFile.getSpawning().getBiomes()) {
                registerBiome(biome.toUpperCase(), particleFile);
            }
        }
    }

    private void registerBiome(String biomeName, ParticleFile particleFile) {
        try {
            registerClass(biomeName, particleFile);
        } catch (Exception exc) {
            Logger.logError("Error loading biome %s from particle %s", biomeName, particleFile.getName());
        }
    }

    public List<ParticleFile> getClass(String biome) {
        return particleCacheMap.getOrDefault(biome, new ArrayList<>());
    }

    public Set<String> getBiomes() {
        return particleCacheMap.keySet();
    }

    public void registerClass(String biome, ParticleFile particleFile) {
        particleCacheMap.computeIfAbsent(namespaced(biome), k -> new ArrayList<>()).add(particleFile);
    }

    public void unregisterFile(String name) {
        particleCacheMap.remove(name);
    }

    public void reload() {
        particleFileManager.reload();
        plugin.reloadParticleFileCache();
    }

    private String namespaced(String name) {
        return name.contains(":") ? name.toUpperCase() : "MINECRAFT:" + name.toUpperCase();
    }
}
