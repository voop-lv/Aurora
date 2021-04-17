package com.zenya.aurora.legacy.worldguard;

import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.zenya.aurora.worldguard.WGManager;
import org.bukkit.Location;
import org.bukkit.World;

public class WGManagerImpl extends WGManager {
    @Override
    public RegionManager getRegionManager(World world) {
        return getWorldGuard().getRegionManager(world);
    }

    @Override
    public ApplicableRegionSet getApplicableRegionSet(Location loc) {
        //BukkitUtil.toVector(Location loc) requires WorldEdit 6 as a dependency
        return getRegionManager(loc.getWorld()).getApplicableRegions(BukkitUtil.toVector(loc));
    }

    @Override
    public <T extends Flag<?>> T registerFlag(T flag) throws FlagConflictException {
        getWorldGuard().getFlagRegistry().register(flag);
        return flag;
    }
}
