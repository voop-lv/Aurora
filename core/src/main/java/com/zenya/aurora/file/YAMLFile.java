package com.zenya.aurora.file;

import com.zenya.aurora.Aurora;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class YAMLFile {
    private static Plugin AURORA = Aurora.getInstance();
    private String directory;
    private String fileName;
    private File configFile;
    private FileConfiguration origConfig;
    private FileConfiguration config;

    public YAMLFile(String fileName) {
        this(AURORA.getDataFolder().getPath(), fileName);
    }

    public YAMLFile(String directory, String fileName) {
        this(directory, fileName, null, null, null);
    }

    /**
     *
     * @param directory Directory the file exists in.
     * @param fileName Full name of the file, excluding its directory.
     * @param fileVersion Version of the file as specified in "config-version".
     * @param ignoredNodes Nodes that will use the latest resource config's values.
     * @param replaceNodes Nodes that will use old config values instead of being appended (applicable to nested keys)
     */
    public YAMLFile(String directory, String fileName, Integer fileVersion, List<String> ignoredNodes, List<String> replaceNodes) {
        this.directory = directory;
        this.fileName = fileName;
        this.configFile = new File(directory, fileName);
        this.origConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(AURORA.getResource(fileName)));
        this.config = YamlConfiguration.loadConfiguration(configFile);

        if(fileVersion != null) {
            try {
                updateFile(fileVersion, ignoredNodes, replaceNodes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean getFileExists() {
        return configFile.exists();
    }

    private int getFileVersion() {
        return getInt("config-version");
    }

    private void updateFile(Integer fileVersion, List<String> ignoredNodes, List<String> replaceNodes) throws IOException {
        boolean resetFile = false;
        ignoredNodes.add("config-version");

        if(!getFileExists()) {
            //Init file
            origConfig.save(configFile);
            config = YamlConfiguration.loadConfiguration(configFile);
        } else {
            //Reset file for backward-compatibility
            if(getFileVersion() > fileVersion) resetFile = true;

            //Update file
            if(getFileVersion() != fileVersion) {
                File oldConfigFile = new File(directory, fileName + ".v" + String.valueOf(getFileVersion()));
                FileUtil.copy(configFile, oldConfigFile);
                configFile.delete();
                origConfig.save(configFile);

                FileConfiguration oldConfig = YamlConfiguration.loadConfiguration(oldConfigFile);
                config = YamlConfiguration.loadConfiguration(configFile);

                //Add old values
                if(!resetFile) {
                    for(String node : config.getKeys(true)) {
                        if(ignoredNodes != null && ignoredNodes.contains(node)) continue;
                        if(oldConfig.getKeys(true).contains(node + ".")) continue;
                        if(ignoredNodes != null && replaceNodes.contains(node)) {
                            config.set(node, null);
                            config.createSection(node);
                        }
                        config.set(node, oldConfig.get(node));
                    }
                }
                //Save regardless
                config.save(configFile);
            }
        }
    }

    public String getString(String node) {
        String val;
        try {
            val = config.getString(node);
        } catch(Exception e) {
            val = "";
        }
        return val;
    }

    public int getInt(String node) {
        int val;
        try {
            val = config.getInt(node);
        } catch(Exception e) {
            val = 0;
        }
        return val;
    }

    public double getDouble(String node) {
        double val;
        try {
            val = config.getDouble(node);
        } catch(Exception e) {
            val = 0d;
        }
        return val;
    }

    public boolean getBool(String node) {
        boolean val;
        try {
            val = config.getBoolean(node);
        } catch(Exception e) {
            val = false;
        }
        return val;
    }

    public ArrayList<String> getKeys(String node) {
        ArrayList<String> val = new ArrayList<String>();
        try {
            for(String key : config.getConfigurationSection(node).getKeys(false)) {
                val.add(key);
            }
        } catch(Exception e) {
            val = new ArrayList<String>();
            e.printStackTrace();
        }
        return val;
    }

    public ArrayList<String> getList(String node) {
        ArrayList<String> val = new ArrayList<String>();
        try {
            for(String s : config.getStringList(node)) {
                val.add(s);
            }
        } catch(Exception e) {
            val = new ArrayList<String>();
            e.printStackTrace();
        }
        return val;
    }
}

