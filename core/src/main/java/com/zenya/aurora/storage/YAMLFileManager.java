package com.zenya.aurora.storage;

import com.zenya.aurora.Aurora;
import com.zenya.aurora.file.YAMLFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class YAMLFileManager {
    public static final YAMLFileManager INSTANCE = new YAMLFileManager();
    private HashMap<String, YAMLFile> fileMap = new HashMap<>();

    public YAMLFileManager() {
        registerFile("config.yml", new YAMLFile(Aurora.getInstance().getDataFolder().getPath(), "config.yml", 1, new ArrayList<>(), new ArrayList<>()));
    }

    public void reloadFiles() {
        fileMap.clear();
        registerFile("config.yml", new YAMLFile("config.yml"));
    }

    public YAMLFile getFile(String fileName) {
        return fileMap.get(fileName);
    }

    public Set<String> getFileNames() {
        return fileMap.keySet();
    }

    public void registerFile(String fileName, YAMLFile file) {
        fileMap.put(fileName, file);
    }

    public void unregisterFile(String fileName) {
        fileMap.remove(fileName);
    }
}
