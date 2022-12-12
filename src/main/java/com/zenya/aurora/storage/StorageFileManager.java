package com.zenya.aurora.storage;

import com.zenya.aurora.Aurora;
import com.zenya.aurora.file.DBFile;
import com.zenya.aurora.file.StorageFile;
import com.zenya.aurora.file.YAMLFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class StorageFileManager {

    private final Aurora plugin;

    /**
     * config.yml *
     */
    private static final int CONFIG_FILE_VERSION = 4;
    private static final boolean CONFIG_RESET_FILE = true;
    private static final List<String> CONFIG_IGNORED_NODES = new ArrayList<String>() {
        {
            add("config-version");
        }
    };
    private static final List<String> CONFIG_REPLACE_NODES = new ArrayList<String>() {
        {
            add("disabled-worlds");
        }
    };

    /**
     * messages.yml *
     */
    private static final int MESSAGES_FILE_VERSION = 1;
    private static final boolean MESSAGES_RESET_FILE = false;
    private static final List<String> MESSAGES_IGNORED_NODES = new ArrayList<String>() {
        {
            add("config-version");
        }
    };
    private static final List<String> MESSAGES_REPLACE_NODES = new ArrayList<>();

    /**
     * biomes.yml *
     */
    private static final int BIOMES_FILE_VERSION = 0; //Unused for now
    private static final boolean BIOMES_RESET_FILE = false;
    private static final List<String> BIOMES_IGNORED_NODES = new ArrayList<String>() {
        {
            add("config-version");
        }
    };
    private static final List<String> BIOMES_REPLACE_NODES = new ArrayList<>();

    /**
     * database.db *
     */
    private static final int DATABASE_FILE_VERSION = 0; //Unused for now
    private static final boolean DATABASE_RESET_FILE = false;

    private final HashMap<String, StorageFile> fileMap;

    public StorageFileManager(Aurora plugin) {
        this.plugin = plugin;
        fileMap = new HashMap<>();

        final String dataFolderPath = plugin.getDataFolder().getPath();
        registerFile("config.yml", new YAMLFile(plugin, dataFolderPath, "config.yml", CONFIG_FILE_VERSION, CONFIG_RESET_FILE, CONFIG_IGNORED_NODES, CONFIG_REPLACE_NODES));
        registerFile("messages.yml", new YAMLFile(plugin, dataFolderPath, "messages.yml", MESSAGES_FILE_VERSION, MESSAGES_RESET_FILE, MESSAGES_IGNORED_NODES, MESSAGES_REPLACE_NODES));
        registerFile("biomes.yml", new YAMLFile(plugin, dataFolderPath, "biomes.yml", BIOMES_FILE_VERSION, BIOMES_RESET_FILE, BIOMES_IGNORED_NODES, BIOMES_REPLACE_NODES));
        registerFile("database.db", new DBFile(plugin, dataFolderPath, "database.db", DATABASE_FILE_VERSION, DATABASE_RESET_FILE));
    }

    public void reloadFiles() {
        fileMap.clear();
        registerFile("config.yml", new YAMLFile(plugin, "config.yml"));
        registerFile("messages.yml", new YAMLFile(plugin, "messages.yml"));
        registerFile("biomes.yml", new YAMLFile(plugin, "biomes.yml"));
        registerFile("database.db", new DBFile(plugin, "database.db"));
    }

    public StorageFile getFile(String fileName) {
        return fileMap.get(fileName);
    }

    public YAMLFile getYAMLFile(String fileName) {
        return (YAMLFile) getFile(fileName);
    }

    public DBFile getDBFile(String fileName) {
        return (DBFile) getFile(fileName);
    }

    public Set<String> getFileNames() {
        return fileMap.keySet();
    }

    public void registerFile(String fileName, StorageFile file) {
        fileMap.put(fileName, file);
    }

    public void unregisterFile(String fileName) {
        fileMap.remove(fileName);
    }

    public YAMLFile getConfig() {
        return (YAMLFile) getFile("config.yml");
    }

    public YAMLFile getMessages() {
        return (YAMLFile) getFile("messages.yml");
    }

    public YAMLFile getBiomes() {
        return (YAMLFile) getFile("biomes.yml");
    }

    public DBFile getDatabase() {
        return (DBFile) getFile("database.db");
    }
}
