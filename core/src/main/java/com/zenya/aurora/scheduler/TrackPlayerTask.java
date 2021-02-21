package com.zenya.aurora.scheduler;

import com.zenya.aurora.Aurora;
import com.zenya.aurora.event.PlayerBiomeChangeEvent;
import com.zenya.aurora.event.PlayerChunkChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class TrackPlayerTask implements AuroraTask {
    private static TrackPlayerTask nmlTask;
    private BukkitTask runnables[];
    private CompletableFuture<HashMap<Player, Location>> playerCoords;

    public TrackPlayerTask() {
        playerCoords = CompletableFuture.supplyAsync(() -> new HashMap<>());
        runTasks();
    }

    @Override
    public TaskKey getKey() {
        return TaskKey.TRACK_PLAYER_TASK;
    }

    @Override
    public void runTasks() {
        //Task to fire events
        BukkitTask task1 = new BukkitRunnable() {
            @Override
            public void run() {
                playerCoords.thenAcceptAsync(coordMap -> {
                    if (Bukkit.getOnlinePlayers() != null && Bukkit.getOnlinePlayers().size() != 0) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            //Chunk change event
                            if(player.getLocation().getChunk().getX() != coordMap.getOrDefault(player, new Location(player.getWorld(), 0, 0, 0)).getChunk().getX() || player.getLocation().getChunk().getZ() != coordMap.getOrDefault(player, new Location(player.getWorld(), 0, 0, 0)).getChunk().getZ()) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        Bukkit.getPluginManager().callEvent(new PlayerChunkChangeEvent(player));
                                    }
                                }.runTask(Aurora.getInstance());
                                //Force update
                                coordMap.put(player, player.getLocation());
                            }

                            //Biome change event
                            if(!player.getLocation().getBlock().getBiome().equals(coordMap.getOrDefault(player, new Location(player.getWorld(), 0, 0, 0)).getBlock().getBiome())) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        Bukkit.getPluginManager().callEvent(new PlayerBiomeChangeEvent(player));
                                    }
                                }.runTask(Aurora.getInstance());
                                //Force update
                                coordMap.put(player, player.getLocation());
                            }
                        }
                    }
                });
            }
        }.runTaskTimerAsynchronously(Aurora.getInstance(), 0, 20);

        //Task to update location map
        BukkitTask task2 = new BukkitRunnable() {
            @Override
            public void run() {
                playerCoords.thenAcceptAsync(coordMap -> {
                    if (Bukkit.getOnlinePlayers() != null && Bukkit.getOnlinePlayers().size() != 0) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (!coordMap.containsKey(player)) {
                                //Initialise player location
                                coordMap.put(player, new Location(player.getWorld(), 0, 0, 0));
                            } else {
                                //Update player location
                                coordMap.put(player, player.getLocation());
                            }
                        }
                    }
                });
            }
        }.runTaskTimerAsynchronously(Aurora.getInstance(), 0, 20*3);

        //Task to remove old player entries
        BukkitTask task3 = new BukkitRunnable() {
            @Override
            public void run() {
                playerCoords.thenAcceptAsync(coordMap -> {
                    if (Bukkit.getOnlinePlayers() != null && Bukkit.getOnlinePlayers().size() != 0) {
                        if(coordMap.keySet() != null && coordMap.keySet().size() != 0) {
                            for(Player player : coordMap.keySet()) {
                                if(!Bukkit.getOnlinePlayers().contains(player)) {
                                    coordMap.remove(player);
                                }
                            }
                        }
                    } else {
                        coordMap.clear();
                    }
                });
            }
        }.runTaskTimerAsynchronously(Aurora.getInstance(), 0, 20*5);

        //Add to runnables[]
        runnables = new BukkitTask[]{task1, task2, task3};
    }

    @Override
    public BukkitTask[] getTasks() {
        return runnables;
    }

    public static TrackPlayerTask getInstance() {
        if(nmlTask == null) {
            nmlTask = new TrackPlayerTask();
        }
        return nmlTask;
    }
}


