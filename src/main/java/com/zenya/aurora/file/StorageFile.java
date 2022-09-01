package com.zenya.aurora.file;

import com.zenya.aurora.Aurora;

import java.io.File;

public abstract class StorageFile {

    public Aurora plugin;
    public String directory;
    public String fileName;
    public Integer fileVersion;
    public boolean resetFile;
    public File file;

    public StorageFile(Aurora plugin, String fileName) {
        this(plugin, plugin.getDataFolder().getPath(), fileName);
    }

    public StorageFile(Aurora plugin, String directory, String fileName) {
        this(plugin, directory, fileName, null, false);
    }

    public StorageFile(Aurora plugin, String directory, String fileName, Integer fileVersion, boolean resetFile) {
        this.plugin = plugin;
        this.directory = directory;
        this.fileName = fileName;
        this.fileVersion = fileVersion;
        this.resetFile = resetFile;
        this.file = new File(directory, fileName);
    }
}
