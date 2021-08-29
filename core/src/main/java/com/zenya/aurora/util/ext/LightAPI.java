package com.zenya.aurora.util.ext;

import com.zenya.aurora.Aurora;
import com.zenya.aurora.storage.StorageFileManager;
import com.zenya.aurora.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.beykerykt.lightapi.LightType;
import ru.beykerykt.lightapi.chunks.ChunkInfo;
import ru.beykerykt.lightapi.events.DeleteLightEvent;
import ru.beykerykt.lightapi.events.SetLightEvent;
import ru.beykerykt.lightapi.events.UpdateChunkEvent;
import ru.beykerykt.lightapi.request.RequestSteamMachine;
import ru.beykerykt.lightapi.server.ServerModInfo;
import ru.beykerykt.lightapi.server.ServerModManager;
import ru.beykerykt.lightapi.server.nms.INMSHandler;
import ru.beykerykt.lightapi.server.nms.craftbukkit.*;
import ru.beykerykt.lightapi.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * LightAPI Fork by Qveshn https://github.com/Qveshn/LightAPI
 *
 * Modified by Zenya4 for Aurora
 *
 * @version 3.4.6
 *
 */
public class LightAPI {

  public static LightAPI INSTANCE = new LightAPI();
  private final int UPDATE_DELAY_TICKS = 2;
  private final int MAX_ITERATIONS_PER_TICK = 5000;
  private static RequestSteamMachine machine;

  // To synchronize nms create/delete light methods to avoid conflicts in multi-threaded calls. Got a better idea?
  private static final Object lock = new Object();

  public LightAPI() {
    ServerModInfo craftbukkit = new ServerModInfo("CraftBukkit");
    craftbukkit.getVersions().put("v1_8_R3", CraftBukkit_v1_8_R3.class);
    craftbukkit.getVersions().put("v1_9_R1", CraftBukkit_v1_9_R1.class);
    craftbukkit.getVersions().put("v1_9_R2", CraftBukkit_v1_9_R2.class);
    craftbukkit.getVersions().put("v1_10_R1", CraftBukkit_v1_10_R1.class);
    craftbukkit.getVersions().put("v1_11_R1", CraftBukkit_v1_11_R1.class);
    craftbukkit.getVersions().put("v1_12_R1", CraftBukkit_v1_12_R1.class);
    craftbukkit.getVersions().put("v1_13_R1", CraftBukkit_v1_13_R1.class);
    craftbukkit.getVersions().put("v1_13_R2", CraftBukkit_v1_13_R2.class);
    craftbukkit.getVersions().put("v1_14_R1", CraftBukkit_v1_14_R1.class);
    craftbukkit.getVersions().put("v1_15_R1", CraftBukkit_v1_15_R1.class);
    craftbukkit.getVersions().put("v1_16_R1", CraftBukkit_v1_16_R1.class);
    craftbukkit.getVersions().put("v1_16_R2", CraftBukkit_v1_16_R2.class);
    craftbukkit.getVersions().put("v1_16_R3", CraftBukkit_v1_16_R3.class);
    craftbukkit.getVersions().put("v1_17_R1", CraftBukkit_v1_17_R1.class);
    ServerModManager.registerServerMod(craftbukkit);

    //Init NMS
    String serverName = Utils.serverName();
    Class<? extends INMSHandler> clazz = ServerModManager.findImplementaion("CraftBukkit");

    if (clazz == null) {
      Logger.logError("No LightAPI implementations was found for §f%s %s",
              serverName, Utils.serverVersion());
      Logger.logError("Support for lighting features may be limited");
      disable();
      return;
    } else {
      try {
        ServerModManager.initImplementaion(clazz);
        Logger.logInfo("Loading LightAPI implementation for §f%s %s",
                serverName, Utils.serverVersion());
        machine = new RequestSteamMachine();
        machine.start(UPDATE_DELAY_TICKS, MAX_ITERATIONS_PER_TICK);
      } catch (Exception exc) {
        Logger.logError("Could not initialise LightAPI implementation for §f%s %s",
                serverName, Utils.serverVersion());
        Logger.logError("Support for lighting features may be limited");
        disable();
        return;
      }
    }

    //Disable if fork uses ca.spottedleaf.starlight lighting engine
    try {
      Class<?> starlight = Class.forName("ca.spottedleaf.starlight.light.StarLightInterface", false, getClass().getClassLoader()); //$2
      if (starlight != null) {
        Logger.logError("No LightAPI implementations was found for §f%s %s",
                serverName, Utils.serverVersion());
        Logger.logError("Support for lighting features may be limited");
        disable();
        return;
      }
    } catch (ClassNotFoundException exc) {
      //Not using starlight, enable LightAPI as per normal
      Logger.logInfo("Enabling LightAPI...");
    }

  }

  public static void disable() {
    if (INSTANCE != null) {
      Logger.logInfo("Disabling LightAPI...");
      machine.shutdown();
      INSTANCE = null;
    }
  }

  @SuppressWarnings("unused")
  public static boolean isSupported(World world, LightType lightType) {
    return ServerModManager.getNMSHandler().isSupported(world, lightType);
  }

  /**
   * A method to remove lighting and instantly update it client-side
   *
   * @param location Location to remove lighting from
   * @param lightType Type of lighting to remove
   * @param async Whether light updating should be asynchronous
   * @see #setLight(Location, LightType, int, boolean)
   */
  public static void clearLight(Location location, LightType lightType, boolean async) {
    setLight(location, lightType, 0, async);
  }

  /**
   * A method to set lighting and instantly update it client-side
   *
   * @param location Location to create lighting
   * @param lightLevel Light level to set
   * @param lightType Type of lighting to create
   * @param async Whether light updating should be asynchronous
   */
  public static void setLight(Location location, LightType lightType, int lightLevel, boolean async) {
    Block block = location.getBlock();
    int oldLightLevel = lightType == LightType.BLOCK ? block.getLightFromBlocks() : block.getLightFromSky();
    if (oldLightLevel != lightLevel) {
      if (lightLevel > 0) {
        LightAPI.createLight(location, lightType, lightLevel, async);
      } else {
        LightAPI.deleteLight(location, lightType, async);
      }
      for (ChunkInfo chunkInfo : LightAPI.collectChunks(location, lightType, Math.max(lightLevel, oldLightLevel))) {
        if (async && lightLevel > 0) {
          new BukkitRunnable() {
            @Override
            public void run() {
              LightAPI.updateChunk(chunkInfo, lightType);
            }
          }.runTaskLater(Aurora.getInstance(), 10);
        } else {
          LightAPI.updateChunk(chunkInfo, lightType);
        }
      }
    }
  }

  @Deprecated
  @SuppressWarnings("unused")
  public static boolean createLight(Location location, int lightlevel, boolean async) {
    return createLight(location, LightType.BLOCK, lightlevel, async);
  }

  @SuppressWarnings("WeakerAccess")
  public static boolean createLight(Location location, LightType lightType, int lightlevel, boolean async) {
    return createLight(
            location.getWorld(),
            location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ(),
            lightType,
            lightlevel,
            async);
  }

  @Deprecated
  public static boolean createLight(
          World world, int x, final int y, final int z, final int lightlevel, boolean async) {
    return createLight(world, x, y, z, LightType.BLOCK, lightlevel, async);
  }

  @SuppressWarnings("WeakerAccess")
  public static boolean createLight(
          World world, int x, final int y, final int z, LightType lightType, final int lightlevel, boolean async) {
    if (Aurora.getInstance().isEnabled()) {
      final SetLightEvent event = new SetLightEvent(world, x, y, z, lightType, lightlevel, async);
      Bukkit.getPluginManager().callEvent(event);

      if (!event.isCancelled()) {
        Runnable request = new Runnable() {
          @Override
          public void run() {
            synchronized (lock) {
              ServerModManager.getNMSHandler().createLight(
                      event.getWorld(),
                      event.getX(),
                      event.getY(),
                      event.getZ(),
                      event.getLightType(),
                      event.getLightLevel());
            }
          }
        };
        if (event.isAsync()) {
          machine.addToQueue(request);
        } else {
          request.run();
        }
        return true;
      }
    }
    return false;
  }

  @Deprecated
  @SuppressWarnings("unused")
  public static boolean deleteLight(Location location, boolean async) {
    return deleteLight(location, LightType.BLOCK, async);
  }

  @SuppressWarnings("WeakerAccess")
  public static boolean deleteLight(Location location, LightType lightType, boolean async) {
    return deleteLight(
            location.getWorld(),
            location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ(),
            lightType,
            async);
  }

  @Deprecated
  public static boolean deleteLight(final World world, final int x, final int y, final int z, boolean async) {
    return deleteLight(world, x, y, z, LightType.BLOCK, async);
  }

  @SuppressWarnings("WeakerAccess")
  public static boolean deleteLight(
          final World world, final int x, final int y, final int z, LightType lightType, boolean async
  ) {
    if (Aurora.getInstance().isEnabled()) {
      final DeleteLightEvent event = new DeleteLightEvent(world, x, y, z, lightType, async);
      Bukkit.getPluginManager().callEvent(event);

      if (!event.isCancelled()) {
        Runnable request = new Runnable() {
          @Override
          public void run() {
            ServerModManager.getNMSHandler().deleteLight(
                    event.getWorld(),
                    event.getX(),
                    event.getY(),
                    event.getZ(),
                    event.getLightType());
          }
        };
        if (event.isAsync()) {
          machine.addToQueue(request);
        } else {
          request.run();
        }
        return true;
      }
    }
    return false;
  }

  @Deprecated
  public static List<ChunkInfo> collectChunks(Location location) {
    return collectChunks(location, LightType.BLOCK, 15);
  }

  @Deprecated
  @SuppressWarnings("unused")
  public static List<ChunkInfo> collectChunks(Location location, int lightLevel) {
    return collectChunks(location, LightType.BLOCK, lightLevel);
  }

  @SuppressWarnings("WeakerAccess")
  public static List<ChunkInfo> collectChunks(Location location, LightType lightType, int lightLevel) {
    return collectChunks(
            location.getWorld(),
            location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ(),
            lightType,
            lightLevel);
  }

  @Deprecated
  public static List<ChunkInfo> collectChunks(final World world, final int x, final int y, final int z) {
    return collectChunks(world, x, y, z, LightType.BLOCK, 15);
  }

  @Deprecated
  public static List<ChunkInfo> collectChunks(World world, int x, int y, int z, int lightLevel) {
    return collectChunks(world, x, y, z, LightType.BLOCK, lightLevel);
  }

  @SuppressWarnings("WeakerAccess")
  public static List<ChunkInfo> collectChunks(World world, int x, int y, int z, LightType lightType, int lightLevel) {
    if (Aurora.getInstance().isEnabled()) {
      return ServerModManager.getNMSHandler().collectChunks(world, x, y, z, lightType, lightLevel);
    }
    return new ArrayList<ChunkInfo>();
  }

  @Deprecated
  public static boolean updateChunks(ChunkInfo info) {
    return updateChunk(info);
  }

  @Deprecated
  public static boolean updateChunk(ChunkInfo info) {
    return updateChunk(info, LightType.BLOCK);
  }

  @SuppressWarnings("WeakerAccess")
  public static boolean updateChunk(ChunkInfo info, LightType lightType) {
    return updateChunk(info, lightType, null);
  }

  @Deprecated
  public static boolean updateChunk(ChunkInfo info, Collection<? extends Player> players) {
    return updateChunk(info, LightType.BLOCK, players);
  }

  @SuppressWarnings("WeakerAccess")
  public static boolean updateChunk(ChunkInfo info, LightType lightType, Collection<? extends Player> players) {
    if (Aurora.getInstance().isEnabled()) {
      UpdateChunkEvent event = new UpdateChunkEvent(info, lightType);
      Bukkit.getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
        machine.addChunkToUpdate(info, lightType, players);
        return true;
      }
    }
    return false;
  }

  @Deprecated
  public static boolean updateChunks(Location location, Collection<? extends Player> players) {
    return updateChunks(
            location.getWorld(),
            location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ(),
            players);
  }

  @Deprecated
  public static boolean updateChunks(World world, int x, int y, int z, Collection<? extends Player> players) {
    if (Aurora.getInstance().isEnabled()) {
      for (ChunkInfo info : collectChunks(world, x, y, z, 15)) {
        info.setReceivers(players);
        updateChunk(info);
      }
      return true;
    }
    return false;
  }

  @Deprecated
  public static boolean updateChunk(Location location, Collection<? extends Player> players) {
    return updateChunk(
            location.getWorld(),
            location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ(),
            players);
  }

  @Deprecated
  public static boolean updateChunk(World world, int x, int y, int z, Collection<? extends Player> players) {
    if (Aurora.getInstance().isEnabled()) {
      updateChunk(new ChunkInfo(world, x, y - 16, z, players));
      updateChunk(new ChunkInfo(world, x, y, z, players));
      updateChunk(new ChunkInfo(world, x, y + 16, z, players));
      return true;
    }
    return false;
  }

  private static BlockFace[] SIDES = {
    BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST
  };

  @Deprecated
  public static Block getAdjacentAirBlock(Block block) {
    for (BlockFace face : SIDES) {
      if (block.getY() == 0x0 && face == BlockFace.DOWN) {
        continue;
      }
      if (block.getY() == 0xFF && face == BlockFace.UP) {
        continue;
      }

      Block candidate = block.getRelative(face);

      if (candidate.getType().isTransparent()) {
        return candidate;
      }
    }
    return block;
  }
}
