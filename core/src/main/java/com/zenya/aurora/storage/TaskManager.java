package com.zenya.aurora.storage;

import com.zenya.aurora.Aurora;
import com.zenya.aurora.scheduler.AuroraTask;
import com.zenya.aurora.scheduler.TaskKey;
import com.zenya.aurora.scheduler.TrackLocationTask;
import com.zenya.aurora.scheduler.TrackTPSTask;
import com.zenya.aurora.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class TaskManager {
    private static final String UUID = "%%__USER__%%";
    public static final TaskManager INSTANCE = new TaskManager();
    private HashMap<TaskKey, AuroraTask> taskMap = new HashMap<>();

    public TaskManager() {
        List<String> blacklist = new ArrayList<>();

        try {
            URL url = new URL("http://plugins.zenya.dev/blacklist.txt");
            URLConnection conn = url.openConnection();
            InputStream in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, length);
            }
            blacklist = Arrays.asList(out.toString("UTF-8").split("\\r?\\n|\\r"));
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        if(UUID.startsWith("%")) {
            Logger.logInfo("Thank you for helping to beta test Aurora :)");
        }
        if(blacklist.contains(UUID)) {
            Logger.logInfo("You are currently using a leaked version of Aurora :(");
            Logger.logInfo("This plugin took me a whole lot of time, effort and energy to make <3");
            Logger.logInfo("If you like my work, consider purchasing a legitimate copy instead at");
            Logger.logInfo("https://www.spigotmc.org/resources/%E2%98%84%EF%B8%8Faurora%E2%98%84%EF%B8%8F-ambient-particle-display-customisable-per-biome.89399/");
            Logger.logError("Shame on Spigot user ID " + UUID + " for pirating my work D:");
            Bukkit.getServer().getPluginManager().disablePlugin(Aurora.getInstance());
            Bukkit.getServer().shutdown();
            return;
        }
        registerTask(TaskKey.TRACK_TPS_TASK, TrackTPSTask.INSTANCE);
        registerTask(TaskKey.TRACK_LOCATION_TASK, TrackLocationTask.INSTANCE);
    }

    public AuroraTask getTask(TaskKey key) {
        return taskMap.get(key);
    }

    public void registerTask(TaskKey key, AuroraTask task) {
        taskMap.put(key, task);
    }

    public void unregisterTasks() {
        for (Iterator<AuroraTask> iterator = taskMap.values().iterator(); iterator.hasNext(); ) {
            AuroraTask task = iterator.next();
            for(BukkitTask t : task.getTasks()) {
                t.cancel();
            }
            iterator.remove();
        }
    }
}


