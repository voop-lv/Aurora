package com.zenya.aurora.event;

import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerBiomeChangeEvent extends Event implements Cancellable {
    private boolean isCancelled;
    private Player player;

    public PlayerBiomeChangeEvent(Player player) {
        this.isCancelled = false;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Biome getBiome() {
        return player.getLocation().getBlock().getBiome();
    }

    public World getWorld() {
        return player.getWorld();
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
