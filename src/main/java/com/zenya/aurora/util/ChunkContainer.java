package com.zenya.aurora.util;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import java.util.Objects;

public class ChunkContainer {

    private World world;
    private Integer chunkX;
    private Integer chunkZ;

    public ChunkContainer() {
        this(null, null);
    }

    public ChunkContainer(Integer chunkX, Integer chunkZ) {
        this(null, chunkX, chunkZ);
    }

    public ChunkContainer(World world, Integer chunkX, Integer chunkZ) {
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public ChunkContainer getContainer() {
        return this;
    }

    public int getX() {
        return chunkX;
    }

    public int getZ() {
        return chunkZ;
    }

    public World getWorld() {
        return world;
    }

    public int[] getChunkCoords() {
        return new int[]{chunkX, chunkZ};
    }

    public Location toLocation(int worldCoordX, double y, int worldCoordZ) {
        check();
        return new Location(world, LocationTools.toWorldCoord(chunkX, worldCoordX), y, LocationTools.toWorldCoord(chunkZ, worldCoordZ));
    }

    public Chunk toChunk() {
        check();
        return world.getChunkAt(chunkX, chunkZ);
    }

    private void check() {
        Objects.requireNonNull(world, "World world cannot be null for ChunkContainer");
        Objects.requireNonNull(chunkX, "Integer chunkX cannot be null for ChunkContainer");
        Objects.requireNonNull(chunkZ, "Integer chunkZ cannot be null for ChunkContainer");
    }

    public ChunkContainer setX(int chunkX) {
        this.chunkX = chunkX;
        return this;
    }

    public ChunkContainer setZ(int chunkZ) {
        this.chunkZ = chunkZ;
        return this;
    }

    public ChunkContainer withWorld(String world) {
        return withWorld(Bukkit.getWorld(world));
    }

    public ChunkContainer withWorld(World world) {
        this.world = world;
        return this;
    }

    public ChunkContainer fromLocation(double coordX, double coordZ) {
        chunkX = LocationTools.toChunkCoord(coordX);
        chunkZ = LocationTools.toChunkCoord(coordZ);
        return this;
    }

    public ChunkContainer fromLocation(Location loc) {
        world = loc.getWorld();
        chunkX = LocationTools.toChunkCoord(loc.getX());
        chunkZ = LocationTools.toChunkCoord(loc.getZ());
        return this;
    }

    public ChunkContainer fromChunk(Chunk c) {
        world = c.getWorld();
        chunkX = c.getX();
        chunkZ = c.getZ();
        return this;
    }
}
