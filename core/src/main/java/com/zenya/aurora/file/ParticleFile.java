package com.zenya.aurora.file;

import com.zenya.aurora.storage.StorageFileManager;
import com.zenya.aurora.util.object.RandomNumber;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ParticleFile {
    @Getter private String name;
    @Getter private boolean enabled;
    @Getter private Spawning spawning;
    @Getter private Particle particle;
    @Getter private Properties properties;

    public static class Spawning {
        private String[] biomes;
        @Getter private double spawnDistance;
        @Getter private double randMultiplier;
        @Getter private boolean relativePlayerPosition;
        @Getter private double minY;
        @Getter private double maxY;
        @Getter private boolean shuffleLocations;

        public String[] getBiomes() {
            YAMLFile presentsFile = StorageFileManager.INSTANCE.getYAMLFile("biomes.yml");
            List<String> finalBiomes = new ArrayList<>();

            if(biomes != null && biomes.length != 0) {
                for (String biome : biomes) {
                    if (biome.toUpperCase().startsWith("PRESENT:")) {
                        String present = biome.substring(8).replaceAll(" ", "");
                        if (presentsFile.getList(present) != null && presentsFile.getList(present).size() != 0) {
                            for (String presentBiome : presentsFile.getList(present)) {
                                finalBiomes.add(presentBiome);
                            }
                        }
                    } else {
                        finalBiomes.add(biome);
                    }
                }
            }
            return finalBiomes.toArray(new String[finalBiomes.size()]);
        }
    }

    public static class Particle {
        @Getter private String particleName;
        @Getter private String particleType;
        @Getter private int maxCount;
        @Getter private boolean enableLighting;
    }

    public static class Properties {
        @Getter private RandomNumber<Double> rate;
        @Getter private RandomNumber<Integer> update;
        @Getter private RandomNumber<Long> duration;
        @Getter private RandomNumber<Double> length;
        @Getter private RandomNumber<Double> radius;
        @Getter private RandomNumber<Double> waveCycles;
        @Getter private RandomNumber<Double> waveAmplitude;
        @Getter private RandomNumber<Double> rotationAngle;
        @Getter private char rotationAxis;
    }
}
