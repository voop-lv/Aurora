package com.zenya.aurora.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WGManager {
    public static WGManager INSTANCE = new WGManager();

    public static WorldGuardPlugin getWorldGuard() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        return (plugin == null || !(plugin instanceof WorldGuardPlugin)) ? null : (WorldGuardPlugin) plugin;
    }

    public RegionManager getRegionManager(World world) {
        try {
            //Post-1.13 (WG 7)
            return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        } catch(Exception exc) {
            //Pre-1.13 (WG 6)
            //return getWorldGuard().getRegionManager(world);
            exc.printStackTrace();
            return null;
        }
    }

    public ApplicableRegionSet getApplicableRegionSet(Player player) {
        return getApplicableRegionSet(player.getLocation());
    }

    public ApplicableRegionSet getApplicableRegionSet(Location loc) {
        return getApplicableRegionSet(getRegionManager(loc.getWorld()), BukkitAdapter.asBlockVector(loc));
    }

    private ApplicableRegionSet getApplicableRegionSet(RegionManager manager, BlockVector3 vec) {
        return manager.getApplicableRegions(vec);
    }

    public <T extends Flag<?>> T registerFlag(final T flag) throws FlagConflictException {
        WorldGuard.getInstance().getFlagRegistry().register(flag);
        //INSTANCE.getWorldGuard().getFlagRegistry().register(flag);
        return flag;
    }
}
