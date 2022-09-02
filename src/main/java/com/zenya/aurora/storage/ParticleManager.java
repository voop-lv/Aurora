package com.zenya.aurora.storage;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.zenya.aurora.particle.ParticleTask;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.Player;

public class ParticleManager {

    private final ListMultimap<Player, ParticleTask> particleMap = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

    public List<ParticleTask> getTasks(Player player) {
        synchronized (particleMap) {
            return particleMap.get(player);
        }
    }

    public Set<Player> getPlayers() {
        synchronized (particleMap) {
            return particleMap.keySet();
        }
    }

    public void registerTask(Player player, ParticleTask task) {
        synchronized (particleMap) {
            particleMap.put(player, task);
        }
    }

    public void unregisterTasks(Player player, boolean isShutdown) {
        List<ParticleTask> oldTasks;
        synchronized (particleMap) {
            oldTasks = particleMap.removeAll(player);
        }

        if (oldTasks != null && !oldTasks.isEmpty()) {
            for (ParticleTask oldTask : oldTasks) {
                oldTask.killTasks(isShutdown);
            }
        }
    }
}
