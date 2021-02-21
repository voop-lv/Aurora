package com.zenya.aurora.storage;

import com.cryptomorin.xseries.XBiome;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.util.LogUtils;
import org.apache.commons.lang.ArrayUtils;

import java.util.HashMap;
import java.util.Set;

public class ParticleFileCache {
    private static ParticleFileCache particleFileCache;
    private static ParticleFileManager particleFileManager;
    private HashMap<XBiome, ParticleFile[]> particleCacheMap = new HashMap<>();

    public ParticleFileCache() {
        particleFileManager = ParticleFileManager.getInstance();

        for(String filename : particleFileManager.getFiles()) {
            ParticleFile particleFile = particleFileManager.getClass(filename);
            if(particleFile.getSpawning() == null || particleFile.getSpawning().getBiomes() == null || particleFile.getSpawning().getBiomes().length == 0) continue;

            for(String biome : particleFile.getSpawning().getBiomes()) {
                try {
                    registerClass(XBiome.valueOf(biome.toUpperCase()), particleFile);
                } catch(Exception exc) {
                    LogUtils.logError("Error loading biome %s from particle %s", biome.toUpperCase(), particleFile.getName());
                }

            }
        }
    }

    public ParticleFile[] getClass(XBiome biome) {
        return particleCacheMap.get(biome);
    }

    public Set<XBiome> getBiomes() {
        return particleCacheMap.keySet();
    }

    public void registerClass(XBiome biome, ParticleFile particleFile) {
        ParticleFile classes[];

        if(getClass(biome) != null) {
            classes = (ParticleFile[]) ArrayUtils.addAll(getClass(biome), new ParticleFile[]{particleFile});
        } else {
            classes = new ParticleFile[]{particleFile};
        }
        particleCacheMap.put(biome, classes);
    }

    public void unregisterFile(String name) {
        particleCacheMap.remove(name);
    }

    public static void reload() {
        ParticleFileManager.reload();
        particleFileCache = null;
        getInstance();
    }

    public static ParticleFileCache getInstance() {
        if(particleFileCache == null) {
            particleFileCache = new ParticleFileCache();
        }
        return particleFileCache;
    }
}
