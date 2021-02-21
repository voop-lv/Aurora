package com.zenya.aurora.storage;

import com.zenya.aurora.scheduler.particle.ParticleTask;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ParticleManager {
    private static ParticleManager particleManager;
    private ConcurrentHashMap<Player, ParticleTask[]> particleMap = new ConcurrentHashMap<>();

    public ParticleTask[] getTasks(Player player) {
        return particleMap.get(player);
    }

    public Set<Player> getPlayers() {
        return particleMap.keySet();
    }

    public void registerTask(Player player, ParticleTask task) {
        ParticleTask[] oldTasks = particleMap.get(player);
        ParticleTask tasks[];

        if(oldTasks != null) {
            tasks = (ParticleTask[]) ArrayUtils.addAll(oldTasks, new ParticleTask[]{task});
        } else {
            tasks = new ParticleTask[]{task};
        }
        particleMap.put(player, tasks);
    }

    public void unregisterTasks(Player player) {
        ParticleTask[] oldTasks = particleMap.remove(player);
        if (oldTasks != null && oldTasks.length != 0) {
            for(ParticleTask oldTask : oldTasks) {
                oldTask.killTasks();
            }
        }
    }

    public static ParticleManager getInstance() {
        if(particleManager == null) {
            particleManager = new ParticleManager();
        }
        return particleManager;
    }
}
