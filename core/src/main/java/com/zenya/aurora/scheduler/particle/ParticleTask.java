package com.zenya.aurora.scheduler.particle;

import com.zenya.aurora.scheduler.AuroraTask;
import org.bukkit.entity.Player;

public interface ParticleTask extends AuroraTask {
    Player getPlayer();
    void killTasks();
}
