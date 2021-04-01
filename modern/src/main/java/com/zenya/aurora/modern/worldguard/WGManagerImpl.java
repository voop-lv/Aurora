package com.zenya.aurora.modern.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.zenya.aurora.worldguard.WGManager;
import org.bukkit.Location;
import org.bukkit.World;

public class WGManagerImpl extends WGManager {
    @Override
    public RegionManager getRegionManager(World world) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
    }

    @Override
    public ApplicableRegionSet getApplicableRegionSet(Location loc) {
        return getRegionManager(loc.getWorld()).getApplicableRegions(BukkitAdapter.asBlockVector(loc));
    }
}
