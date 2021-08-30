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
    return this.chunkX;
  }

  public int getZ() {
    return this.chunkZ;
  }

  public World getWorld() {
    return this.world;
  }

  public int[] getChunkCoords() {
    return new int[]{chunkX, chunkZ};
  }

  public Location toLocation(int worldCoordX, double y, int worldCoordZ) {
    Objects.requireNonNull(world, "World world cannot be null for ChunkContainer");
    Objects.requireNonNull(chunkX, "Integer chunkX cannot be null for ChunkContainer");
    Objects.requireNonNull(chunkZ, "Integer chunkZ cannot be null for ChunkContainer");
    return new Location(world, LocationTools.toWorldCoord(chunkX, worldCoordX), y, LocationTools.toWorldCoord(chunkZ, worldCoordZ));
  }

  public Chunk toChunk() {
    Objects.requireNonNull(world, "World world cannot be null for ChunkContainer");
    Objects.requireNonNull(chunkX, "Integer chunkX cannot be null for ChunkContainer");
    Objects.requireNonNull(chunkZ, "Integer chunkZ cannot be null for ChunkContainer");
    return world.getChunkAt(chunkX, chunkZ);
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
    this.chunkX = LocationTools.toChunkCoord(coordX);
    this.chunkZ = LocationTools.toChunkCoord(coordZ);
    return this;
  }

  public ChunkContainer fromLocation(Location loc) {
    this.world = loc.getWorld();
    this.chunkX = LocationTools.toChunkCoord(loc.getX());
    this.chunkZ = LocationTools.toChunkCoord(loc.getZ());
    return this;
  }

  public ChunkContainer fromChunk(Chunk c) {
    this.world = c.getWorld();
    this.chunkX = c.getX();
    this.chunkZ = c.getZ();
    return this;
  }
}
