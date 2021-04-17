package com.zenya.aurora.worldguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.zenya.aurora.util.CompatibilityHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class WGManager {
    private static final String PACKAGE_DOMAIN = "com.zenya.aurora.";
    private static final String MODERN = "modern.";
    private static final String LEGACY = "legacy.";
    private static final String WORLDGUARD_DEPENDENCY = "worldguard.WGManagerImpl";

    public static WGManager INSTANCE;

    static {
        try {
            if (CompatibilityHandler.getProtocol() >= 13) {
                INSTANCE = (WGManager) Class.forName(PACKAGE_DOMAIN + MODERN + WORLDGUARD_DEPENDENCY).newInstance();
            } else {
                INSTANCE = (WGManager) Class.forName(PACKAGE_DOMAIN + LEGACY + WORLDGUARD_DEPENDENCY).newInstance();
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException exc) {
            //Won't happen
            INSTANCE = null;
            exc.printStackTrace();
        }
    }

    public static WorldGuardPlugin getWorldGuard() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        return (plugin == null || !(plugin instanceof WorldGuardPlugin)) ? null : (WorldGuardPlugin) plugin;
    }

    public abstract RegionManager getRegionManager(World world);

    public ApplicableRegionSet getApplicableRegionSet(Player player) {
        return getApplicableRegionSet(player.getLocation());
    }

    public abstract ApplicableRegionSet getApplicableRegionSet(Location loc);

    public abstract  <T extends Flag<?>> T registerFlag(final T flag) throws FlagConflictException;
}
