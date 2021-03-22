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
    /**
     * config.yml
     * **/
    private static final int CONFIG_FILE_VERSION = 2;
    private static final boolean CONFIG_RESET_FILE = false;
    private static final List<String> CONFIG_IGNORED_NODES = new ArrayList<String>() {{
        add("config-version");
    }};
    private static final List<String> CONFIG_REPLACE_NODES = new ArrayList<>();

    /**
     * database.db
     * **/
    private static final int DATABASE_FILE_VERSION = 0; //Unused for now
    private static final boolean DATABASE_RESET_FILE = false;

    public static final StorageFileManager INSTANCE = new StorageFileManager();
    private HashMap<String, StorageFile> fileMap = new HashMap<>();

    public StorageFileManager() {
        registerFile("config.yml", new YAMLFile(Aurora.getInstance().getDataFolder().getPath(), "config.yml", CONFIG_FILE_VERSION, CONFIG_RESET_FILE, CONFIG_IGNORED_NODES, CONFIG_REPLACE_NODES));
        registerFile("database.db", new DBFile(Aurora.getInstance().getDataFolder().getPath(),"database.db", DATABASE_FILE_VERSION, DATABASE_RESET_FILE));
    }

    public void reloadFiles() {
        fileMap.clear();
        registerFile("config.yml", new YAMLFile("config.yml"));
        registerFile("database.db", new DBFile("database.db"));
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
}
