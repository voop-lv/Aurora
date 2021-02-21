package com.zenya.aurora.file;

import lombok.Getter;

public class ParticleFile {
    @Getter private String name;
    @Getter private boolean enabled;
    @Getter private Spawning spawning;
    @Getter private Particle particle;
    @Getter private Properties properties;

    public static class Spawning {
        @Getter private String[] biomes;
        @Getter private double spawnDistance;
        @Getter private float randMultiplier;
        @Getter private int minY;
        @Getter private int maxY;
        @Getter private boolean shuffleLocations;
    }

    public static class Particle {
        @Getter private String particleName;
        @Getter private String particleType;
        @Getter private int maxCount;
        @Getter private boolean enableLighting;
    }

    public static class Properties {
        @Getter private double rate;
        @Getter private int update;
        @Getter private long duration;
        @Getter private double length;
        @Getter private double radius;
        @Getter private double waveCycles;
        @Getter private double waveAmplitude;
        @Getter private double rotationAngle;
        @Getter private char rotationAxis;
    }
}
