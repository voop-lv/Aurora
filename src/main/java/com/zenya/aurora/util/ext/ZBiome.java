/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Crypto Morin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.zenya.aurora.util.ext;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Enums;
import org.apache.commons.lang.Validate;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * <b>ZBiome</b> - Cross-version support for biome names with support for custom biomes<br>
 * Biomes: https://minecraft.gamepedia.com/Biome Biome: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/Biome.html
 *
 * @author Crypto Morin, Zenya4 (Modded)
 * @version 4.0.0 (ZBiome 1.0.0)
 * @see Biome
 */
public enum ZBiome {
  BADLANDS("MESA"),
  BEACH("BEACHES"),
  BIRCH_FOREST("BIRCH_FOREST"),
  COLD_OCEAN("COLD_OCEAN"),
  DARK_FOREST("ROOFED_FOREST"),
  DEEP_COLD_OCEAN("COLD_DEEP_OCEAN"),
  DEEP_FROZEN_OCEAN("FROZEN_DEEP_OCEAN"),
  DEEP_LUKEWARM_OCEAN("LUKEWARM_DEEP_OCEAN"),
  DEEP_OCEAN("DEEP_OCEAN"),
  DESERT("DESERT"),
  GROVE,
  END_BARRENS(World.Environment.THE_END, "SKY_ISLAND_BARREN"),
  END_HIGHLANDS(World.Environment.THE_END, "SKY_ISLAND_HIGH"),
  END_MIDLANDS(World.Environment.THE_END, "SKY_ISLAND_MEDIUM"),
  ERODED_BADLANDS("MUTATED_MESA", "MESA_BRYCE"),
  FLOWER_FOREST("MUTATED_FOREST"),
  FOREST("FOREST"),
  FROZEN_OCEAN("FROZEN_OCEAN"),
  FROZEN_RIVER("FROZEN_RIVER"),
  OLD_GROWTH_SPRUCE_TAIGA,
  OLD_GROWTH_PINE_TAIGA,
  WINDSWEPT_GRAVELLY_HILLS,
  ICE_SPIKES("MUTATED_ICE_FLATS", "ICE_PLAINS_SPIKES"),
  JUNGLE("JUNGLE"),
  SPARSE_JUNGLE,
  LUKEWARM_OCEAN("LUKEWARM_OCEAN"),
  WINDSWEPT_HILLS,
  SNOWY_SLOPES,
  FROZEN_PEAKS,
  JAGGED_PEAKS,
  STONY_PEAKS,
  MUSHROOM_FIELDS("MUSHROOM_ISLAND"),
  SOUL_SAND_VALLEY(World.Environment.NETHER),
  CRIMSON_FOREST(World.Environment.NETHER),
  WARPED_FOREST(World.Environment.NETHER),
  BASALT_DELTAS(World.Environment.NETHER),
  NETHER_WASTES(World.Environment.NETHER, "NETHER", "HELL"),
  OCEAN("OCEAN"),
  PLAINS("PLAINS"),
  RIVER("RIVER"),
  SAVANNA("SAVANNA"),
  SAVANNA_PLATEAU("SAVANNA_ROCK", "SAVANNA_PLATEAU"),
  WINDSWEPT_SAVANNA,
  SMALL_END_ISLANDS(World.Environment.THE_END, "SKY_ISLAND_LOW"),
  SNOWY_BEACH("COLD_BEACH"),
  SNOWY_TAIGA("TAIGA_COLD", "COLD_TAIGA"),
  SNOWY_PLAINS,
  STONY_SHORE,
  SUNFLOWER_PLAINS("MUTATED_PLAINS"),
  SWAMP("SWAMPLAND"),
  TAIGA("TAIGA"),
  OLD_GROWTH_BIRCH_FOREST,
  THE_END(World.Environment.THE_END, "SKY"),
  THE_VOID("VOID"),
  WARM_OCEAN("WARM_OCEAN"),
  WOODED_BADLANDS,
  BAMBOO_JUNGLE,
  DRIPSTONE_CAVES,
  LUSH_CAVES,
  WINDSWEPT_FOREST,
  MEADOW,
  CUSTOM;

  /**
   * A cached unmodifiable list of {@link ZBiome#values()} to avoid allocating memory for calling the method every time.
   *
   * @since 1.0.0
   */
  public static final List<ZBiome> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
  private static final boolean HORIZONTAL_SUPPORT = XMaterial.supports(16);
  @Nullable
  private final Biome biome;
  @Nonnull
  private final World.Environment environment;

  ZBiome(@Nonnull String... legacies) {
    this(World.Environment.NORMAL, legacies);
  }

  ZBiome(@Nonnull World.Environment environment, @Nonnull String... legacies) {
    this.environment = environment;
    Data.NAMES.put(this.name(), this);
    for (String legacy : legacies) {
      Data.NAMES.put(legacy, this);
    }

    Biome biome = Enums.getIfPresent(Biome.class, this.name()).orNull();
    if (biome == null) {
      for (String legacy : legacies) {
        biome = Enums.getIfPresent(Biome.class, legacy).orNull();
        if (biome != null) {
          break;
        }
      }
    }
    this.biome = biome;
  }

  /**
   * Attempts to build the string like an enum name.<br>
   * Removes all the spaces, numbers and extra non-English characters. Also removes some config/in-game based strings. While this method is hard to maintain, it's extremely efficient. It's approximately more than x5 times faster than the normal RegEx + String Methods approach for both formatted and unformatted material names.
   *
   * @param name the biome name to format.
   *
   * @return an enum name.
   * @since 1.0.0
   */
  @Nonnull
  private static String format(@Nonnull String name) {
    int len = name.length();
    char[] chs = new char[len];
    int count = 0;
    boolean appendUnderline = false;

    for (int i = 0; i < len; i++) {
      char ch = name.charAt(i);
      if (!appendUnderline && count != 0 && (ch == '-' || ch == ' ' || ch == '_') && chs[count] != '_') {
        appendUnderline = true;
      } else {
        if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
          if (appendUnderline) {
            chs[count++] = '_';
            appendUnderline = false;
          }
          chs[count++] = (char) (ch & 0x5f);
        }
      }
    }

    return new String(chs, 0, count);
  }

  /**
   * Parses the ZBiome with the given name.
   *
   * @param biome the name of the biome.
   *
   * @return a matched ZBiome.
   * @since ZBiome 1.0.0
   */
  @Nonnull
  public static ZBiome matchZBiome(@Nonnull String biome) {
    Validate.notEmpty(biome, "Cannot match ZBiome of a null or empty biome name");
    if (!Data.NAMES.containsKey(format(biome))) {
      return Data.NAMES.put(format(biome), ZBiome.CUSTOM);
    }
    return Data.NAMES.get(format(biome));
  }

  /**
   * Parses the ZBiome with the given bukkit biome.
   *
   * @param biome the Bukkit biome.
   *
   * @return a matched biome.
   * @throws IllegalArgumentException may be thrown as an unexpected exception.
   * @since 1.0.0
   */
  @Nonnull
  public static ZBiome matchZBiome(@Nonnull Biome biome) {
    Objects.requireNonNull(biome, "Cannot match ZBiome of a null biome");
    if (!Data.NAMES.containsKey(format(biome.name()))) {
      return Data.NAMES.put(format(biome.name()), ZBiome.CUSTOM);
    }
    return Data.NAMES.get(biome.name());
  }

  /**
   * Gets the enviroment (world type) which this biome originally belongs to.
   *
   * @return the enviroment that this biome belongs to.
   * @since 4.0.0
   */
  @Nonnull
  public World.Environment getEnvironment() {
    return environment;
  }

  /**
   * Parses the ZBiome as a {@link Biome} based on the server version. May return null for custom biomes
   *
   * @return the vanilla biome.
   * @since 1.0.0
   */
  @Nullable
  public Biome getBiome() {
    return this.biome;
  }

  /**
   * Sets the biome of the chunk. If the chunk is not generated/loaded already, it'll be generated and loaded. Note that this doesn't send any update packets to the nearby clients.
   *
   * @param chunk the chunk to change the biome.
   *
   * @return the async task handling this operation.
   * @since 1.0.0
   */
  @Nonnull
  public CompletableFuture<Void> setBiome(@Nonnull Chunk chunk) {
    Objects.requireNonNull(biome, () -> "Unsupported biome: " + this.name());
    Objects.requireNonNull(chunk, "Cannot set biome of null chunk");
    if (!chunk.isLoaded()) {
      Validate.isTrue(chunk.load(true), "Could not load chunk at " + chunk.getX() + ", " + chunk.getZ());
    }

    // Apparently setBiome is thread-safe.
    return CompletableFuture.runAsync(() -> {
      for (int x = 0; x < 16; x++) {
        // y loop for 1.16+ support (vertical biomes).
        // As of now increasing it by 4 seems to work.
        // This should be the minimal size of the vertical biomes.
        for (int y = 0; y < (HORIZONTAL_SUPPORT ? chunk.getWorld().getMaxHeight() : 1); y += 4) {
          for (int z = 0; z < 16; z++) {
            Block block = chunk.getBlock(x, y, z);
            if (block.getBiome() != biome) {
              block.setBiome(biome);
            }
          }
        }
      }
    }).exceptionally((result) -> {
      result.printStackTrace();
      return null;
    });
  }

  /**
   * Change the biome in the selected region. Unloaded chunks will be ignored. Note that this doesn't send any update packets to the nearby clients.
   *
   * @param start the start position.
   * @param end the end position.
   *
   * @since 1.0.0
   */
  @Nonnull
  public CompletableFuture<Void> setBiome(@Nonnull Location start, @Nonnull Location end) {
    Objects.requireNonNull(start, "Start location cannot be null");
    Objects.requireNonNull(end, "End location cannot be null");
    Objects.requireNonNull(biome, () -> "Unsupported biome: " + this.name());
    Validate.isTrue(start.getWorld().getUID().equals(end.getWorld().getUID()), "Location worlds mismatch");

    // Apparently setBiome is thread-safe.
    return CompletableFuture.runAsync(() -> {
      for (int x = start.getBlockX(); x < end.getBlockX(); x++) {
        // y loop for 1.16+ support (vertical biomes).
        // As of now increasing it by 4 seems to work.
        // This should be the minimal size of the vertical biomes.
        for (int y = 0; y < (HORIZONTAL_SUPPORT ? start.getWorld().getMaxHeight() : 1); y += 4) {
          for (int z = start.getBlockZ(); z < end.getBlockZ(); z++) {
            Block block = new Location(start.getWorld(), x, y, z).getBlock();
            if (block.getBiome() != biome) {
              block.setBiome(biome);
            }
          }
        }
      }
    }).exceptionally((result) -> {
      result.printStackTrace();
      return null;
    });
  }

  /**
   * Used for datas that need to be accessed during enum initilization.
   *
   * @since 3.0.0
   */
  private static final class Data {

    private static final Map<String, ZBiome> NAMES = new HashMap<>();
  }
}
