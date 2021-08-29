package com.zenya.aurora.storage;

import com.zenya.aurora.util.ext.ZBiome;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ParticleFileCache {

  public static ParticleFileCache INSTANCE = new ParticleFileCache();
  private HashMap<String, List<ParticleFile>> particleCacheMap = new HashMap<>();

  public ParticleFileCache() {
    for (ParticleFile particleFile : ParticleFileManager.INSTANCE.getParticles()) {
      if (particleFile.getSpawning() == null || particleFile.getSpawning().getBiomes() == null || particleFile.getSpawning().getBiomes().length == 0) {
        continue;
      }

      for (String biome : particleFile.getSpawning().getBiomes()) {
        try {
          if (ZBiome.matchZBiome(biome.toUpperCase()).equals(ZBiome.CUSTOM)) {
            registerClass(biome.toUpperCase(), particleFile);
          } else {
            registerClass(ZBiome.matchZBiome(biome).name(), particleFile);
          }
        } catch (Exception exc) {
          Logger.logError("Error loading biome %s from particle %s", biome.toUpperCase(), particleFile.getName());
        }
      }
    }
  }

  public List<ParticleFile> getClass(String biome) {
    return particleCacheMap.getOrDefault(biome, new ArrayList<>());
  }

  public Set<String> getBiomes() {
    return particleCacheMap.keySet();
  }

  public void registerClass(String biome, ParticleFile particleFile) {
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
