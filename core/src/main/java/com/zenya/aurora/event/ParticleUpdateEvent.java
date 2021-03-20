package com.zenya.aurora.event;

import com.zenya.aurora.util.LocationUtils;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;

public class ParticleUpdateEvent extends Event implements Cancellable {
    private boolean isCancelled;
    private Player player;

    public ParticleUpdateEvent(Player player) {
        this.isCancelled = false;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Chunk getChunk() {
        return player.getLocation().getChunk();
    }

    public World getWorld() {
        return player.getWorld();
    }

    public Chunk getPreviousChunk() {
        double relativeX = player.getVelocity().getX();
        double relativeZ = player.getVelocity().getZ();
        if(relativeX > 0) {
            relativeX = 1;
        } else {
            relativeX = -1;
        }
        if(relativeZ > 0) {
            relativeZ = 1;
        } else {
            relativeZ = -1;
        }

        return getChunk().getWorld().getChunkAt(getChunk().getX() + (int) relativeX, getChunk().getZ() + (int) relativeZ);
    }

    public Chunk[] getNearbyChunks(int radius) {
        return LocationUtils.getSurroundingChunks(getChunk(), radius);
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    //Default custom event methods
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
